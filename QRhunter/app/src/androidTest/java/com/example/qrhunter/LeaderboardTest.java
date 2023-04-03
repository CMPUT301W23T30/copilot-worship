package com.example.qrhunter;

import android.app.Activity;
import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

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

    /**
     * test enter activity through navbar
     * @throws Exception
     */
    @Test
    public void intentButtonTest() throws Exception {
        solo.clickOnView(solo.getView(R.id.navbar_ranking_button));
        solo.assertCurrentActivity("Wrong Activity", LeaderboardActivity.class);
    }

    /**
     * test intent shift on top 1
     * Enter activity and click on top ranker, check if intent shift correctly
     * check if player name has continuity
     * @throws Exception
     */
    @Test
    public void intentFirstPlaceTest() throws Exception {
        solo.clickOnView(solo.getView(R.id.navbar_ranking_button));
        solo.assertCurrentActivity("Wrong Activity", LeaderboardActivity.class);

        solo.clickOnView(solo.getView(R.id.first_place));
        solo.assertCurrentActivity("Wrong Activity", OtherProfiles.class);
        TextView textView = (TextView)solo.getView(R.id.first_place_name);
        String firstPlaceUsername = textView.getText().toString();
        assertTrue(solo.searchText(firstPlaceUsername));
    }
}