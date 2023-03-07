package com.example.qrhunter;

import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;

public class UserPageActivity extends AppCompatActivity {
    final String TAG = "Login Page";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // Code for setting the profile circle (can be adapted for custom profile pics later)
        ImageView profileCircle = (ImageView) findViewById(R.id.default_profile_icon);
        profileCircle.setImageResource(R.drawable._icon__profile_circle_);


        TextView userText = findViewById(R.id.user_page_user_name);
        Bundle bundle = getIntent().getExtras();
        Database db = new Database();
        String username = bundle.getString("username");
        //If nothing passed into page, we can assume that the page is from the user opening it
        if(username == ""){

        }
        Log.d(TAG,username);
        db.getPlayerFromUsername(bundle.getString("username"))
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //TODO Make some kinda error screen
                                Log.d(TAG, "Could not load player from db" + e.getMessage());
                            }
                        })
                       .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                           @Override
                           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                               for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                   userText.setText(doc.getId());
                                   Log.d(TAG, doc.getId());
                               }
                               if(queryDocumentSnapshots.isEmpty()){
                                   Log.d(TAG, "Player not found");
                               }
                           }
                       });

    }
}