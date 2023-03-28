package com.example.qrhunter;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanOptions;


/**
 * Class for QR Camera
 * Takes a photo of a QRCode
 * by Bohan
 */
public class QRScan extends AppCompatActivity {

    /**
     * Start a new activity to open the camera and scan QR/bar codes
     * After scanning, asks user whether they want to take a photo of the real object
     *
     * @param barLauncher
     */
    public void scanCode(ActivityResultLauncher<ScanOptions> barLauncher) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
}
