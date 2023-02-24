package com.example.qrhunter;

import java.util.ArrayList;

/**
 * This represents a user of the app
 */
public class Player {

    private String username;
    private String email;
    private int number; //TODO : Format number?
    private ArrayList<QRCode> codes;
    /**
     * Default constructor for player class
     * Sets the username to "Default username"
     */
    public Player(){
        this.username = "Default username";
        this.email = "Default email";
        this.number = 0;
        this.codes = null;
    }

    /**
     *  Constructor for a Player object
     * @param username
     * @param email
     * @param number
     * @param codes - ArrayList of QRCodes
     */
    public Player(String username, String email, int number, ArrayList<QRCode> codes){
        this.username = username;
        this.email = email;
        this.number = number;
        this.codes = codes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ArrayList<QRCode> getCodes() {
        return codes;
    }

    public void setCodes(ArrayList<QRCode> codes) {
        this.codes = codes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
