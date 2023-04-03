package com.example.qrhunter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
     * Tests that get Total Score works for new player
     */
    @Test
    public void testEmptyTotalScore(){
        Player p = new Player();
        assertEquals(0, p.getTotalScore());
    }
    /**
     * Test that total Score works for non Empty player
     */
    @Test
    public void testTotalScore(){
        Player p = new Player();
        QRCode q1 = new QRCode("hash", "name", null, 1);
        p.addQrCode(q1);
        p.addQrCode(new QRCode("hash", "name", null, 2));
        p.addQrCode(new QRCode("hash", "name", null, 3));
        assertEquals(6, p.getTotalScore());
    }

}
