package com.guardanis.gome;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.guardanis.gome.commands.Command;
import com.guardanis.gome.commands.WebsiteCommand;
import com.guardanis.gome.keyboard.KeyboardController;
import com.guardanis.gome.mouse.MouseController;
import com.guardanis.gome.socket.Host;
import com.guardanis.gome.socket.SocketClient;
import com.guardanis.gome.tools.Callback;
import com.guardanis.gome.tools.DialogBuilder;
import com.guardanis.gome.tools.Svgs;
import com.guardanis.gome.tools.views.ToolbarLayoutBuilder;

public class ControllerActivity extends BaseActivity implements Callback<Command>, SocketClient.ConnectionCallbacks {

    private Host host;

    private SocketClient socketClient;
    private boolean connected = false;

    private KeyboardController keyboardController = new KeyboardController(this);
    private MouseController mouseController = new MouseController(this);

    private Dialog activeLoadingDialog;
    private boolean paused = false;

    private TextView titleView;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_main);
        setupToolbar();

        mouseController.attach(this);
    }

    @Override
    protected void setup(ToolbarLayoutBuilder builder) {
        host = getHost();

        titleView = builder.inflateTitleText();
        titleView.setText(host.isNameKnown()
                ? host.getName()
                : host.getIpAddress());
        titleView.setOnClickListener(v ->
                finish());

        builder.addTitle(titleView);
        builder.addOptionSvg(Svgs.IC__KEYBOARD,
                v ->
                        validateConnected(() -> {
                            mouseController.stopProtectedActions();
                            keyboardController.show(this);
                        }));
    }

    @Override
    public void onPause(){
        paused = true;

        killSocketClient();

        dismissActiveDialog();

        keyboardController.dismiss();

        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        paused = false;

        connectSocketClient();
    }

    @Override
    public void onCalled(Command command) {
        if(socketClient != null)
            socketClient.send(command);
    }

    protected void connectSocketClient(){
        if(connected)
            return;

        connected = true;

        host = getHost();

        if(socketClient != null)
            killSocketClient();

        activeLoadingDialog = new DialogBuilder(this)
                .setMessage(String.format(getString(R.string.controller__alert_connecting),
                        host.getIpAddress()))
                .setPrimaryButton(R.string.alert_action_cancel, (d, v) ->
                        exit(RESULT_CANCELED, null))
                .show();

        socketClient = SocketClient.open(host.getIpAddress(),
                SocketClient.DEFAULT_PORT,
                this);
    }

    @Override
    public void onConnected(String ip) {
        dismissActiveDialog();

        if(getIntent().getData() != null){
            onCalled(new WebsiteCommand(getIntent()
                    .getData()
                    .toString()));

            getIntent().setData(null);
        }
    }

    @Override
    public void onIdentified(String ip, String hostName) {
        host = new Host(ip, hostName);

        setHost(host);

        titleView.setText(host.isNameKnown()
                ? host.getName()
                : host.getIpAddress());
    }

    @Override
    public void onConnectionException(Throwable throwable) {
        if(paused)
            return;

        paused = true;

        dismissActiveDialog();

        activeLoadingDialog = new DialogBuilder(this)
                .setTitle(R.string.alert_title_oops)
                .setMessage(throwable.getMessage())
                .setPrimaryButton(R.string.alert_action_ok, (d, v) ->
                        exit(RESULT_CANCELED, null))
                .show();
    }

    @Override
    public void onConnectionClosed() {
        connected = false;

        if(paused)
            return;

        dismissActiveDialog();

        activeLoadingDialog = new DialogBuilder(this)
                .setTitle(R.string.alert_title_oops)
                .setMessage(R.string.alert_message_connection_closed)
                .setPrimaryButton(R.string.alert_action_ok, (d, v) ->
                        exit(RESULT_CANCELED, null))
                .show();
    }

    protected void killSocketClient(){
        if(socketClient != null)
            socketClient.onDestroyed();

        socketClient = null;
    }

    private void validateConnected(Runnable onConnected){
        if(connected)
            onConnected.run();
        else
            activeLoadingDialog = new DialogBuilder(this)
                    .setTitle(R.string.alert_title_oops)
                    .setMessage(R.string.alert_message_connection_required)
                    .show();
    }

    private void dismissActiveDialog(){
        if(activeLoadingDialog != null){
            activeLoadingDialog.dismiss();
            activeLoadingDialog = null;
        }
    }

}
