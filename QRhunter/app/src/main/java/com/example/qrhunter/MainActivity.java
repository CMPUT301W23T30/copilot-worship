package com.example.qrhunter;

import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        //Throwaway prototype to test database
        Database db = new Database();
        Player p = new Player();
        db.addPlayer(p);
        db.getPlayerFromUsername(p.getUsername())
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            /*
                              Anything that needs the result of the query
                              * must be in here, else since its async we might not be able to
                              * access it...
                             */
                            //We are only expecting one return here
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                System.out.println("username: " + doc.getId());
                            }
                        }
                        else {
                            Log.d("Database Error", "Could not retrieve" +
                                    p.getUsername() + " Error: " + task.getException());


                        }
                    }
                });

    }
}