package com.guardanis.commands;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONArray;

import com.guardanis.gtools.net.JsonHelper;

public class KeyboardCommandController implements CommandController {
	
	protected Robot robot;
	
	private static Map<String, Integer> WRAPPED_KEYS = new HashMap<String, Integer>();
	
	public KeyboardCommandController() throws Exception {
		robot = new Robot();

		WRAPPED_KEYS.put("ALT", KeyEvent.VK_ALT);
		WRAPPED_KEYS.put("CTRL", KeyEvent.VK_CONTROL);
		WRAPPED_KEYS.put("SHIFT", KeyEvent.VK_SHIFT);
	}

	@Override
	public void process(Command command) throws Exception {
		String type = JsonHelper.getString("type", command.getData());
		
		if(type.equals("string")){
			String value = JsonHelper.getString("value", command.getData())
					.toUpperCase(Locale.US);
			
			Object wrappedKeys = command.getData()
					.getOrDefault("wrapped", null);
			
			if(wrappedKeys == null)
				pressKeys(value);
			else {
				JSONArray wrapped = (JSONArray) wrappedKeys;
				
				for(Object key : wrapped)
					if(WRAPPED_KEYS.get((String) key) != null)
						robot.keyPress(WRAPPED_KEYS.get((String) key));
				
				pressKeys(value);
				
				for(Object key : wrapped)
					if(WRAPPED_KEYS.get((String) key) != null)
						robot.keyRelease(WRAPPED_KEYS.get((String) key));
			}
		}
		else if(type.equals("action")){
			int value = JsonHelper.getInt("value", command.getData());
			
			Object wrappedKeys = command.getData()
					.getOrDefault("wrapped", null);
			
			if(wrappedKeys == null){
				robot.keyPress(value);
		        robot.keyRelease(value);
			}
			else {
				JSONArray wrapped = (JSONArray) wrappedKeys;
				
				for(Object key : wrapped)
					if(WRAPPED_KEYS.get((String) key) != null)
						robot.keyPress(WRAPPED_KEYS.get((String) key));

				robot.keyPress(value);
		        robot.keyRelease(value);
				
				for(Object key : wrapped)
					if(WRAPPED_KEYS.get((String) key) != null)
						robot.keyRelease(WRAPPED_KEYS.get((String) key));
			}	
		}
	}
	
	protected void pressKeys(String value){
		for(int i = 0; i < value.length(); i++)
			press(Character.toUpperCase(value.charAt(i)));			
	}
	
	protected void press(char c){
        robot.keyPress(c);
        robot.keyRelease(c);
	}

}
