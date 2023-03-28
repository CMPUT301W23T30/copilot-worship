package com.example.qrhunter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;

public class PlayerTest {

    /**
     * Test that shows the default constructor provides an invalid Player
     */
    @Test
    public void testDefaultConstructor(){
        Player p = new Player();
        assertTrue("Default Constructor does not provide invalid username"
                , p.getUsername().length() > 20);

    }

    /**
    * Test that the total score returns the correct sum
     */
    @Test
    public void totalScoreTest(){
        Player p = new Player();
        QRCode qr1 = new QRCode("hash", "name", null, 1);
        QRCode qr2 = new QRCode("hash", "name", null, 2);
        QRCode qr3 = new QRCode("hash", "name", null, 3);

        ArrayList<QRCode> codes = new ArrayList<>();

        codes.add(qr1); codes.add(qr2); codes.add(qr3);

        p.setQrCodes(codes);
        assertEquals(6, p.getTotalScore());
    }

    /**
     * Tests that a player with no score return 0
     */
    @Test
    public void noScoreTest(){
        Player p = new Player();
        assertEquals(0, p.getTotalScore());
    }

}
