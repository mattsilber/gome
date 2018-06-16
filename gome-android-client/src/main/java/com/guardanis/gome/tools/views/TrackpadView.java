package com.guardanis.gome.tools.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.guardanis.gome.Settings;
import com.guardanis.gome.commands.Command;
import com.guardanis.gome.commands.MouseMoveCommand;
import com.guardanis.gome.commands.MouseMoveCommand.MouseMode;
import com.guardanis.gome.tools.Callback;

public class TrackpadView extends View implements View.OnTouchListener {

    private static final long CLICK_TIMEOUT = 155;

    protected float[] lastTouch = new float[]{0, 0};
    protected long touchDownMs = 0;

    protected Callback<Command> commandCallback;
    protected MouseMode mouseMode = MouseMode.MOVE;
    protected String mouseSensitivityKey = Settings.KEY__MOVE_SPEED;

    protected Runnable clickCallback;

    public TrackpadView(Context context) {
        super(context);
        init();
    }

    public TrackpadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TrackpadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setOnTouchListener(this);
    }

    public TrackpadView setMouseMode(MouseMode mouseMode) {
        this.mouseMode = mouseMode;

        this.mouseSensitivityKey = mouseMode == MouseMode.MOVE
                ? Settings.KEY__MOVE_SPEED
                : Settings.KEY__SCROLL_SPEED;

        return this;
    }

    public TrackpadView setCommandCallback(Callback<Command> commandCallback) {
        this.commandCallback = commandCallback;
        return this;
    }

    public TrackpadView setClickCallback(Runnable clickCallback) {
        this.clickCallback = clickCallback;
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (commandCallback == null)
            return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouch = new float[]{event.getX(), event.getY()};
                touchDownMs = System.currentTimeMillis();

                return true;
            case MotionEvent.ACTION_MOVE:
                float mouseSpeed = 11f - Settings.getInstance(getContext())
                        .get(mouseSensitivityKey, 5);

                commandCallback.onCalled(new MouseMoveCommand(mouseMode,
                        (event.getX() - lastTouch[0]) / mouseSpeed,
                        (event.getY() - lastTouch[1]) / mouseSpeed));

                lastTouch = new float[]{event.getX(), event.getY()};

                return true;
            case MotionEvent.ACTION_UP:
                if (clickCallback != null && System.currentTimeMillis() - touchDownMs < CLICK_TIMEOUT)
                    clickCallback.run();

                return true;
        }

        return false;
    }
}
