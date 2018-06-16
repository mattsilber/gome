package com.guardanis.gome.mouse;

import android.app.Activity;
import android.widget.TextView;
import com.guardanis.gome.R;
import com.guardanis.gome.tools.views.TrackpadView;
import com.guardanis.gome.commands.Command;
import com.guardanis.gome.commands.MouseClickCommand;
import com.guardanis.gome.commands.MouseMoveCommand;
import com.guardanis.gome.tools.Callback;

public class MouseController {

    private static final int ACTION__DRAG = 1;

    public static final String ACTION_LEFT_SINGLE_CLICK = "left_single_click";
    public static final String ACTION_LEFT_DOUBLE_CLICK = "left_double_click";
    public static final String ACTION_WHEEL_CLICK = "wheel_click";
    public static final String ACTION_RIGHT_CLICK = "right_single_click";
    public static final String ACTION_DRAG = "drag";

    private Callback<Command> commandCallback;

    private TrackpadView moveTrackpad;
    private TrackpadView scrollTrackpad;

    private TextView dragActionView;

    private int protectedAction = 0;

    public MouseController(Callback<Command> commandCallback) {
        this.commandCallback = commandCallback;
    }

    public MouseController attach(Activity activity) {
        attach((TrackpadView) activity.findViewById(R.id.main__move_view), (TrackpadView) activity.findViewById(R.id.main__scroll_view));

        attachDragAction((TextView) activity.findViewById(R.id.main__mouse_action_drag));

        activity.findViewById(R.id.main__mouse_action_left_single_click)
                .setOnClickListener(v -> protectAction(ACTION_LEFT_SINGLE_CLICK));

        activity.findViewById(R.id.main__mouse_action_left_double_click)
                .setOnClickListener(v -> protectAction(ACTION_LEFT_DOUBLE_CLICK));

        activity.findViewById(R.id.main__mouse_action_wheel_click)
                .setOnClickListener(v -> protectAction(ACTION_WHEEL_CLICK));

        activity.findViewById(R.id.main__mouse_action_right_click)
                .setOnClickListener(v -> protectAction(ACTION_RIGHT_CLICK));

        return this;
    }

    public MouseController attach(TrackpadView moveTrackpad, TrackpadView scrollTrackpad) {
        this.moveTrackpad = moveTrackpad
                .setMouseMode(MouseMoveCommand.MouseMode.MOVE)
                .setCommandCallback(commandCallback)
                .setClickCallback(() -> protectAction(ACTION_LEFT_SINGLE_CLICK));

        this.scrollTrackpad = scrollTrackpad
                .setMouseMode(MouseMoveCommand.MouseMode.SCROLL)
                .setCommandCallback(commandCallback);

        return this;
    }

    public MouseController attachDragAction(TextView dragActionView) {
        this.dragActionView = dragActionView;

        setupDragActionView();

        return this;
    }

    private void setupDragActionView() {
        dragActionView.setText(dragActionView.getResources()
                .getString(protectedAction == ACTION__DRAG
                        ? R.string.mouse__action_drag_stop
                        : R.string.mouse__action_drag_start));

        dragActionView.setOnClickListener(v -> toggleDrag());
    }

    private void toggleDrag() {
        protectedAction = protectedAction == 0
                ? ACTION__DRAG
                : 0;

        commandCallback.onCalled(new MouseClickCommand(ACTION_DRAG));

        setupDragActionView();
    }

    private void protectAction(String action) {
        stopProtectedActions();

        commandCallback.onCalled(new MouseClickCommand(action));
    }

    public void stopProtectedActions() {
        if (protectedAction == ACTION__DRAG)
            toggleDrag();
    }
}
