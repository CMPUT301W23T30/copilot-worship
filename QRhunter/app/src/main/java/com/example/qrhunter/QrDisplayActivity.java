package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.common.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This class displays a QR Code in more detail
 * With the ability to see others that have scanned the same QR Code
 *
 * Outstanding issues:
 *  Currently Displays very minimal information about the QR Code
 */
public class QrDisplayActivity extends AppCompatActivity {
    String armsFileName, legsFileName, eyesFileName, mouthFileName, hatFileName;
    ImageButton seeOthersButton;
    QRCode qrCode;
    String qrHeightStat;
    String qrWeightStat;
    String qrTypeStat;

    private static final DecimalFormat df = new DecimalFormat("00.00");

    private void display(Bundle bundle){
        String currentPlayer = bundle.getString("currentUsername");

        TextView qrName =  findViewById(R.id.qr_name);
        ImageView qrImage = findViewById(R.id.qr_image);
        TextView qrPower = findViewById(R.id.qr_power);
        TextView qrHeight = findViewById(R.id.qr_height);
        TextView qrWeight = findViewById(R.id.qr_weight);
        TextView qrType = findViewById(R.id.qr_type);

        qrName.setText(qrCode.getName());
        qrImage.setImageBitmap(qrCode.getImage(this));

        qrPower.setText(String.valueOf(qrCode.getScore()));
        qrHeight.setText(qrHeightStat);
        qrWeight.setText(qrWeightStat);
        qrType.setText(qrTypeStat);

        seeOthersButton = findViewById(R.id.other_players);
        seeOthersButton.setVisibility(View.GONE);

        //Gallery to see Shared Players is only visible from QRS you own
        if (currentPlayer != null){
            seeOthersButton.setVisibility(View.VISIBLE);
        }

        //Find other players that have scanned this QR Code
        seeOthersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        QrDisplayActivity.this,
                        PlayerGalleryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("currentUsername",currentPlayer);
                bundle.putString("hash",qrCode.getHash());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        // Set the action bar to show the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_display);

        Bundle bundle = getIntent().getExtras();
        qrCode = bundle.getParcelable("QRCode");
        if (bundle.containsKey("hash")) {
            Database db = new Database();
            db.getQr(bundle.getString("hash")).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                        Location location = new Location("");
                        location.setLatitude(doc.getLong("latitude").doubleValue());
                        location.setLongitude(doc.getLong("longitude").doubleValue());

                        qrCode = new QRCode(doc.getString("hash"), doc.getString("name")
                        ,location, doc.getLong("score").intValue());

                        display(bundle);
                    }
                }
            });
        }
        else{
            display(bundle);
        }
    }

    /**
     * Generates some fun stats for the QR!
     * @param qrCode The qrcode whose states we want to know
     */
    public void setQRStats(QRCode qrCode){
        Double height = Math.abs(qrCode.getLocation().getLongitude());
        String rawHeight = df.format(height);
        qrHeightStat = "" + rawHeight.charAt(0) + "." + rawHeight.charAt(1) + rawHeight.charAt(2) + " cm";

        Double weight = Math.abs(qrCode.getLocation().getLatitude());
        String rawWeight = df.format(weight);
        qrWeightStat = "" + rawWeight.charAt(0) + rawWeight.charAt(1) + "." + rawWeight.charAt(2) + rawWeight.charAt(3) + "G";

        Integer rawType = qrCode.getScore();
        if (rawType <= 15){
            qrTypeStat = "C";
        }else if (rawType <= 30){
            qrTypeStat = "B";
        }else if (rawType <= 50){
            qrTypeStat = "A";
        }else if (rawType <= 80){
            qrTypeStat = "AA";
        }else{
            qrTypeStat = "S";
        }
    }
    // enable back button
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