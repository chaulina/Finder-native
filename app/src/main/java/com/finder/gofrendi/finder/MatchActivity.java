package com.finder.gofrendi.finder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LinearLayout activityLayout = (LinearLayout) findViewById(R.id.activity_match);
        activityLayout.removeAllViews();
        final AppBackEnd backEnd = new AppBackEnd(this);
        try {
            JSONArray matchList = backEnd.getMatchList();
            for(int i=0; i<matchList.length(); i++){
                final JSONObject user = matchList.getJSONObject(i);
                LinearLayout container = new LinearLayout(this);
                container.setOrientation(LinearLayout.HORIZONTAL);
                // create and add img to container
                ImageView img = new ImageView(this);
                if(user.getString("profile_picture").equals("null")){
                    Log.d("my.otherpp", "set image to default");
                    img.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    try {
                        new DownloadImageTask(img).execute(backEnd.protocol + "://" + backEnd.server + "/public/uploads/" + user.getString("profile_picture"));
                    } catch (Exception e) {
                        Log.d("my.otherpp", e.getMessage());
                    }
                }
                container.addView(img);
                img.getLayoutParams().height = 150;
                img.getLayoutParams().width = 150;
                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                img.requestLayout();
                // create and add txt to container
                TextView txt = new TextView(this);
                txt.setText(user.getString("name"));
                container.addView(txt);
                container.setClickable(true);
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtra("targetEmail", user.getString("email"));
                            intent.putExtra("targetName", user.getString("name"));
                            startActivity(intent);
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // add container to layout
                activityLayout.addView(container);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
