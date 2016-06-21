package com.Webtrekk.SDKTest;

import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 21.06.16.
 */
public class OldConfigurationTest extends ActivityInstrumentationTestCase2Base<EmptyActivity>  {
    private Webtrekk mWebtrekk;
    private final long oneMeg = 1024 * 1024;


    public OldConfigurationTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_old_configuration);
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        super.tearDown();
    }

    public void testOldConfiguration()
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
        assertEquals("test_pagecategory2", parcel.getValue("cg2"));

    }

}
