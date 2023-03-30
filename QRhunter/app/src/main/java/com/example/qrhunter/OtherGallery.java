package com.example.qrhunter;


import android.os.Bundle;
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
    TextView textView;
    String username;

    ArrayList<QRCode> qrCodeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_gallery);

        textView = findViewById((R.id.other_gallery_name));

        //galleryView = findViewById(R.id.gallery_content);
        Bundle bundle = getIntent().getExtras();
        player = bundle.getParcelable("Player");
        username = player.getUsername();
        qrCodeArrayList = player.getQrCodes();
        textView.setText(username);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.other_gallery_recycler_view);
        OtherGalleryAdapter adapter = new OtherGalleryAdapter(qrCodeArrayList, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }
}