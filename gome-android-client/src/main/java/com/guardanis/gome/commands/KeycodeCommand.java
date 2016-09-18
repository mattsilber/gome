package com.guardanis.gome.commands;

import org.json.JSONException;
import org.json.JSONObject;

public class KeycodeCommand implements Command {

    public static final int VK_INSERT = 155;
    public static final int VK_HOME = 36;
    public static final int VK_END = 35;

    private int value;
    private boolean shiftEnabled;

    public KeycodeCommand(int value, boolean shiftEnabled){
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
                .put("type", "action")
                .put("value", value)
                .put("shift", shiftEnabled);
    }

}
