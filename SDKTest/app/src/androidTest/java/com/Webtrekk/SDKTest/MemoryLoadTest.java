package com.Webtrekk.SDKTest;

import android.content.Intent;
import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 22.04.16.
 */
public class MemoryLoadTest extends ActivityInstrumentationTestCase2Base<NoAutoTrackActivity>  {
    private Webtrekk mWebtrekk;
    private final long oneMeg = 1024*1024;


    public MemoryLoadTest() {
        super(NoAutoTrackActivity.class);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(getActivity());

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

        initWaitingForString(runnableTrackOnly);
        waitForString();
        initWaitingForString(runnableTrackOnly);
        waitForString();
    }

    public void testLoadCPU()
    {

        Intent intent = new Intent(getActivity(), LoadOSService.class);
        intent.putExtra(LoadOSService.MODE, LoadOSService.Mode.LOAD_CPU.ordinal());

        getActivity().startService(intent);

        Runnable runnableTrackOnly = new Runnable(){

            @Override
            public void run() {
                mWebtrekk.track();
            }
        };

        for (int i = 0 ; i < 10; i++) {
            initWaitingForString(runnableTrackOnly);
            waitForString();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        getActivity().stopService(intent);
    }
}
