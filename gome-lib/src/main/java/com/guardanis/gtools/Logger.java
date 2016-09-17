package com.guardanis.gtools;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;

public class Logger {
	
	private static Logger instance;
	public static synchronized Logger getInstance(){
		if(instance == null)
			instance = new Logger();
		
		return instance;
	}
	
	private Log4jLoggerAdapter logger;
	
	protected Logger(){ 
		logger = (Log4jLoggerAdapter) LoggerFactory.getLogger(Logger.class);
		
		PropertyConfigurator.configure(getClass()
				.getClassLoader()
				.getResourceAsStream("log4j.properties"));
	}

	public org.slf4j.Logger getLogger(){
		return logger;
	}
	
	public static void debug(String message){
		getInstance().logger
			.debug(message);
	}
	
	public static void debug(String message, String... formats){
		getInstance().logger
			.debug(String.format(message, 
				formats));
	}
	
	public static void info(String message){
		getInstance().logger
			.info(message);
	}
	
	public static void info(String message, String... formats){
		getInstance().logger
			.info(String.format(message, 
				formats));
	}
	
	public static void error(String message){
		getInstance().logger
			.error(message);
	}
	
	public static void error(String message, String... formats){
		getInstance().logger
			.error(String.format(message, 
				formats));
	}
	
	public static void log(Throwable e){
		getInstance().logger
			.error(e.getMessage());
	}
}
