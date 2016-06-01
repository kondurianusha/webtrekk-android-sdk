package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.content.Intent;

import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 26.04.16.
 */
public class MiscellaneousTest  extends ActivityInstrumentationTestCase2Base<EmptyActivity> {
    private Webtrekk mWebtrekk;
    private final long oneMeg = 1024 * 1024;


    public MiscellaneousTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_no_auto_track);
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        super.tearDown();
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

        assertTrue(URL.contains("Seite"));
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

        assertFalse(URL.contains("Seite"));
        assertTrue(URL.contains(customPageName));

        Intent newActivityIntent = new Intent(getActivity(), EmptyActivity.class);
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

        assertTrue(URL.contains("Seite"));
        assertFalse(URL.contains(customPageName));
        finishActivitySync(newActivity);
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

        assertTrue(parcel.parseURL(URL));

        assertEquals(parcel.getValue("cs1"), s1);
        assertEquals(parcel.getValue("cs2"), s2);
        assertEquals(parcel.getValue("is"), search);
        assertEquals(parcel.getValue("cd"), customerID);
        assertEquals(parcel.getValue("uc1"), cat1);
        assertEquals(parcel.getValue("uc2"), cat2);
    }

    public void testPageURL()
    {
        internalTestPU(HelperFunctions.urlEncode("http://www.yandex.ru"));

        String google = HelperFunctions.urlEncode("http://wwww.google.com");

        assertTrue(Webtrekk.getInstance().setPageURL("http://wwww.google.com"));

        internalTestPU(google);

        Intent newActivityIntent = new Intent(getInstrumentation().getTargetContext(), PageExampleActivity.class);
        newActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Activity newActivity = getInstrumentation().startActivitySync(newActivityIntent);

        internalTestPU(null);

        finishActivitySync(newActivity);
    }

    private void internalTestPU(String valueToTest)
    {
        //test URL page in configuration xml
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        assertTrue(parcel.parseURL(URL));
        assertEquals(valueToTest, parcel.getValue("pu"));
    }

    public void testAutoAdvertiserId() {
        //test URL page in configuration xml
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        assertTrue(parcel.parseURL(URL));
        assertFalse(parcel.getValue("cb100").isEmpty());
        assertEquals("false", parcel.getValue("cb200"));
    }

    public void testCustomValueIsSaved()
    {
        customValueIsSavedTestInternal();

        Intent newActivityIntent = new Intent(getInstrumentation().getTargetContext(), PageExampleActivity.class);
        newActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Activity newActivity = getInstrumentation().startActivitySync(newActivityIntent);

        //no any new activity start is influence on custom parameters
        customValueIsSavedTestInternal();

        finishActivitySync(newActivity);
    }

    private void customValueIsSavedTestInternal()
    {
        Webtrekk.getInstance().getCustomParameter().put("testCustomParameter", "customValue");

        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });


        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);

        assertEquals("customValue", parcel.getValue("cp113"));
        assertEquals("customValue", parcel.getValue("cr"));
    }

    public void testGoogleEmail()
    {
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);

        assertFalse(parcel.getValue("cp9").isEmpty());
    }
}