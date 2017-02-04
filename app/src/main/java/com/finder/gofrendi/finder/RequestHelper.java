package com.finder.gofrendi.finder;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
