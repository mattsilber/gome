package com.guardanis.gome.commands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class KeyboardCommand implements Command {

    public static final String KEY__DELETE = "\b";
    public static final String KEY__TAB = "\t";

    public static final String ACTION__ALT = "ALT";
    public static final String ACTION__CONTROL = "CTRL";
    public static final String ACTION__SHIFT = "SHIFT";

    private String values;
    private List<String> wrappedValues;

    public KeyboardCommand(String values){
        this.values = values;
    }

    public KeyboardCommand(String values, List<String> wrappedValues){
        this.values = values;
        this.wrappedValues = wrappedValues;
    }

    public KeyboardCommand setWrappedValues(List<String> wrappedValues) {
        this.wrappedValues = wrappedValues;
        return this;
    }

    @Override
    public String getActionIdentifier() {
        return "key";
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject()
                .put("type", "string")
                .put("value", values);

        if(wrappedValues != null){
            JSONArray wrapped = new JSONArray();

            for(String s : wrappedValues)
                wrapped.put(s);

            json.put("wrapped", wrapped);
        }

        return json;
    }

}
