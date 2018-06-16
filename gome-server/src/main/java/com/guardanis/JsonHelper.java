package com.guardanis;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonHelper {

    public static String getString(String keyName, JSONObject object) {
        return getString(keyName, object, "");
    }

    public static String getString(String keyName, JSONObject object, String defaultValue) {
        Object value = object.get(keyName);
        if (value == null)
            return defaultValue;
        else
            return (String) value;
    }

    public static int getInt(String keyName, JSONObject object) {
        return getInt(keyName, object, 0);
    }

    public static int getInt(String keyName, JSONObject object, int defaultValue) {
        Object value = object.get(keyName);

        if (value == null)
            return defaultValue;
        else
            return value instanceof Long
                ? ((Long) value).intValue()
                : (Integer) value;
    }

    public static long getLong(String keyName, JSONObject object) {
        return getLong(keyName, object, 0);
    }

    public static long getLong(String keyName, JSONObject object, long defaultValue) {
        Object value = object.get(keyName);

        if (value == null)
            return defaultValue;
        else
            return (Long) value;
    }
}