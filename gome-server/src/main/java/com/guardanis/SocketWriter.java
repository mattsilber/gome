package com.guardanis;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.guardanis.gtools.Logger;

public class SocketWriter {
	
	private BufferedWriter writer;
	
	public SocketWriter(Socket socket) throws IOException {
		writer = new BufferedWriter(
				new OutputStreamWriter(socket.getOutputStream()));
	}
	
	public void write(String data){
		try{
			writer.write(data);
			writer.newLine();
			writer.flush();
		}
		catch(Exception e){ Logger.log(e); }
	}
	
	public void onDestroy(){
		try{
			if(writer != null)
				writer.close();
		}
		catch(Exception e){ }
	}

}
