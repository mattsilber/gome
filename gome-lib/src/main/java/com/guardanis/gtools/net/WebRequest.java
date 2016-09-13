package com.guardanis.gtools.net;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONObject;

import com.guardanis.gtools.net.NetInterface.FailListener;
import com.guardanis.gtools.net.NetInterface.Jsonable;
import com.guardanis.gtools.net.NetInterface.ResponseParser;
import com.guardanis.gtools.net.NetInterface.SuccessListener;
import com.guardanis.gtools.net.errors.ApiError;
import com.guardanis.gtools.net.errors.ErrorParser;
import com.guardanis.gtools.net.errors.GeneralError;
import com.guardanis.gtools.net.errors.RequestError;
import com.guardanis.gtools.net.streams.InputStreamHelper;
import com.guardanis.gtools.net.streams.OutputStreamHelper;

public abstract class WebRequest<T> implements Runnable {

    public enum ConnectionType {
        GET, POST, PUT, DELETE;
    }

    private static final int CORE_POOL_SIZE = 15;
    private static final int MAXIMUM_POOL_SIZE = 64;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "NetTask #" + mCount.getAndIncrement());
        }
    };
    
    private static final BlockingQueue<Runnable> THREAD_POOL_QUEUE = new LinkedBlockingQueue<Runnable>(CORE_POOL_SIZE * 2);
    protected static final Executor EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, THREAD_POOL_QUEUE, sThreadFactory);
    protected static final ScheduledThreadPoolExecutor SCHEDULED_EXECUTOR = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE);

    protected ConnectionType connectionType;
    protected String targetUrl = "";

    protected Map<String, String> requestProperties = new HashMap<String, String>();

    protected String data;
    protected ResponseParser<T> responseParser;
    protected SuccessListener<T> successListener;
    protected FailListener failListener;

    protected boolean requestExecuted = false;
    protected boolean responseReceived = false;

    protected boolean canceled = false;
    protected boolean failOnCancel = false;

    protected ErrorParser errorParser;

    protected long connectionTimeoutMs = 25000;

    public WebRequest(ConnectionType connectionType){
        this(connectionType, "");
    }

    public WebRequest(ConnectionType connectionType, String targetUrl){
        this.connectionType = connectionType;

        setTargetUrl(targetUrl);
    }

    public WebRequest<T> setTargetUrl(String targetUrl){
        this.targetUrl = targetUrl.trim();
        return this;
    }

    /**
     * Set Jsonable. Throws RuntimeException if there's an issue with the Jsonable
     */
    public WebRequest<T> setData(Jsonable data){
        return setData(data.toJson());
    }

    public WebRequest<T> setData(JSONObject data){
        return setData(data.toString());
    }

    public WebRequest<T> setData(String data){
        this.data = data;
        return this;
    }

    public WebRequest<T> addRequestProperty(String key, String value){
        requestProperties.put(key, value);
        return this;
    }

    public WebRequest<T> setResponseParser(ResponseParser<T> responseParser){
        this.responseParser = responseParser;
        return this;
    }

    public WebRequest<T> onSuccess(SuccessListener<T> successListener){
        this.successListener = successListener;
        return this;
    }

    public WebRequest<T> onFail(FailListener failListener){
        this.failListener = failListener;
        return this;
    }

    public WebRequest<T> setFailOnCancel(boolean failOnCancel){
        this.failOnCancel = failOnCancel;
        return this;
    }

    public WebRequest<T> setErrorParser(ErrorParser errorParser){
        this.errorParser = errorParser;
        return this;
    }

    public WebRequest<T> setConnectionTimeoutMs(long connectionTimeoutMs){
        this.connectionTimeoutMs = connectionTimeoutMs;
        return this;
    }

    public WebRequest<T> execute(){
        if(requestExecuted)
            throw new RuntimeException("You should not execute a WebRequest that has already been run before!");

        requestExecuted = true;

        EXECUTOR.execute(this);

        return this;
    }

    @Override
    public void run(){
        startTimeoutMonitoring();

        try{
            HttpURLConnection connection = openConnection();

            if(connectionType == ConnectionType.POST)
                connection.setDoOutput(true);

            connection.setRequestMethod(connectionType.name());
            setRequestProperties(connection);

            WebResult result = makeRequest(connection, data);

            responseReceived = true;

            onResponseReceived(result);
        }
        catch(final Throwable e){
            postToOriginalThread(new Runnable() {
                public void run() {
                    if(failListener != null)
                        failListener.onFail(new GeneralError(e));
                }
            });

            e.printStackTrace();
        }
    }

    protected HttpURLConnection openConnection() throws Exception {
        URL url = buildUrl();

        return (HttpURLConnection) url.openConnection();
    }

    private void setRequestProperties(HttpURLConnection conn){
        for(String key : requestProperties.keySet())
            conn.setRequestProperty(key, requestProperties.get(key));
        
        applyRequestProperties(conn);
    }
    
    protected abstract void applyRequestProperties(HttpURLConnection conn);

    protected URL buildUrl() throws MalformedURLException, UnsupportedEncodingException {
        return new URL(targetUrl);
    }

    protected WebResult makeRequest(HttpURLConnection conn, String params) throws Exception {
        OutputStreamHelper streamHelper = new OutputStreamHelper(conn);
        streamHelper.write(params);

        WebResult response = readResponse(conn);

        streamHelper.closeConnection();

        NetUtils.getInstance()
                .log("Server response: " + response.getUnparsedResponse());

        return response;
    }

    protected WebResult readResponse(HttpURLConnection conn) throws Exception {
        InputStreamHelper streamHelper = null;
        String response = "";

        try{
            streamHelper = new InputStreamHelper(conn);
            response = streamHelper.read();
        }
        catch(Exception e){ e.printStackTrace(); }
        finally{
            if(streamHelper != null)
                streamHelper.closeConnection();
        }

        return new WebResult(conn.getResponseCode(), response);
    }

    protected void onResponseReceived(WebResult result) throws Exception {
        final RequestError errors = getErrorsFromResult(result);

        if(errors != null && errors.hasErrors())
            failWith(errors);
        else finishWith(responseParser.parse(result));
    }

    protected RequestError getErrorsFromResult(WebResult result){
        ErrorParser activeParser = getErrorParser();

        List<String> errors = activeParser == null
                ? null
                : activeParser.parseErrorMessages(result);

        return errors == null || errors.size() < 1
                ? null
                : new ApiError(result, errors);
    }

    protected ErrorParser getErrorParser(){
        return errorParser == null
                ? NetUtils.getInstance()
                        .getGeneralErrorParser()
                : errorParser;
    }

    protected void failWith(final RequestError errors){
        postToOriginalThread(new Runnable() {
            public void run() {
                if(failListener != null)
                    failListener.onFail(errors);
            }
        });
    }

    protected void finishWith(final T parsedResult){
        postToOriginalThread(new Runnable() {
            public void run() {
                if(successListener != null)
                    successListener.onSuccess(parsedResult);
            }
        });
    }

    /**
     * If the WebRequest is not canceled, post the supplied Runnable back to the originating Thread
     */
    protected void postToOriginalThread(Runnable runnable){
        try{
            if(canceled)
                return;

            runnable.run();
        }
        catch(Throwable e){ e.printStackTrace(); }
    }

    public void cancel(){
        this.canceled = true;

        if(failOnCancel)
            if(failListener != null)
                failListener.onFail(new RequestError("The request was canceled!"));
    }

    protected void startTimeoutMonitoring(){
        if(connectionTimeoutMs < 1)
            return;

        SCHEDULED_EXECUTOR.schedule(new Runnable(){
        	public void run(){
        		if(!(responseReceived || canceled)){
                    WebRequest.this.canceled = true;

                    if(failListener != null)
                        failListener.onFail(new RequestError("The request timed out!"));
                }
        	}
        }, connectionTimeoutMs, TimeUnit.MILLISECONDS);
    }

}
