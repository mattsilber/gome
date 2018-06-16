package com.guardanis.commands;

import com.guardanis.JsonHelper;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MouseCommandController implements CommandController {
	
	protected static final String ACTION_DRAG = "drag";
	
	protected Robot robot;
	
	protected List<String> protectedActions = new ArrayList<String>();	
	protected ProtectedMouseCommand protectedCommand;
	
	public MouseCommandController() throws Exception {
		robot = new Robot();

		protectedActions.add(ACTION_DRAG);
	}

	@Override
	public void process(Command command) throws Exception {
		String type = JsonHelper.getString("type", command.getData());
					
		if(type.equals("move")) {
			Point currentMousePos = MouseInfo.getPointerInfo()
					.getLocation();
			
			int mouseX = currentMousePos.x + JsonHelper.getInt("mouse_x", command.getData());
			int mouseY = currentMousePos.y + JsonHelper.getInt("mouse_y", command.getData());
			
//			Logger.info("Moving to: " + mouseX + ", " + mouseY);
			
			robot.mouseMove(mouseX, mouseY);
		}
		else if(type.equals("scroll"))			
			robot.mouseWheel(JsonHelper.getInt("mouse_y", command.getData()));
		else if(type.equals("left_single_click"))
			click(InputEvent.BUTTON1_MASK);
		else {
			ProtectedMouseCommand lastProtectedCommand = protectedCommand;
			
			closeProtectedCommand();
			
			if(type.equals("left_double_click"))
				doubleClick(InputEvent.BUTTON1_MASK);
			else if(type.equals("wheel_click"))
				click(InputEvent.BUTTON2_DOWN_MASK);
			else if(type.equals("right_single_click"))
				click(InputEvent.BUTTON3_DOWN_MASK);
			else if(type.equals(ACTION_DRAG)
					&& (lastProtectedCommand == null || !lastProtectedCommand.key.equals(ACTION_DRAG))){
				robot.keyPress(KeyEvent.VK_SHIFT);
				robot.mousePress(InputEvent.BUTTON1_MASK);
								
				protectedCommand = new ProtectedMouseCommand(ACTION_DRAG, () -> {
					robot.mouseRelease(InputEvent.BUTTON1_MASK);
					robot.keyRelease(KeyEvent.VK_SHIFT);					
				});
			}
		}
	}
	
	protected void closeProtectedCommand(){		
		if(protectedCommand != null){
			protectedCommand.disableRunnable.run();
			protectedCommand = null;			
		}
	}
	
	protected void toggleDrag(){
		
	}
	
	protected void click(int mask){
		robot.mousePress(mask);
		robot.mouseRelease(mask);
	}
	
	protected void doubleClick(int mask) throws Exception {
		click(mask);
        Thread.sleep(50);        
        click(mask);
	}
	
	protected static class ProtectedMouseCommand {		
		protected String key;
		protected Runnable disableRunnable;
		
		protected ProtectedMouseCommand(String key, Runnable disableRunnable){
			this.key = key;
			this.disableRunnable = disableRunnable;
		}
	}

}
