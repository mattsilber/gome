package com.guardanis.gtools.net.errors;

import java.util.List;

import com.guardanis.gtools.net.WebResult;

public class ApiError extends RequestError {

    private WebResult result;

    public ApiError(WebResult result, List<String> errors){
        super(errors);
        this.result = result;
    }

    public WebResult getResult() {
        return result;
    }

}