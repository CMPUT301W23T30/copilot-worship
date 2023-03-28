package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class OtherProfile extends AppCompatActivity {
    Player otherPlayer;

    private TextView username;
    private TextView total;
    private TextView beefy;
    private TextView squishy;
    private ImageButton gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        Bundle bundle = getIntent().getExtras();
        otherPlayer = bundle.getParcelable("Player");
        Log.d("TASK", "PROFILE PAGE OF " + otherPlayer.getUsername());
        Log.d("TASK", "TOTAL SCORE OF " + otherPlayer.getTotalScore());

        ImageView profileCircle = (ImageView) findViewById(R.id.other_profile_icon);
        username = (TextView) findViewById(R.id.other_player_user_name);
        total = (TextView) findViewById(R.id.other_player_total_score);
        beefy = (TextView) findViewById(R.id.other_player_strongest);
        squishy = (TextView) findViewById(R.id.other_player_weakest);
        gallery = (ImageButton) findViewById(R.id.other_player_gallery);

        username.setText(otherPlayer.getUsername());
  //      total.setText(String.valueOf(otherPlayer.getTotalScore()));
//        beefy.setText(String.valueOf(otherPlayer.getBeefy()));
//        squishy.setText(String.valueOf(otherPlayer.getSquishy()));

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherProfile.this, OtherGallery.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Player", otherPlayer);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }
}