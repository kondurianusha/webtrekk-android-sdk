package com.Webtrekk.SDKTest;

import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 23.06.16.
 */
public class ManualTrackFlushTest extends ActivityInstrumentationTestCase2Base<EmptyActivity> {
    private Webtrekk mWebtrekk;


    public ManualTrackFlushTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_manual_flush);
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        super.tearDown();
    }

    public void testManualFlash()
    {
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        mWaitMilliseconds = 2000;
        waitForTrackedURL(true);

        initWaitingForTrack(null);

        mWebtrekk.send();

        waitForTrackedURL();


    }
}
