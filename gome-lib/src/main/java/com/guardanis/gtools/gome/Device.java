package com.guardanis.gtools.gome;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;

import com.guardanis.gtools.GTools;
import com.guardanis.gtools.net.JsonHelper;
import com.guardanis.gtools.net.NetUtils;

public class Device {
	
	public enum Status {
		CONNECTED, DISCONNECTED, UNKNOWN;
		
		public static Status safelyGetValueOf(String name){
			try{
				return Status.valueOf(name);
			}
			catch(Exception e){ e.printStackTrace(); }
			
			return UNKNOWN;
		}
	}
	
	protected String ip;
	protected String macAddress;
	protected int port = GTools.CONNECTION_PORT;
	
	protected String name;
	protected Status status;
	
	protected long lastPing;
	
	protected Device(){ }
	
	public Device(JSONObject obj){
		ip = JsonHelper.getString("ip_address", obj);
		macAddress = JsonHelper.getString("mac_address", obj);
		port = JsonHelper.getInt("port", obj, GTools.CONNECTION_PORT);
		name = JsonHelper.getString("name", obj);
		status = Status.safelyGetValueOf(JsonHelper.getString("status", obj));
		lastPing = JsonHelper.getLong("last_ping", obj);
	}
	
	public String getIpAddress() {
		return ip;
	}
	
	public void setIpAddress(String ip){
		this.ip = ip;
	}
	
	public String getMacAddress() {
		return macAddress;
	}

	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public long getLastPing() {
		return lastPing;
	}

	public void setLastPing(long lastPing) {
		this.lastPing = lastPing;
	}
	
	public String getIdentifier(){
		return name + macAddress;
	}

	public JSONObject toJson(){
		JSONObject obj = new JSONObject();
		obj.put("ip_address", ip);
		obj.put("mac_address", macAddress);
		obj.put("port", port);
		obj.put("name", name);
		obj.put("status", status.name());
		obj.put("last_ping", lastPing);		
				
		return obj;
	}
	
	public static Device getMine(){
		Device device = new Device();
		device.name = GTools.getComputerName();	
		device.status = Status.UNKNOWN;
		
		try {
			InetAddress currentIp = InetAddress.getLocalHost();
			
			device.ip = currentIp.getHostAddress();
			device.macAddress = NetUtils.getMacAddress(currentIp);
		} 		
		catch (UnknownHostException e) {
			e.printStackTrace();
			
			throw new RuntimeException("Not connection or something?");
		}
		
		return device;
	}

}
