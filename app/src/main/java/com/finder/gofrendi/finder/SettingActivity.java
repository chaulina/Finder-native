package com.finder.gofrendi.finder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

    public void buttonSettingSaveClick(View view) {
        EditText editTextSettingProtocol = (EditText) findViewById(R.id.editText_setting_protocol);
        EditText editTextSettingServer = (EditText) findViewById(R.id.editText_setting_server);
        EditText editTextSettingRadius = (EditText) findViewById(R.id.editText_setting_radius);
        AppBackEnd backEnd = new AppBackEnd(this);
        backEnd.protocol = String.valueOf(editTextSettingProtocol.getText());
        backEnd.server = String.valueOf(editTextSettingServer.getText());
        backEnd.radius = Double.parseDouble(String.valueOf(editTextSettingRadius.getText()));
        backEnd.save();
    }

}
