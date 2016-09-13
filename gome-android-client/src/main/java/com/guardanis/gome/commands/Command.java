package com.guardanis.gome.commands;

import org.json.JSONException;
import org.json.JSONObject;

public interface Command {

    public String getActionIdentifier();

    public JSONObject toJson() throws JSONException;

}
