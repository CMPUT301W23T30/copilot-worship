package com.example.qrhunter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
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

         PlayerGalleryAdapter galleryAdapter;
         ArrayList<Player> playerArrayList = new ArrayList<Player>();
         ListView galleryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_gallery);

        Database db = new Database();
        Bundle bundle = getIntent().getExtras();
        String currentUsername = bundle.getString("currentUsername");
        QRCode qr = bundle.getParcelable("qr");

        galleryView = findViewById(R.id.player_list);

        ImageView qrImage = findViewById(R.id.shared_qr_image);
        qrImage.setImageBitmap(qr.getImage(this));
        galleryAdapter = new PlayerGalleryAdapter(
                PlayerGalleryActivity.this, playerArrayList);
        galleryView.setAdapter(galleryAdapter);
        //Query for players associated with qr
        db.getPlayersFromQRCode(qr.getHash())
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {System.out.println(queryDocumentSnapshots.size());

                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            //This page will not display all the QR Codes the player
                            // has so we can just set it to a new arrayList

                            db.getPlayer(doc.getString("id")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot doc) {
                                    Long number = doc.getLong("number");
                                    if(number == null){number = Long.valueOf(0);}
                                    Player p = new Player(doc.getString("username"),
                                            doc.getString("email"),
                                            number.intValue(), new ArrayList<>());
                                    playerArrayList.add(p);
                                    galleryAdapter.notifyDataSetChanged();

                                }
                            });
                        }


                        galleryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Player selectPlayer = playerArrayList.get(position);
                                String selectPlayerUsername = selectPlayer.getUsername();

                                //TODO startActivity ends in Error and idk why
                                if (!currentUsername.equals(selectPlayerUsername)) {
                                    Intent intent = new Intent(PlayerGalleryActivity.this, OtherProfiles.class);
                                    Bundle newBundle = new Bundle();
                                    newBundle.putString("currentUsername", selectPlayerUsername);
                                    intent.putExtras(newBundle);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(PlayerGalleryActivity.this, "That's you!", Toast.LENGTH_SHORT).show();
                                }

//                                }
                            }
                        });
                    }
                });

    }
}