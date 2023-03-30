package com.example.qrhunter;


import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Gallery Activity
 * Displays the QRCodes owned by a Player using GalleryAdapter
 * Currently displays the QRCodes Name, Location, and Score
 */
public class OtherGallery extends AppCompatActivity {
    Player player;
    TextView galleryName;
    String username;

    ArrayList<QRCode> qrCodeArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_gallery);

        galleryName = findViewById((R.id.other_gallery_name));



        //galleryView = findViewById(R.id.gallery_content);
        Bundle bundle = getIntent().getExtras();
        player = bundle.getParcelable("Player");
        username = player.getUsername();
        qrCodeArrayList = player.getQrCodes();
        galleryName.setText(username + "'s Gallery");

        for (QRCode qrCode : qrCodeArrayList){
            Log.d("TASK", "QR: " + qrCode.getName());
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.other_gallery_recycler_view);

        Log.d("TASK", "START");

        OtherGalleryAdapter adapter = new OtherGalleryAdapter(qrCodeArrayList, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        Log.d("TASK", "SET");
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }
}