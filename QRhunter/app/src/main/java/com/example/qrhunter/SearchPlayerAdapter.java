package com.example.qrhunter;

import android.content.Context;
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

/**
 * Recyclerview adapter for SearchPlayer
 * @author X
 */
public class SearchPlayerAdapter extends RecyclerView.Adapter<SearchPlayerAdapter.myViewHolder> {

    public SearchPlayerAdapter(List<SearchModel> userList, Context context) {
        this.userList = userList;
        this.context = context;
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
        return new myViewHolder(view);
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

//        holder.img.setImageResource(item.getPfp());
        holder.username.setText(item.getUsername());

//        Glide.with(holder.img.getContext())
//                .load(SearchModel.getPfp())
//                .placeholder(R.drawable._icon__profile_circle_)
//                .circleCrop()
//                .error(com.google.android.gms.base.R.drawable.common_google_signin_btn_icon_dark_normal)
//                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return userList.size();
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
