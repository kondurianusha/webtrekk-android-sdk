package com.Webtrekk.SDKTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;

import com.webtrekk.webtrekksdk.Modules.ExceptionHandler;
import com.webtrekk.webtrekksdk.TrackingConfiguration;
import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 14.04.16.
 */
public class ErrorHandlerTest extends ActivityInstrumentationTestCase2<EmptyActivity> {

    Webtrekk mWebtrekk;
    volatile String mSendedURL;
    volatile boolean mStringReceived;
    final Object mSynchronize = new Object();

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


    public ErrorHandlerTest(){
        super(EmptyActivity.class);
    }

    private void URLReceiverRegister()
        {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mURLReceiver,
                new IntentFilter("com.webtrekk.webtrekksdk.TEST_URL"));
        }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        URLReceiverRegister();
        mWebtrekk = Webtrekk.getInstance();
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


    public void testCatchedError()
    {

        mSendedURL = null;
        mStringReceived = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                String s = null;
                mWebtrekk.initWebtrekk(getActivity().getApplication());

                try {
                    s.length();
                } catch (NullPointerException e) {
                    mWebtrekk.trackException(e);
                }
            }
        }).start();

        synchronized (mSynchronize) {
            while (!mStringReceived) {
                try {
                    mSynchronize.wait(10000);
                } catch (InterruptedException e) {
                    assertTrue(false);
                }
                assertTrue(mStringReceived);
            }
        }

        mSendedURL = mSendedURL.substring(mSendedURL.indexOf("ct=webtrekk_ignore"), mSendedURL.length());
        assertEquals(mSendedURL, "ct=webtrekk_ignore&ck910=2&ck911=java.lang.NullPointerException&ck912=Attempt+to+invoke+virtual+method+%27int+java.lang.String.length%28%29%27+on+a+null+object+reference&ck914=com.Webtrekk.SDKTest.ErrorHandlerTest%242.run%28ErrorHandlerTest.java%3A82%29%7Cjava.lang.Thread.run%28Thread.java%3A818%29&eor=1");


    }

    public void testInfoError()
    {

        mSendedURL = null;
        mStringReceived = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.initWebtrekk(getActivity().getApplication());

                mWebtrekk.trackException("nameEx", "messsage Ex");
            }
        }).start();

        synchronized (mSynchronize) {
            while (!mStringReceived) {
                try {
                    mSynchronize.wait(10000);
                } catch (InterruptedException e) {
                    assertTrue(false);
                }
                assertTrue(mStringReceived);
            }
        }

        mSendedURL = mSendedURL.substring(mSendedURL.indexOf("ct=webtrekk_ignore"), mSendedURL.length());
        assertEquals(mSendedURL, "ct=webtrekk_ignore&ck910=3&ck911=nameEx&ck912=messsage+Ex&eor=1");
        assertTrue(mSendedURL.contains("ct=webtrekk_ignore"));
    }

    public void testFatalInit()
    {
        if (Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionHandler)
            return;

        Thread.setDefaultUncaughtExceptionHandler(null);
        mWebtrekk.initWebtrekk(getActivity().getApplication());

        String s = null;

        try {
            s.length();
        } catch (NullPointerException e) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

    public void testFatalComplete()
    {
        if (Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionHandler)
            return;

        mWebtrekk.initWebtrekk(getActivity().getApplication());

        synchronized (mSynchronize) {
            while (!mStringReceived) {
                try {
                    mSynchronize.wait(10000);
                } catch (InterruptedException e) {
                    assertTrue(false);
                }
                assertTrue(mStringReceived);
            }
        }

        assertTrue(mSendedURL.contains("ck911=java.lang.NullPointerException&ck912=Attempt+to+invoke+virtual+method+%27int+java.lang.String.length"));
        assertTrue(mSendedURL.contains("+on+a+null+object+reference&ck914=com.Webtrekk.SDKTest.ErrorHandlerTest.testFatalInit%28ErrorHandlerTest.java"));
        assertTrue(mSendedURL.contains("java.lang.reflect.Method.invoke%28Method.java"));
        assertTrue(mSendedURL.contains("ct=webtrekk_ignore"));
    }
}
