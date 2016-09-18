package com.guardanis.gome.commands;

import org.json.JSONException;
import org.json.JSONObject;

public class KeyboardEventCommand implements Command {

    private int value;
    private boolean shiftEnabled;

    public KeyboardEventCommand(int value, boolean shiftEnabled){
        this.value = value;
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
                .put("value", value)
                .put("shift", shiftEnabled);
    }

}
