package com.example.qrhunter;

import android.app.Activity;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AddPlayerActivityTest {

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
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }


    @Test
    public void testAddPlayerActivityOpened() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.menu_add_player_button));
        solo.assertCurrentActivity("Wrong Activity", AddPlayerActivity.class);
    }

    @Test
    public void testAddPlayerActivitySubmit() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.menu_add_player_button));
        solo.assertCurrentActivity("Wrong Activity", AddPlayerActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "test");
        solo.enterText((EditText) solo.getView(R.id.emailEditText), "test@test.com");
        solo.enterText((EditText) solo.getView(R.id.numberEditText), "1234567890");
        solo.clickOnView(solo.getView(R.id.submitButton));
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
}

