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

/**
 * Adapter for the gallery that displays users that have scanned a QR Code
 *
 *  Outstanding Issues:
 *  Display more than just the player name
 *  make it look good
 *
 */
public class PlayerGalleryAdapter extends ArrayAdapter<Player> {
    /**
     * Constructor for the adapter
     * @param context Current context of the app
     * @param players ArrayList of players to display
     */
    public PlayerGalleryAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
    }

    /**
     *  Returns the View of an item in the adapter
     * @param position Position of the item
     * @param convertView
     * @param parent
     * @return View of the item
     */
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

        //Set information about the user
        username.setText(player.getUsername());

        return view;

    }
}

