package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

/**
 * This class is used to add a new player to the database.
 * When it's complete it will eventually become the login screen.
 * It is called from the MainActivity class.
 * @author: Maarij
 */
public class AddPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText numberEditText = findViewById(R.id.numberEditText);
        Button submitButton = findViewById(R.id.submitButton);

        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String number = numberEditText.getText().toString();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("number", number);

        submitButton.setOnClickListener(v -> {
            finish();
        });
    }
}