package com.example.qrhunter;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LeaderboardAdapter adapter;

    private List<LeaderboardModel> userList = new ArrayList<>();
    private final Database db = new Database();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        recyclerView = (RecyclerView) findViewById(R.id.leaderboard);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db.getPlayerCollectionTotalScore()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

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
}