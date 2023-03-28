package com.example.qrhunter;


import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Gallery Activity
 * Displays the QRCodes owned by a Player using GalleryAdapter
 * Currently displays the QRCodes Name, Location, and Score
 */
public class Gallery extends AppCompatActivity {
    private String username;
    TextView textView;
    ArrayAdapter<GalleryAdapter> galleryAdapter;
     ArrayList<QRCode> qrCodeArrayList = new ArrayList<QRCode>();
     ListView galleryView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Task<QuerySnapshot> querySnapshotTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        galleryView = findViewById(R.id.gallery_content);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        textView = findViewById((R.id.gallery_name));

        galleryView.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        galleryView.setClickable(true);
        galleryView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(Gallery.this, QrDisplayActivity.class));
                return false;
            }
        });
        CollectionReference codeCollection = db.collection("QrCodes");
        CollectionReference userCollection = db.collection("Players").document(username).collection("QRCodes");

        // compares Username to Player collection in QrCodes
        userCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // ignore empty documents
                        // grab list of QR codes in Gallery
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> userList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot userDoc : userList) {

                                // get QR info from Player QRCodes
                                codeCollection.get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                // ignore empty documents
                                                // find QR info from QrCodes
                                                if (!queryDocumentSnapshots.isEmpty()){
                                                    List<DocumentSnapshot> qrList = queryDocumentSnapshots.getDocuments();
                                                    for (DocumentSnapshot qrDoc : qrList){

                                                        if (qrDoc.getId().equals(userDoc.getId())) {

                                                            Location location = new Location("");
                                                            location.setLongitude((Double) qrDoc.get("longitude"));
                                                            location.setLatitude((Double) qrDoc.get("latitude"));
                                                            QRCode qrCode = new QRCode(
                                                                    qrDoc.get("hash").toString(),
                                                                    qrDoc.get("name").toString(),
                                                                    location,
                                                                    Integer.parseInt(qrDoc.get("score").toString()));

                                                            // after getting data from Firebase we are
                                                            // storing that data in our array list
                                                            qrCodeArrayList.add(qrCode);

                                                        }
                                                    }
                                                    GalleryAdapter adapter = new GalleryAdapter(Gallery.this,qrCodeArrayList);
                                                    galleryView.setAdapter(adapter);
                                                    galleryView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                                        //TODO make drop down button work w/ this
                                                        @Override
                                                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                            Intent intent = new Intent(Gallery.this, QrDisplayActivity.class);
                                                            Bundle b = new Bundle();
                                                            b.putString("hash", adapter.getItem(i).getHash());
                                                            intent.putExtras(b);
                                                            startActivity(intent);
                                                            return false;
                                                        }
                                                    });

                                                    // TODO Change to swipe later
                                                    galleryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                        @Override
                                                        public void onItemClick(AdapterView<?> qrCode, View view, int i, long id) {
                                                            DeleteQR(adapter.getItem(i).getHash());
                                                            galleryAdapter.notifyDataSetChanged();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    /**
     * Delete QR from specific Player QRCodes Collection
     * Deletes Player from specific QrCodes Player Collection
     * @param hash
     */
    private void DeleteQR(String hash){
        Database deleteDB = new Database();
        deleteDB.removeQrCodesFromPlayer(username,hash);
        deleteDB.removePlayerFromQRCode(username,hash);
    }
}