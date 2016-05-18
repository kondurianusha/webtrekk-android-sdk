package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webtrekksdk.TrackingConfiguration;
import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.util.Random;
import java.util.UUID;

/**
 * Created by vartbaronov on 26.04.16.
 */
@Suppress
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
        everIDtest("123456789012345678", false);
        everIDtest("12345678901324567890", false);
        everIDtest("1234567890123465789", true);
        everIDtest(HelperFunctions.generateEverid(), true);
    }

    private void everIDtest(String everID, boolean isShouldSet)
    {
        String oldEverID = mWebtrekk.getEverId();

        mWebtrekk.setEverId(everID);

        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        String URL = waitForTrackedURL();

        if (isShouldSet) {
            assertTrue(URL.contains(everID));
            assertFalse(URL.contains(oldEverID));
        }else {
            assertFalse(URL.contains(everID));
            assertTrue(URL.contains(oldEverID));
        }
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

        if (isShouldSet) {
            assertTrue(URL.contains(everID));
            assertFalse(URL.contains(oldEverID));
        }else {
            assertFalse(URL.contains(everID));
            assertTrue(URL.contains(oldEverID));
        }
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

    public void testDifferentParameterTrack()
    {
        final String s1 = "logged.in1";
        final String s2 = "logged.in2";
        final String search = "someSearch";
        final String customerID = "customerID";
        final String cat1 = "userCat1";
        final String cat2 = "userCat2";
        // Session parameter, internal search and custom visitor ID
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                TrackingParameter tp = new TrackingParameter();
                tp.add(TrackingParameter.Parameter.SESSION, "1", s1);
                tp.add(TrackingParameter.Parameter.SESSION, "2", s2);
                tp.add(TrackingParameter.Parameter.INTERN_SEARCH, search);
                tp.add(TrackingParameter.Parameter.CUSTOMER_ID, customerID);
                tp.add(TrackingParameter.Parameter.USER_CAT, "1", cat1);
                tp.add(TrackingParameter.Parameter.USER_CAT, "2", cat2);
                mWebtrekk.track(tp);
            }
        });

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);

        assertEquals(parcel.getValue("cs1"), s1);
        assertEquals(parcel.getValue("cs2"), s2);
        assertEquals(parcel.getValue("is"), search);
        assertEquals(parcel.getValue("cd"), customerID);
        assertEquals(parcel.getValue("uc1"), cat1);
        assertEquals(parcel.getValue("uc2"), cat2);
    }


}