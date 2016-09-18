package com.guardanis.gome.keyboard;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.guardanis.gome.R;
import com.guardanis.gome.commands.Command;
import com.guardanis.gome.commands.KeyboardCommand;
import com.guardanis.gome.commands.KeycodeCommand;
import com.guardanis.gome.tools.Callback;
import com.guardanis.gome.tools.PreventTextWatcher;
import com.guardanis.gome.tools.views.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class KeyboardController {

    private Callback<Command> commandCallback;
    private Dialog activeKeyboardDialog;

    private List<String> wrappedActions = new ArrayList<String>();

    public KeyboardController(Callback<Command> commandCallback){
        this.commandCallback = commandCallback;
    }

    public void show(Activity activity){
        View parent = activity.getLayoutInflater()
                .inflate(R.layout.keyboard, null, false);

        EditText cheating = (EditText) parent.findViewById(R.id.keyboard__input);

        Runnable focusCallback = () ->
                cheating.requestFocus();

        PreventTextWatcher.attachTo(cheating, text ->
                sendCommand(text, parent, focusCallback));

        cheating.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_DEL)
                sendCommand(KeyboardCommand.KEY__DELETE, parent, focusCallback);

            return false;
        });

        setupActionViews(parent, focusCallback);

        parent.findViewById(R.id.keyboard__dialog_action_close)
                .setOnClickListener(v ->
                        dismiss());

        activeKeyboardDialog = new AlertDialog.Builder(activity)
                .setView(parent)
                .setCancelable(false)
                .show();

        cheating.postDelayed(() ->
                        ViewHelper.openSoftInputKeyboard(cheating),
                350);
    }

    private void sendCommand(String command, View parent, Runnable focusCallback){
        commandCallback.onCalled(new KeyboardCommand(command, wrappedActions));

        if(0 < wrappedActions.size())
            setupActionViews(parent, focusCallback);
    }

    private void setupActionViews(View parent, Runnable focusCallback){
        wrappedActions.clear();

        setupWrappedAction(parent, R.id.keyboard__action_alt, KeyboardCommand.ACTION__ALT, focusCallback);
        setupWrappedAction(parent, R.id.keyboard__action_control, KeyboardCommand.ACTION__CONTROL, focusCallback);
        setupWrappedAction(parent, R.id.keyboard__action_shift, KeyboardCommand.ACTION__SHIFT, focusCallback);

        parent.findViewById(R.id.keyboard__action_tab)
                .setOnClickListener(v ->
                        sendCommand(KeyboardCommand.KEY__TAB, parent, focusCallback));

        setupClickAction(parent, R.id.keyboard__action_insert, KeycodeCommand.VK_INSERT, focusCallback);
        setupClickAction(parent, R.id.keyboard__action_home, KeycodeCommand.VK_HOME, focusCallback);
        setupClickAction(parent, R.id.keyboard__action_end, KeycodeCommand.VK_END, focusCallback);
    }

    private void setupWrappedAction(View parent, int id, String keyValue, Runnable focusCallback){
        View action = parent.findViewById(id);

        int bgColorResource = wrappedActions.contains(keyValue)
                ? R.color.keyboard__action_bg_enabled
                : R.color.keyboard__action_bg;

        action.setBackgroundColor(parent.getResources()
                .getColor(bgColorResource));

        action.setOnClickListener(v -> {
            if(wrappedActions.contains(keyValue))
                wrappedActions.remove(keyValue);
            else wrappedActions.add(keyValue);

            setupWrappedAction(parent, id, keyValue, focusCallback);

            focusCallback.run();
        });
    }

    private void setupClickAction(View parent, int id, int keycode, Runnable focusCallback){
        parent.findViewById(id)
                .setOnClickListener(v -> {
                    commandCallback.onCalled(new KeycodeCommand(keycode, wrappedActions));

                    if(0 < wrappedActions.size())
                        setupActionViews(parent, focusCallback);

                    focusCallback.run();
                });
    }

    public void dismiss(){
        if(activeKeyboardDialog != null){
            if(activeKeyboardDialog.getCurrentFocus() != null)
                ViewHelper.closeSoftInputKeyboard(activeKeyboardDialog.getCurrentFocus());

            activeKeyboardDialog.dismiss();
            activeKeyboardDialog = null;
        }
    }

}
