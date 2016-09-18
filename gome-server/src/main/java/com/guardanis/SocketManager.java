package com.guardanis;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import com.guardanis.commands.Command;
import com.guardanis.gtools.Logger;
import com.guardanis.gtools.gome.Device;

public class SocketManager extends Thread {
	
	public interface ConnectionEvents {
		public void onAwaitingNextConnection();
		public void onConnected(String ip, int port);
		public void onDeviceIdentified(ClientHelper client, Device device);
		public void onCommandReceived(ClientHelper client, Command command);
		public void onClientDisconnected(ClientHelper client);
	}
		
	private ServerSocket serverSocket;
	private int connectionPort;
	
	private ConnectionEvents eventsCallback;

	public SocketManager(int connectionPort, final ConnectionEvents eventsCallback){
		this.connectionPort = connectionPort;
		this.eventsCallback = new ConnectionEvents(){

			@Override
			public void onAwaitingNextConnection() {
				java.awt.EventQueue.invokeLater(() ->
					eventsCallback.onAwaitingNextConnection());
			}

			@Override
			public void onConnected(String ip, int port) {
				java.awt.EventQueue.invokeLater(() ->
					eventsCallback.onConnected(ip, port));
			}

			@Override
			public void onCommandReceived(ClientHelper client, Command command) {
				java.awt.EventQueue.invokeLater(() ->
					eventsCallback.onCommandReceived(client, command));
			}

			@Override
			public void onClientDisconnected(ClientHelper client) {
				java.awt.EventQueue.invokeLater(() ->
					eventsCallback.onClientDisconnected(client));				
			}

			@Override
			public void onDeviceIdentified(ClientHelper client, Device device) {
				java.awt.EventQueue.invokeLater(() ->
					eventsCallback.onDeviceIdentified(client, device));					
			} };
	}
	
	@Override
	public void run(){
		Logger.info("Starting server on port " + connectionPort);
		
		try{			
//			SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
//			
//			serverSocket = (SSLServerSocket) socketFactory.createServerSocket(connectionPort);
//			serverSocket.setEnabledCipherSuites(socketFactory.getSupportedCipherSuites());
			
			serverSocket = new ServerSocket(connectionPort);

			attemptConnection();
		}
		catch(Throwable e){ Logger.log(e); }		
		
		stopServer();
	}
	
	private void attemptConnection(){
		try{
			Logger.info("Server standing by for next connection");
			
			eventsCallback.onAwaitingNextConnection();
			
			Socket socket = serverSocket.accept();		
			
			eventsCallback.onConnected(socket.getInetAddress().getHostAddress(), 
					connectionPort);
			
			final ClientHelper client = new ClientHelper(socket, eventsCallback);
			
			new Thread(client)
				.start();
			
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
