package com.guardanis.gome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guardanis.gome.socket.DiscoveryAgent;
import com.guardanis.gome.tools.Callback;
import com.guardanis.gome.tools.Svgs;
import com.guardanis.gome.tools.adapter.SingleSelectAdapter;
import com.guardanis.gome.tools.views.ToolbarLayoutBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends BaseActivity implements Callback<String> {


    private SingleSelectAdapter<String> ipAdapter;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_connect);
        setupToolbar();
        setupIpAddressViews();
    }

    @Override
    protected void setup(ToolbarLayoutBuilder builder) {
        builder.addTitle(R.string.connect__title, v -> { })
                .addOptionSvg(Svgs.IC__SEARCH, v ->
                        requestDevicesForSelection());
    }

    private void setupIpAddressViews(){
        final TextView ipTextView = (TextView) findViewById(R.id.connect__ip);
        ipTextView.setText(getHostIpAddress());

        findViewById(R.id.connect__ip_action_set).setOnClickListener(v ->
                connect(ipTextView.getText().toString()));
    }

    @Override
    protected void onPause(){
        DiscoveryAgent.getInstance(this)
                .cancel();

        super.onPause();
    }

    protected void requestDevicesForSelection(){
        findViewById(R.id.connect__ip_searching_parent)
                .setVisibility(View.VISIBLE);

        if(ipAdapter == null){
            ipAdapter = new IPSelectionAdapter(this, new ArrayList<String>())
                    .setClickCallback(ip ->
                            connect(ip));

            ((ListView) findViewById(R.id.connect__ip_list))
                    .setAdapter(ipAdapter);
        }

        ipAdapter.clear();
        ipAdapter.notifyDataSetChanged();

        DiscoveryAgent.getInstance(this)
                .search(this);
    }

    @Override
    public void onCalled(String value) {
        ipAdapter.add(value);
        ipAdapter.notifyDataSetChanged();
    }

    private void connect(String ip){
        setHostIpAddress(ip);

        startActivityForResult(new Intent(this, ControllerActivity.class),
                RC__CONTROLLER);
    }

    private static class IPSelectionAdapter extends SingleSelectAdapter<String> {

        public IPSelectionAdapter(Context context, @NonNull List data) {
            super(context, data);
        }

        @Override
        protected String getValue(String item) {
            return item;
        }

        @Override
        protected String getSubValue(String item) {
            return "";
        }

        @Override
        protected boolean isFilterMatched(@NonNull String value, String s) {
            return true;
        }
    }

}
