package com.guardanis.gome;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.guardanis.gome.commands.Command;
import com.guardanis.gome.commands.KeyboardCommand;
import com.guardanis.gome.commands.KeyboardEventCommand;
import com.guardanis.gome.commands.MouseClickCommand;
import com.guardanis.gome.socket.SocketClient;
import com.guardanis.gome.tools.Callback;
import com.guardanis.gome.tools.PreventTextWatcher;
import com.guardanis.gome.tools.views.ToolbarLayoutBuilder;
import com.guardanis.gome.tools.views.ViewHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements Callback<Command> {

    private static final String PREFS = "gome-prefs";
    private static final String PREF__IP = "gome__ip_adress";

    private SocketClient socketClient;
    private boolean connected = false;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        setupToolbar();

        setup();
    }

    @Override
    protected void setup(ToolbarLayoutBuilder builder) {
        builder.addTitle("gome", v ->
                        finish())
                .addOptionText("Keyboard", v ->
                        showKeyboard());
    }

    @Override
    public void onPause(){
        killSocketClient();

        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();

        if(connected)
            connectSocketClient(getIpAddress());
    }

    private void setup(){
        ((MoveView) findViewById(R.id.main__move_view))
                .setCommandCallback(this);

        findViewById(R.id.main__mouse_action_left_single_click)
                .setOnClickListener(v ->
                        onCalled(new MouseClickCommand("left_single_click")));

        findViewById(R.id.main__mouse_action_left_double_click)
                .setOnClickListener(v ->
                        onCalled(new MouseClickCommand("left_double_click")));

        findViewById(R.id.main__mouse_action_wheel_click)
                .setOnClickListener(v ->
                        onCalled(new MouseClickCommand("wheel_click")));

        findViewById(R.id.main__mouse_action_right_click)
                .setOnClickListener(v ->
                        onCalled(new MouseClickCommand("right_single_click")));

        findViewById(R.id.main__mouse_action_drag_start)
                .setOnClickListener(v ->
                        onCalled(new MouseClickCommand("drag_start")));

        findViewById(R.id.main__mouse_action_drag_stop)
                .setOnClickListener(v ->
                        onCalled(new MouseClickCommand("drag_stop")));

        final TextView ipTextView = (TextView) findViewById(R.id.main__ip);
        ipTextView.setText(getIpAddress());

        findViewById(R.id.main__ip_action_set)
                .setOnClickListener(v ->
                        connectSocketClient(ipTextView.getText().toString()));
    }

    @Override
    public void onCalled(Command command) {
        if(socketClient != null)
            socketClient.send(command);
    }

    protected void connectSocketClient(String ipAddress){
        connected = true;

        setIpAddress(ipAddress);

        if(socketClient != null)
            killSocketClient();

        socketClient = SocketClient.open(ipAddress, 13337);
    }

    protected void killSocketClient(){
        if(socketClient != null)
            socketClient.onDestroyed();

        socketClient = null;
    }

    protected String getIpAddress(){
        return getSharedPreferences(PREFS, 0)
                .getString(PREF__IP, "192.168.8.101");
    }

    protected void setIpAddress(String ipAddress){
        getSharedPreferences(PREFS, 0)
                .edit()
                .putString(PREF__IP, ipAddress)
                .commit();
    }

    protected void showKeyboard(){
        EditText cheating = new EditText(this);

        PreventTextWatcher.attachTo(cheating, text ->
                onCalled(new KeyboardCommand(text)));

        cheating.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN
                    && (keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_BACK))
                onCalled(new KeyboardEventCommand(keyCode, true));

            return false;
        });

        new AlertDialog.Builder(this)
                .setView(cheating)
                .show();

        cheating.postDelayed(() ->
                    ViewHelper.openSoftInputKeyboard(cheating),
                350);
    }
}
