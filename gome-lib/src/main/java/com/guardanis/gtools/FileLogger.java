package com.guardanis.gtools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;

public class FileLogger {

	private static Map<String, FileLogger> instances = new HashMap<String, FileLogger>();
	public static FileLogger getInstance(String bodegaId){
		if(instances.get(bodegaId) == null)
			instances.put(bodegaId, new FileLogger(bodegaId));
		
		return instances.get(bodegaId);
	}
	
	private static final String LOG_FILE_FORMAT = "logs/%1$s.log";
	
	private Logger logger;
	
	protected FileLogger(String file){ 
		logger = Logger.getLogger(file);
		
		PropertyConfigurator.configure(getClass()
				.getClassLoader()
				.getResourceAsStream("file-logger-base.properties"));
				
		try {
			PatternLayout layout = new PatternLayout("%d{dd-MM-yyyy HH:mm:ss} %-5p - %m%n");
			
			RollingFileAppender appender = new RollingFileAppender(layout, 
					String.format(LOG_FILE_FORMAT, file));			
			
		    appender.setAppend(true);
		    appender.setMaxFileSize("1MB");
		    appender.setMaxBackupIndex(1);
		    
		    logger.addAppender(appender);
		} 		
		catch (IOException e) {
			e.printStackTrace();
			
			throw new RuntimeException(e);
		}		
	}

	public Logger getLogger(){
		return logger;
	}
	
	public void debug(String message){
		logger.debug(message);
	}
	
	public void debug(String message, String... formats){
		logger.debug(String.format(message, 
				formats));
	}
	
	public void info(String message){
		logger.info(message);
	}
	
	public void info(String message, String... formats){
		logger.info(String.format(message, 
				formats));
	}
	
	public void error(String message){
		logger.error(message);
	}
	
	public void error(String message, String... formats){
		logger.error(String.format(message, 
				formats));
	}
	
	public void log(Throwable e){
		logger.error(e.getMessage());
	}
}
