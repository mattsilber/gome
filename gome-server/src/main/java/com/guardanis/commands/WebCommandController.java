package com.guardanis.commands;

import java.awt.Desktop;
import java.net.URI;

import com.guardanis.gtools.Logger;
import com.guardanis.gtools.net.JsonHelper;

public class WebCommandController implements CommandController {
		
	@Override
	public void process(Command command) throws Exception {
		String url = JsonHelper.getString("url", command.getData());
	
		if(Desktop.isDesktopSupported()) 
			  Desktop.getDesktop()
			  		.browse(new URI(url));
		else Logger.error("Desktop URL Browsing is not supported. " + url + " failed.");
	}

}
