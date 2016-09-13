package com.guardanis.gtools.net;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.guardanis.gtools.Logger;

public class WebResult {

    private int responseCode;
    private String unparsedResponse;

    public WebResult(int responseCode, String unparsedResponse) {
        this.responseCode = responseCode;
        this.unparsedResponse = unparsedResponse;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getUnparsedResponse() {
        return unparsedResponse;
    }

    public boolean isSuccessful() {
        return responseCode >= 200 && responseCode < 400;
    }

    public boolean isResponseCodeKnown() {
        return responseCode > 0;
    }

    public boolean isResponseJson() {
        return getResponseJson() != null;
    }

    public JSONObject getResponseJson() {
        try{
            return (JSONObject) new JSONParser()
                    .parse(unparsedResponse);
        }
        catch(ParseException e){ Logger.getInstance().log(e); }

        return null;
    }

}