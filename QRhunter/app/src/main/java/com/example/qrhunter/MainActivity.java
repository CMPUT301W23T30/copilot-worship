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

    public void testAddScannedCode() {

        for (int i = 0; i < 10; i++) {

            int randomScore = (int) Math.floor(Math.random() * 1001); // Generates random score
            int randomUsername = (int) Math.floor(Math.random() * 1001); // Generates random username (int for now)
            int randomQRname = (int) Math.floor(Math.random() * 1001); // Generates random QR name (int for now)

            Database db = new Database(); // Creates database
            Player testPlayer = new Player(String.valueOf(randomUsername)); // Creates player for testing
            QRCode testQRCode = new QRCode(String.valueOf(randomQRname), "location", randomScore); // Creates QRCode for testing
            db.addPlayer(testPlayer); // Adds player to database
            db.addQrCode(testQRCode); // Adds QRCode to database

            /*
              "tasks" is a hashmap containing two keys, "QrToPlayerCol" and "PlayerToQrCol",
              which are .set() commands that execute whenever you call their respective keys
              (hence the success and failure listeners)
             */
            HashMap<String, Task<Void>> tasks = db.addScannedCode(testQRCode, testPlayer);

            // Adds a QRCode to the players collection
            tasks.get("QrToPlayerCol")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Db Test", "qrCode -> playerCollection added successfully");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Db Test", "qrCode -> playerCollection Exception: " + e.getMessage());
                        }
                    });

            // Log statements for testing
            Log.d("Db Test", (String) tasks.keySet().toArray()[0]);
            Log.d("Db Test", (String) tasks.keySet().toArray()[1]);

            // Adds a player to the QRCode collection
            tasks.get("PlayerToQrCol")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Db Test", "player -> qrCollection added succesfully");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Db Test", "player -> qrCollection Exception: " + e.getMessage());
                        }
                    });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Code for setting the profile circle (can be adapted for custom profile pics later)
        ImageView profileCircle = (ImageView) findViewById(R.id.default_profile_icon);
        profileCircle.setImageResource(R.drawable._icon__profile_circle_);

        // Code for testing the database
        testAddScannedCode();
    }
}