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
 * @author Maarij
 */
public class AddPlayerActivity extends AppCompatActivity {
    String passedUserName, passedEmail, passedPhone;
    byte[] passedPicture;

    boolean picAdded = false;

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
            //To update the player info, we need to delete
            // all the documents and re add it
            // To do that we have to reconstruct the player object for deletion and addition
            //TODO Change player username in pics as well
            Database db = new Database();


            String username = usernameEditText.getText().toString();
            if (username.length() > 20 || username.length() <= 0 ) {
                errorText.setText("Username must be between 1-20 Characters");

                return;
            }
            String email = emailEditText.getText().toString();
            String number = phoneEditText.getText().toString();
            Player newUser = new Player(username,email, Integer.parseInt(number), new ArrayList<>());

            //making Sure it is unique
            db.getPlayer(username).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if(documentSnapshot.exists() && !username.equals(passedUserName))
                    {
                        //Username is not unique, so it is invalid

                        errorText.setText("Username is taken dummmy");
                    }
                    else{
                        System.out.println("THis tan???");
                        if(picAdded){savePicture(username);}

                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                        String id = settings.getString("id", "no-id");
                        newUser.setId(id);
                        System.out.println(newUser.getId());
                        db.changeInfo(newUser);
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
                }
            });

        });
        // Set the action bar to show the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    // https://www.geeksforgeeks.org/how-to-select-an-image-from-gallery-in-android/
    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

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

    public void savePicture(String username){

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
        db.storeProfilePicture(id, bitmap);

    }

}