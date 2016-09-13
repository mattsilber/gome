package com.guardanis.gome.commands;

import org.json.JSONException;
import org.json.JSONObject;

public class KeyboardEventCommand implements Command {

    private int values;
    private boolean shiftEnabled;

    public KeyboardEventCommand(int values, boolean shiftEnabled){
        this.values = values;
        this.shiftEnabled = shiftEnabled;
    }

    @Override
    public String getActionIdentifier() {
        return "key";
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put("type", "number")
                .put("value", values)
                .put("shift", shiftEnabled);
    }

}
