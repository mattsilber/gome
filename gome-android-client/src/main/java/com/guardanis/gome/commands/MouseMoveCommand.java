package com.guardanis.gome.commands;

import org.json.JSONException;
import org.json.JSONObject;

public class MouseMoveCommand implements Command {

    public enum MouseMode {
        MOVE("move"),
        SCROLL("scroll");

        private String type;
        MouseMode(String type){
            this.type = type;
        }
    }

    protected MouseMode mouseMode = MouseMode.MOVE;
    private int x;
    private int y;

    public MouseMoveCommand(float x, float y){
        this(MouseMode.MOVE, x, y);
    }

    public MouseMoveCommand(MouseMode mouseMode, float x, float y){
        this.mouseMode = mouseMode;
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
                .put("type", mouseMode.type)
                .put("mouse_x", x)
                .put("mouse_y", y);
    }

}
