package com.Webtrekk.SDKTest;

import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 17.11.16.
 */

public class AutoTrackingParametersTest extends ActivityInstrumentationTestCase2Base<EmptyActivity> {
    private Webtrekk mWebtrekk;
    private final String cs807New = "newcs807";

    public AutoTrackingParametersTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        if (getName().equals("testSimpleAutoTest"))
        {
            mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_parameters_auto_track_test);
        } else
        {
            mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_parameters_auto_track_test_complex);
        }
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        super.tearDown();
    }

// 811 and 812 isn't supported on emulator
    public void testSimpleAutoTest()
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

        assertTrue(parcel.getValue("cs804") != null && !parcel.getValue("cs804").isEmpty());
        assertTrue(parcel.getValue("cs805") != null && !parcel.getValue("cs805").isEmpty());
        assertTrue(parcel.getValue("cs807") != null && !parcel.getValue("cs807").isEmpty());
        assertTrue(parcel.getValue("cs809") != null && !parcel.getValue("cs809").isEmpty());
        //assertTrue(parcel.getValue("cs810") != null && !parcel.getValue("cs810").isEmpty());
        assertTrue(parcel.getValue("cs813") != null && !parcel.getValue("cs813").isEmpty());
        assertTrue(parcel.getValue("cs814") != null && !parcel.getValue("cs814").isEmpty());
        assertTrue(parcel.getValue("cs815") != null && !parcel.getValue("cs815").isEmpty());
        assertTrue(parcel.getValue("cs816") != null && !parcel.getValue("cs816").isEmpty());
        assertTrue(parcel.getValue("cp783") != null && !parcel.getValue("cp783").isEmpty());
        assertTrue(parcel.getValue("cp784") != null && !parcel.getValue("cp784").isEmpty());
        assertTrue(parcel.getValue("cs808") == null);
    }

    // test how overwriten works
    public void testComplexAutoTest()
    {
        final String difValue = "differentValue";
        final String difPageValue = "newcp783";
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                TrackingParameter tp = new TrackingParameter();
                tp.add(TrackingParameter.Parameter.SESSION, "809", difValue);
                mWebtrekk.getCustomParameter().put("newcp783key", difPageValue);

                mWebtrekk.track(tp);
            }
        });

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);

        assertTrue(parcel.getValue("cs804") != null && !parcel.getValue("cs804").isEmpty());
        assertTrue(parcel.getValue("cs805") != null && !parcel.getValue("cs805").isEmpty());
        assertEquals(cs807New, parcel.getValue("cs807"));
        assertEquals(difValue, parcel.getValue("cs809"));
        //assertTrue(parcel.getValue("cs810") != null && !parcel.getValue("cs810").isEmpty());
        assertTrue(parcel.getValue("cs813") != null && !parcel.getValue("cs813").isEmpty());
        assertTrue(parcel.getValue("cs814") != null && !parcel.getValue("cs814").isEmpty());
        assertTrue(parcel.getValue("cs815") != null && !parcel.getValue("cs815").isEmpty());
        assertTrue(parcel.getValue("cs816") != null && !parcel.getValue("cs816").isEmpty());
        assertEquals(difPageValue, parcel.getValue("cp783"));
        assertTrue(parcel.getValue("cp784") != null && !parcel.getValue("cp784").isEmpty());
        assertTrue(parcel.getValue("cs808") != null && !parcel.getValue("cs808").isEmpty());
    }
}
