package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ActivityInstrumentationTestCase2;

import com.Webtrekk.SDKTest.SimpleHTTPServer.HttpServer;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by vartbaronov on 22.04.16.
 */
public abstract class ActivityInstrumentationTestCase2Base<T extends Activity> extends ActivityInstrumentationTestCase2BaseMain<T> {

    private List<String> mSentURLArray = new Vector<String>();
    protected volatile boolean mStringReceived;
    protected final Object mSynchronize = new Object();
    protected long mWaitMilliseconds = 12000;
    protected HttpServer mHttpServer;
    volatile long mStringNumbersToWait = 1;
    volatile private boolean mWaitWhileTimoutFinished;
    private long mStartMessageReceiveNumber;

    public ActivityInstrumentationTestCase2Base(Class<T> activityClass) {
        super(activityClass);
    }

    private HttpServer.UrlNotifier mURLReceiver = new HttpServer.UrlNotifier() {
        @Override
        public void received(String url) {
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

    @Override
    protected void setUp() throws Exception {

        super.setUp();
        //refresh webtrekk instance
        //URLReceiverRegister(mURLReceiver);
        if (mHttpServer == null) {
            mHttpServer = new HttpServer();
            mHttpServer.setContext(mApplication);
            mHttpServer.setNotifier(mURLReceiver);
            mHttpServer.start();
        }
    }

    @Override
    public void tearDown() throws Exception {
        //URLReceiverUnRegister(mURLReceiver);
        mHttpServer.stop();
        super.tearDown();
    }

    protected void initWaitingForTrack(Runnable process)
    {
        initWaitingForTrack(process, 1);
    }

    protected void initWaitingForTrack(Runnable process, long UrlCount)
    {
        mStringNumbersToWait = UrlCount;
        mSentURLArray.clear();;
        mStringReceived = false;

        if (process != null) {
            synchronized (Webtrekk.getInstance()) {

                new Thread(process).start();
            }
        }
    }

    protected String waitForTrackedURL()
    {
        return waitForTrackedURL(false);
    }

    protected String waitForTrackedURL(boolean isNoTrackCheck)
    {
        mWaitWhileTimoutFinished = false;
        processWaitForURL(isNoTrackCheck);
        return isNoTrackCheck ? null : mSentURLArray.get(0);
    }

    protected List<String> waitForTrackedURLs()
    {
        mWaitWhileTimoutFinished = true;
        processWaitForURL(false);
        return mSentURLArray;
    }

    private void processWaitForURL(boolean isNoTrackCheck)
    {
        synchronized (mSynchronize) {
            while (!mStringReceived) {
                try {
                    mSynchronize.wait(mWaitMilliseconds);
                    if (isNoTrackCheck)
                        break;
                } catch (InterruptedException e) {
                    assertTrue(false);
                }
            }
                if (!isNoTrackCheck) {
                    assertTrue(mStringReceived);
                    assertEquals(mStringNumbersToWait, mSentURLArray.size());
                }else {
                    assertFalse(mStringReceived);
                }
        }
    }

    protected void setStartMessageNumber()
    {
        mStartMessageReceiveNumber = mHttpServer.getCurrentRequestNumber();
    }

    protected void waitForMessages(long messageCount)
    {
        while((mHttpServer.getCurrentRequestNumber() - mStartMessageReceiveNumber) != messageCount)
        {
            getInstrumentation().waitForIdleSync();
        }
    }

    protected void cleanConfigPreference()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext());
        sharedPrefs.edit().remove(Webtrekk.PREFERENCE_KEY_CONFIGURATION).apply();
    }

}
