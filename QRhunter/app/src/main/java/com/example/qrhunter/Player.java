package com.example.qrhunter;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class represents a user of the app
 *
 */
public class Player implements Parcelable {

    /**
     * Player username
     */
    private String username;
    /**
     * Players email
     */
    private String email;
    /**
     * Players number
     */
    private int number;
    /**
     * QR Codes associated with the player
     */
    private ArrayList<QRCode> qrCodes;

    private String id;
    /**
     * Test constructor with invalid username for testing
     * provides us with an invalid username (above 20 characters, so that we are sure
     * it is not in the database)
     */
    public Player(){
        this.username = "qwertyuiopasdfghjklzxcvbnm";//Invalid username
        this.email = "Default email";
        this.number = 0;
        this.qrCodes = null;
    }
    /**
     * Test constructor for player class
     * Sets the username only, everything else test values
     */
    public Player(String username){
        this.username = username;
        this.email = "Default email";
        this.number = 0;
        this.qrCodes = new ArrayList<>();
        this.id = username;
    }

    /**
     *  Constructor for a Player object
     * @param username - Player's username
     * @param  email - Player's email
     * @param number - Player's number
     * @param qrCodes - ArrayList of QRCodes
     */
    public Player(String username, String email, int number, ArrayList<QRCode> qrCodes){
        this.username = username;
        this.email = email;
        this.number = number;
        this.qrCodes = qrCodes;
    }

    // Getter and Setter methods for username, email, number, and QRCodes


    protected Player(Parcel in) {
        username = in.readString();
        email = in.readString();
        number = in.readInt();
        qrCodes = in.createTypedArrayList(QRCode.CREATOR);
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public ArrayList<QRCode> getQrCodes() { return qrCodes; }
    public void setQrCodes(ArrayList<QRCode> qrCodes) { this.qrCodes = qrCodes; }
    public void addQrCode(QRCode qrcode){this.qrCodes.add(qrcode);}

    /**
     * TODO Write tests for this
     * @return total score of player
     */
    public int getTotalScore() {
        if (qrCodes.size() == 0) {return 0;}
        int totalScore = 0;
        for(QRCode qr: qrCodes){
            totalScore += qr.getScore();
        }

        return totalScore;
    }

    public int getSquishy(){
        ArrayList<Integer> scores = new ArrayList<>();
        for (QRCode qr: qrCodes){
            scores.add(qr.getScore());
        }

        return Collections.min(scores);
    }

    public int getBeefy(){
        ArrayList<Integer> scores = new ArrayList<>();
        for (QRCode qr: qrCodes){
            scores.add(qr.getScore());
        }

        return Collections.max(scores);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeInt(number);
        dest.writeTypedList(qrCodes);
    }
}
