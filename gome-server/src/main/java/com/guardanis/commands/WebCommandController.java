package com.guardanis.commands;

import com.guardanis.JsonHelper;
import com.guardanis.Logger;

import java.awt.*;
import java.net.URI;

public class WebCommandController implements CommandController {

    @Override
    public void process(Command command) throws Exception {
        String url = JsonHelper.getString("url", command.getData());

        if (Desktop.isDesktopSupported())
            Desktop.getDesktop()
                    .browse(new URI(url));
        else
            Logger.INSTANCE.log("Desktop URL Browsing is not supported. " + url + " failed.");
    }
}
