package com.example.qrhunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.myViewHolder>{
    private OnItemClickListener onItemClickListener1;
    public LeaderboardAdapter(List<LeaderboardModel> userList, Context context, OnItemClickListener onItemClickListener1) {
        this.userList = userList;
        this.context = context;
        this.onItemClickListener1 = onItemClickListener1;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_item,parent,false);
        return new LeaderboardAdapter.myViewHolder(view, onItemClickListener1);
    }

    /**
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     * @author X
     * TODO implement extra player profile features: Profile picture, total score and add them to binder
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull myViewHolder holder, int position) {
        LeaderboardModel item = userList.get(position);

        holder.username.setText(item.getUsername());
        holder.ranking.setText(Integer.toString(position + 1 + 3));
        holder.totalScore.setText(item.getTotalScore().toString());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    /**
     * The actual view of the item in the recyclerview
     * @author X
     */
    class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView username;
        TextView ranking;
        TextView totalScore;


        /** for recycler views there is no onItemClickListener, so we have to make our own for each of the views inside
         * the RecyclerView
        */
        OnItemClickListener onItemClickListener;

        // we pass the onItemClickListener so when it is created we can set the onItemClickListener
        public myViewHolder(@NonNull @NotNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            username = (TextView)itemView.findViewById(R.id.profile_name);
            ranking = (TextView)itemView.findViewById(R.id.ranking_number);
            totalScore = (TextView)itemView.findViewById(R.id.total_score);
            // set the onItemClickListener
            this.onItemClickListener = onItemClickListener;
            // attach the listener to the view
            itemView.setOnClickListener(this);
        }

        /**
         * this override is called when any of the viewholders inside the RecyclerView gets clicked through the
         * listener set in the constructor (itemView.setOnClickListener(this))
         * @author X
         */
        @Override
        public void onClick(View view) {
            onItemClickListener.OnItemClick(getAdapterPosition());
        }
    }

    /**
     * interface used to detect and interpret the click, to be implemented inside the activity
     * @author X
     */
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
}
