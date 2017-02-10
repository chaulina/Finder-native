package com.finder.gofrendi.finder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
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
        backEnd = new AppBackEnd(this);
        if(!backEnd.loginBySession()){
            Toast toast = Toast.makeText(this, backEnd.getErrorMessage(), Toast.LENGTH_SHORT);
            toast.show();
            textViewMainName.setText("");
            imageViewMainPP.setImageResource(R.mipmap.ic_launcher);
            loginState = false;
        }
        else {
            textViewMainName.setText(backEnd.getCurrentUserName() + " " + backEnd.getCurrentUserEmail());
            try {
                new DownloadImageTask(imageViewMainPP).execute(backEnd.protocol + "://" + backEnd.server + "/public/uploads/" + backEnd.getCurrentUserProfilePicture());
                /*
                URL ppUrl = new URL(backEnd.protocol + "://" + backEnd.server + "/public/uploads/" + backEnd.getCurrentUserProfilePicture());
                InputStream content = (InputStream) ppUrl.getContent();
                Drawable d = Drawable.createFromStream(content, "src");
                imageViewMainPP.setImageDrawable(d);
                Log.d("my.pp", String.valueOf(ppUrl));
                */
            } catch(Exception e){
                Log.d("my.pp", e.getMessage());
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

    public void buttonLogoutClick(View view) {
        backEnd = new AppBackEnd(this);
        backEnd.logout();
        changeState();
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
