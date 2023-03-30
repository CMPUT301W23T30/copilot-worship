package com.example.qrhunter;


import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Gallery Activity
 * Displays the QRCodes owned by a Player using GalleryAdapter
 * Currently displays the QRCodes Name, Location, and Score
 */
public class Gallery extends AppCompatActivity {
    Player player;
    TextView textView;
    String username;

    ArrayList<QRCodeComment> qrCodeComments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        Log.d("TASK", "INSIDE GALLERY");

        textView = findViewById((R.id.gallery_name));

        //galleryView = findViewById(R.id.gallery_content);
        Bundle bundle = getIntent().getExtras();

        player = bundle.getParcelable("Player");
        username = bundle.getString("Username");
        qrCodeComments = bundle.getParcelableArrayList("QRArray");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gallery_recycler_view);
        GalleryAdapter adapter = new GalleryAdapter(qrCodeComments, username, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                QRCodeComment deleteCode = qrCodeComments.get(viewHolder.getAdapterPosition());

                DeleteQR(deleteCode);
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);
    }

    /**
     * Makes sure Profile is updated to any changes made in Gallery
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Gallery.this,MainActivity.class);;
        startActivity(intent);
    }

    /**
     * Delete QR from specific Player QRCodes Collection
     * Deletes Player from specific QrCodes Player Collection
     * @param qrCodeComment
     */
    private void DeleteQR(QRCodeComment qrCodeComment){
        QRCode deleteQR = qrCodeComment.getQrCode();
        Database deleteDB = new Database();
        // remove from local database
        ArrayList<QRCode> changeList = player.getQrCodes();
        changeList.remove(deleteQR);
        player.setQrCodes(changeList);

        qrCodeComments.remove(qrCodeComment);

        // remove from firestore
        deleteDB.removeQrCodesFromPlayer(username,deleteQR.getHash());
        deleteDB.removePlayerFromQRCode(username,deleteQR.getHash());
    }
}