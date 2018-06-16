package com.guardanis.gome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.guardanis.gome.socket.DiscoveryAgent;
import com.guardanis.gome.socket.Host;
import com.guardanis.gome.tools.Callback;
import com.guardanis.gome.tools.DialogBuilder;
import com.guardanis.gome.tools.Svgs;
import com.guardanis.gome.tools.adapter.SingleSelectAdapter;
import com.guardanis.gome.tools.views.ToolbarLayoutBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConnectActivity extends BaseActivity implements Callback<Host> {

    private IPSelectionAdapter ipAdapter;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_connect);
        setupToolbar();
        setupIpAddressViews();

        handleSharing();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        handleSharing();
    }

    private void handleSharing() {
        if (getIntent().getData() != null) {
            if (getHost().isIpAddressEmpty())
                new DialogBuilder(this)
                        .setTitle(R.string.alert_title_oops)
                        .setMessage(R.string.alert_message_connection_required)
                        .show();
            else {
                Intent intent = new Intent(this, ControllerActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .setData(getIntent().getData());

                startActivityForResult(intent, RC__CONTROLLER);
            }
        }
    }

    @Override
    protected void setup(ToolbarLayoutBuilder builder) {
        builder.addTitle(R.string.connect__title, null);
        builder.addOptionSvg(Svgs.IC__SEARCH, v -> requestDevicesForSelection());
    }

    private void setupIpAddressViews() {
        final TextView ipTextView = (TextView) findViewById(R.id.connect__ip);
        ipTextView.setText(getHost().getIpAddress());

        findViewById(R.id.connect__ip_action_set)
                .setOnClickListener(v ->
                        connect(new Host(ipTextView.getText()
                                .toString()
                                .trim(),
                                "Unknown")));
    }

    @Override
    protected void onPause() {
        DiscoveryAgent.getInstance(this)
                .cancel();

        findViewById(R.id.connect__ip_searching_parent)
                .setVisibility(View.GONE);

        super.onPause();
    }

    protected void requestDevicesForSelection() {
        findViewById(R.id.connect__ip_searching_parent)
                .setVisibility(View.VISIBLE);

        if (ipAdapter == null) {
            ipAdapter = (IPSelectionAdapter) new IPSelectionAdapter(this, new ArrayList<Host>())
                    .setClickCallback(host -> {
                        ((TextView) findViewById(R.id.connect__ip))
                                .setText(host.getIpAddress());

                        connect(host);
                    });

            ((ListView) findViewById(R.id.connect__ip_list))
                    .setAdapter(ipAdapter);
        }

        ipAdapter.clear();
        ipAdapter.notifyDataSetChanged();

        DiscoveryAgent.getInstance(this)
                .search(this);
    }

    @Override
    public void onCalled(Host host) {
        ipAdapter.add(host);
        ipAdapter.notifyDataSetChanged();
    }

    private void connect(Host host) {
        Log.i(TAG__BASE, "Connecting to: " + host.getIpAddress());

        setHost(host);

        Intent intent = new Intent(this, ControllerActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivityForResult(intent, RC__CONTROLLER);
    }

    private static class IPSelectionAdapter extends SingleSelectAdapter<Host> {

        public IPSelectionAdapter(Context context, @NonNull List<Host> data) {
            super(context, data);
        }

        @Override
        protected String getValue(Host item) {
            return item.getName();
        }

        @Override
        protected String getSubValue(Host item) {
            return item.getIpAddress();
        }

        @Override
        protected boolean isFilterMatched(@NonNull String value, Host s) {
            return true;
        }
    }
}
