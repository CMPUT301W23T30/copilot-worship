package com.example.qrhunter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{
    private ArrayList<QRCodeComment> qrCodeComments;
    private String username;
    private Context context;
    private String qrComment;

    public interface OnItemLongClickListener{
        public boolean onItemLongClicked(int position);
    }

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;TextView name;TextView longitude;TextView latitude;TextView score;
        ImageButton showMore; ImageButton showLess;
        ImageButton editButton;ImageButton saveButton;
        CardView commentCard; TextView comment;EditText editComment;
        ImageView photo;

        public ViewHolder(View itemView){
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.QRmon_image);
            this.name = (TextView) itemView.findViewById(R.id.QRmon_name);
            this.longitude = (TextView) itemView.findViewById(R.id.QRmon_long);
            this.latitude = (TextView) itemView.findViewById(R.id.QRmon_lat);
            this.score = (TextView) itemView.findViewById(R.id.QRmon_score);

            this.showMore = (ImageButton) itemView.findViewById(R.id.show_more_button);
            this.showLess = (ImageButton) itemView.findViewById(R.id.show_less_button);

            this.commentCard = (CardView) itemView.findViewById(R.id.cardView);
            this.editButton = (ImageButton) itemView.findViewById(R.id.edit_comment);
            this.saveButton = (ImageButton) itemView.findViewById(R.id.save_comment);
            this.comment = (TextView) itemView.findViewById(R.id.QRmon_comment);
            this.editComment = (EditText) itemView.findViewById(R.id.QRmon_edit_comment);
            this.photo = (ImageView) itemView.findViewById(R.id.QRmon_photo);

        }
    }

    public GalleryAdapter(ArrayList<QRCodeComment> qrCodeComments, String username, Context context){
        this.qrCodeComments = qrCodeComments;
        this.username = username;
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
        qrComment = qrCodeComments.get(listPosition).getComment();

        //Standard Information of QRCode
        ImageView image = holder.image;
        TextView name = holder.name;
        TextView longitude = holder.longitude;
        TextView latitude = holder.latitude;
        TextView score = holder.score;

        //TODO set QRCode Image
        name.setText(qrCode.getName());
        longitude.setText(df.format(qrCode.getLocation().getLongitude()));
        latitude.setText(df.format(qrCode.getLocation().getLatitude()));
        score.setText(String.format("%d", qrCode.getScore()));

        //ShowMore and ShowLess Buttons
        ImageButton showMore = holder.showMore;
        ImageButton showLess = holder.showLess;


        //Extra Information of QRCode
        CardView commentCard = holder.commentCard;
        ImageButton editButton = holder.editButton;
        ImageButton saveButton = holder.saveButton;
        TextView comment = holder.comment;
        EditText editComment = holder.editComment;
        ImageView photo = holder.photo;


        //TODO SET QRCode Photo
        comment.setText(qrComment);
        //set picture
        Database db = new Database();
        db.getPlayerFromUsername(username).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    db.getQRPicture(doc.getString("id"), qrCode.getHash())
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    //From https://stackoverflow.com/questions/7359173/create-bitmap-from-bytearray-in-android
                                    //TODO Cite properly
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inMutable = true;
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                                    photo.setImageBitmap(bmp);
                                }
                            });
                }
            }
        });


        //TODO don't forget to HIDE QRCode photo
        //Hide Extras
        showLess.setVisibility(View.GONE); //Will control visibility of everything that comes after it

        commentCard.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        editComment.setVisibility(View.GONE);

        //ShowMore sets Extra Information to Visible
        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMore.setVisibility(View.GONE);

                //TODO don't forget to SHOW QRCode photo
                showLess.setVisibility(View.VISIBLE); //Will control visibility of everything that comes after it

                commentCard.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                editComment.setVisibility(View.VISIBLE);
            }
        });

        //Sets comment to new string input by user
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment.setVisibility(View.GONE);
                editComment.setVisibility(View.VISIBLE);

                //Redirects focus to EditText
                //Opens soft keyboard
                editComment.requestFocus();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        //Save comment to local and firebase storage
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrComment = editComment.getText().toString();

                editComment.setVisibility(View.GONE);
                comment.setText(qrComment);
                comment.setVisibility(View.VISIBLE);

                Database db = new Database();
                db.getPlayerFromUsername(username).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot doc: queryDocumentSnapshots ){
                            db.editComment(doc.getString("id"), qrComment, qrCode.getHash());

                            //Hides soft keyboard
                            //https://stackoverflow.com/questions/19451395/how-to-hide-the-soft-keyboard-in-android-after-doing-something-outside-of-editte
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                            Toast.makeText(context, "Your comment has been saved.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

        //Hides the extra information again
        showLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO don't forget to REHIDE QRCode photo
                showLess.setVisibility(View.GONE); //Will control visibility of everything that comes after it

                commentCard.setVisibility(View.GONE);
                editButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.GONE);
                editComment.setVisibility(View.GONE);

                showMore.setVisibility(View.VISIBLE);
            }
        });


        //ON LONG CLICK
        //Goes to QRDisplayActivity to see QR details
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(v.getContext(), QrDisplayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("currentUsername", username);
                bundle.putParcelable("QRCode", qrCode);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {return qrCodeComments.size();}

    //https://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
}
