package com.example.qrhunter;

import static androidx.core.content.ContextCompat.startActivity;

import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PlayerGalleryTest {
//    private Solo solo;
//    @Rule
//    public ActivityTestRule<MainActivity> rule =
//            new ActivityTestRule<>(MainActivity.class, true, true);
//    /**
//     * Runs before all tests and creates solo instance.
//     * @throws Exception
//     */
//    @Before
//    public void setUp() throws Exception{
//
//        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
//    }
//
//
//    /**
//     * Gets the Activity
//     * @throws Exception
//     */
//    @Test
//    public void start() throws Exception{
//        Activity activity = rule.getActivity();
//    }
//
//    /**
//     * Tests we can open the activity and see that more than
//     * one person has scanned the QR Code
//     * TODO test when no player has scanned the QR Code
//     */
//    @Test
//    public void open(){
//        ImageButton button = (ImageButton) solo.getView(R.id.navbar_gallery_button);
//        //Mock the on click listener with a new bundle here
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Bundle bundle = new Bundle();
//                Intent intent = new Intent(solo.getCurrentActivity(),Gallery.class);
//                bundle.putString("username", new Player().getUsername());
//                intent.putExtras(bundle);
//
//                solo.getCurrentActivity().startActivity(intent);
//            }
//        });
//        solo.clickOnView(button);
//        solo.assertCurrentActivity("Wrong Activity", Gallery.class);
//        Gallery activity = (Gallery) solo.getCurrentActivity();
//        ListView l = activity.galleryView;
//
//        GalleryAdapter adapter = new GalleryAdapter(activity.getBaseContext(), activity.qrCodeArrayList);
//        l.setAdapter(adapter);
//
//        //Wait for atleast 3 seconds before attmepting to click on view
//        Long start = System.currentTimeMillis();
//        Long end = Long.valueOf("0");
//        while (l.getChildAt(0) == null || end - start == Long.valueOf("3000")){
//           end = System.currentTimeMillis();
//        }
//        //Mock opening the activity
//        final String[] hash = new String[1];
//        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Intent intent = new Intent(activity, QrDisplayActivity.class);
//                Bundle b = new Bundle();
//                hash[0] = adapter.getItem(i).getHash();
//                b.putString("hash", adapter.getItem(i).getHash());
//                intent.putExtras(b);
//                solo.getCurrentActivity().startActivity(intent);
//
//            }
//        });
//        solo.clickOnView(l.getChildAt(0));
//        solo.assertCurrentActivity("Wrong Activity", QrDisplayActivity.class);
//        Button b = (Button) solo.getView(R.id.other_players);
//        //Mock opening the other players page
//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(
//                        solo.getCurrentActivity(),
//                        PlayerGalleryActivity.class);
//                Bundle b = new Bundle();
//                b.putString("hash", hash[0]);
//                intent.putExtras(b);
//                solo.getCurrentActivity().startActivity(intent);
//            }
//        });
//        solo.clickOnView(b);
//        solo.assertCurrentActivity("Wrong Activity", PlayerGalleryActivity.class);
//        PlayerGalleryActivity galleryActivity = (PlayerGalleryActivity) solo.getCurrentActivity();
//        //Wait for atleast 3 seconds before attmepting to click on view
//        start = System.currentTimeMillis();
//        end = Long.valueOf("0");
//        while (galleryActivity.galleryAdapter == null || end - start == Long.parseLong("3000")){
//            end = System.currentTimeMillis();
//        }
//        assertTrue(galleryActivity.galleryAdapter.getCount() > 1);
//
//    }
//
//    /**
//     * Close activity after each test
//     * @throws Exception
//     */
//    @After
//    public void tearDown() throws Exception{
//        solo.finishOpenedActivities();
//    }


}
