package com.example.qrhunter;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

public class MainActivity extends AppCompatActivity {
    final String TAG = "User Profile Page";
    String username;



    Button scanButton;
    Button photoButton;

    ImageButton mapButton;
    ImageButton galleryButton;
    ImageButton addQRButton;
    ImageButton searchButton;
    ImageButton rankingButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_player_button:
                Intent intent = new Intent(this, AddPlayerActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start populate xml
        ImageView profileCircle = (ImageView) findViewById(R.id.profile_icon);
        //in the future if we want to add profile pictures
        profileCircle.setImageResource(R.drawable._icon__profile_circle_);


        TextView userText = findViewById(R.id.user_page_user_name);
        Bundle bundle = getIntent().getExtras();
        Database db = new Database();
        //db.populateDB();
        //https://stackoverflow.com/questions/10209814/saving-user-information-in-app-settings
        //Roughly following
        //TODO properly cite
        if(bundle != null){
            username = bundle.getString("username");
        }
        else{

            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            //If login info saved already
            if(settings.contains("Username")){
                username = settings.getString("Username", "");
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
        userText.setText(username);

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
                Intent intent = new Intent(MainActivity.this,Gallery.class);
                startActivity(intent);
            }
        });

        addQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Start Scanning", Toast.LENGTH_SHORT).show();
                QRScan newClass = new QRScan();
                newClass.scanCode(barLaucher);
            }
        });

    }

    // TODO: to fix!!!
    private String hasher(String unhashedQRCode) {
        return sha256Hex(unhashedQRCode);
    }

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
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result->
    {
        if(result.getContents() !=null)
        {
//            String hashedCode = hasher(result.getContents()); // If this fails alert won't appear, makes it easier to test
//            int score = scoreCalculator(hashedCode);

            Toast.makeText(this, "make object", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    });
}