package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This class is used to edit player information
 * It is called from the MainActivity class.
 * @author: Maarij
 */
public class AddPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText numberEditText = findViewById(R.id.numberEditText);
        Button submitButton = findViewById(R.id.submitButton);



        /*
        //Need to convert this to a bundle
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("number", number);

         */

        submitButton.setOnClickListener(v -> {
            //To update the player info, we need to delete
            // all the documents and re add it
            // To do that we have to reconstruct the player object for deletion and addition


            //TODO make sure new username is unique!
            //TODO maybe once add player can handle QR Codes, we might wanna revamp this
            Bundle bundle = getIntent().getExtras();
            String currentUserName = bundle.getString("username");
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String number = numberEditText.getText().toString();
            Player newUser = new Player(username,email, Integer.parseInt(number), new ArrayList<>());
            Database db = new Database();
            //TODO add on Failure listener
            db.removePlayer(currentUserName);
            db.addPlayer(newUser);
            //TODO add on failure listener
            //Need to delete the player from the qr too
            db.getQrCodesFromPlayer(currentUserName)
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                                        //TODO add failure listeners
                                        //Not sure what they can do since no rollback but atleast we can
                                        //let the user know there was a glitch
                                        String hash = doc.getString("hash");
                                        db.removePlayerFromQRCode(currentUserName, hash);
                                        db.addScannedCode(new QRCode(hash ,null, null, 0),
                                                newUser);
                                    }
                                    SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                    //Save this new username locally, how nice
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.clear();
                                    editor.putString("Username", username);
                                    editor.apply();
                                }
                            });
            finish();
        });
    }
}