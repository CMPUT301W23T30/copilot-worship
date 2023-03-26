package com.example.qrhunter;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = (RecyclerView) findViewById(R.id.leaderboard);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firstUsername = findViewById(R.id.first_place_name);
        firstScore = findViewById(R.id.first_place_score);
        secondUsername = findViewById(R.id.second_place_name);
        secondScore = findViewById(R.id.second_place_score);
        thirdUsername = findViewById(R.id.third_place_name);
        thirdScore = findViewById(R.id.third_place_score);

        db.getPlayerCollectionTotalScore()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

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
                        adapter = new LeaderboardAdapter(userList, LeaderboardActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                });
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
}