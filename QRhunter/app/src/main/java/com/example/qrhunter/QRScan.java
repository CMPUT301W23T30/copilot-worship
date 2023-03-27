package com.example.qrhunter;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanOptions;


public class QRScan extends AppCompatActivity {

    /**
     * Start a new activity to open the camera and scan QR/bar codes
     * After scanning, asks user whether they want to take a photo of the real object
     *
     * @param barLaucher
     */
    public void scanCode(ActivityResultLauncher<ScanOptions> barLaucher) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);


    }

    public void askTakePhoto(){
        new AlertDialog.Builder(this)
                .setTitle("Add Object Photo")
                .setMessage("Do you want to take a Photo of the real object?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 100);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
