package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        seeOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QrDisplayActivity.this, PlayerGalleryActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

    }


}