package com.example.qrhunter;

import static org.junit.Assert.assertEquals;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class HasherAndScoreTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    /**
     * Gets the Activity
     * @throws Exception
     */

    @Test
    public void testHasher() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        final GenerateQReatureAttributes generateQReatureAttributes = new GenerateQReatureAttributes(solo.getCurrentActivity());
        String QrToHash = "https://twitter.com/yegvalleyLRT";
        String hashed = generateQReatureAttributes.hasher(QrToHash);
        assertEquals("7e33059f42adee31abd5ec2da8d4ccea35283aa01a47976af97f5ab5672fa412", hashed);

    }

    @Test
    public void testScore() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        final GenerateQReatureAttributes generateQReatureAttributes = new GenerateQReatureAttributes(solo.getCurrentActivity());
        String QrToHash = "https://twitter.com/yegvalleyLRT";
        String hashed = generateQReatureAttributes.hasher(QrToHash);
        int score = generateQReatureAttributes.scoreCalculator(hashed);
        assertEquals(39, score);
    }
}
