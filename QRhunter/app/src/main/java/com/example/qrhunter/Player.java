package com.example.qrhunter;

import java.util.ArrayList;

/*
 * This class represents a user of the app
 */
public class Player {

    private String username;
    private String email;
    private int number; //TODO : Format number?
    private ArrayList<QRCode> qrCodes;

    /**
     * Default constructor for player class
     * Sets the username to "Default username", email to "Default email",
     * number to 0, codes to null
     */
    public Player(){
        this.username = "Default username";
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
