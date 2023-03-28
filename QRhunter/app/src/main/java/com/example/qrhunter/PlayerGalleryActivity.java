package com.example.qrhunter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Adapter for the Player Gallery that displays Users with the same QR Code as
 *
 * Outstanding Issues:
 *  See if we can pass a query to an activity so we can recycle them for different queries
 *
 *  Make this look good
 *
 *  Make this exclude the current player from the query to truly see others with the qr code
 *  and not just everyone
 *
 */
public class PlayerGalleryActivity extends AppCompatActivity {
    Player player;

     PlayerGalleryAdapter galleryAdapter;
     QRCode qrCode;
     ArrayList<Player> playerArrayList = new ArrayList<Player>();
     ListView galleryView;

    //TODO  make better player gallery xml
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_gallery);

        Database db = new Database();
        Bundle bundle = getIntent().getExtras();
        qrCode = bundle.getParcelable("QRCode");
        String currentPlayer = bundle.getString("CurrentPlayer");
        Log.d("TASK", currentPlayer);

        galleryView = findViewById(R.id.player_list);


        //Query for players associated with qr
        db.getPlayersFromQRCode(qrCode.getHash())
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            //This page will not display all the QR Codes the player
                            // has so we can just set it to a new arrayList
                            Object number = doc.get("number");
                            if(number == null){number = "0";}
                            player = new Player(doc.getString("username"),
                                    doc.getString("email"),
                                    Integer.valueOf((String) number), new ArrayList<>());
                            playerArrayList.add(player);

                        }
                        galleryAdapter = new PlayerGalleryAdapter(PlayerGalleryActivity.this, playerArrayList);
                        galleryView.setAdapter(galleryAdapter);

                        galleryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Player selectPlayer = playerArrayList.get(position);

                                if (selectPlayer.getUsername() != currentPlayer){
                                    Intent intent = new Intent (PlayerGalleryActivity.this, OtherProfile.class);
                                    Bundle newBundle = new Bundle();
                                    newBundle.putParcelable("Player", selectPlayer);
                                    intent.putExtras(newBundle);

                                    startActivity(intent);
                                }else{
                                    Log.d("TASK", "CANNOT CLICK ON YOUR OWN PROFILE HERE");
                                }
                            }
                        });
                    }
                });
    }
}