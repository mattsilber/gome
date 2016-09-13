package com.guardanis.gtools.net.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

import com.guardanis.gtools.net.NetUtils;

public class InputStreamHelper {

    private InputStream inputStream;

    public InputStreamHelper(HttpURLConnection conn) throws NullPointerException, IOException {
        inputStream = conn.getResponseCode() < 400
                ? conn.getInputStream()
                : conn.getErrorStream();

        String contentEncoding = conn.getContentEncoding();

        if(contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip"))
            inputStream = new GZIPInputStream(inputStream);
    }
    
    public InputStreamHelper(InputStream inputStream){
    	this.inputStream = inputStream;
    }

    public String read() throws IOException {
        return read("");
    }

    public String read(String lineDelimiter) throws IOException {
        String response = "";
        String line;

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        while((line = reader.readLine()) != null)
            response += line + lineDelimiter;

        reader.close();
        
        if(lineDelimiter.length() < response.length())
        	response = response.substring(0, response.length() - lineDelimiter.length());

        return response;
    }

    public void closeConnection() {
        NetUtils.close(inputStream);
    }

}