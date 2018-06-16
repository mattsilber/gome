package com.guardanis.gome.commands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class KeycodeCommand implements Command {

    public static final int VK_INSERT = 155;
    public static final int VK_HOME = 36;
    public static final int VK_END = 35;
    public static final int VK_ESCAPE = 27;

    private int value;
    private List<String> wrappedValues;

    public KeycodeCommand(int value, List<String> wrappedValues) {
        this.value = value;
        this.wrappedValues = wrappedValues;
    }

    @Override
    public String getActionIdentifier() {
        return "key";
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject()
                .put("type", "action")
                .put("value", value);

        if (wrappedValues != null) {
            JSONArray wrapped = new JSONArray();

            for (String s : wrappedValues)
                wrapped.put(s);

            json.put("wrapped", wrapped);
        }

        return json;
    }
}
