package com.guardanis.gome;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.guardanis.gome.socket.DiscoveryAgent;
import com.guardanis.gome.socket.Host;
import com.guardanis.gome.tools.Callback;
import com.guardanis.gome.tools.DialogBuilder;
import com.guardanis.gome.tools.adapter.SingleSelectAdapter;

import java.util.ArrayList;

public class DiscoveryHelper implements Callback<Host> {

    private static DiscoveryHelper instance;
    public static DiscoveryHelper getInstance(){
        if(instance == null)
            instance = new DiscoveryHelper();

        return instance;
    }

    private SingleSelectAdapter<Host> singleSelectAdapter;
    private Dialog dialog;

    protected DiscoveryHelper(){ }

    public void search(Activity activity, Callback<Host> ipSelectionCallback){
        singleSelectAdapter = new SingleSelectAdapter<Host>(activity, new ArrayList<Host>()) {
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
        };

        DiscoveryAgent.getInstance(activity)
                .search(this);

        dialog = new DialogBuilder(activity)
                .setTitle(R.string.devices__searching_alert_title)
                .setSingleSelectItems(singleSelectAdapter, ip ->
                        ipSelectionCallback.onCalled(ip))
                .setCancelListener(d -> cancel(activity))
                .show();
    }

    @Override
    public void onCalled(Host value) {
        singleSelectAdapter.add(value);
        singleSelectAdapter.notifyDataSetChanged();
    }

    public void cancel(Context context){
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }

        DiscoveryAgent.getInstance(context)
                .cancel();
    }
}
