package com.guardanis.gtools.net.errors;

import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.ssl.SSLException;

public class GeneralError extends RequestError {

    private Throwable throwable;
    private boolean connectionIssue = false;

    public GeneralError(Throwable throwable){
        super(new ArrayList<String>());

        this.throwable = throwable;

        if(isLikelyConnectionError()){
            this.connectionIssue = true;

            errors.add("A connection-related issue occurred!");
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }

    private boolean isLikelyConnectionError() {
        return throwable != null &&
                (throwable instanceof SocketTimeoutException
                        || throwable instanceof SocketException
                        || throwable instanceof InterruptedIOException
                        || throwable instanceof UnknownHostException
                        || throwable instanceof SSLException);
    }

    public boolean isConnectionIssue(){
        return connectionIssue;
    }
}