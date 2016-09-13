package com.guardanis.gtools.net;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class GlobalApiRequestProperties {

    private static GlobalApiRequestProperties instance;
    public static GlobalApiRequestProperties getInstance() {
        if(instance == null)
            instance = new GlobalApiRequestProperties();

        return instance;
    }

    private Map<String, String> requestProperties = new HashMap<String, String>();

    protected GlobalApiRequestProperties() { }

    public GlobalApiRequestProperties register(String key, String value) {
        requestProperties.put(key, value);

        return this;
    }

    public GlobalApiRequestProperties unregister(String key) {
        requestProperties.remove(key);

        return this;
    }

    public GlobalApiRequestProperties addProperties(HttpURLConnection conn) {
        NetUtils.getInstance()
                .addBasicAuthRequestProperty(conn);

        NetUtils.getInstance()
                .addVersionRequestProperty(conn);

        for(String key : requestProperties.keySet())
            conn.setRequestProperty(key, requestProperties.get(key));

        return this;
    }
}
