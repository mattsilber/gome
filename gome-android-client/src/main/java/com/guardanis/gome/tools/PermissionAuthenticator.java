package com.guardanis.gome.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.guardanis.collections.tools.ListUtils;
import com.guardanis.gome.BaseActivity;
import com.guardanis.gome.R;

import java.util.List;

public class PermissionAuthenticator {

    public static final String P__STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public static final void validate(Activity activity, Runnable sucessListener, @NonNull String... permissions){
        List<String> required = getRequiredPermissions(activity, permissions);

        if(required.size() < 1)
            sucessListener.run();
        else {
            PendingEvents.getInstance()
                    .register(PendingEvents.PE__PERMISSION_RETURN, () -> {
                        if(getRequiredPermissions(activity, permissions).size() < 1)
                            sucessListener.run();
                        else onPermissionError(activity);
                    });

            ActivityCompat.requestPermissions(activity,
                    required.toArray(new String[required.size()]),
                    BaseActivity.RC__PERMISSIONS);
        }
    }

    private static List<String> getRequiredPermissions(Context context, String... permissions){
        return new ListUtils<String>(permissions)
                .filter(v ->
                        ContextCompat.checkSelfPermission(context, v) != PackageManager.PERMISSION_GRANTED)
                .values();
    }

    private static final void onPermissionError(Context context){
        new AlertDialog.Builder(context)
                .setTitle(R.string.alert_title_error)
                .setMessage(R.string.alert_message_permission_required)
                .show();
    }

}
