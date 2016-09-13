package com.guardanis.gtools.net;

import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.util.Map;

import com.guardanis.gtools.Logger;
import com.guardanis.gtools.Properties;
import com.guardanis.gtools.net.errors.DefaultErrorParser;
import com.guardanis.gtools.net.errors.ErrorParser;
import com.owtelse.codec.Base64;

public class NetUtils {

    private static NetUtils instance;
    public static NetUtils getInstance() {
        if(instance == null)
            instance = new NetUtils();

        return instance;
    }

    private String apiUrl;

    private boolean loggingEnabled;

    private ErrorParser generalErrorParser;

    protected NetUtils(){
        this.apiUrl = Properties.getInstance()
        		.getString(Properties.API_URL);
        
        this.loggingEnabled = Properties.getInstance()
        		.getBoolean(Properties.NET_DEBUG_LOG_ENABLED, false);
        
        this.generalErrorParser = new DefaultErrorParser();
    }

    public String getApiUrl(){
        return apiUrl;
    }

    public NetUtils overrideApiUrl(String apiUrl){
        this.apiUrl = apiUrl;
        return this;
    }

    public void addBasicAuthRequestProperty(HttpURLConnection conn){
        if(isBasicAuthEnabled())
            conn.setRequestProperty("Authorization", "Basic " + getBasicAuthEncodedHeader());
    }

    private String getBasicAuthEncodedHeader(){
        String value = Properties.getInstance().getString(Properties.API_USER, "")
                + ":" + Properties.getInstance().getString(Properties.API_PASS, "");

        try {
			return new String(Base64.encode(value));
		} 
        catch (UnsupportedEncodingException e) { e.printStackTrace(); }
        
        return "";
    }

    public boolean isBasicAuthEnabled(){
        return !(Properties.getInstance().getString(Properties.API_USER, null) == null
                && Properties.getInstance().getString(Properties.API_PASS, null) == null);
    }

    public void addVersionRequestProperty(HttpURLConnection conn){
        if(isApiVersionHeaderEnabled())
            conn.setRequestProperty("Api-Version", Properties.getInstance().getString(Properties.API_VERSION));
    }

    public boolean isApiVersionHeaderEnabled(){
        return Properties.getInstance().getString(Properties.API_VERSION) != null;
    }

    public String encodeParams(Map<String, String> params) throws UnsupportedEncodingException {
        if(params == null)
            return "";

        String encoded = "";

        for(String key : params.keySet())
            encoded += getUrlEncodedValue(key) + "=" + getUrlEncodedValue(params.get(key)) + "&";

        return encoded.substring(0, encoded.length() - 1);
    }

    public String getUrlEncodedValue(String toEncode) throws UnsupportedEncodingException {
        return URLEncoder.encode(toEncode, "UTF-8");
    }

    public NetUtils setGeneralErrorParser(ErrorParser errorParser){
        this.generalErrorParser = errorParser;

        return this;
    }

    public ErrorParser getGeneralErrorParser(){
        return generalErrorParser;
    }
    
    public static String getMacAddress(InetAddress ip){
    	try{
        	NetworkInterface network = NetworkInterface.getByInetAddress(ip);

    		byte[] mac = network.getHardwareAddress();

    		StringBuilder sb = new StringBuilder();
    		
    		for (int i = 0; i < mac.length; i++)
    			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
    		
    		return sb.toString();
    		
    	}
    	catch(Exception e){ e.printStackTrace(); }
    	
    	throw new RuntimeException("Not connected to the network or something weird...");
    }

    public static void close(Closeable closeable){
        try{
            if(closeable != null)
                closeable.close();
        }
        catch(Throwable e){ e.printStackTrace(); }
    }

    public void log(String message){
        if(loggingEnabled)
        	Logger.getInstance()
        		.debug(message);
    }


}