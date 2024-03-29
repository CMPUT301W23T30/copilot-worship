package com.example.qrhunter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * Other Profiles Activity
 * Shows the profile info of another Player
 */
public class OtherProfiles extends AppCompatActivity {
    private Player currentPlayer;
    private String currentUsername;

    /**
     * Sometimes the player can click on a null player from the leaderboard
     * This method handles that case
     */
    public void handleNullPlayer(){
        new AlertDialog.Builder(OtherProfiles.this)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences settings = getSharedPreferences("LocalLeaderboard", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("playersSaved", false);
                        editor.commit();
                        Intent intent = new Intent(OtherProfiles.this, LeaderboardActivity.class);
                        startActivity(intent);
                    }
                })
                .setMessage("Player " + currentUsername + " not found \nYour leaderboard might not be up to date \nDo you want to refresh it?")
                .show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profiles);

        Bundle bundle = getIntent().getExtras();
        currentUsername = bundle.getString("currentUsername");
        Database db = new Database();


        // Player Information
        TextView username = findViewById(R.id.other_player_user_name);
        TextView totalScore = findViewById(R.id.other_player_total_score);
        TextView beefyQR = findViewById(R.id.other_player_strongest);
        TextView squishyQR = findViewById(R.id.other_player_weakest);

        username.setText(currentUsername);

        //Check first to see if null
        db.getPlayerFromUsername(currentUsername).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                   handleNullPlayer();
                }else{
                    db.getPlayerInfo(currentUsername, new PlayerInfoListener() {
                        @Override
                        public void playerInfoCallback(Player player) {
                            Log.d("TASK", "START");
                            currentPlayer = player;
                            System.out.println(player.getId());
                            db.getProfilePicture(player.getId()).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    //From https://stackoverflow.com/questions/7359173/create-bitmap-from-bytearray-in-android
                                    ImageView profileCircle = findViewById(R.id.other_profile_icon);
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
                                    for (Map.Entry<String,String> qrEntry : map.entrySet()) {
                                        db.getQRCodeInfo(qrEntry.getKey(), new QRCodeListener() {
                                            @Override
                                            public void qrCodeCallback(QRCode qrCode) {
                                                Log.d("TASK", "." + qrEntry.getKey() +".");
                                                currentPlayer.addQrCode(qrCode);

                                                if (currentPlayer.getTotalScore() != 0) {
                                                    totalScore.setText(String.valueOf(currentPlayer.getTotalScore()));
                                                    beefyQR.setText(String.valueOf(currentPlayer.getBeefy()));
                                                    squishyQR.setText(String.valueOf(currentPlayer.getSquishy()));
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });


        // GALLERY Button
        ImageButton galleryButton = findViewById(R.id.other_player_gallery);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherProfiles.this, OtherGallery.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Player", currentPlayer);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
        // Set the action bar to show the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Back button to previous Activity
     * @param item The menu item that was selected.
     *
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}