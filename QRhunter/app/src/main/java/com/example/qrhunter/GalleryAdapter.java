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

/**
 * Adapter that displays the QRCodes owned by the current Player
 *
 * Outstanding Issues:
 * TODO make UI bigger and make name,score,location information obvious
 */
public class GalleryAdapter extends ArrayAdapter<QRCode> {
    /**
     * Constructor for GalleryAdapater
     * @param context
     * @param visits
     */
    public GalleryAdapter(Context context, ArrayList<QRCode> visits) {
        super(context, 0, visits);
    }

    /**
     * Returns View of an QRCode item in ArrayAdapter
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
     */
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
