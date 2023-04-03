package com.example.qrhunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import de.hdodenhof.circleimageview.CircleImageView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Recyclerview adapter for SearchPlayer
 * @author X
 */
public class SearchPlayerAdapter extends RecyclerView.Adapter<SearchPlayerAdapter.myViewHolder> {
    private OnItemClickListener onItemClickListener1;
    private final Database db = new Database();

    public SearchPlayerAdapter(List<SearchModel> userList, Context context, OnItemClickListener onItemClickListener1) {
        this.userList = userList;
        this.context = context;
        this.onItemClickListener1 = onItemClickListener1;
    }

    private List<SearchModel> userList;
    private Context context;

    /**
     * Basic binder for SearchPlayerAdapter
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
        return new myViewHolder(view, onItemClickListener1);
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
        SearchModel item = userList.get(position);

        holder.username.setText(item.getUsername());

        Glide.with(holder.img.getContext())
                .load(item.getBmp())
                .placeholder(R.drawable._icon__profile_circle_)
                .circleCrop()
                .error(R.drawable._icon__profile_circle_)
                .into(holder.img);


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
        CircleImageView img;
        TextView username;
        /** for recycler views there is no onItemClickListener, so we have to make our own for each of the views inside
         * the RecyclerView
         */
        OnItemClickListener onItemClickListener;

        public myViewHolder(@NonNull @NotNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            img = (CircleImageView)itemView.findViewById(R.id.profile_picture);
            username = (TextView)itemView.findViewById(R.id.profile_name);
            // set the onItemClickListener
            this.onItemClickListener = onItemClickListener;
            // attach the listener to the view
            itemView.setOnClickListener((View.OnClickListener) this);
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
