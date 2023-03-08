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

import java.util.ArrayList;

public class GalleryAdapter extends ArrayAdapter<QRCode> {
    public GalleryAdapter(Context context, ArrayList<QRCode> visits) {
        super(context, 0, visits);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.gallery_item, parent,false);
        } else {
            view = convertView;
        }

        QRCode qrMon = getItem(position);
        ImageView image = view.findViewById(R.id.QRmon_image);
        TextView name = view.findViewById(R.id.QRmon_name);
        TextView location = view.findViewById(R.id.QRmon_location);
        TextView score = view.findViewById(R.id.QRmon_score);

        // set
        name.setText(qrMon.getName());
        //temp set location to longtitude and latitude?
        Location qrMonLocation = qrMon.getLocation();
        String locationString = "" + qrMonLocation.getLongitude() + qrMonLocation.getLatitude();
        location.setText(locationString);
        score.setText(String.format("%d",qrMon.getScore()));

        return view;

    }
}
