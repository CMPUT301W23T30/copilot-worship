package com.example.qrhunter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that represents the leaderboard
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


    private SharedPreferences settings;
    private SharedPreferences.Editor editor ;


    public void storeTop10(List<DocumentSnapshot> list){
        Set<String> top10 = new HashSet<>();
        //Store top >=10  locally
        for(int i = 0; i < 10 || i < list.size(); i++){
            //We can store a set easily so we use the format i-username-score
            //Where i is the number of the player
            String nullFlag = "\u0000";
            top10.add( i + nullFlag + list.get(i).get("username").toString() + nullFlag
                    + list.get(i).get("totalScore").toString());
        }
        editor.putStringSet("localTop10", top10);
        editor.putBoolean("playersSaved", true);
        editor.commit();
    }

    public List<LeaderboardModel> getTop10(){
        List<LeaderboardModel> userList = new ArrayList<>();
        Set<String> top10 = new HashSet<>();
        top10 = settings.getStringSet("localTop10", top10);
        Object[] topList = top10.stream().sorted().toArray();
        String nullFlag = "\u0000";
        for (Object formattedObject : topList) {
            String formattedUser = (String) formattedObject;
            System.out.println(formattedUser);
            //Cut off first terminator that stored position
            formattedUser = formattedUser.substring(formattedUser.indexOf(nullFlag) + 1);
            String username = formattedUser.substring(0, formattedUser.indexOf(nullFlag));
            String score = formattedUser.substring(formattedUser.indexOf(nullFlag) + 1);
            System.out.println(username + ":  - :" + score);
            LeaderboardModel user = new LeaderboardModel(username, Integer.valueOf(score));
            userList.add(user);
        }
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
        thirdUsernameStr = userList.get(3).getUsername();
        thirdUsername.setText(thirdUsernameStr);
        thirdScore.setText(userList.get(2).getTotalScore().toString());
        userList.remove(0);
        userList.remove(1);
        userList.remove(2);
        adapter = new LeaderboardAdapter(userList, LeaderboardActivity.this, LeaderboardActivity.this::OnItemClick);
        recyclerView.setAdapter(adapter);
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

        Boolean saved;
        saved = settings.getBoolean("playersSaved", false);

        System.out.println(saved);
        if(saved){
            displayLeaderboardSaved(getTop10());
            System.out.println("Gotten locally");
        }
        else{
            db.getPlayerCollectionTotalScore()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            storeTop10(list);
                            displayLeaderboardQuery(list);
                        }
                    });
        }

    }

    public void onClickFirst(View view) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
        bundle.putString("username", firstUsernameStr);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onClickSecond(View view) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
        bundle.putString("username", secondUsernameStr);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onClickThird(View view) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
        bundle.putString("username", thirdUsernameStr);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void OnItemClick(int position) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(LeaderboardActivity.this, MainActivity.class);
        bundle.putString("username", userList.get(position).getUsername());
        intent.putExtras(bundle);
        startActivity(intent);
    }
}