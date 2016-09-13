package com.guardanis.gtools;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.guardanis.gtools.general.MappedValuesHelper;

public class Properties {
	
	private static Properties instance;
	public static Properties getInstance(){
		if(instance == null)
			instance = new Properties();
		
		return instance;
	}

    public static final String ENVIRONMENT = "g__environment";
    public static final String API_URL = "g__api_url";
    public static final String API_USER = "g__api_user";
    public static final String API_PASS = "g__api_pass";
    public static final String API_VERSION = "g__api_version";
    public static final String NET_DEBUG_LOG_ENABLED = "g__net_debug_log_enabled";

    public static final String CONFIG_FILE = "environment.config";
    
	private Map<String, String> properties = new HashMap<String, String>();
    
    protected Properties(){
    	URL url = getClass()
				.getClassLoader()
				.getResource(CONFIG_FILE);
    	
    	if(url != null)
    		this.properties = MappedValuesHelper.parseResource(CONFIG_FILE);   	
    	
    	//logProperties();
    }

    public String getString(String key){
        return getString(key, "");
    }

    public String getString(String key, String defaultValue){
        return properties.get(key) == null
        		? defaultValue
        		: properties.get(key);
    }

    public int getInt(String key, int defaultValue){
        try{
        	return Integer.parseInt(properties.get(key));
        }
        catch(Exception e){ e.printStackTrace(); }
        
        return defaultValue;
    }

    public double getDouble(String key, double defaultValue){
        try{
        	return Double.parseDouble(properties.get(key));
        }
        catch(Exception e){ e.printStackTrace(); }
        
        return defaultValue;
    }

    public boolean getBoolean(String key){
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue){
        try{
        	return Boolean.parseBoolean(properties.get(key));
        }
        catch(Exception e){ e.printStackTrace(); }
        
        return defaultValue;
    }
    
    public void logProperties(){    	
    	for(String key : properties.keySet())
    		Logger.getInstance()
    			.info("Property: " + key + " = " + properties.get(key));    	
    }
    
    public boolean isAvailable(String... keys){
    	for(String key : keys)
    		if(properties.get(key) == null)
    			return false;
    	
    	return true;
    }
    
    public Properties register(String key, String value){
    	properties.put(key,  value);
    	return this;
    }
    
    public static Properties from(String[] args){
    	Properties props = new Properties();    	
    	props.properties = MappedValuesHelper.parse(args);   	
    	
    	return props;
    }

}
