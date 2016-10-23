package com.guardanis.gome.mouse;

import android.app.Activity;
import android.widget.TextView;

import com.guardanis.gome.R;
import com.guardanis.gome.TrackpadView;
import com.guardanis.gome.commands.Command;
import com.guardanis.gome.commands.MouseClickCommand;
import com.guardanis.gome.commands.MouseMoveCommand;
import com.guardanis.gome.tools.Callback;

public class MouseController {

    private static final int ACTION__SCROLL = 1;
    private static final int ACTION__DRAG = 2;

    public static final String ACTION_LEFT_SINGLE_CLICK = "left_single_click";
    public static final String ACTION_LEFT_DOUBLE_CLICK = "left_double_click";
    public static final String ACTION_WHEEL_CLICK = "wheel_click";
    public static final String ACTION_RIGHT_CLICK = "right_single_click";
    public static final String ACTION_DRAG = "drag";

    private Callback<Command> commandCallback;

    private TrackpadView trackpad;

    private TextView dragActionView;
    private TextView scrollActionView;

    private int protectedAction = 0;

    public MouseController(Callback<Command> commandCallback){
        this.commandCallback = commandCallback;
    }

    public MouseController attach(Activity activity){
        attach((TrackpadView) activity.findViewById(R.id.main__move_view));
        attachDragAction((TextView) activity.findViewById(R.id.main__mouse_action_drag));
        attachScrollAction((TextView) activity.findViewById(R.id.main__mouse_action_scroll));

        activity.findViewById(R.id.main__mouse_action_left_single_click)
                .setOnClickListener(v ->
                        protectAction(ACTION_LEFT_SINGLE_CLICK));

        activity.findViewById(R.id.main__mouse_action_left_double_click)
                .setOnClickListener(v ->
                        protectAction(ACTION_LEFT_DOUBLE_CLICK));

        activity.findViewById(R.id.main__mouse_action_wheel_click)
                .setOnClickListener(v ->
                        protectAction(ACTION_WHEEL_CLICK));

        activity.findViewById(R.id.main__mouse_action_right_click)
                .setOnClickListener(v ->
                        protectAction(ACTION_RIGHT_CLICK));

        return this;
    }

    public MouseController attach(TrackpadView trackpad){
        this.trackpad = trackpad;

        trackpad.setCommandCallback(commandCallback)
                .setClickCallback(() ->
                        protectAction(ACTION_LEFT_SINGLE_CLICK));

        return this;
    }

    public MouseController attachDragAction(TextView dragActionView){
        this.dragActionView = dragActionView;

        setupDragActionView();

        return this;
    }

    private void setupDragActionView(){
        dragActionView.setText(dragActionView.getResources()
                .getString(protectedAction == ACTION__DRAG
                        ? R.string.mouse__action_drag_stop
                        : R.string.mouse__action_drag_start));

        dragActionView.setOnClickListener(v ->
                toggleDrag());
    }

    private void toggleDrag(){
        if(protectedAction == ACTION__SCROLL)
            toggleScroll();

        protectedAction = protectedAction == 0
                ? ACTION__DRAG
                : 0;

        commandCallback.onCalled(new MouseClickCommand(ACTION_DRAG));

        setupDragActionView();
    }

    public MouseController attachScrollAction(TextView scrollActionView){
        this.scrollActionView = scrollActionView;

        setupScrollActionView();

        return this;
    }

    private void setupScrollActionView(){
        scrollActionView.setText(scrollActionView.getResources()
                .getString(protectedAction == ACTION__SCROLL
                        ? R.string.mouse__action_scroll_stop
                        : R.string.mouse__action_scroll_start));

        scrollActionView.setOnClickListener(v ->
                toggleScroll());
    }

    private void toggleScroll(){
        if(protectedAction == ACTION__DRAG)
            toggleDrag();

        protectedAction = protectedAction == 0
                ? ACTION__SCROLL
                : 0;

        trackpad.setMouseMode(protectedAction == ACTION__SCROLL
                ? MouseMoveCommand.MouseMode.SCROLL
                : MouseMoveCommand.MouseMode.MOVE);

        setupScrollActionView();
    }

    private void protectAction(String action){
        stopProtectedActions();

        commandCallback.onCalled(new MouseClickCommand(action));
    }

    public void stopProtectedActions(){
        if(protectedAction == ACTION__DRAG)
            toggleDrag();
        else if(protectedAction == ACTION__SCROLL)
            toggleScroll();
    }

}
