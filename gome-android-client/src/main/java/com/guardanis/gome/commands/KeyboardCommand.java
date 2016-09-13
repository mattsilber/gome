package com.guardanis.gome.commands;

import org.json.JSONException;
import org.json.JSONObject;

public class KeyboardCommand implements Command {

    private String values;

    public KeyboardCommand(String values){
        this.values = values;
    }

    @Override
    public String getActionIdentifier() {
        return "key";
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put("type", "string")
                .put("value", values);
    }

}
