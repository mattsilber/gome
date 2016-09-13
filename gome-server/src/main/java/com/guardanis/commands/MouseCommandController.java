package com.guardanis.commands;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import com.guardanis.gtools.Logger;
import com.guardanis.gtools.net.JsonHelper;

public class MouseCommandController implements CommandController {
	
	protected Robot robot;
	
	public MouseCommandController() throws Exception {
		robot = new Robot();
	}

	@Override
	public void process(Command command) throws Exception {
		String type = JsonHelper.getString("type", command.getData());
					
		if(type.equals("move")) {
			Point currentMousePos = MouseInfo.getPointerInfo()
					.getLocation();
			
			int mouseX = currentMousePos.x + JsonHelper.getInt("mouse_x", command.getData());
			int mouseY = currentMousePos.y + JsonHelper.getInt("mouse_y", command.getData());
			
			Logger.info("Moving to: " + mouseX + ", " + mouseY);
			
			robot.mouseMove(mouseX, mouseY);
		}
		else if(type.equals("left_single_click"))
			click(InputEvent.BUTTON1_MASK);
		else if(type.equals("left_double_click"))
			doubleClick(InputEvent.BUTTON1_MASK);
		else if(type.equals("right_single_click"))
			click(InputEvent.BUTTON2_MASK);
		else if(type.equals("wheel_move")){
			int value = JsonHelper.getInt("wheel_value", command.getData());			
			
			robot.mouseWheel(value);
		}
		else if(type.equals("drag_start")){
			robot.keyPress(KeyEvent.VK_SHIFT);
			robot.mousePress(InputEvent.BUTTON3_MASK);
		}
		else if(type.equals("drag_end")){
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
			robot.keyRelease(KeyEvent.VK_SHIFT);
		}
	}
	
	protected void click(int mask){
		robot.mousePress(mask);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	
	protected void doubleClick(int mask) throws Exception {
		click(mask);
        Thread.sleep(50);        
        click(mask);
	}

}
