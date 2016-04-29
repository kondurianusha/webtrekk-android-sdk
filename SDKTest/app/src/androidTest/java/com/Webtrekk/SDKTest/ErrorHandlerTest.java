package com.Webtrekk.SDKTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ActivityInstrumentationTestCase2;

import com.webtrekk.webtrekksdk.Modules.ExceptionHandler;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vartbaronov on 14.04.16.
 */
public class ErrorHandlerTest extends ActivityInstrumentationTestCase2<EmptyActivity> {

    Webtrekk mWebtrekk;
    volatile List<String> mSendedURLArray = new ArrayList<String>();
    volatile boolean mStringReceived;
    final Object mSynchronize = new Object();
    int mStringNumbersToWait = 1;

    private BroadcastReceiver mURLReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSendedURLArray.add(intent.getStringExtra("URL"));

            if (mStringNumbersToWait == mSendedURLArray.size()) {
                mStringReceived = true;
                synchronized (mSynchronize) {
                    mSynchronize.notifyAll();
                }
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

        mSendedURLArray.clear();
        mStringReceived = false;
        mStringNumbersToWait = 1;

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

        String URL = mSendedURLArray.get(0);

        URLParsel parcel = new URLParsel();
        parcel.parseURL(URL);

        URL = URL.substring(URL.indexOf("ct=webtrekk_ignore"), URL.length());
        assertEquals(parcel.getValue("ct"), "webtrekk_ignore");
        assertEquals(parcel.getValue("ck910"), "2");
        assertEquals(parcel.getValue("ck911"), "java.lang.NullPointerException");
        assertEquals(parcel.getValue("ck912"), "Attempt+to+invoke+virtual+method+%27int+java.lang.String.length%28%29%27+on+a+null+object+reference");
        assertEquals(getNormString(parcel.getValue("ck914")), "com.Webtrekk.SDKTest.ErrorHandlerTest%242.run%28ErrorHandlerTest.java%3A%29%7Cjava.lang.Thread.run%28Thread.java%29");
    }

    public void testInfoError()
    {

        mSendedURLArray.clear();
        mStringReceived = false;
        mStringNumbersToWait = 1;

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

        String URL = mSendedURLArray.get(0);
        URL = URL.substring(URL.indexOf("ct=webtrekk_ignore"), URL.length());
        assertEquals(URL, "ct=webtrekk_ignore&ck910=3&ck911=nameEx&ck912=messsage+Ex&eor=1");
        assertTrue(URL.contains("ct=webtrekk_ignore"));
    }

    public void testFatalCompeteSimple()
    {
        internalTestFatalComplete(1);
    }

    public void testFatalCompeteComplex()
    {
        internalTestFatalComplete(2);
    }

    private void internalTestFatalComplete(int errorNumbers)
    {
        mSendedURLArray.clear();
        mStringReceived = false;
        mStringNumbersToWait = errorNumbers;

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

        assertEquals(mSendedURLArray.size(), errorNumbers);


        for (int i = 0 ; i < mSendedURLArray.size(); i++)
        {
            URLParsel parcel = new URLParsel();
            parcel.parseURL(mSendedURLArray.get(i));

            assertEquals(parcel.getValue("ct"), "webtrekk_ignore");
            assertTrue(HelperFunctions.urlDecode(parcel.getValue("ck911")).length() <= 255);
            assertTrue(HelperFunctions.urlDecode(parcel.getValue("ck912")).length() <= 255);
            assertTrue(HelperFunctions.urlDecode(parcel.getValue("ck913")).length() <= 255);
            assertTrue(HelperFunctions.urlDecode(parcel.getValue("ck914")).length() <= 255);
            assertTrue(HelperFunctions.urlDecode(parcel.getValue("ck915")).length() <= 255);
            assertEquals(parcel.getValue("ck910"), "1");

            switch (i)
            {
                case 0:
                    assertEquals(parcel.getValue("ck911"), "java.lang.RuntimeException");
                    assertEquals(getNormString(parcel.getValue("ck912")), "Unable+to+start+activity+ComponentInfo%7Bcom.Webtrekk.SDKTest%2Fcom.Webtrekk.SDKTest.ThrowExceptionActivity%7D%3A+java.lang.NullPointerException%3A+Attempt+to+invoke+virtual+method+%27int+java.lang.String.length%28%29%27+on+a+null+object+reference");
                    assertEquals(getNormString(parcel.getValue("ck913")), "Attempt+to+invoke+virtual+method+%27int+java.lang.String.length%28%29%27+on+a+null+object+reference");
                    assertEquals(getNormString(parcel.getValue("ck914")), "android.app.ActivityThread.performLaunchActivity%28ActivityThread.java%29%7Candroid.app.ActivityThread.handleLaunchActivity%28ActivityThread.java%29%7Candroid.app.ActivityThread.access%24800%28ActivityThread.java%29%7C");
                    assertEquals(getNormString(parcel.getValue("ck915")), "com.Webtrekk.SDKTest.ThrowExceptionActivity.onCreate%28ThrowExceptionActivity.java%3A%29%7Candroid.app.Activity.performCreate%28Activity.java%29%7Candroid.app.Instrumentation.callActivityOnCreate%28Instrumentation.java%29%7C");
                    break;
                case 1:
                    assertEquals(parcel.getValue("ck911"), "java.lang.RuntimeException");
                    assertEquals(getNormString(parcel.getValue("ck912")), "Unable+to+start+activity+ComponentInfo%7Bcom.Webtrekk.SDKTest%2Fcom.Webtrekk.SDKTest.ThrowExceptionActivity%7D%3A+java.lang.NumberFormatException%3A+Invalid+int%3A+%22sdfsdf%22");
                    assertEquals(getNormString(parcel.getValue("ck913")), "Invalid+int%3A+%22sdfsdf%22");
                    assertEquals(getNormString(parcel.getValue("ck914")), "android.app.ActivityThread.performLaunchActivity%28ActivityThread.java%29%7Candroid.app.ActivityThread.handleLaunchActivity%28ActivityThread.java%29%7Candroid.app.ActivityThread.access%24800%28ActivityThread.java%29%7C");
                    assertEquals(getNormString(parcel.getValue("ck915")), "java.lang.Integer.invalidInt%28Integer.java%29%7Cjava.lang.Integer.parse%28Integer.java%29%7Cjava.lang.Integer.parseInt%28Integer.java%29%7Cjava.lang.Integer.parseInt%28Integer.java%29%7Cjava.lang.Integer.valueOf%28Integer.java%29%7C");
                    break;
            }

        }
    }

    private String getNormString(String orig)
    {
        return orig.replaceAll("%3A([-\\d]+)", "%3A");
    }
}
