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
    TextView galleryCount;
    String username;
    ArrayList<QRCode> qrCodesList = new ArrayList<>();
    ArrayList<QRCode> qrCodeArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_gallery);

        galleryName = findViewById((R.id.other_gallery_name));
        galleryCount = findViewById(R.id.other_gallery_qr_count);

        //Unpack bundle to get Player info
        Bundle bundle = getIntent().getExtras();
        player = bundle.getParcelable("Player");
        username = player.getUsername();
        qrCodeArrayList = player.getQrCodes();
        qrCodesList = player.getQrCodes();

        galleryName.setText(username + "'s Gallery");

        //Find number of QRCode Player has found
        if (!qrCodesList.equals(null)){
            galleryName.setText(String.valueOf(qrCodesList.size()));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.other_gallery_recycler_view);

        OtherGalleryAdapter adapter = new OtherGalleryAdapter(qrCodeArrayList, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }
}