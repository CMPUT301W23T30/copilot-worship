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

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.ViewHolder>{
    private ArrayList<QRCodeComment> qrCodeComments;
    private Context context;

    public interface OnItemLongClickListener{
        public boolean onItemLongClicked(int position);
    }

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView longitude;
        TextView latitude;
        TextView score;
        ImageButton editButton;
        ImageButton deleteButton;
        TextView comment;
        ImageView photo;


        public ViewHolder(View itemView){
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.QRmon_image);
            this.name = (TextView) itemView.findViewById(R.id.QRmon_name);
            this.longitude = (TextView) itemView.findViewById(R.id.QRmon_long);
            this.latitude = (TextView) itemView.findViewById(R.id.QRmon_lat);
            this.score = (TextView) itemView.findViewById(R.id.QRmon_score);
            this.editButton = (ImageButton) itemView.findViewById(R.id.edit_comment);
            this.deleteButton = (ImageButton) itemView.findViewById(R.id.delete_comment);
            this.comment = (TextView) itemView.findViewById(R.id.QRmon_comment);
            this.photo = (ImageView) itemView.findViewById(R.id.QRmon_photo);
        }
    }

    public GalleryRecyclerAdapter(ArrayList<QRCodeComment> qrCodeComments, Context context){
        this.qrCodeComments = qrCodeComments;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, final int listPosition){
        QRCode qrCode = qrCodeComments.get(listPosition).getQrCode();

        ImageView image = holder.image;
        TextView name = holder.name;
        TextView longitude = holder.longitude;
        TextView latitude = holder.latitude;
        TextView score = holder.score;
        ImageButton editButton = holder.editButton;
        ImageButton deleteButton = holder.deleteButton;
        TextView comment = holder.comment;
        ImageView photo = holder.photo;

        name.setText(qrCode.getName());
        longitude.setText(df.format(qrCode.getLocation().getLongitude()));
        latitude.setText(df.format(qrCode.getLocation().getLatitude()));
        score.setText(String.format("%d", qrCode.getScore()));
        comment.setText(qrCodeComments.get(listPosition).getComment());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TASK", "Edit Comment");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TASK", "delete Comment");
            }
        });

        //ON LONG CLICK
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(v.getContext(), QrDisplayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("QRCode", qrCode);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {return qrCodeComments.size();}
}
