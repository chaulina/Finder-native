package com.finder.gofrendi.finder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DiscoverActivity extends AppCompatActivity {
    JSONArray availableUserList;
    ArrayList<Integer> swipedIndexList;
    int userIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        this.swipedIndexList = new ArrayList<Integer>();
        this.userIndex = 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBackEnd backEnd = new AppBackEnd(this);
        this.availableUserList = backEnd.getAvailableUserList(backEnd.radius);
        this.parse();
    }

    public void onButtonDiscoverBackClick(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onButtonDiscoverYesClick(View view){
        if(userIndex < this.availableUserList.length()) {
            try {
                JSONObject user = this.availableUserList.getJSONObject(this.userIndex);
                AppBackEnd backEnd = new AppBackEnd(this);
                backEnd.swipeRight(user.getString("email"));
                this.swipedIndexList.add(this.userIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.parse();
    }

    public void onButtonDiscoverNoClick(View view){
        this.parse();
    }

    private void parse() {
        Button buttonDiscoverYes = (Button) findViewById(R.id.button_discover_yes);
        Button buttonDiscoverNo = (Button) findViewById(R.id.button_discover_no);
        ImageView imageViewDiscoverPP = (ImageView) findViewById(R.id.imageView_discover_pp);
        TextView textViewDiscoverName = (TextView) findViewById(R.id.textView_discover_name);
        if(this.availableUserList.length() - this.swipedIndexList.size() > 0){
            buttonDiscoverYes.setEnabled(true);
            buttonDiscoverNo.setEnabled(true);
            try {
                while (this.swipedIndexList.contains(this.userIndex)) {
                    if(this.userIndex <= this.availableUserList.length() -1) {
                        this.userIndex++;
                    }
                    else{
                        this.userIndex = 0;
                    }
                }
                AppBackEnd backEnd = new AppBackEnd(this);
                JSONObject user = this.availableUserList.getJSONObject(this.userIndex);
                textViewDiscoverName.setText(user.getString("name"));
                if(user.getString("profile_picture").equals("null")){
                    Log.d("my.otherpp", "set image to default");
                    imageViewDiscoverPP.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    try {
                        new DownloadImageTask(imageViewDiscoverPP).execute(backEnd.protocol + "://" + backEnd.server + "/public/uploads/" + user.getString("profile_picture"));
                    } catch (Exception e) {
                        Log.d("my.otherpp", e.getMessage());
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            buttonDiscoverYes.setEnabled(false);
            buttonDiscoverNo.setEnabled(false);
            imageViewDiscoverPP.setImageResource(R.mipmap.ic_launcher);
        }
    }

}
