package com.example.qrhunter;

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
}

