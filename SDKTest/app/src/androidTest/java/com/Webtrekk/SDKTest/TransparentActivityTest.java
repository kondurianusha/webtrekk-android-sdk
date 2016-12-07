package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.content.Intent;

import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 27.05.16.
 */
public class TransparentActivityTest extends ActivityInstrumentationTestCase2Base<EmptyActivity> {

    Webtrekk mWebtrekk;

    public TransparentActivityTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testTransparentActivity()
    {
        initWaitingForTrack(null);
        getActivity();

        waitForTrackedURL();

        initWaitingForTrack(null);
        Intent newActivityIntent = new Intent(getActivity(), TransparentActivity.class);
        newActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Activity newActivity = getInstrumentation().startActivitySync(newActivityIntent);

        waitForTrackedURL();

        initWaitingForTrack(null);
        finishActivitySync(newActivity);

        waitForTrackedURL(true);

        finishActivitySync(getActivity());
    }
}