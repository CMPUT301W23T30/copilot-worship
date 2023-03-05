package com.example.qrhunter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the database the app uses
 */
public class Database {
    /**
     * db a firestore database
     */
    private final FirebaseFirestore db;
    private CollectionReference playersCollection;
    private CollectionReference qrCodeCollection;
    final String TAG = "Database";


    /**
     * Default constructor for Database
     */
    public Database(){
        db = FirebaseFirestore.getInstance();
        playersCollection = db.collection("Players");
        qrCodeCollection = db.collection("QrCodes");
    }



//TODO are you SURE we cant just return a player?
    /**
     * returns task of querysnapshot for finding player
     * @param user Username of player to be found
     * @return Task of Query with the result
     */
    public Task<QuerySnapshot> getPlayerFromUsername(String user){
        return playersCollection
                .whereEqualTo("username", user)
                .get();
    }

    //TODO see if we should return a task here as well
    public Task<Void> addPlayer(Player p) {
        //TODO Do QR Codes
        Map<String, Object> playerInfo = new HashMap<>();
        playerInfo.put("email", p.getEmail());
        playerInfo.put("number", p.getNumber());
        playerInfo.put("username", p.getUsername());

        return playersCollection
                .document(p.getUsername())
                .set(playerInfo);
    }

    //TODO see if we should return task
    public Task<Void> removePlayer(Player p){
        return playersCollection
                .document(p.getUsername())
                .delete();
    }
    public Task<Void> removePlayer(String p){
        return playersCollection
                .document(p)
                .delete();

    }


}
