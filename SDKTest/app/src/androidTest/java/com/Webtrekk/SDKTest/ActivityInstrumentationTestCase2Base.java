package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ActivityInstrumentationTestCase2;

import com.Webtrekk.SDKTest.SimpleHTTPServer.HttpServer;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vartbaronov on 22.04.16.
 */
public abstract class ActivityInstrumentationTestCase2Base<T extends Activity> extends ActivityInstrumentationTestCase2BaseMain<T> {

    private List<String> mSentURLArray = new ArrayList<String>();
    protected volatile boolean mStringReceived;
    protected final Object mSynchronize = new Object();
    protected long mWaitMilliseconds = 12000;
    protected HttpServer mHttpServer;
    int mStringNumbersToWait = 1;
    volatile private boolean mWaitWhileTimoutFinished;


    public ActivityInstrumentationTestCase2Base(Class<T> activityClass) {
        super(activityClass);
    }

    private BroadcastReceiver mURLReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getStringExtra("URL");
            mSentURLArray.add(url);
            onReceiveURLProcess(url);

            if (mStringNumbersToWait >= mSentURLArray.size()) {
                mStringReceived = true;
                if (!mWaitWhileTimoutFinished) {
                    synchronized (mSynchronize) {
                        mSynchronize.notifyAll();
                    }
                }
            }
        }
    };

    protected void onReceiveURLProcess(String url){};

    protected void URLReceiverRegister(BroadcastReceiver URLReceiver)
    {
        LocalBroadcastManager.getInstance(getInstrumentation().getTargetContext()).registerReceiver(URLReceiver,
                new IntentFilter("com.webtrekk.webtrekksdk.TEST_URL"));
    }


    @Override
    protected void setUp() throws Exception {

        super.setUp();
        //refresh webtrekk instance
        URLReceiverRegister(mURLReceiver);
        if (mHttpServer == null) {
            mHttpServer = new HttpServer();
            mHttpServer.setContext(mApplication);
            mHttpServer.start();
        }

    }

    @Override
    public void tearDown() throws Exception {
        URLReceiverUnRegister(mURLReceiver);
        mHttpServer.stop();
        super.tearDown();
    }

    protected void URLReceiverUnRegister(BroadcastReceiver URLReceiver)
    {
        LocalBroadcastManager.getInstance(getInstrumentation().getTargetContext()).unregisterReceiver(URLReceiver);
    }

    protected void initWaitingForTrack(Runnable process)
    {
        initWaitingForTrack(process, 1);
    }

    protected void initWaitingForTrack(Runnable process, int UrlCount)
    {
        mStringNumbersToWait = UrlCount;
        mSentURLArray.clear();;
        mStringReceived = false;

        synchronized (Webtrekk.getInstance()) {

            new Thread(process).start();
        }
    }

    protected String waitForTrackedURL()
    {
        processWaitForURL();
        return mSentURLArray.get(0);
    }

    protected List<String> waitForTrackedURLs()
    {
        mWaitWhileTimoutFinished = true;
        processWaitForURL();
        return mSentURLArray;
    }

    private void processWaitForURL()
    {
        synchronized (mSynchronize) {
            while (!mStringReceived) {
                try {
                    mSynchronize.wait(mWaitMilliseconds);
                } catch (InterruptedException e) {
                    assertTrue(false);
                }
                assertTrue(mStringReceived);
                assertEquals(mStringNumbersToWait, mSentURLArray.size());
            }
        }

        //wait while response is proceded completely
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
