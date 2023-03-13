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

}
