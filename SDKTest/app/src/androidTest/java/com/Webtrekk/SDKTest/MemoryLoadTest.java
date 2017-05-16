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
 * Created by Arsen Vartbaronov on 22.04.16.
 */

package com.Webtrekk.SDKTest;

import android.content.Intent;
import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webtrekksdk.Webtrekk;

public class MemoryLoadTest extends ActivityInstrumentationTestCase2Base<EmptyActivity>  {
    private Webtrekk mWebtrekk;
    private final long oneMeg = 1024*1024;


    public MemoryLoadTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_no_auto_track);
        getActivity();
/*
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        Runtime info = Runtime.getRuntime();

        Log.d(getClass().getName(),"current free memory runtime is:"+info.freeMemory()/oneMeg);
        Log.d(getClass().getName(),"current total memory runtime is:"+info.totalMemory()/oneMeg);
        Log.d(getClass().getName(),"current available memory is:"+mi.availMem/oneMeg);
        Log.d(getClass().getName(),"current is lowMemory value:"+mi.lowMemory);
        Log.d(getClass().getName(),"current threshold value:"+mi.threshold/oneMeg);
*/
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        super.tearDown();
    }

    @Suppress
    public void testLoadMemory()
    {
        Runnable runnableTrackOnly = new Runnable(){

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mWebtrekk.track();
            }
        };

        initWaitingForTrack(runnableTrackOnly);
        waitForTrackedURL();
        initWaitingForTrack(runnableTrackOnly);
        waitForTrackedURL();
    }

    public void testLoadCPU()
    {
        Intent intent = new Intent(getInstrumentation().getTargetContext(), LoadOSService.class);
        intent.putExtra(LoadOSService.MODE, LoadOSService.Mode.LOAD_CPU.ordinal());

        getInstrumentation().getTargetContext().startService(intent);

        Runnable runnableTrackOnly = new Runnable(){

            @Override
            public void run() {
                mWebtrekk.track();
            }
        };

        for (int i = 0 ; i < 10; i++) {
            initWaitingForTrack(runnableTrackOnly, 1);
            waitForTrackedURLs();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        getInstrumentation().getTargetContext().stopService(intent);
    }
}
