package com.guardanis.gome.socket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.guardanis.gome.commands.Command;
import com.guardanis.gome.tools.NetworkDeviceUtils;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SocketClient implements Runnable {

    public interface ConnectionCallbacks {
        public void onIdentified(String ip, String hostName);
        public void onConnectionException(Throwable throwable);
        public void onConnectionClosed();
    }

    public static final int DEFAULT_PORT = 13337;
    public static final int DEFAULT_PING_PORT = 13338;

    private static final String TAG = "gome-socket";

    private String ip;
    private int port;

    private ConnectionCallbacks connectionCallbacks;

    private BufferedWriter writer;
    private BufferedReader reader;

    private boolean canceled = false;

    private Handler socketThreadHandler;
    private Handler meainThreadHandler = new Handler(Looper.getMainLooper());

    public static SocketClient open(String ip, int port, ConnectionCallbacks connectionCallbacks) {
        return new SocketClient(ip, port, connectionCallbacks);
    }

    private SocketClient(String ip, int port, ConnectionCallbacks connectionCallbacks) {
        this.ip = ip;
        this.port = port;
        this.connectionCallbacks = connectionCallbacks;

        new Thread(this)
                .start();
    }

    @Override
    public void run() {
        try {
            Log.i(TAG, "Attempting connection...");

//            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();//new v3SocketFactory();//
//
//            SSLSocket socket = (SSLSocket) socketFactory.createSocket(ip, port);
//
//            for(String c : socketFactory.getSupportedCipherSuites())
//                Log.i(TAG, "Suite: " + c);
//
//            socket.setEnabledCipherSuites(socketFactory.getSupportedCipherSuites());
////            socket.setEnabledProtocols(new String[] { "TLSv1" });

            Socket socket = new Socket(ip, port);

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Log.i(TAG, "Connected to socket write stream...");

            sendDeviceInfo();

            Log.i(TAG, "Wrote device info to socket...");

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String hostName = reader.readLine();

            Log.i(TAG, "Received host name: " + hostName);

            meainThreadHandler.post(() -> connectionCallbacks.onIdentified(ip, hostName));

            Looper.prepare();

            socketThreadHandler = new Handler();

            Looper.loop();
        }
        catch (Throwable e) {
            e.printStackTrace();

            meainThreadHandler.post(() ->
                    connectionCallbacks.onConnectionException(new RuntimeException("Couldn't connect to host!!!")));
        }
        finally { onDestroyed(); }

        meainThreadHandler.post(() ->
                connectionCallbacks.onConnectionClosed());
    }

    private void sendDeviceInfo() throws Exception {
        JSONObject obj = new JSONObject()
                .put("ip_address", NetworkDeviceUtils.getIPAddress(true))
                .put("mac_address", NetworkDeviceUtils.getMACAddress("wlan0"))
                .put("name", NetworkDeviceUtils.getDeviceName());

        write(obj.toString());
    }

    public void send(Command command) {
        try {
            String commandData = command.getActionIdentifier() + ":" + command.toJson().toString();

//            Log.i(TAG, "Writing command: " + commandData);

            socketThreadHandler.post(() -> {
                try {
                    write(commandData);
                }
                catch (Exception e) { e.printStackTrace(); }
            });
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    private void write(String data) throws Exception {
        writer.write(data);
        writer.newLine();
        writer.flush();
    }

    public void onDestroyed() {
        Log.i(TAG, "Socket Client Destroyed...");

        canceled = true;

        try {
            writer.close();
        }
        catch (Exception e) { e.printStackTrace(); }

        try {
            reader.close();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    public class v3SocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLSv1");

        public v3SocketFactory() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super();

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return new String[0];
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return new String[0];
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            SSLSocket S = (SSLSocket) sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
            S.setEnabledProtocols(new String[]{"TLSv1"});
            return S;
        }

        @Override
        public Socket createSocket() throws IOException {
            SSLSocket S = (SSLSocket) sslContext.getSocketFactory().createSocket();
            S.setEnabledProtocols(new String[]{"TLSv1"});
            return S;
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            SSLSocket S = (SSLSocket) sslContext.getSocketFactory().createSocket(host, port);
            S.setEnabledProtocols(new String[]{"TLSv1"});
            return S;
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            return null;
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            return null;
        }
    }
}
