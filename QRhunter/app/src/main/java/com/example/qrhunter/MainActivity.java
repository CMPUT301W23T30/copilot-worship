package com.example.qrhunter;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ImageView selectedImage;

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
            scanCode();
        });

        photoButton = findViewById(R.id.Photo_Button);
        photoButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Open Camera", Toast.LENGTH_SHORT).show();
            takePhoto();
        });

        selectedImage = findViewById(R.id.imageView);
    }

    /**
     * Scan QR code
     */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
        Toast.makeText(this, "Make QR objects", Toast.LENGTH_SHORT).show();
    }

    /**
     * Scan QR code
     */
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
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

    /**
     * Take object photo
     */
    private void takePhoto(){
        askCameraPermissions();
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, 101);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //open camera
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, 102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            Bitmap image = (Bitmap)  data.getExtras().get("data");
            selectedImage.setImageBitmap(image);
        }
    }
}