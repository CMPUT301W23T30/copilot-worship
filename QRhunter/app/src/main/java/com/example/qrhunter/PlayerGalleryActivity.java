package com.example.qrhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PlayerGalleryActivity extends AppCompatActivity {

        private PlayerGalleryAdapter galleryAdapter;
        private ArrayList<Player> playerArrayList = new ArrayList<Player>();
        private ListView galleryView;
    //TODO  make better player gallery xml
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_gallery);

        Database db = new Database();
        Bundle bundle = getIntent().getExtras();
        galleryView = findViewById(R.id.player_list);


        //Query for players associated with qr
        db.getPlayersFromQRCode(bundle.getString("hash"))
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                            //TODO see if we need to get the qr codes?
                            Object number = doc.get("number");
                            if(number == null){number = "0";}
                            Player p = new Player(doc.getString("username"),
                                    doc.getString("email"),
                                    Integer.valueOf((String) number), new ArrayList<>());
                            playerArrayList.add(p);

                        }
                        galleryAdapter = new PlayerGalleryAdapter(
                                PlayerGalleryActivity.this, playerArrayList);
                        galleryView.setAdapter(galleryAdapter);
                    }
                });



    }
}