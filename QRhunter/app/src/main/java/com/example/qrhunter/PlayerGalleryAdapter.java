package com.example.qrhunter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

//TODO have this display more than just the player name
public class PlayerGalleryAdapter extends ArrayAdapter<Player> {
    public PlayerGalleryAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.player_gallery_item, parent,false);
        } else {
            view = convertView;
        }

        Player player = getItem(position);
        TextView username = view.findViewById(R.id.player_gallery_item_username);

        username.setText(player.getUsername());

        return view;

    }
}

