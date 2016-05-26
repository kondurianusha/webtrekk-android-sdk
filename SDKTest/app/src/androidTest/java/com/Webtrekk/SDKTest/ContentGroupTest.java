package com.Webtrekk.SDKTest;

import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 26.05.16.
 */
public class ContentGroupTest  extends ActivityInstrumentationTestCase2Base<EmptyActivity> {
    private Webtrekk mWebtrekk;

    public ContentGroupTest() {
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

    public void testContentGroup() {
        final String pageCat1 = "Cat1";
        final String pageCatLong = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456";

        assertEquals(256, pageCatLong.length());
        final String trunkPageCatLong = pageCatLong.substring(0, 255);
        assertEquals(255, trunkPageCatLong.length());


        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                TrackingParameter par = new TrackingParameter();
                par.add(TrackingParameter.Parameter.PAGE_CAT, "1", pageCat1);
                par.add(TrackingParameter.Parameter.PAGE_CAT, "2", pageCatLong);
                mWebtrekk.track(par);
            }
        });

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);

        assertEquals(pageCat1, HelperFunctions.urlDecode(parcel.getValue("cg1")));
        assertEquals(trunkPageCatLong, HelperFunctions.urlDecode(parcel.getValue("cg2")));
    }
}