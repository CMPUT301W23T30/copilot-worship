package com.example.qrhunter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

/**
 * Abstraction for interfacing with database document objects for SearchPlayer
 * @author X
 */
public class SearchModel {
    private final Database db = new Database();

    String username;
    Bitmap bmp;

    public Bitmap getBmp() {
        return bmp;
    }

    SearchModel(String username){
        this.username = username;
        db.getPlayerFromUsername(username).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    db.getProfilePicture(doc.getString("id")).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            //From https://stackoverflow.com/questions/7359173/create-bitmap-from-bytearray-in-android
                            //TODO Cite properly
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inMutable = true;
                            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                        }
                    });
                }
            }
        });

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
