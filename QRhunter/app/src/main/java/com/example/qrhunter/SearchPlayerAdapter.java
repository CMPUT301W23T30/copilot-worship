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

public class SearchPlayerAdapter extends RecyclerView.Adapter<SearchPlayerAdapter.myViewHolder> {

    public SearchPlayerAdapter(List<SearchModel> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    private List<SearchModel> userList;
    private Context context;

    @NonNull
    @NotNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_player_item,parent,false);
        return new myViewHolder(view);
    }

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
