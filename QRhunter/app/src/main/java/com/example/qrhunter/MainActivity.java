package com.example.qrhunter;

import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public void testAddScannedCode(){
        Database db = new Database();
        Player p = new Player();
        QRCode qr = new QRCode("name", "location", 123);
        db.addPlayer(p);
        db.addQrCode(qr);
        HashMap<String, Task<Void>> tasks = db.addScannedCode(qr, p);
        tasks.get("QrToPlayerCol")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Db Test", "qr -> player added succesfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Db Test", "qr -> player Exception: " + e.getMessage());
                    }
                });
        System.out.println(tasks.keySet().toArray()[0]);
        System.out.println(tasks.keySet().toArray()[1]);
        tasks.get("PlayerToQrCol")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Db Test", "player -> qr added succesfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Db Test", "player -> qr Exception: " + e.getMessage());
                    }
                });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start populate xml
        ImageView profileCircle = (ImageView) findViewById(R.id.default_profile_icon);
        //in the future if we want to add profile pictures
        profileCircle.setImageResource(R.drawable._icon__profile_circle_);
        //throwaway to test add scanned code
        testAddScannedCode();
    }
}