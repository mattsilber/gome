package com.guardanis.gtools.general;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.guardanis.gtools.Logger;

public class MappedValuesHelper {
	
	public static Map<String, String> parseResource(String resourceFile){
		String data = TextFileHelper.readResource(resourceFile);
				
		return parse(data.split("\n"));
	}

	public static Map<String, String> parse(File file){
		String data = TextFileHelper.readFile(file);
		
		return parse(data.split("\n"));
	}
	
	public static Map<String, String> parse(String[] data){
		return parse(Arrays.asList(data));
	}
	
	public static Map<String, String> parse(List<String> data){
		HashMap<String, String> values = new HashMap<String, String>();
		
		for(String arg : data){
			try{
				if(arg.contains("=")){
					if(arg.endsWith("="))
						values.put(arg.substring(0, arg.length() - 1), "");					
					else{
						String[] split = arg.split("=");
						
						values.put(split[0], split[1]);							
					}						
				}		
				else values.put(arg, "");
			}
			catch(Exception e){ Logger.getInstance().log(e); }
		}
		
		return values;
	}
	
}
