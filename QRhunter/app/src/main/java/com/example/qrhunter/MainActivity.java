package com.example.qrhunter;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.Hashing;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * User login page
 * @author Sean X
 *
 * Outstanding Issues
 * Move code from here that doesnt belong to proper classes
 * Properly Cite the method to store usernames
 */
public class MainActivity extends AppCompatActivity {
    //Tag for logging any issues
    final String TAG = "User Profile Page";
    String username;



    Button scanButton;
    Button photoButton;

    ImageButton mapButton;
    ImageButton galleryButton;
    ImageButton addQRButton;
    ImageButton searchButton;
    ImageButton rankingButton;


    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu
     * @return true
     * @author: Maarij
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_player, menu);
        return true;
    }

    /**
     * Handles action bar item clicks here. Starts the AddPlayerActivity when the add player button is clicked.
     * @param item
     * @return true if the button is clicked, false otherwise
     * @author: Maarij
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_player_button:
                // Intent intent = new Intent(this, AddPlayerActivity.class);
                Intent intent = new Intent(this, TestImageActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles doing the players username according to the following specs
     *
     * - If new user, assign a unique username then save it to phone
     * - If old user, set the name to the saved username on the phone
     * - If this is just displaying a user (that is a username was sent through a bundle
     * then just display the user)
     *
     * TODO have this return whether the user is the current user or not so we can re use this for
     * other things apart from the main page
     *
     * @param bundle bundle of data that will include a username if is sent through another activity
     * @param db Database instance to query from
     * @param userText User text to set the username too
     */
    public void getUsername(Bundle bundle, Database db, TextView userText){
        //https://stackoverflow.com/questions/10209814/saving-user-information-in-app-settings
        //Roughly following
        //TODO properly cite
        if(bundle != null){
            username = bundle.getString("username");
            userText.setText(username);
        }
        else{

            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            //If login info saved already
            if(settings.contains("Username")){
                username = settings.getString("Username", "");
                userText.setText(username);
            }
            //else we can assume a new player
            else{
                SharedPreferences.Editor editor = settings.edit();
                db.getPlayerCount()
                        .addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    username = "Player-" + (task.getResult().getCount()
                                            + 1);
                                }
                                else{
                                    //TODO add an error message here
                                    Log.d(TAG, "Failed to get player count for new player");
                                    username = "Player-?";
                                }
                                editor.putString("Username", username);
                                editor.apply();
                                userText.setText(username);
                                db.addPlayer(new Player(username));
                            }
                        })
                ;

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start populate xml
        ImageView profileCircle = (ImageView) findViewById(R.id.profile_icon);
        //in the future if we want to add profile pictures

        CharacterImage testCharacter = new CharacterImage(this, 120, "arms1", "legs1", "eyes1", "mouth1", "hat1");
        profileCircle.setImageBitmap(testCharacter.getCharacterImage());

        // profileCircle.setImageResource(R.drawable._icon__profile_circle_);


        TextView userText = findViewById(R.id.user_page_user_name);
        Bundle bundle = getIntent().getExtras();
        Database db = new Database();
        //db.populateDB(); Run only when we need to redo db after a purge
        getUsername(bundle, db, userText);



//        photoButton = findViewById(R.id.Photo_Button);
//        photoButton.setOnClickListener(v -> {
//            Toast.makeText(MainActivity.this, "Open Camera", Toast.LENGTH_SHORT).show();
//            PhotoTake newClass = new PhotoTake();
//            newClass.takePhoto();
//        });

        // NAVBAR Buttons
        mapButton = findViewById(R.id.navbar_map_button);
        galleryButton = findViewById(R.id.navbar_gallery_button);
        addQRButton = findViewById(R.id.navbar_add_button);
        searchButton = findViewById(R.id.navbar_search_button);
        rankingButton = findViewById(R.id.navbar_ranking_button);

        /**
         * onClick Actions for Navbar
         *
         * TODO Add in Intents for Camera (addQRButton), Search, and Rankings
         * TODO Disable buttons until username is not null as some activities require
         * the user username
         */

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchPlayerActivity.class);
                startActivity(intent);
            }
        });

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MainActivity.this,Gallery.class);
                bundle.putString("username", username);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        addQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                Intent intent = new Intent(MainActivity.this, CameraActivity.class);

                Toast.makeText(MainActivity.this, "Start Scanning", Toast.LENGTH_SHORT).show();
                QRScan newClass = new QRScan();
                newClass.scanCode(barLauncher);
            }
        });

    }

    /**
     * This method hashes the QR code to SHA256 and returns the hashed hex string
     * @param unhashedQRCode the unhashed QR code
     * @return the hashed QR code (String)
     * @author Maarij
     */
    private String hasher(String unhashedQRCode) {
        final String hashed = Hashing.sha256()
                .hashString(unhashedQRCode, StandardCharsets.UTF_8)
                .toString();
        return hashed;
    }

    /**
     * This method calculates the score of the QR code based on the number of contiguous repeated numbers or characters
     * @param hashedQRCode the hashed QR code
     * @return the score of the QR code (int)
     * @author Maarij
     */
    private int scoreCalculator(String hashedQRCode) {
        // Find contiguous repeated numbers or characters in hex string
        // Each number or character is equal to number^(n-1) points where n is the number of times it is repeated
        int score = 0;
        int n = 0;
        String prev = "";
        for (int i = 0; i < hashedQRCode.length(); i++) {
            if (hashedQRCode.substring(i, i + 1).equals(prev)) {
                n++;
            } else {
                if (n > 1) {
                    score += Math.pow(Integer.parseInt(prev, 16), n - 1);
                }
                n = 1;
                prev = hashedQRCode.substring(i, i + 1);
            }
        }

        return score;

    }

    /**
     * Scan QR code
     */
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
            String hashedCode = hasher(result.getContents()); // If this fails alert won't appear, makes it easier to test
            int score = scoreCalculator(hashedCode);

            Toast.makeText(this, "make object", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(score + " points");

            // Set Random Location for now
            Location l = new Location("");
            //Incomplete but acceptable locations
            l.setLongitude(Math.random() * 180);
            l.setLatitude(Math.random() * 90);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    AddQR(new QRCode(hashedCode, hashedCode, l,score));
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });

    /**
     * This method adds a QR code to the database
     * @param newQR the QR code to be added
     */
    public void AddQR(QRCode newQR){
        Database db = new Database();
        db.addQrCode(newQR);
        db.addScannedCode(newQR, new Player(username));
    }
}