package com.example.qrhunter;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;


/**
 * User login page
 * @author Sean X
 *
 * Outstanding Issues
 * Move code from here that doesnt belong to proper classes
 * Properly Cite the method to store usernames
 */
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;
    //Tag for logging any issues
    final String TAG = "User Profile Page";
    private final GenerateQReatureAttributes generateQReatureAttributes = new GenerateQReatureAttributes(this);
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
                    ImageView profile = findViewById(R.id.profile_icon);
                    SharedPreferences settings = getSharedPreferences("profilePicture", 0);
                    byte[] data = null;
                    if(settings.getBoolean("saved", false)){
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) profile.getDrawable();
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        data = baos.toByteArray();
                    }
                    bundle.putByteArray("pictureBytes", data);
                    intent.putExtras(bundle);
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", "");
                    bundle.putString("email", "");
                    bundle.putString("phone", "");
                    byte[] noBytes = new byte[0];
                    bundle.putByteArray("pictureBytes", noBytes);
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
     *
     * @param bundle bundle of data that will include a username if is sent through another activity
     * @param db Database instance to query from
     * @param userText User text to set the username too
     * @return true if new player
     */
    public Boolean getUsername(Bundle bundle, Database db, TextView userText,
    ImageView profileCircle, TextView totalScoreTextView, TextView userEmailTextView, TextView
                               userPhoneTextView, TextView beefyQRTextView, TextView
                               squishyQRTextView) {
        //https://stackoverflow.com/questions/10209814/saving-user-information-in-app-settings
        //Roughly following
        if (bundle != null) {
            username = bundle.getString("username");
            userText.setText(username);
            setInfo(db, profileCircle, totalScoreTextView, userEmailTextView, userPhoneTextView, beefyQRTextView,
                    squishyQRTextView);
        } else {

            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            //If login info saved already
            if (settings.contains("Username")) {
                username = settings.getString("Username", "");
                userText.setText(username);
                setInfo(db, profileCircle, totalScoreTextView, userEmailTextView, userPhoneTextView, beefyQRTextView,
                        squishyQRTextView);
            }
            //else we can assume a new player
            else {
                SharedPreferences.Editor editor = settings.edit();
                db.getPlayerCount()
                        .addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    long count = (task.getResult().getCount()
                                            + 1);
                                    username = "Player-" + count;
                                    editor.putString("Username", username);
                                    editor.putString("id", username);
                                    System.out.println(username + "ID PUTTED");
                                    editor.apply();
                                } else {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setMessage("Could not register as a new user\nPlease restart the app and check wifi connection")
                                                    .show();
                                    Log.d(TAG, "Failed to get player count for new player");
                                    username = "Player-?";
                                }
                                userText.setText(username);
                                db.addPlayer(new Player(username))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                setInfo(db, profileCircle, totalScoreTextView, userEmailTextView, userPhoneTextView, beefyQRTextView,
                                                        squishyQRTextView);
                                            }
                                        });
                            }
                        });
                return true;
            }

        }
        return false;
    }

    /**
     * Sets the information of the player in the text views
     * @param db
     * @param profileCircle
     * @param totalScoreTextView
     * @param userEmailTextView
     * @param userPhoneTextView
     * @param beefyQRTextView
     * @param squishyQRTextView
     *
     */
    public void setInfo(Database db, ImageView profileCircle, TextView totalScoreTextView, TextView userEmailTextView, TextView userPhoneTextView, TextView beefyQRTextView, TextView squishyQRTextView){
        db.getPlayerInfo(username, new PlayerInfoListener() {
            @Override
            public void playerInfoCallback(Player player) {
                currentPlayer = player;
                userEmailTextView.setText(currentPlayer.getEmail());
                userPhoneTextView.setText(String.valueOf(currentPlayer.getNumber()));
                db.getProfilePicture(currentPlayer.getId()).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        //From https://stackoverflow.com/questions/7359173/create-bitmap-from-bytearray-in-android
                        //TODO Cite properly
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                        bmp = Bitmap.createScaledBitmap(bmp, profileCircle.getHeight(), profileCircle.getWidth(), true);
                        profileCircle.setImageBitmap(bmp);
                    }
                });
                db.getPlayerCollection(player.getId(), new PlayerCollectionListener() {
                    @Override
                    public void playerCollectionCallback(Map<String, String> map) {
                        for (Map.Entry<String, String> qrEntry : map.entrySet()) {
                            db.getQRCodeInfo(qrEntry.getKey(), new QRCodeListener() {
                                @Override
                                public void qrCodeCallback(QRCode qrCode) {
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //NO MORE DARK MODE
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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

        String testHash = "2CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824";
        String firstSixDigits = generateQReatureAttributes.getFirstSixDigits(testHash);
        //CharacterImage testCharacter = characterCreator(firstSixDigits);
        //profileCircle.setImageBitmap(testCharacter.getCharacterImage());


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
        //db.populateDB(); //Run only when we need to redo db after a purge
        //db.populateScore(20);// Run only after populate db
        getUsername(bundle, db, usernameText, profileCircle, totalScoreTextView,
                userEmailTextView, userPhoneTextView, beefyQRTextView, squishyQRTextView);

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
                Intent intent = new Intent(MainActivity.this, SearchPlayerActivity.class);
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
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(MainActivity.this, Gallery.class);
                bundle.putParcelable("Player", currentPlayer);
                bundle.putString("Username", username);
                bundle.putParcelableArrayList("QRArray",(ArrayList<? extends Parcelable>) qrCodeComments);
                intent.putExtras(bundle);
                Log.d("TASK", "STARTING GALLERY");
                startActivity(intent);
            }
        });

        addQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
                /**
                //Permission check for fine location
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                }
                Toast.makeText(MainActivity.this, "Start Scanning", Toast.LENGTH_SHORT).show();
                QRScan newClass = new QRScan();
                newClass.scanCode(barLauncher);
                 **/
            }
        });

        /*
        //Commented out cuz we have profile pictures now
        profileCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testHash = "2CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824";
                String firstSixDigits = getFirstSixDigits(testHash);
                CharacterImage testCharacter = characterCreator(firstSixDigits);
                profileCircle.setImageBitmap(testCharacter.getCharacterImage());
                smallerTextView.setText(generateRandomName(firstSixDigits));
            }
        });*/

    }

    /**
     * This method hashes the QR code to SHA256 and returns the hashed hex string
     * @param unhashedQRCode the unhashed QR code (String)
     * @return the hashed QR code (String)
     * @author Maarij
     */
    private String hasher(String unhashedQRCode) {
        return generateQReatureAttributes.hasher(unhashedQRCode);
    }

    /**
     * This method calculates the score of the QR code based on the number of contiguous repeated numbers or characters
     * @param hashedQRCode the hashed QR code (String)
     * @return the score of the QR code (int)
     * @author Maarij
     */
    private int scoreCalculator(String hashedQRCode) {
        // Find contiguous repeated numbers or characters in hex string
        // Each number or character is equal to number^(n-1) points where n is the number of times it is repeated

        return generateQReatureAttributes.scoreCalculator(hashedQRCode);
    }

    public String generateName(String firstSixDigits) {

        /*
        Random rand = new Random();
        String adjective = adjectivesList.get(rand.nextInt(adjectivesList.size()));
        adjective = randomAdjective.substring(0, 1).toUpperCase() + randomAdjective.substring(1);
        String noun = nounsList.get(rand.nextInt(nounsList.size()));
        */

        return generateQReatureAttributes.generateName(firstSixDigits);
    }

    private String getFirstSixDigits(String hashedQRCode) {
        return generateQReatureAttributes.getFirstSixDigits(hashedQRCode);
    }

    private CharacterImage characterCreator(String firstSixDigitsString) {
        return generateQReatureAttributes.characterCreator(firstSixDigitsString);
    }

    public void testThis(){
        Toast.makeText(MainActivity.this, "try this", Toast.LENGTH_SHORT).show();
    }

    public void startScan(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
        }
        Toast.makeText(MainActivity.this, "Start Scanning", Toast.LENGTH_SHORT).show();
        QRScan newClass = new QRScan(barLauncher);
        newClass.scanCode();
    }

    /**
     * Scan QR code
     */
    public ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {
        if (result.getContents() != null) {
            String hashedCode = generateQReatureAttributes.hasher(result.getContents()); // If this fails alert won't appear, makes it easier to test
            int score = generateQReatureAttributes.scoreCalculator(hashedCode);

            Toast.makeText(this, "make object", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");

            String testHash = generateQReatureAttributes.getFirstSixDigits(hashedCode);
            String name = generateQReatureAttributes.generateName(testHash);

            builder.setMessage("You found a " + name + " worth " + score + " points !" + "\nWould you like to keep it?");

            FusedLocationProviderClient fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(this);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Store Location")
                    .setMessage("Do you want to store the location of this Qreature?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
                            }
                            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    builder.setPositiveButton("Keep", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Log.d("ADDQR", "Hash: " + hashedCode);
                                            Log.d("ADDQR", "Score: " + score);

                                            QRCode one = new QRCode(hashedCode, generateQReatureAttributes.generateName(generateQReatureAttributes.getFirstSixDigits(hashedCode)), location, score);
                                            Database db = new Database();
                                            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                            String id = settings.getString("id", "no-id");
                                            System.out.println(id);
                                            db.getPlayer(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    System.out.println(documentSnapshot.get("totalScore"));
                                                    AddQR(one, documentSnapshot.get("totalScore").toString());
                                                    askAndTakePhoto(one);
                                                }
                                            });
                                        }
                                    })
                                            .setNegativeButton("Discard", null)
                                            .show();
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            builder.setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.d("ADDQR", "Hash: " + hashedCode);
                                    Log.d("ADDQR", "Score: " + score);

                                    QRCode one = new QRCode(hashedCode, generateQReatureAttributes.generateName(generateQReatureAttributes.getFirstSixDigits(hashedCode)), new Location(""), score);
                                    Database db = new Database();
                                    SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                                    String id = settings.getString("id", "no-id");
                                    db.getPlayer(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            AddQR(one, documentSnapshot.get("totalScore").toString());
                                            askAndTakePhoto(one);
                                        }
                                    });
                                }
                            })
                                    .setNegativeButton("Discard", null)
                                    .show();

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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
            Database db = new Database();
            String hash = getIntent().getStringExtra("CamHash");
            SharedPreferences setting = getSharedPreferences("UserInfo", 0);
            String id = setting.getString("id", "no-id");
            db.storeQRPicture(id, hash, image);
            //Reload to prevent duping
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            startActivity(i);

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
                        //Because we are in main we have to use main's intent
                        getIntent().putExtra("CamHash", one.getHash());
                        startActivityForResult(intent, 100);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Reload to prevent duping
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * This method adds a QR code to the database
     * @param newQR the QR code to be added
     */
    public void AddQR(QRCode newQR, String totalScore){
        Database db = new Database();
        SharedPreferences setting = getSharedPreferences("UserInfo", 0);
        String id = setting.getString("id", "no-id");
        db.getQRCountFromPlayer(id, newQR.hash).addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
            @Override
            public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
                //If player does not already have qr
                if(aggregateQuerySnapshot.getCount() == 0){
                    db.addQrCode(newQR);

                    db.addScannedCode(newQR, currentPlayer);
                    //Check to see if we think we moved up in the db, if first place or no score to beat
                    // Then refresh always with lessScore
                    SharedPreferences settings = getSharedPreferences("LocalLeaderboard", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    int lessScore = (Integer.parseInt(totalScore) - 1);
                    System.out.println(lessScore);
                    String scoretoBeat = settings.getString("scoreToBeat", lessScore + "");
                    System.out.println(scoretoBeat);
                    System.out.println(totalScore);
                    if(Integer.parseInt(scoretoBeat) < Integer.parseInt(totalScore)){
                        editor.putBoolean("playersSaved", false);
                        editor.commit();
                    }
                }
            }
        });


    }


}
