package com.example.qrhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


//Activity Class for login page
public class LoginActivity extends AppCompatActivity {
    final String TAG = "Login page";
    public void toUserPage(String username){
        Intent intent = new Intent(LoginActivity.this, UserPageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("player", username);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button devLogin = findViewById(R.id.dev_login_button);
        Button registerButton = findViewById(R.id.register_button);
        TextView usernameTextView = findViewById(R.id.login_page_user_name);

        Database db = new Database();
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        if(settings.contains("Username")){
            usernameTextView.setText(settings.getString("Username", "").toString());
        }

        //Temporary Dev log in
        devLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toUserPage(new Player().getUsername());
            }
        });

        //Register new User
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO handle bad input
                String username = usernameTextView.getText().toString();
                db.getPlayerFromUsername(username)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Todo handle failure
                                Log.d(TAG, "Could not register New User");
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                //If username is not taken
                                if(queryDocumentSnapshots.isEmpty())
                                {
                                    //https://stackoverflow.com/questions/10209814/saving-user-information-in-app-settings
                                    //
                                    //TODO cite this properly
                                    SharedPreferences settings = getSharedPreferences(
                                            "UserInfo",
                                            0);

                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("Username", username);
                                    //editor.putString("Password",txtPWD.getText().toString()); password?
                                    editor.apply();

                                    db.addPlayer(new Player(username))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    toUserPage(username);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //TODO tell player there was an error
                                                }
                                            });
                                }
                                else{
                                    //TODO tell user that they need a new username
                                }
                            }
                        });
            }
        });
    }
}