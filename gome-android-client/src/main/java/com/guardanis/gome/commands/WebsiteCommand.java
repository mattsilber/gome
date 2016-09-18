package com.guardanis.gome.commands;

import org.json.JSONException;
import org.json.JSONObject;

public class WebsiteCommand implements Command {

    private String url;

    public WebsiteCommand(String url){
        this.url = url;
    }

    @Override
    public String getActionIdentifier() {
        return "web";
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put("url", url);
    }

}
