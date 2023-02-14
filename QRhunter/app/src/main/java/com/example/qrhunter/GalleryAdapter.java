package com.example.qrhunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class GalleryAdapter extends ArrayAdapter<QRCode> {
    public GalleryAdapter(Context context, List<QRCode> visits) {
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
        ImageButton image = view.findViewById(R.id.QRmon_image);
        TextView name = view.findViewById(R.id.QRmon_name);
        TextView location = view.findViewById(R.id.QRmon_location);
        TextView score = view.findViewById(R.id.QRmon_score);

        // temp
        name.setText("uniquename");
        location.setText("Edmonton, AB");
        score.setText("69");

        return view;

    }
}
