package com.finder.gofrendi.finder;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by gofrendi on 2/12/17.
 */

class GpsListener implements LocationListener {
    private MainActivity activity;

    GpsListener(MainActivity activity){
        super();
        this.activity = activity;
    }

    @Override
    public void onLocationChanged(Location loc) {
        double lon = loc.getLongitude();
        double lat = loc.getLatitude();
        Log.d("my.loc.lat", String.valueOf(lat));
        Log.d("my.loc.lon", String.valueOf(lon));
        AppBackEnd backEnd = new AppBackEnd(this.activity);
        backEnd.move(lat,lon);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

}
