package com.guardanis.gtools.general;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.guardanis.gtools.Logger;
import com.guardanis.gtools.net.streams.InputStreamHelper;

public class TextFileHelper {
	
	public static String readFile(String file){
		return readFile(new File(file));
	}
	
	public static String readResource(String resourceFile){
		try{
			InputStream stream = TextFileHelper.class
					.getClassLoader()
					.getResourceAsStream(resourceFile);
			
			InputStreamHelper streamHelper = new InputStreamHelper(stream);
						
			return streamHelper.read("\n");
		}
		catch(Exception e){
			e.printStackTrace();
			
			throw new RuntimeException(e);
		}
	}
	
	public static String readFile(File file){
		return readFile(file.toURI());
	}
	
	public static String readFile(URI uri){
		String fileText = "";
		
		try {
			List<String> lines = Files.readAllLines(Paths.get(uri), 
					StandardCharsets.UTF_8);
			
			for(String line : lines) 
				fileText += line + "\n";
	    }
		catch(Exception e){ 
			e.printStackTrace();
			
			Logger.getInstance().log(e); 
		}
		
		return fileText;
	}
	
	public static void append(File file, String text){
		PrintWriter out = null;
		try {
		    out = new PrintWriter(
		    		new BufferedWriter(
		    				new FileWriter(file, true)));
		    
		    out.println(text);
		}
		catch (IOException e) { Logger.getInstance().log(e); }
		finally{
		    if(out != null)
		    	out.close();
		} 
	}

}