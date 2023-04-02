package com.example.qrhunter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that displays the Leaderboard
 */
public class LeaderboardActivity extends AppCompatActivity implements LeaderboardAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;

    private List<LeaderboardModel> userList = new ArrayList<>();
    private final Database db = new Database();

    private TextView firstUsername;
    private TextView secondUsername;
    private TextView thirdUsername;
    private TextView firstScore;
    private TextView secondScore;
    private TextView thirdScore;
    private String firstUsernameStr;
    private String secondUsernameStr;
    private String thirdUsernameStr;
    private CircleImageView firstPlace, secondPlace, thirdPlace;

    private String currentUser;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor ;


    public void storeLeaderboard(List<DocumentSnapshot> list){
        Set<String> LeaderBoardSet = new HashSet<>();
        //Store locally
        for(int i = 0;  i < list.size(); i++){
            //We can store a set easily so we use the format i-username-score
            //Where i is the number of the player
            String nullFlag = "\u0000";
            LeaderBoardSet.add( i + nullFlag + list.get(i).get("username").toString() + nullFlag
                    + list.get(i).get("totalScore").toString());
            //Store the score before the current user as the "score to beat"
            //Then we can update the leaderboard anytime we think we have beaten this score
            if(list.get(i).get("username").toString().equals(currentUser)){
                if(i != 0 ) {
                    editor.putString("scoreToBeat", list.get(i - 1).get("totalScore").toString());
                    editor.commit();
                }
            }
        }

        editor.putStringSet("localLeaderboard", LeaderBoardSet);
        editor.putBoolean("playersSaved", true);
        editor.commit();
    }

    public List<LeaderboardModel> getLeaderboard(){
        Set<String> leaderBoardSet = new HashSet<>();
        leaderBoardSet = settings.getStringSet("localLeaderboard", leaderBoardSet);
        Object[] topList = leaderBoardSet.stream().toArray();
        String nullFlag = "\u0000";
        for (Object formattedObject : topList) {
            String formattedUser = (String) formattedObject;
            //Cut off first terminator that stored position
            formattedUser = formattedUser.substring(formattedUser.indexOf(nullFlag) + 1);
            String username = formattedUser.substring(0, formattedUser.indexOf(nullFlag));
            String score = formattedUser.substring(formattedUser.indexOf(nullFlag) + 1);
            LeaderboardModel user = new LeaderboardModel(username, Integer.valueOf(score));
            userList.add(user);
        }
        userList.sort(new LeaderboardComparator());
        return userList;
    }

    public void displayLeaderboardQuery(List<DocumentSnapshot> list){
        // extract the top 3
        DocumentSnapshot d1 = list.get(0);
        firstUsernameStr = d1.get("username").toString();
        firstUsername.setText(firstUsernameStr);
        firstScore.setText(d1.get("totalScore").toString());
        list.remove(0);
        d1 = list.get(0);
        secondUsernameStr = d1.get("username").toString();
        secondUsername.setText(secondUsernameStr);
        secondScore.setText(d1.get("totalScore").toString());
        list.remove(0);
        d1 = list.get(0);
        thirdUsernameStr = d1.get("username").toString();
        thirdUsername.setText(thirdUsernameStr);
        thirdScore.setText(d1.get("totalScore").toString());
        list.remove(0);
        glideTopThree();
        for (DocumentSnapshot d : list) {

            LeaderboardModel user = new LeaderboardModel(d.get("username").toString(),Integer.parseInt(d.get("totalScore").toString()));

            // after getting data from Firebase we are
            // storing that data in our array list
            userList.add(user);
        }
        adapter = new LeaderboardAdapter(userList, LeaderboardActivity.this, LeaderboardActivity.this::OnItemClick);
        recyclerView.setAdapter(adapter);
    }

    public void displayLeaderboardSaved(List<LeaderboardModel> userList){
        firstUsernameStr = userList.get(0).getUsername();
        firstScore.setText(userList.get(0).getTotalScore().toString());
        firstUsername.setText(firstUsernameStr);
        secondUsernameStr = userList.get(1).getUsername();
        secondScore.setText(userList.get(1).getTotalScore().toString());
        secondUsername.setText(secondUsernameStr);
        thirdUsernameStr = userList.get(2).getUsername();
        thirdUsername.setText(thirdUsernameStr);
        thirdScore.setText(userList.get(2).getTotalScore().toString());
        glideTopThree();
        userList.remove(0);
        userList.remove(0);
        userList.remove(0);
        adapter = new LeaderboardAdapter(userList, LeaderboardActivity.this, LeaderboardActivity.this::OnItemClick);

        recyclerView.setAdapter(adapter);

    }

    /**
     * ugly function to get the images for the pfps of the top 3 and glide.
     * @author X
     */
    protected void glideTopThree(){
        db.getProfilePicture(firstUsernameStr).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                Glide.with(firstPlace)
                        .load(bmp)
                        .placeholder(R.drawable._icon__profile_circle_)
                        .circleCrop()
                        .error(R.drawable._icon__profile_circle_)
                        .into(firstPlace);
            }
        });
        db.getProfilePicture(secondUsernameStr).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                Glide.with(secondPlace)
                        .load(bmp)
                        .placeholder(R.drawable._icon__profile_circle_)
                        .circleCrop()
                        .error(R.drawable._icon__profile_circle_)
                        .into(secondPlace);
            }
        });
        db.getProfilePicture(thirdUsernameStr).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

                Glide.with(thirdPlace)
                        .load(bmp)
                        .placeholder(R.drawable._icon__profile_circle_)
                        .circleCrop()
                        .error(R.drawable._icon__profile_circle_)
                        .into(thirdPlace);
            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        settings = getSharedPreferences("LocalLeaderboard", 0);
        editor = settings.edit();
        recyclerView = (RecyclerView) findViewById(R.id.leaderboard);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firstUsername = findViewById(R.id.first_place_name);
        firstScore = findViewById(R.id.first_place_score);
        secondUsername = findViewById(R.id.second_place_name);
        secondScore = findViewById(R.id.second_place_score);
        thirdUsername = findViewById(R.id.third_place_name);
        thirdScore = findViewById(R.id.third_place_score);
        firstPlace = findViewById(R.id.first_place);
        secondPlace = findViewById(R.id.second_place);
        thirdPlace = findViewById(R.id.third_place);
        currentUser = getIntent().getExtras().getString("username");

        Boolean saved;
        saved = settings.getBoolean("playersSaved", false);

        if(saved){
            displayLeaderboardSaved(getLeaderboard());
        }
        else{
            db.getPlayerCollectionTotalScore()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            storeLeaderboard(list);
                            displayLeaderboardQuery(list);
                        }
                    });
        }
        // Set the action bar to show the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onClickFirst(View view) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(LeaderboardActivity.this, OtherProfiles.class);
        bundle.putString("username", firstUsernameStr);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onClickSecond(View view) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(LeaderboardActivity.this, OtherProfiles.class);
        bundle.putString("username", secondUsernameStr);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onClickThird(View view) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(LeaderboardActivity.this, OtherProfiles.class);
        bundle.putString("username", thirdUsernameStr);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void OnItemClick(int position) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(LeaderboardActivity.this, OtherProfiles.class);
        bundle.putString("username", userList.get(position).getUsername());
        intent.putExtras(bundle);
        startActivity(intent);
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