package com.finder.gofrendi.finder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public void updateLocation() {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Log.d("location", "start");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("location", "failed");
            return;
        }
        Log.d("location", "askForUpdate");
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 1, new GpsListener(this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void changeState() {
        ImageView imageViewMainPP = (ImageView) findViewById(R.id.imageView_main_pp);
        TextView textViewMainName = (TextView) findViewById(R.id.textView_main_name);
        Button buttonShowSetting = (Button) findViewById(R.id.button_show_setting);
        Button buttonShowLogin = (Button) findViewById(R.id.button_show_login);
        Button buttonShowRegister = (Button) findViewById(R.id.button_show_register);
        Button buttonLogout = (Button) findViewById(R.id.button_logout);
        Button buttonShowUpdateProfilePicture = (Button) findViewById(R.id.button_show_update_profile_picture);
        Button buttonShowDiscover = (Button) findViewById(R.id.button_show_discover);
        Button buttonShowMatch = (Button) findViewById(R.id.button_show_match);

        boolean loginState;
        AppBackEnd backEnd = new AppBackEnd(this);
        if(!backEnd.loginBySession()){
            Toast toast = Toast.makeText(this, backEnd.getErrorMessage(), Toast.LENGTH_SHORT);
            toast.show();
            textViewMainName.setText("");
            imageViewMainPP.setImageResource(R.mipmap.ic_launcher);
            loginState = false;
        }
        else {
            textViewMainName.setText(backEnd.getCurrentUserName() + " " + backEnd.getCurrentUserEmail());
            Log.d("my.pp", backEnd.getCurrentUserProfilePicture());
            if(backEnd.getCurrentUserProfilePicture().equals("null")){
                Log.d("my.pp", "set image to default");
                imageViewMainPP.setImageResource(R.mipmap.ic_launcher);
            }
            else {
                try {
                    new DownloadImageTask(imageViewMainPP).execute(backEnd.protocol + "://" + backEnd.server + "/public/uploads/" + backEnd.getCurrentUserProfilePicture());
                } catch (Exception e) {
                    Log.d("my.pp", e.getMessage());
                }
            }
            loginState = true;
        }
        buttonShowSetting.setEnabled(true);
        buttonShowLogin.setEnabled(!loginState);
        buttonShowRegister.setEnabled(!loginState);
        buttonLogout.setEnabled(loginState);
        buttonShowUpdateProfilePicture.setEnabled(loginState);
        buttonShowDiscover.setEnabled(loginState);
        buttonShowMatch.setEnabled(loginState);
    }

    @Override
    protected void onStart(){
        super.onStart();
        changeState();
        updateLocation();
    }

    public void buttonShowSettingClick(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        AppBackEnd backEnd = new AppBackEnd(this);
        intent.putExtra("protocol", backEnd.protocol);
        intent.putExtra("server", backEnd.server);
        intent.putExtra("session", backEnd.session);
        intent.putExtra("radius", backEnd.radius);
        startActivity(intent);
    }

    public void buttonShowLoginClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        AppBackEnd backEnd = new AppBackEnd(this);
        intent.putExtra("protocol", backEnd.protocol);
        intent.putExtra("server", backEnd.server);
        intent.putExtra("session", backEnd.session);
        intent.putExtra("radius", backEnd.radius);
        startActivity(intent);
    }

    public void buttonShowRegisterClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        AppBackEnd backEnd = new AppBackEnd(this);
        intent.putExtra("protocol", backEnd.protocol);
        intent.putExtra("server", backEnd.server);
        intent.putExtra("session", backEnd.session);
        intent.putExtra("radius", backEnd.radius);
        startActivity(intent);
    }

    public void buttonLogoutClick(View view) {
        AppBackEnd backEnd = new AppBackEnd(this);
        backEnd.logout();
        changeState();
    }

    public void buttonShowUpdateProfilePictureClick(View view) {
        Intent intent = new Intent(this, UpdateProfilePictureActivity.class);
        AppBackEnd backEnd = new AppBackEnd(this);
        intent.putExtra("protocol", backEnd.protocol);
        intent.putExtra("server", backEnd.server);
        intent.putExtra("session", backEnd.session);
        intent.putExtra("radius", backEnd.radius);
        startActivity(intent);
    }

    public void buttonShowDiscoverClick(View view) {
        Intent intent = new Intent(this, DiscoverActivity.class);
        AppBackEnd backEnd = new AppBackEnd(this);
        intent.putExtra("protocol", backEnd.protocol);
        intent.putExtra("server", backEnd.server);
        intent.putExtra("session", backEnd.session);
        intent.putExtra("radius", backEnd.radius);
        startActivity(intent);
    }

    public void buttonShowMatchClick(View view) {
        Intent intent = new Intent(this, MatchActivity.class);
        Log.d("my.match", "start");
        AppBackEnd backEnd = new AppBackEnd(this);
        intent.putExtra("protocol", backEnd.protocol);
        intent.putExtra("server", backEnd.server);
        intent.putExtra("session", backEnd.session);
        intent.putExtra("radius", backEnd.radius);
        startActivity(intent);
    }

}
