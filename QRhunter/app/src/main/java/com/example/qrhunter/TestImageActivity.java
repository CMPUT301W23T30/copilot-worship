package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class TestImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_image);

        ImageView testImage = (ImageView) findViewById(R.id.testImage);

        CharacterImage testCharacter = new CharacterImage(this, 120, "arms1", "legs1", "eyes1", "mouth1", "hat1");
        testImage.setImageBitmap(testCharacter.getCharacterImage());
    }
}