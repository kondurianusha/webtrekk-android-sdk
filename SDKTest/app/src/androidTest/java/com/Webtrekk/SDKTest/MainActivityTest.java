package com.Webtrekk.SDKTest;
import android.test.ActivityInstrumentationTestCase2;

import com.webtrekk.webtrekksdk.Webtrekk;

import com.Webtrekk.SDKTest.MainActivity;

/**
 * Created by user on 09/11/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mMainActivity;
    private Webtrekk wt;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mMainActivity = getActivity();
        wt = mMainActivity.getWebtrekk();
    }

    public void testPreconditions() {
        //Try to add a message to add context to your assertions. These messages will be shown if
        //a tests fails and make it easy to understand why a test failed
        assertNotNull("mFirstTestActivity is null", mMainActivity);
    }

    /**
     * make sure webtrekk was initialized
     */
    public void testInitWebtrekk() {
        assertNotNull(mMainActivity.getWebtrekk());
    }


    /**
     * test that all default sdk values are overriden by the local ones form the local config
     */
    public void testAllValuesOverrideWithApplicationConfig() {

        assertTrue(wt.getTrackDomain().contains("://q3.webtrekk.net"));
        assertEquals(wt.getVersion(), 1);
        assertEquals(wt.getTrackId(), "123451234512345");
        assertEquals(wt.getSampling(), 0);
        assertEquals(wt.getSendDelay(), 30);
        //assertEquals(wt.getResendOnStartEventTime(), 303);
        assertEquals(wt.getMaxRequests(), 100);
        assertEquals(wt.getTrackingConfigurationUrl(), "https://d1r27qvpjiaqj3.cloudfront.net/238713152098253/34629.xml");
        assertEquals(wt.isAutoTracked(), true);
        assertEquals(wt.isAutoTrackApiLevel(), true);

    }

    /**
     * test that when a config value is invalid it chooses the default from the sdk
     */
    public void testInvalidValuesInApplicationConfig() {
        assertEquals(wt.isEnableRemoteConfiguration(), false);
    }

    /**
     * test that when an empty value is provided it also uses the default from the sdk
     */

    public void testEmptyValuesInApplicationConfig() {
        assertEquals(wt.getResendOnStartEventTime(), 30);
    }
}
