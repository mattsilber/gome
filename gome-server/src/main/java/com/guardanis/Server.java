package com.guardanis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.guardanis.SocketManager.ConnectionEvents;
import com.guardanis.commands.Command;
import com.guardanis.commands.CommandController;
import com.guardanis.commands.KeyboardCommandController;
import com.guardanis.commands.MouseCommandController;
import com.guardanis.display.DisplayController;
import com.guardanis.gtools.GTools;
import com.guardanis.gtools.Logger;
import com.guardanis.gtools.general.Callback;
import com.guardanis.gtools.gome.Device;

public class Server implements ConnectionEvents {

    public static void main(String[] args){
    	try {
			new Server();
		} 
    	catch (Exception e) {
			e.printStackTrace();
			
			throw new RuntimeException("Unable to start the Server!");
		}
    }

    public static final String ACTION_MOUSE = "mouse";
    public static final String ACTION_KEYBOARD = "key";

	private SocketManager socketManager;
	private PingManager pingManager;
	
    private List<Device> connectedDevices = new CopyOnWriteArrayList<Device>();
    
    private Map<String, CommandController> commandControllers = new HashMap<String, CommandController>();
    
    private DisplayController displayController;
    
    protected Server() throws Exception {
    	this.socketManager = new SocketManager(GTools.CONNECTION_PORT, this);
    	this.pingManager = new PingManager(GTools.PING_PORT);

    	commandControllers.put(ACTION_MOUSE, new MouseCommandController());
    	commandControllers.put(ACTION_KEYBOARD, new KeyboardCommandController());
    	
    	socketManager.start();
    	pingManager.start();
    	
    	displayController = new DisplayController()
    			.show(connectedDevices);
    }

	@Override
	public void onAwaitingNextConnection() {
		
	}

	@Override
	public void onConnected(String ip, int port) {
		
	}

	@Override
	public void onDeviceIdentified(ClientHelper client, Device device) {
		Logger.info("%1$s connected from %2$s", 
				device.getName(), 
				device.getIpAddress());
		
		connectedDevices.add(device);
		
		displayController.onDeviceAdded(device);
	}

	@Override
	public void onCommandReceived(ClientHelper client, Command command) {
		try{
//			Logger.info("Received action [%1$s] with: %2$s", 
//					command.getAction(), 
//					command.getData().toString());
			
			if(commandControllers.containsKey(command.getAction()))
				commandControllers.get(command.getAction())
					.process(command);
		}
		catch(Exception e){ e.printStackTrace(); }
	}

	@Override
	public void onClientDisconnected(ClientHelper client) {
		Logger.info("%1$s disconnected", 
				client.getDevice().getName());
		
		withDeviceOrForget(client, device 
				-> connectedDevices.remove(device));

		displayController.onDeviceRemoved(client.getDevice());
	}
	
	private void withDeviceOrForget(ClientHelper client, Callback<Device> callback){
		for(Device device : connectedDevices){
			if(device.getIdentifier().equals(client.getDevice().getIdentifier())){
				callback.onCalled(device);
				
				return;
			}
		}
	}

}
