package com.example.qrhunter;

import android.app.Activity;
import android.widget.ArrayAdapter;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Check UI Of Gallery, need seperate test for GalleryAdapter(?)
 */

@RunWith(AndroidJUnit4.class)
public class GalleryTest {
    private Player testPlayer = new Player("testPlayer");
    private ArrayAdapter<GalleryAdapter> testGalleryAdapter;

    private String testName = "QRMON";
    private Integer testScore = 43;
    private Double testLat = 36.93179;
    private Double testLong = 54.90824;

    private Database testDB;
    private FirebaseFirestore fireDB = FirebaseFirestore.getInstance();

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);


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

//    /**
//     * Adds a Test Player
//     */
//
//    @Test
//    public void addTestPlayer() {
//        testDB.addPlayer(testPlayer);
//    }

//    /**
//     * Enter activity, check if Gallery is empty.
//     * Go back.
//     * @throws Exception
//     */
//
//    @Test
//    public void intentCheckGallery() throws Exception {
//
//    }
//
//    @Test
//    public void addTestScannedQR() {
//        Location testLocation = new Location ("");
//        testLocation.setLatitude(testLat);
//        testLocation.setLatitude(testLong);
//
//        QRCode testQR = new QRCode(testName, testName, testLocation, testScore);
//
//        testDB.addScannedCode(testQR, testPlayer);
//    }
//
//    /**
//     * Enter activity and see if the QR is displayed in Player gallery
//     * check for correct results
//     * @throws Exception
//     */
//    @Test
//    public void searchPlayerTest() throws Exception {
//        solo.clickOnView(solo.getView(R.id.navbar_gallery_button));
//        solo.assertCurrentActivity("Wrong Activity", Gallery.class);
//    }


}
