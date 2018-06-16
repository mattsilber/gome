package com.guardanis.commands;

import org.json.simple.JSONObject;

public class Command {
	
	protected String action;
	protected JSONObject data;
	
	public Command(String action, JSONObject data){
		this.action = action;
		this.data = data;
	}

	public String getAction() {
		return action;
	}

	public JSONObject getData() {
		return data;
	}
}
