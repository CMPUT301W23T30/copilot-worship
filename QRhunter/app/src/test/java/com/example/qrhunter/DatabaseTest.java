package com.example.qrhunter;

//DO NOT RUN THIS

import static org.junit.Assert.assertEquals;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.firebase.database.*;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
/*
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

 */

//@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(JUnit4.class)
//@PrepareForTest({ Database.class})
public class DatabaseTest {

    /**
     * Holds all documents added to database
     * so we can clear it from the database once we are done
     */
    final String TAG = "Database Testing";
    ArrayList<String> documents;
    Database db = new Database();
    /**
     *  Test to see if we can search a Player from the username
     */
    @Test
    public void testGettingPlayerFromUsername(){
        Player p = new Player();
        p.setUsername("qwertyuiopasdfghjklzxcvbnm"); //Invalid username
        db.addPlayer(p)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        documents.add(p.getUsername());
                        db.getPlayerFromUsername(p.getUsername())
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                            assertEquals(p.getUsername(), doc.getId());
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,
                                                "Failed to aquire profile: " + e.getMessage());
                                    }
                                })
                        ;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to put in new profile : " + e.getMessage());
                        testGettingPlayerFromUsername();
                    }
                })
        ;
    }

    @Test
    public void testAddScannedCode(){
        Player p = new Player();
        QRCode qr = new QRCode("name", "location", 123);
        db.addPlayer(p);
        db.addQrCode(qr);
        HashMap<String, Task<Void>> tasks = db.addScannedCode(qr, p);

    }

    /**
     * Clears all added documents to the database
     */
    @After
    public void clearDatabase(){
        for(String p : documents){
            db.removePlayer(p);
        }
    }
}
