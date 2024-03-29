package com.example.qrhunter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Activity for SearchPlayer
 * @author X
 */
public class SearchPlayerActivity extends AppCompatActivity implements SearchPlayerAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SearchPlayerAdapter adapter;

    private List<SearchModel> userList = new ArrayList<>();
    private List<SearchModel> userList1 = new ArrayList<>();
    private final Database db = new Database();

    /**
     * Landing screen for SearchPlayer, populates recyclerview with players according to SearchModel
     * Shown are the profile picture and name
     * @author X
     * TODO maybe add their total score and some flavour elements
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_player);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db.getPlayerCollection()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {

                            SearchModel user = new SearchModel(d.get("username").toString());

                            // after getting data from Firebase we are
                            // storing that data in our array list
                            userList1.add(user);
                        }
                        // userList1 stores all users and onCreate we need to use userList for onClick
                        userList.addAll(userList1);
                        adapter = new SearchPlayerAdapter(userList, SearchPlayerActivity.this, SearchPlayerActivity.this::OnItemClick);
                        recyclerView.setAdapter(adapter);
                    }
                });
        // Set the action bar to show the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Override to call query function whenever input box changes
     * @author X
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_player,menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setId(View.generateViewId());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtSearch(query);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Queries database with provided string and updates recyclerview
     * @param str string fragment you want to search
     * @author X
     */
    private void txtSearch(String str){
        userList.clear();

        for(SearchModel item: userList1){
            if(item.getUsername().toUpperCase().contains(str.toUpperCase())){
                userList.add(item);
            }
        }
        adapter = new SearchPlayerAdapter(userList, SearchPlayerActivity.this, SearchPlayerActivity.this::OnItemClick);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnItemClick(int position) {
        Bundle bundle = new Bundle();
        Intent intent = new Intent(SearchPlayerActivity.this, OtherProfiles.class);
        bundle.putString("currentUsername", userList.get(position).getUsername());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}