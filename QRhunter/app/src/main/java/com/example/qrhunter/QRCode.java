package com.example.qrhunter;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

/**
 * This class represents a QR Code
 */
public class QRCode {

    public String hash;
    public String name;
    public Location location;

    public int score;
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

}

