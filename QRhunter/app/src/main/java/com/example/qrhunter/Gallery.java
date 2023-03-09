package com.example.qrhunter;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {
    private ArrayAdapter<GalleryAdapter> galleryAdapter;
    private Iterable<DocumentSnapshot> userQRList;
    private ArrayList<QRCode> qrCodeArrayList = new ArrayList<QRCode>();
    private String username;
    private ListView galleryView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        galleryView = findViewById(R.id.gallery_content);
        username = getIntent().getStringExtra("Player ID");

        CollectionReference codeCollection = db.collection("QrCodes");
        CollectionReference userCollection = db.collection("Players").document(username).collection("QRCodes");

        // Comparing User Collection of QRs to QrCodes Collection
        userCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // grab list of QRs collected by User
                        // ignore empty documents
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> userQRList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot userQRDoc : userQRList) {

                                codeCollection.get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                // grab list of existing QRs to compare
                                                // ignore empty documents
                                                if(!queryDocumentSnapshots.isEmpty()){
                                                    List<DocumentSnapshot> codeList = queryDocumentSnapshots.getDocuments();
                                                    for (DocumentSnapshot codeDoc : codeList) {

                                                        if (userQRDoc.getId().equals(codeDoc.getId())){

                                                            Location location = new Location("");
                                                            location.setLongitude((Double) codeDoc.get("longitude"));
                                                            location.setLatitude((Double) codeDoc.get("latitude"));
                                                            Log.d("LOCATION", "GOT IT");
                                                            QRCode qrCode = new QRCode(
                                                                    codeDoc.get("hash").toString(),
                                                                    codeDoc.get("name").toString(),
                                                                    location,
                                                                    Integer.parseInt(codeDoc.get("score").toString()));

                                                            // after getting data from Firebase we are
                                                            // storing that data in our array list
                                                            qrCodeArrayList.add(qrCode);
                                                        }
                                                    }
                                                    Log.d("ADAPTER", "SEEN");
                                                    GalleryAdapter adapter = new GalleryAdapter(Gallery.this,qrCodeArrayList);
                                                    galleryView.setAdapter(adapter);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}