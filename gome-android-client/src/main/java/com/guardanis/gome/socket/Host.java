package com.guardanis.gome.socket;

import android.content.Context;
import android.content.SharedPreferences;
import com.guardanis.gome.BaseActivity;

public class Host {

    private static final String PREF__IP = "host__ip";
    private static final String PREF__NAME = "host__name";

    public static final String HOST__NAME_DEFAULT = "Unknown";

    private String ipAddress;
    private String name;

    public Host(String ipAddress, String name) {
        this.ipAddress = ipAddress;
        this.name = name;
    }

    public Host(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(BaseActivity.PREFS, 0);

        this.ipAddress = prefs.getString(PREF__IP, "");
        this.name = prefs.getString(PREF__NAME, HOST__NAME_DEFAULT);
    }

    public boolean isIpAddressEmpty() {
        return ipAddress == null || ipAddress.length() < 1;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getName() {
        return name;
    }

    public boolean isNameKnown() {
        return !(name == null || name.equals(HOST__NAME_DEFAULT));
    }

    public void save(Context context) {
        context.getSharedPreferences(BaseActivity.PREFS, 0)
                .edit()
                .putString(PREF__IP, ipAddress)
                .putString(PREF__NAME, name)
                .commit();
    }
}
