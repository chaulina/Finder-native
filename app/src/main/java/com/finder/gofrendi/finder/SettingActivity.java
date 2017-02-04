package com.finder.gofrendi.finder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    AppBackEnd config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String server = intent.getStringExtra("server");
        String protocol = intent.getStringExtra("protocol");
        String session = intent.getStringExtra("session");
        String radius = intent.getStringExtra("radius");
    }

}
