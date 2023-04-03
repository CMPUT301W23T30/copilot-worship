package com.example.qrhunter;

import android.content.Context;
import android.util.Log;

import com.google.common.hash.Hashing;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

public class GenerateQReatureAttributes {
    private Context context;

    public GenerateQReatureAttributes(Context context) {
        this.context = context;
    }

    /**
     * This method hashes the QR code to SHA256 and returns the hashed hex string
     *
     * @param unhashedQRCode the unhashed QR code (String)
     * @return the hashed QR code (String)
     * @author Maarij
     */
    String hasher(String unhashedQRCode) {
        final String hashed = Hashing.sha256()
                .hashString(unhashedQRCode, StandardCharsets.UTF_8)
                .toString();
        return hashed;
    }

    /**
     * This method calculates the score of the QR code based on the number of contiguous repeated numbers or characters
     *  - Each number or character is equal to number^(n-1) points where n is the number of times it is repeated
     *  - If the number is 0, it is equal to 20^(n-1) points
     *
     * @param hashedQRCode the hashed QR code (String)
     * @return the score of the QR code (int)
     * @author Maarij
     */
    int scoreCalculator(String hashedQRCode) {
        int score = 0;
        int n = 0;
        String prev = "";
        for (int i = 0; i < hashedQRCode.length(); i++) {
            if (hashedQRCode.substring(i, i + 1).equals(prev)) {
                n++;
            } else {
                if (n > 1) {
                    if (prev.equals("0")) {
                        score += Math.pow(20, n - 1);
                    } else {
                        score += Math.pow(Integer.parseInt(prev, 16), n - 1);
                    }
                }
                n = 1;
                prev = hashedQRCode.substring(i, i + 1);
            }
        }
        return score;

    }

    /**
     * This method generates the name of the QReature based on the first 6 digits of the hashed QR code
     *   - The first 3 digits are used to select an adjective from the list of adjectives (english_adjectives.txt)
     *      - These were taken from https://copylists.com/words/list-of-adjectives/
     *
     *   - The last 3 digits are used to select a noun from the list of nouns (nouns_list.txt)
     *      - These are a mixture of Old English and Yiddish insults,
     *        as well as some Hitchhiker's Guide to the Galaxy and Discworld characters
     *
     * @param firstSixDigits the first 6 digits of the hashed QR code (String)
     * @return the name of the QReature (String)
     * @author Maarij
     */
    public String generateName(String firstSixDigits) {

        final ArrayList<String> adjectivesList = new ArrayList<String>();
        final ArrayList<String> nounsList = new ArrayList<String>();

        InputStream adjectivesFile = context.getResources().openRawResource(R.raw.english_adjectives);
        InputStream nounsFile = context.getResources().openRawResource(R.raw.nouns_list);

        Scanner adjectivesScanner = new Scanner(adjectivesFile);
        Scanner nounsScanner = new Scanner(nounsFile);

        while (adjectivesScanner.hasNextLine()) {
            String word = adjectivesScanner.nextLine();
            adjectivesList.add(word);
        }
        adjectivesScanner.close();

        while (nounsScanner.hasNextLine()) {
            String word = nounsScanner.nextLine();
            nounsList.add(word);
        }
        nounsScanner.close();

        /*
        Random rand = new Random();
        String adjective = adjectivesList.get(rand.nextInt(adjectivesList.size()));
        adjective = randomAdjective.substring(0, 1).toUpperCase() + randomAdjective.substring(1);
        String noun = nounsList.get(rand.nextInt(nounsList.size()));
        */

        int firstFourDigits = Integer.parseInt(firstSixDigits.substring(0, 4));
        int adjectiveIndex = firstFourDigits - (firstFourDigits / adjectivesList.size()) * adjectivesList.size();
        String adjective = adjectivesList.get(adjectiveIndex);
        adjective = adjective.substring(0, 1).toUpperCase() + adjective.substring(1);

        int lastTwoDigits = Integer.parseInt(firstSixDigits.substring(4, 6));
        int nounIndex = lastTwoDigits - (lastTwoDigits / nounsList.size()) * nounsList.size();
        String noun = nounsList.get(nounIndex);

        return adjective + " " + noun;

    }

    /**
     * This method returns the first six digits of the hashed QR code (for non-random generation purposes)
     *
     * @param hashedQRCode the hashed QR code (String)
     * @return the first six digits of the hashed QR code (String)
     * @author Maarij
     */
    String getFirstSixDigits(String hashedQRCode) {
        return new BigInteger(hashedQRCode, 16).toString().substring(0, 6);
    }

    /**
     * This method returns the first six digits of the hashed QR code (for random generation purposes)
     *
     * @param firstSixDigitsString the first six digits of the hashed QR code (String)
     * @return It returns a test character of the CharacterImage class, which can be used to generate the character image
     * @author Maarij
     */
    CharacterImage characterCreator(String firstSixDigitsString) {
        String armsFileName, legsFileName, eyesFileName, mouthFileName, hatFileName;

//        armsFileName = "arms" + (int) (Math.random() * 10);
//        legsFileName = "legs" + (int) (Math.random() * 10);
//        eyesFileName = "eyes" + (int) (Math.random() * 10);
//        mouthFileName = "mouth" + (int) (Math.random() * 10);
//        hatFileName = "hat" + (int) (Math.random() * 10);

        armsFileName = "arms" + firstSixDigitsString.substring(0, 1);
        legsFileName = "legs" + firstSixDigitsString.substring(1, 2);
        eyesFileName = "eyes" + firstSixDigitsString.substring(2, 3);
        mouthFileName = "mouth" + firstSixDigitsString.substring(3, 4);
        hatFileName = "hat" + firstSixDigitsString.substring(4, 5);


        CharacterImage testCharacter = new CharacterImage(context, armsFileName, legsFileName, eyesFileName, mouthFileName, hatFileName, firstSixDigitsString);

        return testCharacter;
    }
}