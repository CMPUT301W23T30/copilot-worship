package com.example.qrhunter;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Class for taking a photo
 *
 * @author bohan
 */
public class PhotoTakeNew extends AppCompatActivity {


    private Intent intent;
    private Bitmap bitmap;
    public PhotoTakeNew(Intent intent){
        this.intent = intent;
    }

    final static int CAMERA_PERM_CODE = 101;
    final static int CAMERA_REQUEST_CODE = 100;

    public Bitmap takePhoto(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }

        return start();
    }

    public Bitmap start(){

        return bitmap;
    }
}
