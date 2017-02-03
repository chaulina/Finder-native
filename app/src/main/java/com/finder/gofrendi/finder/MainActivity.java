package com.finder.gofrendi.finder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {
    AppConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config = new AppConfig(this);
        config.protocol = "http";
        config.server = "10.0.2.2:5000";
        boolean loggedIn = config.login();
    }

    public void buttonSettingClick(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("protocol", this.config.protocol);
        intent.putExtra("server", this.config.server);
        intent.putExtra("session", this.config.session);
        intent.putExtra("radius", this.config.radius);
        startActivity(intent);
    }

    public void buttonLoginClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        //String message = "login";
        //intent.putExtra(EXTRA_MESSAGE, message);
    }

    public void buttonUpdateProfilePictureClick(View view) {
        Intent intent = new Intent(this, UpdateProfilePictureActivity.class);
        startActivity(intent);
    }

    public void buttonDiscoverClick(View view) {
        Intent intent = new Intent(this, DiscoverActivity.class);
        startActivity(intent);
    }

    public void buttonMatchClick(View view) {
        Intent intent = new Intent(this, MatchActivity.class);
        startActivity(intent);
    }
}
