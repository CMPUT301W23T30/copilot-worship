package com.example.qrhunter;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Other Gallery Activity
 * Displays the QRCodes owned by another Player using OtherGalleryAdapter
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
        if (!qrCodesList.isEmpty()){
            galleryCount.setText(String.valueOf(qrCodesList.size()));
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.other_gallery_recycler_view);

        OtherGalleryAdapter adapter = new OtherGalleryAdapter(qrCodeArrayList, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        // Set the action bar to show the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Back button to previous Activity
     * @param item The menu item that was selected.
     *
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}