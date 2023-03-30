package com.example.qrhunter;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
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
    private QuerySnapshot playerSnapshotResult;
    private Task<QuerySnapshot> playerSnapshot;


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
     * Gets Player information from username
     * @param username Username of player whose info is being asked for
     * @param callback Listener
     */
    public void getPlayerInfo(String username, final PlayerInfoListener callback){
        getPlayerFromUsername(username).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Player player;
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        player = new Player(
                                username,
                                document.get("email").toString(),
                                Integer.parseInt(document.get("number").toString()),
                                new ArrayList<>()
                        );

                        callback.playerInfoCallback(player);
                    }
                } else {
                    Log.d("DATABASE", "Error getting document: ", task.getException());
                }
            }
        });
    }

    /**
     * Gets an array of the Player's collection of QR codes hashes
     * @param username Username of the player
     * @param callback Listener for player info from database
     */
    public void getPlayerCollection(String username, final PlayerCollectionListener callback){
        playersCollection.document(username).collection("QRCodes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                Map<String, String> qrMap = new HashMap<String, String>();
                for (DocumentSnapshot doc : value.getDocuments()) {
                    String hash = doc.get("hash").toString();
                    String comment = "";

                    if (doc.get("comment") == null){
                        Log.d("DATABASE", "NO COMMENT");
                        comment = "";
                    }else{
                        Log.d("DATABASE", "HAS COMMENT: " + doc.get("comment").toString());
                        comment = doc.get("comment").toString();
                    }

                    qrMap.put(hash, comment);
                }
                callback.playerCollectionCallback(qrMap);
            }
        });
    }

    /**
     * Gets QRCode Info to create a QR Code
     * @param hash Hash of the QR code
     * @param callback Listener for QR info from database
     */
    public void getQRCodeInfo (String hash, final QRCodeListener callback){
        qrCodeCollection.document(hash).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        Log.d("DATABASE", "IT EXISTS");
                        Location location = new Location("");
                        location.setLatitude((Double) doc.get("longitude"));
                        location.setLongitude((Double) doc.get("latitude"));
                        QRCode qrCode = new QRCode(
                                doc.get("hash").toString(),
                                doc.get("name").toString(),
                                location,
                                Integer.parseInt(doc.get("score").toString())
                        );
                        callback.qrCodeCallback(qrCode);
                    } else {
                        Log.d("DATABASE", "DOES NOT EXIST");
                    }
                } else {
                    Log.d("DATABASE", "TASK FAILED");
                }
            }
        });
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

        playerInfo.put("totalScore", player.getTotalScore());
        return playersCollection
                .document(player.getUsername())
                .set(playerInfo);
    }

    public Task<Void> changeInfo(Player player){
        WriteBatch batch = db.batch();
        batch.update(playersCollection.document(player.getUsername()),
                "email", player.getEmail());
        batch.update(playersCollection.document(player.getUsername()),
                "number", player.getNumber());
        return batch.commit();
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
        qrInfo.put("latitude", qrCode.getLocation().getLatitude());
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
     * @param username Username of the player to be found
     * @return Task of Query with the result
     */
    public Task<QuerySnapshot> getQrCodesFromPlayer(String username) {
        return playersCollection
                .document(username)

                .collection("QRCodes")
                .get();
    }

    public Task<DocumentSnapshot> getPlayer(String username) {
        return playersCollection
                .document(username)
                .get();
    }

    public Task<QuerySnapshot> getPlayerCollection(){
        return playersCollection.get();
    }

    public Task<QuerySnapshot> getPlayerCollectionTotalScore(){
        return playersCollection.orderBy("totalScore",Query.Direction.DESCENDING).get();
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
     * Assigns p1's QrCode to p2
     * @param p1 Player that is losing the qr
     * @param p2 Player that is receiving the qr
     * @param hash hash of qr
     * @return Task of type integer that has the updated score of p2
     */
    public Task<Integer> giveQRCode(String p1, String p2, String hash){
        return db.runTransaction(new Transaction.Function<Integer>() {
            @Nullable
            @Override
            public Integer apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot qr = transaction.get(qrCodeCollection.document(hash));
                DocumentSnapshot p2Snap = transaction.get(playersCollection.document(p2));
                HashMap<String, Object> qrInfo = new HashMap<>();
                HashMap<String, Object> playerInfo = new HashMap<>();
                playerInfo.put("player", p2);
                qrInfo.put("hash", hash);
                //p2 now has the qr code
                transaction.set(playersCollection.document(p2).
                        collection("QRCodes").document(hash), qrInfo);

                Long newScore = (Long) p2Snap.get("totalScore") + (Long) qr.get("score");
                //p2 now has an updated Score
                transaction.update(playersCollection.document(p2), "totalScore", newScore);
                //p1 now loses the qr
                transaction.delete(playersCollection.document(p1).
                        collection("QRCodes").document(hash));
                //qr forgets p1
                transaction.delete(qrCodeCollection.document(hash).collection("Players")
                        .document(p1));
                //qr remembers p2
                transaction.set(qrCodeCollection.document(hash).collection("Players").document(p2),
                        playerInfo);
                return newScore.intValue();
            }
        });
    }

    /**
     * Associates a QR Code with a Player
     * Assumptions:
     *   - QR Code and Player are already in the database
     *   - QRCode hash & score is correct
     * Also updates the players total score in the db
     * Returns a map of tasks for the caller to handle
     * @param qrCode qrcode to be added
     * @param player Player that scanned the qr code
     * @return A map with the tasks {associate, updateTotalScore}
     */
    public HashMap<String, Task<Void>> addScannedCode(@NonNull QRCode qrCode, @NonNull Player player){
        HashMap<String, Task<Void>> tasks = new HashMap<>();
        HashMap<String, Object> qrInfo = new HashMap<>();
        HashMap<String, Object> playerInfo = new HashMap<>();

        WriteBatch batch = db.batch();
        qrInfo.put("hash", qrCode.getHash());
        batch.set(playersCollection
                .document(player.getUsername())
                .collection("QRCodes")
                .document(qrCode.getHash()), qrInfo);


        tasks.put("updateTotalScore", playersCollection.document(player.getUsername()).update(
                "totalScore", FieldValue.increment(qrCode.getScore())
        ));


        playerInfo.put("username", player.getUsername());

        batch.set(qrCodeCollection
                        .document(qrCode.getHash())
                        .collection("Players")
                        .document(player.getUsername()), playerInfo);

        tasks.put("associate", batch.commit());
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

    /**
     *  Returns task of global top "n" qr codes
     * @param n
     * @return
     */
    public Task<QuerySnapshot> getTopNScores(int n){
        return qrCodeCollection
                .orderBy("score", Query.Direction.ASCENDING)
                .limit(n)
                .get();
    }

    //Test method for popualting DB MUST Call pOPULATE SCORE WHEN DONE
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
            QRCode qr = new QRCode(hashname,hashname, l, score);
            addQrCode(qr).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    String username = "Player-" + count;

                    addScannedCode(qr, new Player(username));
                }
            });

        }
    }

    //Get all the scores and stuff
    public void populateScore(int count){
        for(int i = 1; i <= count; i++ ){

            int finalI = i;
            playersCollection.document("Player-" + i)
                    .collection("QRCodes")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Player p = new Player("Player-" + finalI);
                            for(QueryDocumentSnapshot doc : queryDocumentSnapshots ){

                                QRCode qr = new QRCode(doc.getString("hash"), null, null, Integer.parseInt(doc.getString("hash")))
                                        ;


                                p.getQrCodes().add(qr);
                            }
                            addPlayer(p);
                        }
                    });
        }
}

}
