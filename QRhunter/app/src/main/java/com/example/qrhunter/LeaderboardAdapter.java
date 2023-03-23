package com.example.qrhunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.myViewHolder>{
    public LeaderboardAdapter(List<LeaderboardModel> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    private List<LeaderboardModel> userList;
    private Context context;
    /**
     * Basic binder for LeaderboardAdapter
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @author X
     */
    @NonNull
    @NotNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_player_item,parent,false);
        return new LeaderboardAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LeaderboardAdapter.myViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    /**
     * The actual view of the item in the recyclerview
     * @author X
     */
    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView username;

        public myViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            img = (CircleImageView)itemView.findViewById(R.id.profile_picture);
            username = (TextView)itemView.findViewById(R.id.profile_name);
        }
    }
}
