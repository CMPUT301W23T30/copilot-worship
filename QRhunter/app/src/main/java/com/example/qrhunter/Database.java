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


    /**
     * Default constructor for Database
     */
    public Database(){
        db = FirebaseFirestore.getInstance();
        playersCollection = db.collection("Players");
        qrCodeCollection = db.collection("QrCodes");
    }




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


    /**
     * Adds a player to the database
     * @param p : Player to add
     * @return Void Task of the player being added to the db
     */
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

    /**
     * Removes a player from the db
     * @param p username of player to remove
     * @return Void task of player being removed
     */
    public Task<Void> removePlayer(String p){
        return playersCollection
                .document(p)
                .delete();
    }

    public Task<QuerySnapshot> getPlayersFromQRCode(QRCode qrCode){
        return qrCodeCollection
                .document(qrCode.getName())
                .collection("Players")
                .get()
        ;
    }

    /**
     * Adds a Player and the scanned QR code to the database
     * Returns a map of tasks for the caller to handle
     * @param qrCode qrcode to be added
     * @param player Player that scanned the qr code
     * @return A map with the tasks {QrToPlayerCol, PlayerToQrCol}
     */
    public HashMap<String, Task<Void>> addScannedCode(@NonNull QRCode qrCode, @NonNull Player player){
        HashMap<String, Task<Void>> tasks = new HashMap<>();
        HashMap<String, Object> qrInfo = new HashMap<>();
        HashMap<String, Object> playerInfo = new HashMap<>();
        qrInfo.put("hash", qrCode.getName());
        tasks.put("QrToPlayerCol", playersCollection
                .document(player.getUsername())
                .collection("QRCodes")
                .document(qrCode.getName())
                .set(qrInfo));
        playerInfo.put("username", player.getUsername());
        tasks.put("PlayerToQrCol", qrCodeCollection
                .document(qrCode.getName())
                .collection("Players")
                .document(player.getUsername())
                .set(playerInfo));
        return tasks;
    }

    public Task<Void> addQrCode(@NonNull QRCode qrCode){
        HashMap<String, Object> qrInfo = new HashMap<>();
        qrInfo.put("hash", qrCode.getName());
        qrInfo.put("score",qrCode.getScore());
        qrInfo.put("location", qrCode.getLocation());
        return qrCodeCollection
                .document(qrCode.getName())
                .set(qrInfo);
    }


}
