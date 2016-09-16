package com.guardanis.gome.socket;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.guardanis.collections.tools.ListUtils;
import com.guardanis.gome.BaseActivity;
import com.guardanis.gome.tools.Callback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class DiscoveryAgent implements Runnable {

    private static DiscoveryAgent instance;
    public static DiscoveryAgent getInstance(Context context){
        if(instance == null)
            instance = new DiscoveryAgent(context);

        return instance;
    }

    private Context context;
    private Callback<List<String>> devicesCallback;
    private long requestStartMs;

    protected DiscoveryAgent(Context context){
        this.context = context.getApplicationContext();
    }

    public DiscoveryAgent search(Callback<List<String>> devicesCallback){
        this.requestStartMs = System.currentTimeMillis();
        this.devicesCallback = devicesCallback;

        new Thread(this)
                .start();

        return this;
    }

    @Override
    public void run() {
        final long currentRequest = Long.valueOf(requestStartMs);

        List<String> ips = new ArrayList<String>();

        String baseIpAddress = getWifiIpAddress();

        if(baseIpAddress != null){
            try {
                Log.i(BaseActivity.TAG__BASE, "WIFI IP: " + baseIpAddress);

                String gateway = baseIpAddress.substring(0, baseIpAddress.lastIndexOf(".") + 1);

                for (int i = 1; i <= 254; i++) {
                    if(currentRequest != requestStartMs)
                        return;

                    String ipValue = gateway + i;

                    try {
                        Socket socket = new Socket(ipValue, SocketClient.DEFAULT_PORT);

                        new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                        Log.i(BaseActivity.TAG__BASE, "Writer connected to: " + ipValue);

                        ips.add(ipValue);
                    }
                    catch (Exception e) {
                        Log.d(BaseActivity.TAG__BASE, ipValue + " failed with " + e.getMessage());
                    }
                }
            }
            catch (Exception e) { e.printStackTrace(); }
        }

        if(currentRequest == requestStartMs)
            new Handler(Looper.getMainLooper())
                .post(() ->
                        devicesCallback.onCalled(ips));
    }

    protected String getWifiIpAddress() {
        int ipAddress = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                .getConnectionInfo()
                .getIpAddress();

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
            ipAddress = Integer.reverseBytes(ipAddress);

        byte[] ipByteArray = BigInteger.valueOf(ipAddress)
                .toByteArray();

        try {
            return InetAddress.getByAddress(ipByteArray)
                    .getHostAddress();
        }
        catch (UnknownHostException e) { }

        return null;
    }

    public void cancel(){
        requestStartMs = -1;
    }
}
