package com.guardanis.gome;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.guardanis.gome.commands.Command;
import com.guardanis.gome.commands.MouseMoveCommand;
import com.guardanis.gome.tools.Callback;

public class MoveView extends View implements View.OnTouchListener {

    protected float[] lastTouch = new float[]{ 0, 0 };

    protected Callback<Command> commandCallback;

    public MoveView(Context context) {
        super(context);
        init();
    }

    public MoveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setWillNotDraw(false);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(commandCallback == null)
            return false;

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastTouch = new float[]{ event.getX(), event.getY() };
                return true;
            case MotionEvent.ACTION_MOVE:
                commandCallback.onCalled(new MouseMoveCommand(event.getX() - lastTouch[0],
                        event.getY() - lastTouch[1]));

                lastTouch = new float[]{ event.getX(), event.getY() };

                return true;
        }

        return false;
    }

    public MoveView setCommandCallback(Callback<Command> commandCallback){
        this.commandCallback = commandCallback;
        return this;
    }
}
