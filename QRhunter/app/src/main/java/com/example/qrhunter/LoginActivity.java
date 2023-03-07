package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button dev_login = findViewById(R.id.dev_login_button);
        dev_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, UserPageActivity.class);
                //https://stackoverflow.com/questions/56193835/is-it-possible-put-a-custom-class-into-bundle
                //TODO cite properly
                Gson gson = new Gson();
                Bundle bundle = new Bundle();
                bundle.putString("player", gson.toJson(new Player()));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}