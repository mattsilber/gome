package com.guardanis.gome;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.guardanis.gome.socket.DiscoveryAgent;
import com.guardanis.gome.tools.Callback;
import com.guardanis.gome.tools.DialogBuilder;
import com.guardanis.gome.tools.adapter.SingleSelectAdapter;

import java.util.ArrayList;

public class DiscoveryHelper implements Callback<String> {

    private static DiscoveryHelper instance;
    public static DiscoveryHelper getInstance(){
        if(instance == null)
            instance = new DiscoveryHelper();

        return instance;
    }

    private SingleSelectAdapter<String> singleSelectAdapter;
    private Dialog dialog;

    protected DiscoveryHelper(){ }

    public void search(Activity activity, Callback<String> ipSelectionCallback){
        singleSelectAdapter = new SingleSelectAdapter<String>(activity, new ArrayList<String>()) {
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
    public void onCalled(String value) {
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
