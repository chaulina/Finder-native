package com.finder.gofrendi.finder;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gofrendi on 2/1/17.
 */

public class RequestHelper extends AsyncTask<String, Integer, String> {
    public String urlAddress;
    public String method;
    HashMap<String, String> requestParams;
    HashMap<String, String> fileParams;

    public RequestHelper(){
        super();
        this.requestParams = new HashMap<String, String>();
        this.fileParams = new HashMap<String, String>();
    }

    // function to handle POST and FILES
    protected String doInBackground(String... params) {
        // response
        String responseResult = "";
        try {
            // build query
            if(this.method.equalsIgnoreCase("GET") && this.requestParams != null) {
                String query = null;
                Uri.Builder builder = new Uri.Builder();
                for (Map.Entry<String, String> entry : this.requestParams.entrySet()) {
                    String key = entry.getKey();
                    String val = entry.getValue();
                    if(!val.equals("")) {
                        builder = builder.appendQueryParameter(key, val);
                    }
                }
                query = builder.build().getEncodedQuery();
                if(query != null){
                    this.urlAddress += "?" + query;
                    Log.d("my.rh.query", String.valueOf(query));
                }
            }
            URL url = new URL(this.urlAddress);
            Log.d("my.rh.urlAddress", this.urlAddress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // define connection parameters
            con.setRequestMethod(this.method);
            con.setReadTimeout(15000);
            con.setConnectTimeout(15000);
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setDoInput(true);
            // if POST
            if(this.method.equalsIgnoreCase("POST")) {
                // configuration to generate low-level HTTP request
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                con.setDoOutput(true);
                // data stream
                DataOutputStream dos = null;
                dos = new DataOutputStream(con.getOutputStream());
                // get size of requestParams + size of requestFiles
                int totalFieldCount = this.requestParams.size() + this.fileParams.size();
                int fieldCount = 0;
                Log.d("my.rh.fieldCount", String.valueOf(fieldCount));
                Log.d("my.rh.totalField", String.valueOf(totalFieldCount));
                if(totalFieldCount > 0){
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                }
                // post parameters
                for (Map.Entry<String, String> entry : this.requestParams.entrySet()) {
                    String key = entry.getKey();
                    String val = entry.getValue();
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(val);
                    dos.writeBytes(lineEnd);
                    // create field closer
                    fieldCount++;
                    if(fieldCount == totalFieldCount) {
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        Log.d("my.rh", "penutup");
                    }
                    else{
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        Log.d("my.rh", "batas");
                    }
                }
                // file parameters
                for (Map.Entry<String, String> entry : this.fileParams.entrySet()) {
                    String key = entry.getKey();
                    String val = entry.getValue();
                    FileInputStream fileInputStream = new FileInputStream(new File(String.valueOf(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Finder/" + val))
                    ));
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + val + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: image/jpeg" + lineEnd);
                    dos.writeBytes(lineEnd);
                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    // send multipart form data necessary after file data...
                    dos.writeBytes(lineEnd);
                    // create field closer
                    fieldCount++;
                    if(fieldCount == totalFieldCount) {
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        Log.d("my.rh", "penutup");
                    }
                    else{
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        Log.d("my.rh", "batas");
                    }
                    // close streams
                    Log.d("my.rh", "File is written");
                    fileInputStream.close();
                }
                dos.flush();
                dos.close();
            }
            // connect and send the request
            con.connect();
            // get the response (should be 200)
            int responseCode = con.getResponseCode();
            Log.d("my.rh.responseCode", String.valueOf(responseCode));
            if(responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer response = new StringBuffer();
                String inputLine;
                while((inputLine = in.readLine()) != null){
                    response.append(inputLine);
                }
                in.close();
                responseResult = response.toString();
            }
            else{
                Log.d("my.rh.error", con.getResponseMessage());
            }
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseResult = String.valueOf(responseResult);
        Log.d("my.rh.response", responseResult);
        return responseResult;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
