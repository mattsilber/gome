package com.guardanis.commands;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Locale;

import com.guardanis.gtools.net.JsonHelper;

public class KeyboardCommandController implements CommandController {
	
	protected Robot robot;
	
	public KeyboardCommandController() throws Exception {
		robot = new Robot();
	}

	@Override
	public void process(Command command) throws Exception {
		String type = JsonHelper.getString("type", command.getData());
		
		if(type.equals("string")){
			String value = JsonHelper.getString("value", command.getData())
					.toUpperCase(Locale.US);
			
			for(int i = 0; i < value.length(); i++)
				press(Character.toUpperCase(value.charAt(i)));			
		}
		else if(type.equals("action")){
			int value = JsonHelper.getInt("value", command.getData());
			
			if(JsonHelper.getBoolean("shift", command.getData())){
				robot.keyPress(KeyEvent.VK_SHIFT);
				robot.keyPress(value);
		        robot.keyRelease(value);	
				robot.keyRelease(KeyEvent.VK_SHIFT);
			}
			else{
				robot.keyPress(value);
		        robot.keyRelease(value);					
			}		
		}
	}
	
	protected void press(char c){
        robot.keyPress(c);
        robot.keyRelease(c);
	}

}
