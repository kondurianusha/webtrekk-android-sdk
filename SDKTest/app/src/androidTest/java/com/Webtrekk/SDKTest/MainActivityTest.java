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

        assertEquals(wt.getTrackDomain(), "http://trackingtest.nglab.orgO");
        assertEquals(wt.getVersion(), 3);
        assertEquals(wt.getTrackId(), "1111111111112O");
        assertEquals(wt.getSampling(), 333);
        assertEquals(wt.getSendDelay(), 602);
        //assertEquals(wt.getResendOnStartEventTime(), 303);
        assertEquals(wt.getMaxRequests(), 4000);
        assertEquals(wt.getTrackingConfigurationUrl(), "http://remotehost/tracking_config.xml");
        assertEquals(wt.isAutoTracked(), false);
        assertEquals(wt.isAutoTrackApiLevel(), false);

    }

    /**
     * test that when a config value is invalid it chooses the default from the sdk
     */
    public void testInvalidValuesInApplicationConfig() {
        assertEquals(wt.isEnableRemoteConfiguration(), true);
    }

    /**
     * test that when an empty value is provided it also uses the default from the sdk
     */

    public void testEmptyValuesInApplicationConfig() {
        assertEquals(wt.getResendOnStartEventTime(), 30);
    }
}
