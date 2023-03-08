package com.example.qrhunter;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {
    private ArrayAdapter<GalleryAdapter> galleryAdapter;
    private ArrayList<QRCode> qrCodeArrayList = new ArrayList<QRCode>();
    private ListView galleryView;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        galleryView = findViewById(R.id.gallery_content);

        // temp for now to see all qrcodes, will change to see only the selected profile's qr codes
        db.collection("QrCodes").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // ignore empty documents
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {

                                QRCode qrCode = new QRCode(
                                        d.get("hash").toString(),
                                        d.get("location").toString(),
                                        Integer.parseInt(d.get("score").toString()));

                                // after getting data from Firebase we are
                                // storing that data in our array list
                                qrCodeArrayList.add(qrCode);
                            }
                            GalleryAdapter adapter = new GalleryAdapter(Gallery.this,qrCodeArrayList);
                            galleryView.setAdapter(adapter);
                        }
                    }
                });


    }
}