package com.example.qrhunter;

import android.app.Activity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * Intent Test Class for LeaderboardActivity. Tests are written using Robotium
 */

@RunWith(AndroidJUnit4.class)
public class LeaderboardTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }


    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }
}