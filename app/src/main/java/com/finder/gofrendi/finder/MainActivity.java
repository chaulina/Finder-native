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

public class MainActivity extends AppCompatActivity {
    AppBackEnd config;
    public double lon = 0;
    public double lat = 0;

    /**
     * GPS_Listener
     */
    private class GPS_Listener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            lon = loc.getLongitude();
            lat = loc.getLatitude();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, new GPS_Listener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        config = new AppBackEnd(this);
        boolean loggedIn = config.loginBySession();
        Log.d("my.logInBySession", String.valueOf(loggedIn));

        config = new AppBackEnd(this);
        loggedIn =config.loginByEmail("test@test.com", "test");
        Log.d("my.logInByEmail", String.valueOf(loggedIn));
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
        intent.putExtra("protocol", this.config.protocol);
        intent.putExtra("server", this.config.server);
        intent.putExtra("session", this.config.session);
        intent.putExtra("radius", this.config.radius);
        startActivity(intent);
    }

    public void buttonRegisterClick(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("protocol", this.config.protocol);
        intent.putExtra("server", this.config.server);
        intent.putExtra("session", this.config.session);
        intent.putExtra("radius", this.config.radius);
        startActivity(intent);
    }

    public void buttonLogoutClick(View view) {

    }

    public void buttonUpdateProfilePictureClick(View view) {
        Intent intent = new Intent(this, UpdateProfilePictureActivity.class);
        intent.putExtra("protocol", this.config.protocol);
        intent.putExtra("server", this.config.server);
        intent.putExtra("session", this.config.session);
        intent.putExtra("radius", this.config.radius);
        startActivity(intent);
    }

    public void buttonDiscoverClick(View view) {
        Intent intent = new Intent(this, DiscoverActivity.class);
        intent.putExtra("protocol", this.config.protocol);
        intent.putExtra("server", this.config.server);
        intent.putExtra("session", this.config.session);
        intent.putExtra("radius", this.config.radius);
        startActivity(intent);
    }

    public void buttonMatchClick(View view) {
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra("protocol", this.config.protocol);
        intent.putExtra("server", this.config.server);
        intent.putExtra("session", this.config.session);
        intent.putExtra("radius", this.config.radius);
        startActivity(intent);
    }
}
