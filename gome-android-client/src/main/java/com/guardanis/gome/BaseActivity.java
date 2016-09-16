package com.guardanis.gome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.guardanis.gome.tools.PendingEvents;
import com.guardanis.gome.tools.views.ToolbarLayoutBuilder;

public abstract class BaseActivity extends AppCompatActivity {

    public static final int RC__PERMISSIONS = 100;

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

}
