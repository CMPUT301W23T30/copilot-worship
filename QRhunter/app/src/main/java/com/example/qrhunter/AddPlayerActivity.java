package com.example.qrhunter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;


import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

/**
 * This class is used to add a new player to the database.
 * When it's complete it will eventually become the login screen.
 * It is called from the MainActivity class.
 * @author: Maarij
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used to edit player information
 * It is called from the MainActivity class.
 * @author Maarij and Sean Mandu
 */
public class AddPlayerActivity extends AppCompatActivity {
    String passedUserName, passedEmail, passedPhone;
    byte[] passedPicture;

    boolean picAdded = false;

    /**
     * Saves and returns to the main screen
     * @param newUser Object of the new User to be added
     * @param username Username of the new player
     * @param db Instance of Database Class
     */
    public void saveAndReturn(Player newUser, String username, Database db){
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        String id = settings.getString("id", "no-id");
        newUser.setId(id);
        db.changeInfo(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("Username");
                editor.putString("Username", username);
                editor.commit();
                SharedPreferences settings2 = getSharedPreferences("LocalLeaderboard", 0);
                SharedPreferences.Editor editor1 = settings2.edit();
                editor1.putBoolean("playersSaved", false); //reload leaderboard next time
                editor1.commit();

                Intent intent = new Intent(AddPlayerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * This method is called when the activity is created.
     * It sets up the buttons and text fields.
     * @param savedInstanceState The saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        ImageView profilePicImageView = findViewById(R.id.edit_player_profile_pic);
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText phoneEditText = findViewById(R.id.numberEditText);
        Button selectButton = findViewById(R.id.select_profile_pic_button);
        Button randomizeButton = findViewById(R.id.randomize_player_char_button);
        Button submitButton = findViewById(R.id.submitButton);
        TextView errorText = findViewById(R.id.errorText);

        Bundle bundle = getIntent().getExtras();
        //Set player info to the passed information
        if (bundle != null) {
            passedUserName = bundle.getString("username");
            passedEmail = bundle.getString("email");
            passedPhone = bundle.getString("phone");
            passedPicture = bundle.getByteArray("pictureBytes");
            usernameEditText.setText(passedUserName);
            emailEditText.setText(passedEmail);
            phoneEditText.setText(passedPhone);
            if(passedPicture != null){
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bmp = BitmapFactory.decodeByteArray(passedPicture, 0, passedPicture.length, options);
                profilePicImageView.setImageBitmap(bmp);
            }

        }


        selectButton.setOnClickListener(v -> {
            imageChooser();
        });

        randomizeButton.setOnClickListener(v -> {

            profilePicImageView.setImageBitmap(generateRandomQReature());
            picAdded = true;
        });

        submitButton.setOnClickListener(v -> {
            Database db = new Database();

            String username = usernameEditText.getText().toString();
            if (username.length() > 20 || username.length() <= 0 ) {
                errorText.setText("Username must be between 1-20 Characters");

                return;
            }
            String email = emailEditText.getText().toString();
            if (email.length() <= 0 ) {
                errorText.setText("You need an email silly!");

                return;
            }
            String number = phoneEditText.getText().toString();
            if (number.length() <= 0 ) {
                errorText.setText("Forgetting something?");

                return;
            }

            Player newUser = new Player(username,email, Integer.parseInt(number), new ArrayList<>());

            //making Sure it is unique
           db.getPlayerFromUsername(username).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
               @Override
               public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                   if(!queryDocumentSnapshots.isEmpty() && !username.equals(passedUserName) && !username.equals("test!!!!"))
                   {
                       //Username is not unique, so it is invalid
                       errorText.setText("Username is taken dummy");

                       //Check to make sure username is not in id format
                   } else if (username.contains("Player-")) {
                       String end = username.substring(6);
                       try{
                           Integer.parseInt(end);
                           errorText.setText("Player- followed by a number is not a valid username");
                       } catch (NumberFormatException e){
                           if(picAdded){
                               savePicture(username, newUser);
                           }
                           else{
                               saveAndReturn(newUser, username, db);
                           }
                       }

                   } else{
                       if(picAdded){
                           savePicture(username, newUser);
                       }
                       else{
                           saveAndReturn(newUser, username, db);
                       }
                   }
               }
           });

        });
        // Set the action bar to show the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * This method operates the back button
     * @param item The item that was selected (in this case the back button)
     * @return boolean of whether the back button was selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
    This method creates the back button menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * This method generates a random QReature using the CharacterImage class, and returns it as a Bitmap
     * @return Bitmap of the random QReature
     */
    private Bitmap generateRandomQReature() {
        String armsFileName = "arms" + (int) (Math.random() * 10);
        String legsFileName = "legs" + (int) (Math.random() * 10);
        String eyesFileName = "eyes" + (int) (Math.random() * 10);
        String mouthFileName = "mouth" + (int) (Math.random() * 10);
        String hatFileName = "hat" + (int) (Math.random() * 10);
        String firstSixDigitsString = String.valueOf((int) (Math.random() * 1000000));

        CharacterImage testCharacter = new CharacterImage(this, armsFileName, legsFileName, eyesFileName, mouthFileName, hatFileName, firstSixDigitsString);

        return testCharacter.getCharacterImage();
    }

    /**
     * This method opens the gallery and allows the user to select an image as their profile picture.
     * Credit to: https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
     */
    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    // Create an ActivityResultLauncher for the Select an Image Activity
    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                        if(selectedImageBitmap != null){
                            picAdded = true;
                        }

                        ImageView profilePicImageView = findViewById(R.id.edit_player_profile_pic);
                        profilePicImageView.setImageBitmap(selectedImageBitmap);
                    }
                }
            });

    /**
     * Saves the picture to the player profile
     * Both arguments should have the new player information
     * @param username username of the player
     * @param newUser Player object of the player
     */
    public void savePicture(String username, Player newUser){

        SharedPreferences settings = getSharedPreferences("profilePicture", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("saved", true);
        editor.commit();
        ImageView iv = findViewById(R.id.edit_player_profile_pic);
        Drawable drawable = iv.getDrawable();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Database db = new Database();
        SharedPreferences setting = getSharedPreferences("UserInfo", 0);
        String id = setting.getString("id", "no-id");
        db.storeProfilePicture(id, bitmap).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                saveAndReturn(newUser, username, db);
            }
        });

    }

}