package com.example.qrhunter;

/*
 * This class represents a QR Code
 */
public class QRCode {

    public String name;
    public String location;
    public int score;

    /*
     * Default constructor for QRCode class
     */
    public QRCode(String name, String location, int score){
        this.name = name;
        this.location = location;
        this.score = score;
    }

    // Getter and Setter methods for name, location, and score

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
}

