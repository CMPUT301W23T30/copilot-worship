package com.example.qrhunter;

import android.app.Activity;
import android.view.View;
import android.widget.SearchView;
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
 * Intent Test Class for SearchPlayerActivity. Tests are written using Robotium
 */

@RunWith(AndroidJUnit4.class)
public class SearchPlayerTest {
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
     * Enter activity through navbar
     * @throws Exception
     */
    @Test
    public void intentButtonTest() throws Exception {
        solo.clickOnView(solo.getView(R.id.navbar_search_button));
        solo.assertCurrentActivity("Wrong Activity", SearchPlayerActivity.class);
    }

    /**
     * Enter activity and go back, check if returns correctly
     * @throws Exception
     */
    @Test
    public void intentGoBackTest() throws Exception {
        solo.clickOnView(solo.getView(R.id.navbar_search_button));
        solo.assertCurrentActivity("Wrong Activity", SearchPlayerActivity.class);
        solo.goBack();
        solo.assertCurrentActivity("Go back didn't work", MainActivity.class);
    }

    /**
     * Enter activity and search for player "Player-2"
     * check for correct results
     * @throws Exception
     */
    @Test
    public void searchPlayerTest() throws Exception {
        solo.clickOnView(solo.getView(R.id.navbar_search_button));
        solo.assertCurrentActivity("Wrong Activity", SearchPlayerActivity.class);

        // I can't seem to get click on view to work,
        // in this case search is in a menu as an icon with no text so "" refers to
        // the blank search item in the menu
        solo.clickOnMenuItem("");
        View searchView = solo.getCurrentViews(SearchView.class).get(0);
        solo.clickOnView(searchView);
        solo.enterText(0,"Player-2");

        assertTrue(solo.searchText("Player-20"));
        assertTrue(solo.searchText("Player-2"));
    }
}
