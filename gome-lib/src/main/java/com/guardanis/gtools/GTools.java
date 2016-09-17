package com.guardanis.gtools;

import java.util.Map;

public class GTools {

	public static final int CONNECTION_PORT = 13337;
	public static final int PING_PORT = 13338;

    public static void main(String[] args){

    }
    
    public static String getComputerName(){
        Map<String, String> env = System.getenv();
        
        if(env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            return env.get("HOSTNAME");
        else
            return "Unknown Computer";
    }

}
