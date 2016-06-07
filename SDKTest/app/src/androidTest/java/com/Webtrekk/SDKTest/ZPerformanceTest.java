package com.Webtrekk.SDKTest;

import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webtrekksdk.Request.RequestUrlStore;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.File;
import java.io.IOException;

/**
 * Created by vartbaronov on 01.06.16.
 * Name it with Z to call it at last.
 */
public class ZPerformanceTest extends ActivityInstrumentationTestCase2Base<EmptyActivity> {
    private Webtrekk mWebtrekk;

    public ZPerformanceTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_performance_test);
        mHttpServer.resetRequestNumber();
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        super.tearDown();
    }


    //works in reality only on real HW
    public void testTimePerformance()
    {
        final int numberOfTest = 1000;
        long timeDifSum = 0;

        setStartMessageNumber();

        for (int i = 0; i<numberOfTest; i++)
        {
            long before = System.currentTimeMillis();
            mWebtrekk.track();
            long timeOfTrack = System.currentTimeMillis() - before;
            WebtrekkLogging.log("time of track("+i+"):"+timeOfTrack);
            timeDifSum += timeOfTrack;
        }

        float result = timeDifSum/numberOfTest;

        WebtrekkLogging.log("Performance test is shown:"+result + " milliseconds per call");

        waitForMessages(numberOfTest);

        assertTrue("Performance test is shown:"+result + " milliseconds per call", result < 10);
    }

    public void testMessageNumberPerformance() {
        final int numberOfTest = 50000;

        setStartMessageNumber();

        for (int i = 0; i < numberOfTest; i++) {
            WebtrekkLogging.log("track:" + i);
            mWebtrekk.track();
        }

        waitForMessages(numberOfTest);
    }

    public void testSavingToFlashByTimeout()
    {
        RequestUrlStore urlStore = new RequestUrlStore(getInstrumentation().getTargetContext());

        File file = urlStore.getRequestStoreFile();
        long length = file.length();
        mHttpServer.stop();
        mWebtrekk.track();

        // sleep for a while to make saving happends
        try {
            Thread.sleep(95000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(file.length() > length);
        try {
            mHttpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        waitForTrackedURLs();
    }
}
