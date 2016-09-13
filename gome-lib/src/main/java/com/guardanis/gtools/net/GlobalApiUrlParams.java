package com.guardanis.gtools.net;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GlobalApiUrlParams {

    private static GlobalApiUrlParams instance;
    public static GlobalApiUrlParams getInstance() {
        if(instance == null)
            instance = new GlobalApiUrlParams();

        return instance;
    }

    private Map<String, String> globalUrlAdditions = new HashMap<String, String>();

    protected GlobalApiUrlParams() { }

    public GlobalApiUrlParams register(String key, String value) {
        globalUrlAdditions.put(key, value);

        return this;
    }

    public GlobalApiUrlParams unregister(String key) {
        globalUrlAdditions.remove(key);

        return this;
    }

    public String addAdditions(String url) throws UnsupportedEncodingException {
        if(!globalUrlAdditions.isEmpty()){
            url += url.contains("?")
                    ? "&"
                    : "?";

            url += NetUtils.getInstance()
                    .encodeParams(globalUrlAdditions);
        }

        NetUtils.getInstance()
                .log("URL (with additions): " + url);

        return url;
    }
}