package com.guardanis.gtools.net;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiRequest<T> extends WebRequest<T> {

    public ApiRequest(ConnectionType connectionType) {
        super(connectionType);
    }

    public ApiRequest(ConnectionType connectionType, String targetUrl) {
        super(connectionType, targetUrl);
    }

    @Override
    public ApiRequest<T> setTargetUrl(String targetUrl){
        this.targetUrl = NetUtils.getInstance()
        		.getApiUrl() + targetUrl.trim();
        
        return this;
    }

    @Override
    protected void applyRequestProperties(HttpURLConnection conn){
		conn.setRequestProperty("Accept", "application/json");
		conn.setRequestProperty("Content-type", "application/json");
		
		NetUtils.getInstance()
			.addVersionRequestProperty(conn);
		
		NetUtils.getInstance()
			.addBasicAuthRequestProperty(conn);
		
        GlobalApiRequestProperties.getInstance()
                .addProperties(conn);
    }

    @Override
    protected URL buildUrl() throws MalformedURLException, UnsupportedEncodingException {
        return new URL(GlobalApiUrlParams.getInstance()
                .addAdditions(targetUrl));
    }
    
}