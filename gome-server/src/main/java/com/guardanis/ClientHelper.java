package com.guardanis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.guardanis.SocketManager.ConnectionEvents;
import com.guardanis.commands.Command;
import com.guardanis.gtools.Logger;
import com.guardanis.gtools.gome.Device;

public class ClientHelper extends Thread {
		
	private Socket socket;
	private Device device;
	private ConnectionEvents eventsCallback;
	
    private BufferedReader reader;
	private SocketWriter writer;
	
	private Runnable finishedCallback;
	
	public ClientHelper(Socket socket, final ConnectionEvents eventsCallback){
		this.socket = socket;
		this.eventsCallback = eventsCallback;
		
		Logger.info("Client connected from %1$s, preparing to read.", 
				socket.getInetAddress().getHostAddress());
	}
	
	public ClientHelper setFinishedCallback(Runnable finishedCallback){
		this.finishedCallback = finishedCallback;
		return this;
	}
	
	@Override
	public void run(){
		try{
			reader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));	
			
			this.device = readDevice();
			
			eventsCallback.onDeviceIdentified(this, device);
			
			writer = new SocketWriter(socket);

			writer.write(InetAddress.getLocalHost()
					.getHostName());
			
			readInputStream();
		}
		catch(Throwable e){ Logger.log(e); }
		
		eventsCallback.onClientDisconnected(this);
		
		if(finishedCallback != null)
			finishedCallback.run();
	}
	
	private Device readDevice() throws Throwable {
		JSONObject deviceData = (JSONObject) new JSONParser()
				.parse(reader.readLine());
		
		return new Device(deviceData);	
	}
	
	private void readInputStream() throws Throwable {		
		String line;		
		
		while ((line = reader.readLine()) != null) 
			process(line);			
	}
	
	private void process(String data) throws Exception {		
//		Logger.info("Data received: " + data);
		
		int delimiter = data.indexOf(":");
		
		Command command = new Command(data.substring(0, delimiter),
				(JSONObject) new JSONParser()
					.parse(data.substring(delimiter + 1)));
		
		device.setLastPing(System.currentTimeMillis());
		
		eventsCallback.onCommandReceived(this, command);
	}
	
	public void write(String data){
		writer.write(data);
	}
	
	public void destroy(){
		try{
			if(reader != null)
				reader.close();
		}
		catch(Exception e){ }

		writer.onDestroy();
		
		try{
			socket.close();
		}
		catch(Exception e){ }
	}
	
	public Device getDevice(){
		return device;
	}

}
