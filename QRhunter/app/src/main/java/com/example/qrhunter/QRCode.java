package com.example.qrhunter;

public class QRCode {
    public String name;
    public String location;

    public int score;

    public QRCode(String name, String location, int score){
        this.name = name;
        this.location = location;
        this.score = score;
    }

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

