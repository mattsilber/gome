package com.guardanis;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonHelper {

    public static String getString(String keyName, JSONObject object){
        return getString(keyName, object, "");
    }

    public static String getString(String keyName, JSONObject object, String defaultValue){
        Object value = object.get(keyName);
        if(value == null)
            return defaultValue;
        else return (String) value;
    }

    public static int getInt(String keyName, JSONObject object){
        return getInt(keyName, object, 0);
    }

    public static int getInt(String keyName, JSONObject object, int defaultValue){
        Object value = object.get(keyName);
        if(value == null)
            return defaultValue;
        else return value instanceof Long
                ? ((Long) value).intValue()
                : (Integer) value;
    }

    public static long getLong(String keyName, JSONObject object){
        return getLong(keyName, object, 0);
    }

    public static long getLong(String keyName, JSONObject object, long defaultValue){
        Object value = object.get(keyName);
        if(value == null)
            return defaultValue;
        else return (Long) value;
    }

    public static float getFloat(String keyName, JSONObject object){
        Object value = object.get(keyName);
        if(value == null)
            return 0;
        else return (Float) value;
    }

    public static boolean getBoolean(String keyName, JSONObject obj){
        Object value = obj.get(keyName);
        return value == null
                ? false
                : (Boolean) value;
    }

    public static int[] getIntArray(String key, JSONObject obj){
        return getIntArray(key, obj, null);
    }

    public static int[] getIntArray(String key, JSONObject obj, int[] defaultValue){
        if(obj.containsKey(key)){
            Object unparsed = obj.get(key);

            if(unparsed == null || !(unparsed instanceof JSONArray))
                return defaultValue;
            else {
                JSONArray parsed = (JSONArray) unparsed;
                int[] parsedValues = new int[parsed.size()];

                for(int i = 0; i < parsed.size(); i++)
                    parsedValues[i] = ((Long) parsed.get(i)).intValue();

                return parsedValues;
            }
        }
        else return defaultValue;
    }

    public static ArrayList<Integer> getIntArrayList(String key, JSONObject obj){
        return getIntArrayList(key, obj, new ArrayList<Integer>());
    }

    public static ArrayList<Integer> getIntArrayList(String key, JSONObject obj, ArrayList<Integer> defaultValue){
        if(obj.containsKey(key)){
            Object unparsed = obj.get(key);

            if(unparsed == null || !(unparsed instanceof JSONArray))
                return defaultValue;
            else {
                ArrayList<Integer> values = new ArrayList<Integer>();

                JSONArray parsed = (JSONArray) unparsed;

                for(int i = 0; i < parsed.size(); i++)
                    values.add(((Long) parsed.get(i)).intValue());

                return values;
            }
        }
        else return defaultValue;
    }

    public static Map<String, String> getMappedStrings(String key, JSONObject obj){
        Map<String, String> mappedValues = new HashMap<String, String>();

        if(obj.containsKey(key)){
            Object unparsed = obj.get(key);

            if(unparsed == null || !(unparsed instanceof JSONObject))
                return mappedValues;
            else {
                JSONObject parsed = (JSONObject) unparsed;

                Iterator<String> keys = parsed.keySet().iterator();
                while(keys.hasNext()){
                    String item = keys.next();
                    mappedValues.put(item, String.valueOf(parsed.get(item)));
                }
            }
        }

        return mappedValues;
    }

    public static String[] getStringArray(String key, JSONObject obj){
        return getStringArray(key, obj, null);
    }

    public static String[] getStringArray(String key, JSONObject obj, String[] defaultValue){
        if(obj.containsKey(key)){
            Object unparsed = obj.get(key);

            if(unparsed == null || !(unparsed instanceof JSONArray))
                return defaultValue;
            else {
                JSONArray parsed = (JSONArray) unparsed;
                String[] parsedValues = new String[parsed.size()];

                for(int i = 0; i < parsed.size(); i++)
                    parsedValues[i] = String.valueOf(parsed.get(i));

                return parsedValues;
            }
        }

        return defaultValue;
    }

    public static JSONObject readLocalFile(String file) throws Exception {
        return (JSONObject) new JSONParser()
                .parse(new FileReader(file));
    }
}