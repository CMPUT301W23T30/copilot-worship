package com.example.qrhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.data.DataBufferObserverSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * This class is used to add a new player to the database.
 * When it's complete it will eventually become the login screen.
 * It is called from the MainActivity class.
 * @author: Maarij
 */
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        TextView errorText = findViewById(R.id.errorText);


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


            //TODO maybe once add player can handle QR Codes, we might wanna revamp this
            Bundle bundle = getIntent().getExtras();
            String currentUserName = bundle.getString("username");
            String username = usernameEditText.getText().toString();
            if(username.length() > 20 || username.length() <= 0 ){
                errorText.setText("Username must be between 1-20 Characters");
                return;
            }
            String email = emailEditText.getText().toString();
            String number = numberEditText.getText().toString();
            Player newUser = new Player(username,email, Integer.parseInt(number), new ArrayList<>());
            Database db = new Database();
            //making Sure it is unique
            db.getPlayer(username).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists())
                    {
                        //Username is not unique, so it is invalid

                        errorText.setText("Username is taken dummmy");
                    }
                    else {
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
                                            db.addScannedCode(new QRCode(hash ,null, null, 0),
                                                    newUser);
                                            db.removePlayerFromQRCode(currentUserName, hash);

                                        }
                                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                        //Save this new username locally, how nice
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.clear();
                                        editor.putString("Username", username);
                                        editor.apply();
                                        Intent intent = new Intent(AddPlayerActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        //finish();
                    }
                }
            });

        });
    }
}