package com.example.qrhunter;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * This class represents a user of the app
 *
 */
public class Player {

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
        this.qrCodes = null;
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

}
