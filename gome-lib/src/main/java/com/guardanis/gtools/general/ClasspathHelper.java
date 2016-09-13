package com.guardanis.gtools.general;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


public class ClasspathHelper {
	
	public static void add(String path) throws Exception {
	    File classPathFile = new File(path);
	    
	    Method method = URLClassLoader.class
	    		.getDeclaredMethod("addURL", new Class[]{ URL.class });
	    
	    method.setAccessible(true);
	    
	    method.invoke((URLClassLoader) ClassLoader.getSystemClassLoader(), 
	    		new Object[]{ classPathFile.toURL() });
	}

}
