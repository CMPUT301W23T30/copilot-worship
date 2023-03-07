package com.example.qrhunter;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    final String TAG = "User Profile Page";
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Code for setting the profile circle (can be adapted for custom profile pics later)
        ImageView profileCircle = (ImageView) findViewById(R.id.default_profile_icon);
        profileCircle.setImageResource(R.drawable._icon__profile_circle_);


        TextView userText = findViewById(R.id.user_page_user_name);
        Bundle bundle = getIntent().getExtras();
        Database db = new Database();
        //https://stackoverflow.com/questions/10209814/saving-user-information-in-app-settings
        //Roughly following
        //TODO properly cite
        if(bundle != null){
            username = bundle.getString("username");
        }
        else{

            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            //If login info saved already
            if(settings.contains("Username")){
                username = settings.getString("Username", "");
            }
            //else we can assume a new player
            else{
                SharedPreferences.Editor editor = settings.edit();
                db.getPlayerCount()
                        .addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
                            @Override
                            public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
                                username = "Player-" + (aggregateQuerySnapshot
                                        .getCount() + 1);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //TODO add an error message here
                                Log.d(TAG, "Failed to get player count for new player");
                                username = "Player-?";
                            }
                        })
                ;

                editor.putString("Username", username);
                editor.apply();
                userText.setText(username);
                db.addPlayer(new Player(username));
            }
        }
        userText.setText(username);

        db.getPlayerFromUsername(username)
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