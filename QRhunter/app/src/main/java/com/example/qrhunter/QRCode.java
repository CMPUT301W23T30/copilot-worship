package com.example.qrhunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * This class represents a QR Code
 */
public class QRCode implements Parcelable {

    public String hash;
    public String name;
    public Location location;
    public int score;
    public String comments;
    /*
     * Default constructor for QRCode class
     */
    public QRCode(String hash, String name, Location location, int score){
        this.hash = hash;
        this.location = location;
        this.score = score;
        this.name = name;
    }

    // Getter and Setter methods for hash, location, and score


    protected QRCode(Parcel in) {
        hash = in.readString();
        name = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        score = in.readInt();
    }

    public static final Creator<QRCode> CREATOR = new Creator<QRCode>() {
        @Override
        public QRCode createFromParcel(Parcel in) {
            return new QRCode(in);
        }

        @Override
        public QRCode[] newArray(int size) {
            return new QRCode[size];
        }
    };

    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeString(name);
        dest.writeParcelable(location, flags);
        dest.writeInt(score);
    }

    /**
     * Gets the unique-ish image of the qrCode
     * @param context Conext for where it will be displayed
     * @return Bitmap of QRCode representation
     */
    public Bitmap getImage(Context context){

        String digits = getDigitsHash();
        String armsFileName = "arms" + digits.substring(0,1);
        String legsFileName = "legs" + digits.substring(1,2);
        String eyesFileName = "eyes" + digits.substring(2,3);
        String mouthFileName = "mouth" + digits.substring(3,4);
        String hatFileName = "hat" + digits.substring(4,5);
        String firstSixDigitsString = digits.substring(0, 6);
        CharacterImage cI = new CharacterImage(context, armsFileName,
                legsFileName, eyesFileName, mouthFileName, hatFileName,
                firstSixDigitsString);
        return  cI.getCharacterImage();
    }

    /**
     * Turns hash into digits only
     * For image to be somewhat unique
     */
    public String getDigitsHash(){
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "";
        for(int i = 0; i < hash.length(); i++){
            String s = hash.substring(i, i+1);
            try{
                digits += Integer.parseInt(s);
            } catch (NumberFormatException e){
                digits += alphabet.indexOf(s);
            }
        }
        return digits;

    }
}

