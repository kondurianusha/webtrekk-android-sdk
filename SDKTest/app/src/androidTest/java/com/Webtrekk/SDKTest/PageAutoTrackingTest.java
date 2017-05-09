package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.TouchUtils;
import android.util.Log;
import android.widget.Button;

import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 09.05.17.
 */

public class PageAutoTrackingTest extends ActivityInstrumentationTestCase2Base<MainActivity> {

    Webtrekk mWebtrekk;

    public PageAutoTrackingTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication);
    }


    public void testActivityChanging() {

        setActivityInitialTouchMode(true);

        Instrumentation instrumentation = getInstrumentation();

        Instrumentation.ActivityMonitor pageActivityMonitor = instrumentation.addMonitor(PageExampleActivity.class.getName(), null, false);
        Instrumentation.ActivityMonitor nextPageActivityMonitor = instrumentation.addMonitor(NextPageExampleActivity.class.getName(), null, false);

        //track main activity
        initWaitingForTrack(null);
        Activity mainActivity = getActivity();

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);
        assertTrue(parcel.getValue("p").contains("Startseite"));

        //track first page activity
        initWaitingForTrack(null);

        Button pageExampleButton = (Button) mainActivity.findViewById(R.id.button_page);
        TouchUtils.clickView(this, pageExampleButton);

        Activity pageActivity = getInstrumentation().waitForMonitorWithTimeout(pageActivityMonitor, 5);
        assertNotNull(pageActivity);
        instrumentation.removeMonitor(pageActivityMonitor);

        URL = waitForTrackedURL();
        parcel.parseURL(URL);
        assertTrue(parcel.getValue("p").contains("Seite"));

        //track next page activity
        initWaitingForTrack(null);

        Button nextPageButton = (Button) pageActivity.findViewById(R.id.button_next_page);
        TouchUtils.clickView(this, nextPageButton);

        URL = waitForTrackedURL();
        parcel.parseURL(URL);
        assertTrue(parcel.getValue("p").contains("NextPage"));

        Activity nextPageActivity = getInstrumentation().waitForMonitorWithTimeout(nextPageActivityMonitor, 5);
        assertNotNull(nextPageActivity);
        instrumentation.removeMonitor(nextPageActivityMonitor);

        //track first page activity
        initWaitingForTrack(null);

        finishActivitySync(nextPageActivity);

        URL = waitForTrackedURL();
        parcel.parseURL(URL);
        assertTrue(parcel.getValue("p").contains("Seite"));

        //track main activity
        initWaitingForTrack(null);

        finishActivitySync(pageActivity);

        URL = waitForTrackedURL();
        parcel.parseURL(URL);
        assertTrue(parcel.getValue("p").contains("Startseite"));

        finishActivitySync(mainActivity);
    }
}