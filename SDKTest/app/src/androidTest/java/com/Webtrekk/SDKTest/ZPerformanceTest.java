/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Webtrekk GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Created by Arsen Vartbaronov on 01.06.16.
 */

package com.Webtrekk.SDKTest;

import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webtrekksdk.Request.RequestUrlStore;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
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
            Thread.yield();
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
        final int numberOfTest = 20000;

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

    public void testFileCorruption()
    {
        RequestUrlStore urlStore = new RequestUrlStore(getInstrumentation().getTargetContext());
        final File file = urlStore.getRequestStoreFile();

        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 30; i++) {
                    mWebtrekk.track();
                }
                file.setReadable(false, false);
            }
        }, 20);
        waitForTrackedURLs();
        file.setReadable(true, false);
    }
}
