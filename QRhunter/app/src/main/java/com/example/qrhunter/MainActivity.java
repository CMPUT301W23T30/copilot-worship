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
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.Collections;


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

    TextView beefyQR;
    TextView squishyQR;
    TextView userEmail;
    TextView userPhone;

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
//        switch (item.getItemId()) {
//            case R.id.menu_add_player_button:
//                Intent intent = new Intent(this, AddPlayerActivity.class);
//
//                Bundle b = new Bundle();
//                b.putString("username", username);
//                intent.putExtras(b);
//                startActivity(intent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return true;
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

    public void getRankingEstimates(Bundle bundle, Database db){
        if(bundle != null){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView profileCircle = (ImageView) findViewById(R.id.profile_icon);
        TextView userText = findViewById(R.id.user_page_user_name);
        TextView smallerTextView = findViewById(R.id.user_page_total_score);

        CharacterImage testCharacter = characterCreator("2CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824");
        profileCircle.setImageBitmap(testCharacter.getCharacterImage());

        smallerTextView.setText("Click on the monster to generate a new one!!!!");

        // profileCircle.setImageResource(R.drawable._icon__profile_circle_);

        TextView userText = findViewById(R.id.user_page_user_name);
        Bundle bundle = getIntent().getExtras();
        Database db = new Database();
        //db.populateDB(); Run only when we need to redo db after a purge
        //db.populateScore(20);// Run only after populate db
        getUsername(bundle, db, userText);

        // Player Information
        TextView totalScore = findViewById(R.id.user_page_total_score);
        TextView beefyQR = findViewById(R.id.user_page_strongest);
        TextView squishyQR = findViewById(R.id.user_page_weakest);
        TextView userEmail = findViewById(R.id.user_page_email);
        TextView userPhone = findViewById(R.id.user_page_phone);

        db.getPlayerContact(username, new PlayerContactListener() {
            @Override
            public void playerContactCallback(Bundle bundle) {
                userEmail.setText(bundle.getString("email"));
                userPhone.setText(bundle.getString("number"));
            }
        });
        //TODO change back to username
        db.getPlayerStats(username, new PlayerStatsListener() {

            @Override
            public void playerStatsCallback(Bundle bundle) {
                Integer total = 0;
                ArrayList<Integer> qrScore = new ArrayList<>();

                for (String hash : bundle.getStringArrayList("HashList")){
                    Integer score = scoreCalculator(hash);
                    qrScore.add(score);
                    total = total + score;
                }

                // Leave as default N/A if no QRs in Player collection
                if (total != 0) {
                    totalScore.setText(String.valueOf(total));
                    beefyQR.setText(String.valueOf(Collections.max(qrScore)));
                    squishyQR.setText(String.valueOf(Collections.min(qrScore)));
                }

            }
        });

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

        profileCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharacterImage testCharacter = characterCreator("2CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824");
                profileCircle.setImageBitmap(testCharacter.getCharacterImage());
                smallerTextView.setText(generateRandomName());
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
        Log.d("HASHER", "Unhashed: " + unhashedQRCode);
        Log.d("HASHER", "Hash: " + hashed);
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
        Log.d("CALCULATOR", "Hash: " + hashedQRCode);
        Log.d("CALCULATOR", "Score: " + score);
        return score;

    }

    public String generateRandomName() {

        final ArrayList<String> adjectivesList = new ArrayList<String>();
        final ArrayList<String> nounsList = new ArrayList<String>();

        InputStream adjectivesFile = getResources().openRawResource(R.raw.english_adjectives);
        InputStream nounsFile = getResources().openRawResource(R.raw.nouns_list);

        Scanner adjectivesScanner = new Scanner(adjectivesFile);
        Scanner nounsScanner = new Scanner(nounsFile);

        while (adjectivesScanner.hasNextLine()) {
            String word = adjectivesScanner.nextLine();
            adjectivesList.add(word);
        }
        adjectivesScanner.close();

        while (nounsScanner.hasNextLine()) {
            String word = nounsScanner.nextLine();
            nounsList.add(word);
        }
        nounsScanner.close();

        Random rand = new Random();

        String randomAdjective = adjectivesList.get(rand.nextInt(adjectivesList.size()));
        randomAdjective = randomAdjective.substring(0, 1).toUpperCase() + randomAdjective.substring(1);
        String randomNoun = nounsList.get(rand.nextInt(nounsList.size()));

        return randomAdjective + " " + randomNoun;

    }

    private CharacterImage characterCreator(String hashedQRCode) {
        String armsFileName, legsFileName, eyesFileName, mouthFileName, hatFileName;

        BigInteger hashedQRCodeBigInt = new BigInteger(hashedQRCode, 16);
        String firstSixDigitsString = hashedQRCodeBigInt.toString().substring(0, 6);

//        armsFileName = "arms" + firstSixDigitsString.substring(0, 1);
//        legsFileName = "legs" + firstSixDigitsString.substring(1, 2);
//        eyesFileName = "eyes" + firstSixDigitsString.substring(2, 3);
//        mouthFileName = "mouth" + firstSixDigitsString.substring(3, 4);
//        hatFileName = "hat" + firstSixDigitsString.substring(4, 5);

        armsFileName = "arms" + (int) (Math.random() * 10);
        legsFileName = "legs" + (int) (Math.random() * 10);
        eyesFileName = "eyes" + (int) (Math.random() * 10);
        mouthFileName = "mouth" + (int) (Math.random() * 10);
        hatFileName = "hat" + (int) (Math.random() * 10);

        CharacterImage testCharacter = new CharacterImage(this, armsFileName, legsFileName, eyesFileName, mouthFileName, hatFileName, firstSixDigitsString);

        return testCharacter;
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

            String name = generateRandomName();

            builder.setMessage(name + " " + score + " points");

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
                    Log.d("ADDQR", "Hash: " + hashedCode);
                    Log.d("ADDQR", "Score: " + score);
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
