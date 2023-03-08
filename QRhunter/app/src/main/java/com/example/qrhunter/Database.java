package com.example.qrhunter;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the database the app uses
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
     * Returns a task of QuerySnapshot for finding a player in the QRCode collection
     * @param qrCode Name of the QRCode to be found
     * @return Task of Query with the result
     */
    public Task<QuerySnapshot> getPlayersFromQRCode(QRCode qrCode){
        return qrCodeCollection
                .document(qrCode.getHash())
                .collection("Players")
                .get()
        ;
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

    //Temp module for testing
    //Deletes entire db :( DO NOT RUN THIS METHOD WITHOUT TELLING PPL
    public static void destroy(){
        qrCodeCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots ){
                            doc.d
                        }
                    }
                })
    }

}
