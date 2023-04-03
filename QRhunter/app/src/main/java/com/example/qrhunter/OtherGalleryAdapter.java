package com.example.qrhunter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Other Gallery Adapter
 * Displays the QRCodes owned by a Player using GalleryAdapter
 * Includes a drop down feature to show additional QR info
 */
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

            this.image = (ImageView) itemView.findViewById(R.id.QReature_image);
            this.name = (TextView) itemView.findViewById(R.id.QReature_name);
            this.longitude = (TextView) itemView.findViewById(R.id.QReature_long);
            this.latitude = (TextView) itemView.findViewById(R.id.QReature_lat);
            this.score = (TextView) itemView.findViewById(R.id.QReature_score);
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

        image.setImageBitmap(qrCode.getImage(this.context));
        name.setText(qrCode.getName());
        //Concactenation
        if(qrCode.getName().length() > 16){
            name.setText(qrCode.getName().substring(0, 13) + "...");
        }
        longitude.setText(df.format(qrCode.getLocation().getLongitude()));
        latitude.setText(df.format(qrCode.getLocation().getLatitude()));
        score.setText(String.format("%d", qrCode.getScore()));

        //ON LONG CLICK
        //Goes to QRDisplayActivity to see QR details
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(v.getContext(), QrDisplayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("currentUsername", null);
                bundle.putParcelable("QRCode", qrCode);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
                return true;
            }
        });

    }
    @Override
    public int getItemCount() {
        return qrCodeArrayList.size();
    }
}