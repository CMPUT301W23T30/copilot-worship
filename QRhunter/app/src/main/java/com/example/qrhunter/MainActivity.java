package com.example.qrhunter;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
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

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
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
    Player currentPlayer;
    String username;
    ArrayList<QRCodeComment> qrCodeComments = new ArrayList<>();

    ArrayList<String> qrList = new ArrayList<>();
    TextView beefyQRTextView;
    TextView squishyQRTextView;
    TextView userEmailTextView;
    TextView userPhoneTextView;
    Bitmap image;

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

                Intent intent = new Intent(this, AddPlayerActivity.class);
                if (currentPlayer != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", currentPlayer.getUsername());
                    bundle.putString("email", currentPlayer.getEmail());
                    bundle.putString("phone", String.valueOf(currentPlayer.getNumber()));
                    intent.putExtras(bundle);
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", "");
                    bundle.putString("email", "");
                    bundle.putString("phone", "");
                    intent.putExtras(bundle);
                }

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

    public void getRankingEstimates(Bundle bundle, Database db){
        if(bundle != null){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageView profileCircle = (ImageView) findViewById(R.id.profile_icon);
        TextView usernameText = findViewById(R.id.user_page_user_name);
        TextView smallerTextView = findViewById(R.id.user_page_total_score);
        TextView totalScoreTextView = findViewById(R.id.user_page_total_score);
        TextView beefyQRTextView = findViewById(R.id.user_page_strongest);
        TextView squishyQRTextView = findViewById(R.id.user_page_weakest);
        TextView userEmailTextView = findViewById(R.id.user_page_email);
        TextView userPhoneTextView = findViewById(R.id.user_page_phone);

        CharacterImage testCharacter = characterCreator("2CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824");
        profileCircle.setImageBitmap(testCharacter.getCharacterImage());

        smallerTextView.setText("Click on the monster to generate a new one!!!!");

        // profileCircle.setImageResource(R.drawable._icon__profile_circle_);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            bundle.getString("username");
            bundle.getString("email");
            bundle.getString("phone");

            usernameText.setText(bundle.getString("username"));
            userEmailTextView.setText(bundle.getString("email"));
            userPhoneTextView.setText(bundle.getString("phone"));
        }

        Database db = new Database();
        //db.populateDB(); Run only when we need to redo db after a purge
        //db.populateScore(20);// Run only after populate db
        getUsername(bundle, db, usernameText);

        // Player Information
        db.getPlayerInfo(username, new PlayerInfoListener() {
            @Override
            public void playerInfoCallback(Player player) {
                Log.d("TASK", "START");
                currentPlayer = player;
                userEmailTextView.setText(currentPlayer.getEmail());
                userPhoneTextView.setText(String.valueOf(currentPlayer.getNumber()));
                db.getPlayerCollection(player.getUsername(), new PlayerCollectionListener() {
                    @Override
                    public void playerCollectionCallback(Map<String, String> map) {
                        for (Map.Entry<String,String> qrEntry : map.entrySet()) {
                            db.getQRCodeInfo(qrEntry.getKey(), new QRCodeListener() {
                                @Override
                                public void qrCodeCallback(QRCode qrCode) {
                                    Log.d("TASK", "." + qrEntry.getKey() +".");
                                    currentPlayer.addQrCode(qrCode);
                                    qrCodeComments.add(new QRCodeComment(qrCode, qrEntry.getValue()));


                                    if (currentPlayer.getTotalScore() != 0) {
                                        totalScoreTextView.setText(String.valueOf(currentPlayer.getTotalScore()));
                                        beefyQRTextView.setText(String.valueOf(currentPlayer.getBeefy()));
                                        squishyQRTextView.setText(String.valueOf(currentPlayer.getSquishy()));
                                    }
                                }
                            });
                        }
                    }
                });
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

        rankingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LeaderboardActivity.class);
                Bundle b = new Bundle();
                b.putString("username", username);
                intent.putExtras(b);
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
                bundle.putParcelable("Player", currentPlayer);
                bundle.putParcelableArrayList("QRArray",(ArrayList<? extends Parcelable>) qrCodeComments);
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

                    QRCode one = new QRCode(hashedCode, hashedCode, l,score);

                    askAndTakePhoto(one);
                }
            }).show();
        }
    });

    /**
     * Take Real World photos
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100){
            image = (Bitmap) data.getExtras().get("data");
        }
    }

    /**
     * Asks whether the users want to take a photo
     * If Yes, then starts Taking Photo
     */
    public void askAndTakePhoto(QRCode one){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add Object Photo")
                .setMessage("Do you want to take a Photo of the real object?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 100);

                        /**
                         * need to add image (Bitmap object) to one (QRCode object)
                         */


                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

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
