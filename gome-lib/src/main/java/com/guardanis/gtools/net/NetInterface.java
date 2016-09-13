package com.guardanis.gtools.net;

import org.json.simple.JSONObject;

import com.guardanis.gtools.net.errors.RequestError;

public class NetInterface {

    public interface ResponseParser<T> {
        public T parse(WebResult result) throws Exception;
    }

    public interface SuccessListener<T> {
        public void onSuccess(T result);
    }

    public interface FailListener {
        public void onFail(RequestError error);
    }

    public interface Jsonable {
        public JSONObject toJson() throws RuntimeException;
    }

}
