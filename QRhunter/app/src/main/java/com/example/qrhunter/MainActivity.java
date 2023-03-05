package com.example.qrhunter;

import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start populate xml
        ImageView profileCircle = (ImageView) findViewById(R.id.default_profile_icon);
        //in the future if we want to add profile pictures
        profileCircle.setImageResource(R.drawable._icon__profile_circle_);


        FirebaseFirestore db;

        db = FirebaseFirestore.getInstance();

        final CollectionReference collectionReference = db.collection("Cities");

        HashMap<String, String> data = new HashMap<>();
        data.put("Province Name", "lol");


        collectionReference
                .document("Lmao")
                .set(data);
    }
}