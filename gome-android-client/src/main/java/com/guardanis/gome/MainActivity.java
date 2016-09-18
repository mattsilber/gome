package com.guardanis.gome;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.guardanis.gome.commands.Command;
import com.guardanis.gome.commands.MouseClickCommand;
import com.guardanis.gome.keyboard.KeyboardController;
import com.guardanis.gome.mouse.MouseController;
import com.guardanis.gome.socket.SocketClient;
import com.guardanis.gome.tools.Callback;
import com.guardanis.gome.tools.DialogBuilder;
import com.guardanis.gome.tools.views.ToolbarLayoutBuilder;

public class MainActivity extends BaseActivity implements Callback<Command>, SocketClient.ConnectionCallbacks {

    private static final String PREFS = "gome-prefs";
    private static final String PREF__IP = "gome__ip_adress";

    private SocketClient socketClient;
    private boolean connected = false;

    private KeyboardController keyboardController = new KeyboardController(this);
    private MouseController mouseController = new MouseController(this);

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
                        validateConnected(() -> {
                            mouseController.stopProtectedActions();
                            keyboardController.show(this);
                        }));
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

        keyboardController.dismiss();

        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();

        if(connected)
            connectSocketClient(getIpAddress());
    }

    private void setup(){
        mouseController.attach(this);

        setupIpAddressViews();
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
