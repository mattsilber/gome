package com.guardanis.gome;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guardanis.gome.commands.Command;
import com.guardanis.gome.commands.KeyboardCommand;
import com.guardanis.gome.commands.MouseClickCommand;
import com.guardanis.gome.socket.DiscoveryAgent;
import com.guardanis.gome.socket.SocketClient;
import com.guardanis.gome.tools.Callback;
import com.guardanis.gome.tools.DialogBuilder;
import com.guardanis.gome.tools.PreventTextWatcher;
import com.guardanis.gome.tools.views.ToolbarLayoutBuilder;
import com.guardanis.gome.tools.views.ViewHelper;

public class MainActivity extends BaseActivity implements Callback<Command>, SocketClient.ConnectionCallbacks {

    private static final String PREFS = "gome-prefs";
    private static final String PREF__IP = "gome__ip_adress";

    private SocketClient socketClient;
    private boolean connected = false;

    private boolean dragEnabled = false;

    private Dialog activeLoadingDialog;

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
                        validateConnected(() ->
                                showKeyboard()));
    }

    @Override
    public void onPause(){
        killSocketClient();

        if(activeLoadingDialog != null){
            activeLoadingDialog.dismiss();
            activeLoadingDialog = null;
        }

        DiscoveryHelper.getInstance()
                .cancel(this);

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

        setupMouseActionViews();
        setupDragView();
        setupIpAddressViews();
    }

    private void setupMouseActionViews(){
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
    }

    private void setupDragView(){
        TextView dragView = (TextView) findViewById(R.id.main__mouse_action_drag);

        dragView.setText(getString(dragEnabled
                ? R.string.mouse__action_drag_stop
                : R.string.mouse__action_drag_start));

        dragView.setOnClickListener(v -> {
            dragEnabled = !dragEnabled;

            onCalled(new MouseClickCommand(dragEnabled
                    ? "drag_start"
                    : "drag_stop"));

            setupDragView();
        });
    }

    private void setupIpAddressViews(){
        final TextView ipTextView = (TextView) findViewById(R.id.main__ip);
        ipTextView.setText(getIpAddress());

        TextView actionSet = (TextView) findViewById(R.id.main__ip_action_set);
        actionSet.setOnClickListener(v -> {
            actionSet.setText(R.string.ip__action_update);
            connectSocketClient(ipTextView.getText().toString());
        });

        findViewById(R.id.main__ip_action_search)
                .setOnClickListener(v ->
                        requestDevicesForSelection());
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

        socketClient = SocketClient.open(ipAddress,
                SocketClient.DEFAULT_PORT,
                this);
    }

    @Override
    public void onConnected(String ip) {
        Toast.makeText(this, "Connected to " + ip, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onConnectionException(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onConnectionClosed() {
        Toast.makeText(this, "Disconnected from server", Toast.LENGTH_SHORT)
                .show();
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
        cheating.setFocusable(true);
        cheating.setFocusableInTouchMode(true);
        cheating.setTextColor(0x00000000);
        cheating.setHintTextColor(getResources().getColor(R.color.base__text_dark_2));
        cheating.setHint(getString(R.string.keyboard__input_hint));

        PreventTextWatcher.attachTo(cheating, text ->
                onCalled(new KeyboardCommand(text)));

        cheating.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_DEL)
                onCalled(new KeyboardCommand("\b"));
            else if(event.getAction() == KeyEvent.ACTION_DOWN)
                Log.d(TAG__BASE, "Key code: " + event.getCharacters());

            return false;
        });

        new AlertDialog.Builder(this)
                .setView(cheating)
                .setOnCancelListener(d ->
                        ViewHelper.closeSoftInputKeyboard(cheating))
                .setOnDismissListener(d ->
                        ViewHelper.closeSoftInputKeyboard(cheating))
                .show();

        cheating.postDelayed(() ->
                    ViewHelper.openSoftInputKeyboard(cheating),
                350);
    }

    protected void requestDevicesForSelection(){
        DiscoveryHelper.getInstance()
                .search(this, ip -> {
                    setIpAddress(ip);
                    setupIpAddressViews();

                    connectSocketClient(ip);
                });
    }

    private void validateConnected(Runnable onConnected){
        if(connected)
            onConnected.run();
        else
            new DialogBuilder(this)
                    .setTitle(R.string.alert_title_oops)
                    .setMessage(R.string.alert_message_connection_required)
                    .show();
    }

}
