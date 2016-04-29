package com.Webtrekk.SDKTest;


import android.app.Activity;
import android.content.Intent;

import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.util.Random;
import java.util.UUID;

/**
 * Created by vartbaronov on 26.04.16.
 */
public class MiscellaneousTest  extends ActivityInstrumentationTestCase2Base<NoAutoTrackActivity> {
    private Webtrekk mWebtrekk;
    private final long oneMeg = 1024 * 1024;


    public MiscellaneousTest() {
        super(NoAutoTrackActivity.class);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
    }

    public void testOverrideEverID()
    {
        String oldEverID = mWebtrekk.getEverId();
        final String newEverID = "NewEverID"+ UUID.randomUUID().toString();

        mWebtrekk.setEverId(newEverID);

        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        String URL = waitForTrackedURL();

        assertTrue(URL.contains(newEverID));
        assertFalse(URL.contains(oldEverID));

        // check for event tracking
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                TrackingParameter pt = new TrackingParameter();
                pt.add(TrackingParameter.Parameter.ACTION_NAME, "test");
                mWebtrekk.track(pt);
            }
        });

        URL = waitForTrackedURL();

        assertTrue(URL.contains(newEverID));
        assertFalse(URL.contains(oldEverID));

    }

    public void testMediaCodeSet()
    {
        final String mediaCode = "mediaCode";

        // just ordinary track. no media code
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        String URL = waitForTrackedURL();

        assertFalse(URL.contains("&"+TrackingParameter.Parameter.ADVERTISEMENT+"="));

        // media code check
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.setMediaCode(mediaCode);
                mWebtrekk.track();
            }
        });

        URL = waitForTrackedURL();

        assertTrue(URL.contains("&"+TrackingParameter.Parameter.ADVERTISEMENT+"="+mediaCode));

        //next track - no media code
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        URL = waitForTrackedURL();

        assertFalse(URL.contains("&"+TrackingParameter.Parameter.ADVERTISEMENT+"="));
    }

    public void testCustomPageOverride()
    {
        final String customPageName = "customPageName";

        //next track - no media code
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        String URL = waitForTrackedURL();

        assertTrue(URL.contains(NoAutoTrackActivity.class.getName()));
        assertFalse(URL.contains(customPageName));

        //next track - no media code
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.setCustomPageName(customPageName);
                mWebtrekk.track();
            }
        });

        URL = waitForTrackedURL();

        assertFalse(URL.contains(NoAutoTrackActivity.class.getName()));
        assertTrue(URL.contains(customPageName));

        Intent newActivityIntent = new Intent(getActivity(), NoAutoTrackActivity.class);
        newActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Activity newActivity = getInstrumentation().startActivitySync(newActivityIntent);

        //next track - no media code
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        URL = waitForTrackedURL();

        assertTrue(URL.contains(NoAutoTrackActivity.class.getName()));
        assertFalse(URL.contains(customPageName));
        newActivity.finish();
    }
}