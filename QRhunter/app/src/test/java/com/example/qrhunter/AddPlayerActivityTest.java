package com.example.qrhunter;

import android.app.Activity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import com.robotium.solo.Solo;

public class AddPlayerActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<AddPlayerActivityTest> rule = new ActivityTestRule<>(MainActivity.class, true, true);

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
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }


    @Test
    public void testAddPlayerActivity() {
        assertCurrentActivity("Wrong Activity", MainActivity.class);

    }
}
