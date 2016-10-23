package com.guardanis.gome;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class Settings {

    private static Settings instance;
    public static Settings getInstance(Context context){
        if(instance == null)
            return new Settings(context);

        return instance;
    }

    private static final String PREFS = "settings";
    public static final String KEY__MOVE_SPEED = "move_speed"; // Integer 1-10
    public static final String KEY__SCROLL_SPEED = "scroll_speed"; // Integer 1-10

    private SharedPreferences preferences;

    private Map<String, Object> values = new HashMap<String, Object>();

    protected Settings(Context context){
        this.preferences = context.getSharedPreferences(PREFS, 0);

        this.values.put(KEY__MOVE_SPEED,
                preferences.getInt(KEY__MOVE_SPEED, 10));

        this.values.put(KEY__SCROLL_SPEED,
                preferences.getInt(KEY__SCROLL_SPEED, 3));
    }

    public <T> T get(String key, T defaultValue){
        try{
            return (T) values.get(key);
        }
        catch(NullPointerException e){ e.printStackTrace(); }
        catch(ClassCastException e){ e.printStackTrace(); }

        return defaultValue;
    }

    public <T> void put(String key, T value){
        values.put(key, value);

        SharedPreferences.Editor editor = preferences.edit();

        if(value instanceof Integer)
            editor.putInt(key, (Integer) value);
        else if(value instanceof String)
            editor.putString(key, (String) value);
        else if(value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);

        editor.commit();
    }

}
