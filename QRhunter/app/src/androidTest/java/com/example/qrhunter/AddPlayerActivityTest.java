package com.example.qrhunter;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

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
        solo.waitForActivity(AddPlayerActivity.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", AddPlayerActivity.class);
    }

    // TODO: Crashes cuz username already exists
    @Test
    public void testAddPlayerActivityConditionChecksAndSubmit() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.menu_add_player_button));
        solo.waitForActivity(AddPlayerActivity.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", AddPlayerActivity.class);

        TextView errorText = (TextView) solo.getView(R.id.errorText);
        solo.clickOnView(solo.getView(R.id.randomize_player_char_button));

        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "");
        solo.enterText((EditText) solo.getView(R.id.emailEditText), "");
        solo.enterText((EditText) solo.getView(R.id.numberEditText), "");

        solo.clickOnView(solo.getView(R.id.submitButton));
        solo.sleep(1000);
        assertEquals("Username must be between 1-20 Characters", errorText.getText().toString());
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "test1234567890987654321123");
        solo.clickOnView(solo.getView(R.id.submitButton));
        solo.sleep(1000);
        assertEquals("Username must be between 1-20 Characters", errorText.getText().toString());
        solo.clearEditText((EditText) solo.getView(R.id.usernameEditText));
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "test");
        solo.sleep(1000);

        solo.clickOnView(solo.getView(R.id.submitButton));
        solo.sleep(1000);
        assertEquals("You need an email silly!", errorText.getText().toString());
        solo.clearEditText((EditText) solo.getView(R.id.emailEditText));
        solo.enterText((EditText) solo.getView(R.id.emailEditText), "test@test.com");

        solo.clickOnView(solo.getView(R.id.submitButton));
        solo.sleep(1000);
        assertEquals("Forgetting something?", errorText.getText().toString());
        solo.clearEditText((EditText) solo.getView(R.id.numberEditText));
        solo.enterText((EditText) solo.getView(R.id.numberEditText), "1234567890");

        solo.clickOnView(solo.getView(R.id.submitButton));
        solo.waitForActivity(MainActivity.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

    }


    @Test
    public void testAddPlayerActivityCancel() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.menu_add_player_button));
        solo.waitForActivity(AddPlayerActivity.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", AddPlayerActivity.class);

        solo.goBack();
        solo.waitForActivity(MainActivity.class, 2000);
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
}

