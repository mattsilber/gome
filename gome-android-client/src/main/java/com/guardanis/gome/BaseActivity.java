package com.guardanis.gome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.guardanis.gome.tools.PendingEvents;
import com.guardanis.gome.tools.views.ToolbarLayoutBuilder;

public abstract class BaseActivity extends AppCompatActivity {

    public static final int RC__PERMISSIONS = 100;
    public static final int RC__CONTROLLER = 101;

    public static final String TAG__BASE = "gome";

    private static final String PREFS = "gome-prefs";
    private static final String PREF__HOST_IP = "gome__ip_adress";

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    protected void setupToolbar(){
        setupToolbar(R.id.base__toolbar, R.layout.base__toolbar);
    }

    protected void setupToolbar(int toolbarId){
        setupToolbar(toolbarId, R.layout.base__toolbar);
    }

    protected void setupToolbar(int toolbarId, int inflatedResId){
        setup(new ToolbarLayoutBuilder((Toolbar) findViewById(toolbarId), inflatedResId));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == RC__PERMISSIONS)
            PendingEvents.getInstance()
                    .trigger(PendingEvents.PE__PERMISSION_RETURN);
    }

    protected abstract void setup(ToolbarLayoutBuilder builder);

    protected String getHostIpAddress(){
        return getSharedPreferences(PREFS, 0)
                .getString(PREF__HOST_IP, "192.168.8.101");
    }

    protected void setHostIpAddress(String ipAddress){
        getSharedPreferences(PREFS, 0)
                .edit()
                .putString(PREF__HOST_IP, ipAddress)
                .commit();
    }

    protected void exit(int resultCode, Intent data){
        if(data != null)
            setResult(resultCode, data);
        else setResult(resultCode);

        finish();
    }

}
