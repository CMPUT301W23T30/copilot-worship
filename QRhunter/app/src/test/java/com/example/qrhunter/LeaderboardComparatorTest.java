package com.example.qrhunter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LeaderboardComparatorTest {
    /**
     * Test that it compares equal Players Properly
     */
    @Test
    public void equalTest(){
        LeaderboardModel lm1 = new LeaderboardModel("p1", 1);
        LeaderboardModel lm2 = new LeaderboardModel("p2", 1);
        LeaderboardComparator lbc = new LeaderboardComparator();
        int result = lbc.compare(lm1, lm2);
        assertEquals(0, result);
    }

    /**
     * Test that it compares equal Players Properly
     */
    @Test
    public void moreTest(){
        LeaderboardModel lm1 = new LeaderboardModel("p1", 1);
        LeaderboardModel lm2 = new LeaderboardModel("p2", 2);
        LeaderboardComparator lbc = new LeaderboardComparator();
        int result = lbc.compare(lm1, lm2);
        assertTrue("Wrong number", result > 0);
    }

    /**
     * Test that it compares equal Players Properly
     */
    @Test
    public void lessTest(){
        LeaderboardModel lm1 = new LeaderboardModel("p1", 2);
        LeaderboardModel lm2 = new LeaderboardModel("p2", 1);
        LeaderboardComparator lbc = new LeaderboardComparator();
        int result = lbc.compare(lm1, lm2);
        assertTrue("Wrong number " + result, result < 0);
    }
}
