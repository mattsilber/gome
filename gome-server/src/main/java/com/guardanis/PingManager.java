package com.guardanis;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.guardanis.gtools.Logger;

public class PingManager extends Thread {
		
	private ServerSocket serverSocket;
	private int connectionPort;

	public PingManager(int connectionPort){
		this.connectionPort = connectionPort;
	}
	
	@Override
	public void run(){
		Logger.info("Starting Ping Manager on port " + connectionPort);
		
		try{						
			serverSocket = new ServerSocket(connectionPort);

			attemptConnection();
		}
		catch(Throwable e){ Logger.log(e); }		
		
		stopServer();
	}
	
	private void attemptConnection(){
		try{
			Logger.info("Ping-Server standing by for next connection on port " + connectionPort);
			
			Socket socket = serverSocket.accept();		
			
			Logger.info("Ping requested by " + socket.getInetAddress()
						.toString());
			
			SocketWriter writer = new SocketWriter(socket);
			
			writer.write(InetAddress.getLocalHost()
					.getHostName());
			
			writer.onDestroy();
			
			attemptConnection();
		}
		catch(Exception e){ e.printStackTrace(); }
	}
	
	public void stopServer(){
		try{
			serverSocket.close();
		}
		catch(Throwable e){ Logger.log(e); }
	}

}
