package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Created by vartbaronov on 22.04.16.
 */
public abstract class ActivityInstrumentationTestCase2Base<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    private volatile String mSendedURL;
    private volatile boolean mStringReceived;
    private final Object mSynchronize = new Object();

    public ActivityInstrumentationTestCase2Base(Class<T> activityClass) {
        super(activityClass);
    }

    private BroadcastReceiver mURLReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSendedURL = intent.getStringExtra("URL");
            mStringReceived = true;

            synchronized (mSynchronize)
            {
                mSynchronize.notifyAll();
            }
        }
    };

    private void URLReceiverRegister()
    {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mURLReceiver,
                new IntentFilter("com.webtrekk.webtrekksdk.TEST_URL"));
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        URLReceiverRegister();
    }

    @Override
    public void tearDown() throws Exception {
        URLReceiverUnRegister();
        super.tearDown();
    }

    private void URLReceiverUnRegister()
    {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mURLReceiver);
    }

    protected void initWaitingForTrack(Runnable process)
    {
        mSendedURL = null;
        mStringReceived = false;

        new Thread(process).start();
    }

    protected String waitForTrackedURL()
    {
        synchronized (mSynchronize) {
            while (!mStringReceived) {
                try {
                    mSynchronize.wait(5000);
                } catch (InterruptedException e) {
                    assertTrue(false);
                }
                assertTrue(mStringReceived);
            }
        }
        return mSendedURL;
    }
}
