package com.example.qrhunter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OtherGalleryAdapter extends RecyclerView.Adapter<OtherGalleryAdapter.ViewHolder>{
    private ArrayList<QRCode> qrCodeArrayList;
    private Context context;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView longitude;
        TextView latitude;
        TextView score;

        public ViewHolder(View itemView) {
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.QRmon_image);
            this.name = (TextView) itemView.findViewById(R.id.QRmon_name);
            this.longitude = (TextView) itemView.findViewById(R.id.QRmon_long);
            this.latitude = (TextView) itemView.findViewById(R.id.QRmon_lat);
            this.score = (TextView) itemView.findViewById(R.id.QRmon_score);
        }

    }

    public OtherGalleryAdapter(ArrayList<QRCode> qrCodeArrayList, Context context){
        this.qrCodeArrayList = qrCodeArrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_gallery_item, parent, false);
        return new OtherGalleryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (OtherGalleryAdapter.ViewHolder holder, final int listPosition){
        QRCode qrCode = qrCodeArrayList.get(listPosition);

        ImageView image = holder.image;
        TextView name = holder.name;
        TextView longitude = holder.longitude;
        TextView latitude = holder.latitude;
        TextView score = holder.score;

        name.setText(qrCode.getName());
        longitude.setText(df.format(qrCode.getLocation().getLongitude()));
        latitude.setText(df.format(qrCode.getLocation().getLatitude()));
        score.setText(String.format("%d", qrCode.getScore()));

    }
    @Override
    public int getItemCount() {
        return qrCodeArrayList.size();
    }
}