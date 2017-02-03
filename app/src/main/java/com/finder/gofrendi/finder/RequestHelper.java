package com.finder.gofrendi.finder;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gofrendi on 2/1/17.
 */

public class RequestHelper extends AsyncTask<String, Integer, String> {
    public String urlAddress;
    public String method;

    protected String doInBackground(String... params) {
        String responseResult = "";
        try {
            URL url = new URL(this.urlAddress);
            Log.d("myDebug", this.urlAddress);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // define parameters
            con.setRequestMethod(this.method);
            con.setReadTimeout(15000);
            con.setConnectTimeout(15000);
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            // do connect
            con.connect();
            // get the response (should be 200)
            int responseCode = con.getResponseCode();
            Log.d("myDebug", String.valueOf(responseCode));
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
            Log.d("myDebug", "Error");
        }
        Log.d("myDebug", String.valueOf(responseResult));
        return responseResult;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
