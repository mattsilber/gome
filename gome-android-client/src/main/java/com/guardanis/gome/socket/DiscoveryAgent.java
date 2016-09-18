package com.guardanis.gome.socket;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.guardanis.gome.BaseActivity;
import com.guardanis.gome.tools.Callback;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class DiscoveryAgent implements Runnable {

    private static DiscoveryAgent instance;
    public static DiscoveryAgent getInstance(Context context){
        if(instance == null)
            instance = new DiscoveryAgent(context);

        return instance;
    }

    private Context context;
    private Callback<String> devicesCallback;
    private long requestStartMs;

    protected DiscoveryAgent(Context context){
        this.context = context.getApplicationContext();
    }

    public DiscoveryAgent search(Callback<String> devicesCallback){
        this.requestStartMs = System.currentTimeMillis();
        this.devicesCallback = devicesCallback;

        new Thread(this)
                .start();

        return this;
    }

    @Override
    public void run() {
        final long currentRequest = Long.valueOf(requestStartMs);

        String baseIpAddress = getWifiIpAddress();

        if(baseIpAddress != null){
            try {
                Log.i(BaseActivity.TAG__BASE, "WIFI IP: " + baseIpAddress);

                String gateway = baseIpAddress.substring(0, baseIpAddress.lastIndexOf(".") + 1);

                for(int i = 0; i < 32; i++){
                    IpRangeTester tester = new IpRangeTester(gateway,
                            new int[]{
                                    i * 8,
                                    (i * 8) + 8
                            },
                            currentRequest,
                            ip ->
                                    new Handler(Looper.getMainLooper())
                                            .post(() ->
                                                    devicesCallback.onCalled(ip)));

                    new Thread(tester)
                            .start();
                }
            }
            catch (Exception e) { e.printStackTrace(); }
        }
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

    private class IpRangeTester implements Runnable {
        private String gateway;
        private int[] ipRange;
        private long currentRequest;
        private Callback<String> ipCallback;

        protected IpRangeTester(String gateway, int[] ipRange, long currentRequest, Callback<String> ipCallback) {
            this.gateway = gateway;
            this.ipRange = ipRange;
            this.currentRequest = currentRequest;
            this.ipCallback = ipCallback;
        }

        @Override
        public void run() {
            for (int i = ipRange[0]; i <= ipRange[1]; i++) {
                if(currentRequest != requestStartMs)
                    return;

                String ipValue = gateway + i;

                try {
                    Socket socket = new Socket(ipValue, SocketClient.DEFAULT_PING_PORT);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    if(currentRequest != requestStartMs)
                        return;

                    if(reader.readLine().equals("1")){
                        Log.i(BaseActivity.TAG__BASE, "Writer connected to: " + ipValue);

                        ipCallback.onCalled(ipValue);
                    }
                }
                catch (Exception e) {
                    Log.d(BaseActivity.TAG__BASE, ipValue + " failed with " + e.getMessage());
                }
            }
        }

    }
}
