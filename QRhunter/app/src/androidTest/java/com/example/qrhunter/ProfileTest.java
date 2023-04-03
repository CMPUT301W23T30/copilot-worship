package com.example.qrhunter;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Enter activity through navbar
     * @throws Exception
     */
    @Test
    public void intentButtonTest() throws Exception {
        solo.clickOnView(solo.getView(R.id.navbar_gallery_button));
        solo.assertCurrentActivity("Wrong Activity", Gallery.class);
    }

    /**
     * Enter activity and go back, check if returns correctly
     * @throws Exception
     */
    @Test
    public void intentGoBackTest() throws Exception {
        solo.clickOnView(solo.getView(R.id.navbar_gallery_button));
        solo.assertCurrentActivity("Wrong Activity", Gallery.class);
        solo.goBack();
        solo.assertCurrentActivity("Go back didn't work", MainActivity.class);
    }

}
