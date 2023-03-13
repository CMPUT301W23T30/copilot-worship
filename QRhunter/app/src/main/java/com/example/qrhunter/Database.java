package com.example.qrhunter;

import android.location.Location;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the database the app uses
 *
 *  Outstanding issues:
 *      - TODO integrate proper deletion for player nad qr c
 *        ie remove player from the qr collection once player deleted
 *
 */
public class Database {

    // Initialize database and collections
    private final FirebaseFirestore db;
    private CollectionReference playersCollection;
    private CollectionReference qrCodeCollection;


    // Default constructor for Database, creates a new instance of the database and collections
    public Database(){
        db = FirebaseFirestore.getInstance();
        playersCollection = db.collection("Players");
        qrCodeCollection = db.collection("QrCodes");
    }


    /**
     * Returns task of QuerySnapshot for finding player
     * @param username Username of player to be found
     * @return Task of Query with the result
     */
    public Task<QuerySnapshot> getPlayerFromUsername(String username){
        return playersCollection
                .whereEqualTo("username", username)
                .get();
    }


    /**
     * Adds a player to the database
     * @param player : Player to add
     * @return Void Task of the player being added to the database
     */
    public Task<Void> addPlayer(Player player) {
        Map<String, Object> playerInfo = new HashMap<>();
        playerInfo.put("email", player.getEmail());
        playerInfo.put("number", player.getNumber());
        playerInfo.put("username", player.getUsername());

        return playersCollection
                .document(player.getUsername())
                .set(playerInfo);
    }

    /**
     * Removes a player from the database
     * @param player username of player to remove
     * @return Void task of player being removed
     */
    public Task<Void> removePlayer(String player){
        return playersCollection
                .document(player)
                .delete();

    }

    /**
     * Returns a task of QuerySnapshot for finding all the players associated with a qr code
     * @param hash Name of the QRCode to be found
     * @return Task of Query with the result
     */
    public Task<QuerySnapshot> getPlayersFromQRCode(String hash){
        return qrCodeCollection
                .document(hash)
                .collection("Players")
                .get()
        ;
    }

    /**
     * Deletes Player from a QR Code's Players collection
     * @param username
     * @param hash
     */
    public void removePlayerFromQRCode(String username, String hash){
        qrCodeCollection
                .document(hash)
                .collection("Players")
                .document(username)
                .delete();
    }

    /**
     * Adds a QR code to the database
     * @param qrCode QR code to be added
     * @return Void task of qr code being added
     */
    public Task<Void> addQrCode(@NonNull QRCode qrCode){
        HashMap<String, Object> qrInfo = new HashMap<>();
        qrInfo.put("hash", qrCode.getHash());
        qrInfo.put("score",qrCode.getScore());
        qrInfo.put("longitude", qrCode.getLocation().getLongitude());
        qrInfo.put("latitude", qrCode.getLocation().getLongitude());
        qrInfo.put("name", qrCode.getName());
        return qrCodeCollection
                .document(qrCode.getHash())
                .set(qrInfo);
    }

    /**
     * Retrieves Qr Codes
     * @param hash hash of qr code to be found
     * @return
     */
    public Task<QuerySnapshot> getQr(String hash){
        return qrCodeCollection
                .whereEqualTo("hash", hash)
                .get();
    }

    /**
     * Removes a QR code from the database
     * @param qrCode QR code to be removed
     * @return Void task of qr code being removed
     */
    public Task<Void> removeQrCode(@NonNull QRCode qrCode) {
        return qrCodeCollection
                .document(qrCode.getHash())
                .delete();
    }

    /**
     * Returns a task of QuerySnapshot for finding a QR code in the player collection
     * @param player Username of the player to be found
     * @return Task of Query with the result
     */
    public Task<QuerySnapshot> getQrCodesFromPlayer(Player player) {
        return playersCollection
                .document(player.getUsername())
                .collection("QRCodes")
                .get();
    }

    /**
     * Removes specified QR from Player QRCode Collection
     * @param userName
     * @param hash
     */
    public void removeQrCodesFromPlayer(String userName, String hash){
        playersCollection
                .document(userName)
                .collection("QRCodes")
                .document(hash)
                .delete();
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
        qrInfo.put("hash", qrCode.getHash());
        tasks.put("QrToPlayerCol", playersCollection
                .document(player.getUsername())
                .collection("QRCodes")
                .document(qrCode.getHash())
                .set(qrInfo));
        playerInfo.put("username", player.getUsername());
        tasks.put("PlayerToQrCol", qrCodeCollection
                .document(qrCode.getHash())
                .collection("Players")
                .document(player.getUsername())
                .set(playerInfo));
        return tasks;
    }

    /**
     * get number of players
     * @return Aggregate task that gets the player count
     */
    public Task<AggregateQuerySnapshot> getPlayerCount(){
        return qrCodeCollection.count()
                .get(AggregateSource.SERVER);
    }
    //Test method for popualting DB
    //TODO DELETE THIS
    public void populateDB(){
        populatePlayer(20, 20);
    }
    public void populatePlayer(int count, int count2){
        if(count == 0){
            return;
        }
        String username = "Player-" + count;
        Player p = new Player(username);
        addPlayer(p).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                populateQR(count, count2 * 5);
                populatePlayer(count -1, count2);
            }
        });
    }

    public void populateQR(int count, int count2){
        int numCodes = (int) Math.floor(Math.random() * 5);
        for(int i = 0; i < numCodes; i++){
            Location l = new Location("");
            //Incomplete but acceptable locations
            l.setLongitude(Math.random() * 180);
            l.setLatitude(Math.random() * 90);
            int score = (int) Math.floor(Math.random() * count2);
            String hashname = "" + score;
            QRCode qr = new QRCode(hashname,hashname, l, score );
            addQrCode(qr).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    String username = "Player-" + count;
                    addScannedCode(qr, new Player(username));
                }
            });

        }
    }



}
