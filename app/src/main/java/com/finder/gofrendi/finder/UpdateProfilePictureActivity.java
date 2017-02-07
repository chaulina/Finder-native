package com.finder.gofrendi.finder;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateProfilePictureActivity extends AppCompatActivity {
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private UpdateProfilePictureActivity self;

    String server = "";
    String protocol = "";
    String session = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_picture);
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);
        self = this;
    }

    public void buttonCaptureClick(View v){
        mCamera.takePicture(null, null, mPicture);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        server = intent.getStringExtra("server");
        protocol = intent.getStringExtra("protocol");
        session = intent.getStringExtra("session");
    }

    /**
     * Helper method to access the camera returns null if it cannot get the
     * camera or does not exist
     *
     * @return
     */
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d("my.cam", "Picture file is null");
                return;
            }
            Log.d("my.cam", String.valueOf(pictureFile.getAbsoluteFile()));
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                Log.d("my.cam", String.valueOf(fos));
                fos.close();

                // upload
                AppBackEnd backEnd = new AppBackEnd(self);
                backEnd.changeProfilePicture(pictureFile.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Finder")));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("my.cam", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String mediaFileName = "IMG_" + timeStamp + ".jpg";
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mediaFileName);

        return mediaFile;
    }
}
