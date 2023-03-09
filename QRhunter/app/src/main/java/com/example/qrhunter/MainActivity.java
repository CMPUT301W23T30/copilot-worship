package com.example.qrhunter;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {



    Button scanButton;
    Button photoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start populate xml
        ImageView profileCircle = (ImageView) findViewById(R.id.profile_icon);
        //in the future if we want to add profile pictures
        profileCircle.setImageResource(R.drawable._icon__profile_circle_);

        FirebaseFirestore db;

        db = FirebaseFirestore.getInstance();

        final CollectionReference collectionReference = db.collection("Cities");

        HashMap<String, String> data = new HashMap<>();
        data.put("Province Name", "lol");


        collectionReference
                .document("Lmao")
                .set(data);

        scanButton = findViewById(R.id.Scan_Button);
        scanButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Start Scanning", Toast.LENGTH_SHORT).show();
            QRScan newClass = new QRScan();
            newClass.scanCode(barLaucher);
        });

        photoButton = findViewById(R.id.Photo_Button);
        photoButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Open Camera", Toast.LENGTH_SHORT).show();
            PhotoTake newClass = new PhotoTake();
            newClass.takePhoto();
        });
    }

    private String hasher(String unhashedQRCode) {
        return sha256Hex(unhashedQRCode);
    }

    private int scoreCalculator(String hashedQRCode) {
        // Find contiguous repeated numbers or characters in hex string
        // Each number or character is equal to number^(n-1) points where n is the number of times it is repeated
        int score = 0;
        int n = 0;
        String prev = "";
        for (int i = 0; i < hashedQRCode.length(); i++) {
            if (hashedQRCode.substring(i, i + 1).equals(prev)) {
                n++;
            } else {
                if (n > 1) {
                    score += Math.pow(Integer.parseInt(prev, 16), n - 1);
                }
                n = 1;
                prev = hashedQRCode.substring(i, i + 1);
            }
        }

        return score;

    }

    /**
     * Scan QR code
     */
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            String hashedCode = hasher(result.getContents()); // If this fails alert won't appear, makes it easier to test
            int score = scoreCalculator(hashedCode);

            Toast.makeText(this, "make object", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(score + " points");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
}