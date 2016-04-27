package com.Webtrekk.SDKTest;

import android.provider.Settings;

import com.webtrekk.webtrekksdk.TrackingConfiguration;
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

        assertTrue(URL.contains(newEverID));
        assertFalse(URL.contains(oldEverID));

    }
}