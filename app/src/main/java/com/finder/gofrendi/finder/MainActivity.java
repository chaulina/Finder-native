package com.finder.gofrendi.finder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    AppBackEnd backEnd;
    public double lon = 0;
    public double lat = 0;

    private MainActivity self;

    /**
     * GPS_Listener
     */
    private class GPS_Listener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            lon = loc.getLongitude();
            lat = loc.getLatitude();
            Log.d("location.lat", String.valueOf(lat));
            Log.d("location.lon", String.valueOf(lon));
            backEnd = new AppBackEnd(self);
            backEnd.move(lat,lon);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

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
                LocationManager.GPS_PROVIDER, 5000, 1, new GPS_Listener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        self = this;
    }

    @Override
    protected void onStart(){
        super.onStart();
        Intent intent = getIntent();
        String server = intent.getStringExtra("server");
        String protocol = intent.getStringExtra("protocol");
        String session = intent.getStringExtra("session");
        String radius = intent.getStringExtra("radius");

        // logged in by session
        backEnd = new AppBackEnd(this);
        boolean loggedIn = backEnd.loginBySession();
        if(!loggedIn){
            Toast toast = Toast.makeText(this, intent.getStringExtra("server"), Toast.LENGTH_SHORT);
            toast.show();
        }
        Log.d("my.logInBySession", String.valueOf(loggedIn));

        updateLocation();
    }

    public void buttonShowSettingClick(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("protocol", this.backEnd.protocol);
        intent.putExtra("server", this.backEnd.server);
        intent.putExtra("session", this.backEnd.session);
        intent.putExtra("radius", this.backEnd.radius);
        startActivity(intent);
    }

    public void buttonShowLoginClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("protocol", this.backEnd.protocol);
        intent.putExtra("server", this.backEnd.server);
        intent.putExtra("session", this.backEnd.session);
        intent.putExtra("radius", this.backEnd.radius);
        startActivity(intent);
    }

    public void buttonShowRegisterClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("protocol", this.backEnd.protocol);
        intent.putExtra("server", this.backEnd.server);
        intent.putExtra("session", this.backEnd.session);
        intent.putExtra("radius", this.backEnd.radius);
        startActivity(intent);
    }

    public void buttonShowLogoutClick(View view) {

    }

    public void buttonShowUpdateProfilePictureClick(View view) {
        Intent intent = new Intent(this, UpdateProfilePictureActivity.class);
        intent.putExtra("protocol", this.backEnd.protocol);
        intent.putExtra("server", this.backEnd.server);
        intent.putExtra("session", this.backEnd.session);
        intent.putExtra("radius", this.backEnd.radius);
        startActivity(intent);
    }

    public void buttonShowDiscoverClick(View view) {
        Intent intent = new Intent(this, DiscoverActivity.class);
        intent.putExtra("protocol", this.backEnd.protocol);
        intent.putExtra("server", this.backEnd.server);
        intent.putExtra("session", this.backEnd.session);
        intent.putExtra("radius", this.backEnd.radius);
        startActivity(intent);
    }

    public void buttonShowMatchClick(View view) {
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra("protocol", this.backEnd.protocol);
        intent.putExtra("server", this.backEnd.server);
        intent.putExtra("session", this.backEnd.session);
        intent.putExtra("radius", this.backEnd.radius);
        startActivity(intent);
    }

}
