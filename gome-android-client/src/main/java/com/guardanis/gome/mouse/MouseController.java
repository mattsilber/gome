package com.guardanis.gome.mouse;

import android.widget.TextView;

import com.guardanis.gome.R;
import com.guardanis.gome.TrackpadView;
import com.guardanis.gome.commands.Command;
import com.guardanis.gome.commands.MouseClickCommand;
import com.guardanis.gome.commands.MouseMoveCommand;
import com.guardanis.gome.tools.Callback;

public class MouseController {

    private Callback<Command> commandCallback;

    private TrackpadView trackpad;

    private boolean scrollingModeEnabled = false;
    private boolean dragEnabled = false;

    public MouseController(Callback<Command> commandCallback){
        this.commandCallback = commandCallback;
    }

    public MouseController attach(TrackpadView trackpad){
        this.trackpad = trackpad;

        trackpad.setCommandCallback(commandCallback);

        return this;
    }

    public MouseController attachDragAction(TextView dragActionView){
        dragActionView.setText(dragActionView.getResources()
                .getString(dragEnabled
                        ? R.string.mouse__action_drag_stop
                        : R.string.mouse__action_drag_start));

        dragActionView.setOnClickListener(v -> {
            dragEnabled = !dragEnabled;

            commandCallback.onCalled(new MouseClickCommand(dragEnabled
                    ? "drag_start"
                    : "drag_stop"));

            attachDragAction(dragActionView);
        });

        return this;
    }

    public MouseController attachScrollAction(TextView scrollActionView){
        scrollActionView.setText(scrollActionView.getResources()
                .getString(scrollingModeEnabled
                        ? R.string.mouse__action_scroll_stop
                        : R.string.mouse__action_scroll_start));

        scrollActionView.setOnClickListener(v -> {
            scrollingModeEnabled = !scrollingModeEnabled;

            trackpad.setMouseMode(scrollingModeEnabled
                    ? MouseMoveCommand.MouseMode.SCROLL
                    : MouseMoveCommand.MouseMode.MOVE);

            attachScrollAction(scrollActionView);
        });

        return this;
    }

}
