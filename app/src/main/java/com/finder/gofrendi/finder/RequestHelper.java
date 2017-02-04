package com.finder.gofrendi.finder;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    protected String doInBackground(String... params) {
        String responseResult = "";
        try {
            // build query
            String query = null;
            if(this.requestParams != null) {
                Uri.Builder builder = new Uri.Builder();
                for (Map.Entry<String, String> entry : this.requestParams.entrySet()) {
                    String key = entry.getKey();
                    String val = entry.getValue();
                    if(!val.equals("")) {
                        builder = builder.appendQueryParameter(key, val);
                    }
                }
                query = builder.build().getEncodedQuery();
            }
            Log.d("my.rh.query", String.valueOf(query));
            // add parameters
            if(query != null && this.method.equalsIgnoreCase("GET")) {
                this.urlAddress += "?" + query;
            }
            URL url = new URL(this.urlAddress);
            Log.d("my.rh.urlAddress", this.urlAddress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // define connection parameters
            con.setRequestMethod(this.method);
            con.setReadTimeout(15000);
            con.setConnectTimeout(15000);
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setDoInput(true);
            // if not get, add query
            if(!this.method.equalsIgnoreCase("GET")) {
                con.setDoOutput(true);
                if(query != null){
                    OutputStream os = con.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
                }
            }

            // do connect
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
            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        responseResult = String.valueOf(responseResult);
        Log.d("my.rh.response", responseResult);
        return responseResult;
    }

    private void doFileUpload() {

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String existingFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mypic.png";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String responseFromServer = "";
        String urlString = "http://mywebsite.com/directory/upload.php";

        try {

            //------------------ CLIENT REQUEST
            FileInputStream fileInputStream = new FileInputStream(new File(existingFileName));
            // open a URL connection to the Servlet
            URL url = new URL(urlString);
            // Open a HTTP connection to the URL
            conn = (HttpURLConnection) url.openConnection();
            // Allow Inputs
            conn.setDoInput(true);
            // Allow Outputs
            conn.setDoOutput(true);
            // Don't use a cached copy.
            conn.setUseCaches(false);
            // Use a post method.
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName + "\"" + lineEnd);
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

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // close streams
            Log.e("Debug", "File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (IOException ioe) {
            Log.e("Debug", "error: " + ioe.getMessage(), ioe);
        }

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
