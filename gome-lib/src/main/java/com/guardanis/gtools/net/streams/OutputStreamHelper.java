package com.guardanis.gtools.net.streams;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import com.guardanis.gtools.net.NetUtils;

public class OutputStreamHelper {

    private HttpURLConnection connection;
    private OutputStreamWriter writer;

    public OutputStreamHelper(HttpURLConnection connection)  {
        this.connection = connection;
    }

    public void write(String params) throws IOException {
        if(!(params == null || params.length() < 1)){
            connection.setDoOutput(true);

            writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(params);
            writer.flush();
        }
    }

    public void closeConnection() {
        NetUtils.close(writer);

        connection = null;
    }

}
