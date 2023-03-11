package com.example.qrhunter;

import android.location.Location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/*
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

    public String generateRandomName() {

        final ArrayList<String> adjectivesList = new ArrayList<String>();

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("android.resource://com.example.qrhunter/raw/english_adjectives.txt"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (scanner.hasNextLine()) {
            String word = scanner.nextLine();
            adjectivesList.add(word);
        }
        scanner.close();

        Random rand = new Random();
        String randomAdjective = adjectivesList.get(rand.nextInt(adjectivesList.size()));

        return randomAdjective;
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

