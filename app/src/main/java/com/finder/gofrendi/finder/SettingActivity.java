package com.finder.gofrendi.finder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    AppConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        Toast toast = Toast.makeText(this, intent.getStringExtra("server"), Toast.LENGTH_SHORT);
        toast.show();
    }

}
