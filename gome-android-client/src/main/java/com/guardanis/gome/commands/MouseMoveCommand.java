package com.guardanis.gome.commands;

import org.json.JSONException;
import org.json.JSONObject;

public class MouseMoveCommand implements Command {

    private int x;
    private int y;

    public MouseMoveCommand(float x, float y){
        this.x = (int) x;
        this.y = (int) y;
    }

    @Override
    public String getActionIdentifier() {
        return "mouse";
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put("type", "move")
                .put("mouse_x", x)
                .put("mouse_y", y);
    }

}
