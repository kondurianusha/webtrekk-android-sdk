package wtrack.tracking;

import android.app.Application;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;

import wtrack.tracking.HelperFunctions;
import wtrack.tracking.TrackingParams;

/**
 * Created by user on 03/03/15.
 */


public class TestApplication extends ApplicationTestCase {
    public TestApplication(Class applicationClass) {
        super(applicationClass);
    }

    private class MockApplication extends WTrackApplication {

    }

    WTrackApplication app;

    public void setUp() {
        createApplication();
        app = (WTrackApplication)getApplication();
    }

    public void test_getTracker() {
        // for now there is only one default tracker in use, later it might be good to have separate trackers
        Tracker t = app.getTracker("default");

        t.track(Tracker.Events.APP_STARTET);
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
