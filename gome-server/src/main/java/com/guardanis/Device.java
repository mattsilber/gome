package com.guardanis;

import org.json.simple.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Map;

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
	protected int port = 133337;
	protected String name;
	protected Status status;
	protected long lastPing;
	
	protected Device(){ }
	
	public Device(JSONObject obj){
		ip = JsonHelper.getString("ip_address", obj);
		macAddress = JsonHelper.getString("mac_address", obj);
		port = JsonHelper.getInt("port", obj, port);
		name = JsonHelper.getString("name", obj);
		status = Status.safelyGetValueOf(JsonHelper.getString("status", obj));
		lastPing = JsonHelper.getLong("last_ping", obj);
	}
	
	public static Device getMine() {
        Device device = new Device();
        device.name = getComputerName();
        device.status = Status.UNKNOWN;

        try {
            InetAddress currentIp = InetAddress.getLocalHost();

            device.ip = currentIp.getHostAddress();
            device.macAddress = getMacAddress(currentIp);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();

            throw new RuntimeException("Not connection or something?");
        }

        return device;
    }
	
    private static String getComputerName(){
        Map<String, String> env = System.getenv();

        if(env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            return env.get("HOSTNAME");
        else
            return "Unknown Computer";
    }
	
    private static String getMacAddress(InetAddress ip){
        try{
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < mac.length; i++)
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));

            return sb.toString();

        }
        catch(Exception e){ e.printStackTrace(); }

        throw new RuntimeException("Not connected to the network or something weird...");
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

	@Override
	public String toString(){
		return name
				+ "\n Connected from: " + ip
				+ "\n Last Ping: " + lastPing;
	}
}
