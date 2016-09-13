package com.guardanis.gome.commands;

import org.json.JSONException;
import org.json.JSONObject;

public class MouseClickCommand implements Command {

    private String type;

    public MouseClickCommand(String type){
        this.type = type;
    }

    @Override
    public String getActionIdentifier() {
        return "mouse";
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put("type", type);
    }

}
