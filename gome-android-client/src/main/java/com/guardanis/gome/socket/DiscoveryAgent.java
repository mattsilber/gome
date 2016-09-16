package com.guardanis.gome.socket;

import android.os.Handler;
import android.os.Looper;

import com.guardanis.collections.tools.ListUtils;
import com.guardanis.gome.tools.Callback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class DiscoveryAgent implements Runnable {

    private static DiscoveryAgent instance;
    public static DiscoveryAgent getInstance(){
        if(instance == null)
            instance = new DiscoveryAgent();

        return instance;
    }

    private Callback<List<String>> devicesCallback;
    private long requestStartMs;

    protected DiscoveryAgent(){ }

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

        try {
            Socket socket = new Socket();

            byte[] ip = InetAddress.getLocalHost()
                    .getAddress();

            for (int i = 1; i <= 254; i++) {
                ip[3] = (byte) i;

                String ipValue = joinIp(ip);

                InetAddress address = InetAddress.getByAddress(ip);

                if (address.isReachable(250)) {
                    try {
                        socket.connect(new InetSocketAddress(ipValue, SocketClient.DEFAULT_PORT),
                                250);

                        ips.add(ipValue);
                    }
                    catch (Exception e) { }
                }
            }
        }
        catch (Exception e) { e.printStackTrace(); }

        if(currentRequest == requestStartMs)
            new Handler(Looper.getMainLooper())
                .post(() ->
                        devicesCallback.onCalled(ips));
    }

    public String joinIp(byte[] values){
        String joined = "";

        if(0 < values.length){
            joined += values[0];

            for(int i = 1; i < values.length; i++)
                joined += "." + values[i];
        }

        return joined;
    }

    public void cancel(){
        requestStartMs = -1;
    }
}
