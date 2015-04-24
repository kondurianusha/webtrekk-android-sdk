package com.webtrekk.android.test;

import android.test.AndroidTestCase;

import com.webtrekk.android.tracking.HelperFunctions;
import com.webtrekk.android.tracking.Tracker;
import com.webtrekk.android.tracking.TrackingParams;
import com.webtrekk.android.tracking.WTrackApplication;


/**
 * Created by user on 03/03/15.
 */

public class TestApplication extends AndroidTestCase {


    WTrackApplication app;


    public TestApplication() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

    }
    public void test_getTracker() {
        // for now there is only one default tracker in use, later it might be good to have separate trackers
        Tracker t = app.getTracker("default");

        t.track("hi");
    }
    public void test_helperFunktionsNexus4() {
        assertEquals(HelperFunctions.getAPILevel(), "21");
        assertEquals(HelperFunctions.getDevice(), "Google Nexus 4");
        assertTrue(HelperFunctions.generateEverid().length() > 4);
        assertEquals(HelperFunctions.getCountry(), "germany");
        assertEquals(HelperFunctions.getOSVersion(), "Lollipop");
        HelperFunctions.getAdvertiserID(app);
        assertEquals(app.getWTRack().getAuto_tracked_values().get(TrackingParams.Params.ADVERTISER_ID), "asdf");
    }
}
