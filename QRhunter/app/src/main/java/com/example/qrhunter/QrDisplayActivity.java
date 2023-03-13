package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * This class displays a QR Code in more detail
 * With the ability to see others that have scanned the same QR Code
 *
 * Outstanding issues:
 *  Currently Displays very minimal information about the QR Code
 */
public class QrDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_display);

        Database db = new Database();
        Bundle bundle = getIntent().getExtras();
        String hash = bundle.getString("hash");
        TextView qrName =  findViewById(R.id.qr_name);

        //TODO change this to name after querying from db
        qrName.setText(hash);

        Button seeOthers = findViewById(R.id.other_players);
        //Find other players that have scanned this QR Code
        seeOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        QrDisplayActivity.this,
                        PlayerGalleryActivity.class);
                Bundle b = new Bundle();
                b.putString("hash", hash);
                intent.putExtras(b);
                startActivity(intent);
            }
        });

    }


}