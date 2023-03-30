package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class OtherProfiles extends AppCompatActivity {
    private Player currentPlayer;
    private String currentUsername;
    ArrayList<QRCodeComment> qrCodeComments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profiles);

        Log.d("TASK", "ENTERED NEW PROFILE");
        Bundle bundle = getIntent().getExtras();
        currentUsername = bundle.getString("username");
        Database db = new Database();


        // Player Information
        TextView username = findViewById(R.id.other_player_user_name);
        TextView totalScore = findViewById(R.id.other_player_total_score);
        TextView beefyQR = findViewById(R.id.other_player_strongest);
        TextView squishyQR = findViewById(R.id.other_player_weakest);

        username.setText(currentUsername);

        db.getPlayerInfo(currentUsername, new PlayerInfoListener() {
            @Override
            public void playerInfoCallback(Player player) {
                Log.d("TASK", "START");
                currentPlayer = player;
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
    }
}