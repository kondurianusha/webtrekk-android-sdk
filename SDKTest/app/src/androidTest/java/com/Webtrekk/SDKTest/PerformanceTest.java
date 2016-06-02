package com.Webtrekk.SDKTest;

import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 01.06.16.
 */
public class PerformanceTest extends ActivityInstrumentationTestCase2Base<EmptyActivity> {
    private Webtrekk mWebtrekk;

    public PerformanceTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_performance_test);
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

    @Suppress
    public void testMessageNumberPerformance() {
        final int numberOfTest = 100000;

        setStartMessageNumber();

        for (int i = 0; i < numberOfTest; i++) {
            mWebtrekk.track();
            WebtrekkLogging.log("track:" + i);
        }

        waitForMessages(numberOfTest);
    }
}
