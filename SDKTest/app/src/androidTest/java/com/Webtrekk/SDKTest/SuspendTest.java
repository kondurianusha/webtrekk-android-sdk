package com.Webtrekk.SDKTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.webtrekk.webtrekksdk.RequestUrlStore;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 10.05.16.
 */
public class SuspendTest extends ActivityInstrumentationTestCase2Base<SuspendActivity> {
    private Webtrekk mWebtrekk;
    final static long DELAY_FOR_SEND = 30000;
    final static int MESSAGES_NUMBER = 500;
    final static String SUSPEND_TEST_RECEIVED_MESSAGE = "SUSPEND_TEST_RECEIVED_MESSAGE";


    public SuspendTest() {
        super(SuspendActivity.class);
    }

    protected void onReceiveURLProcess(String url)
    {
        SharedPreferences preferences = HelperFunctions.getWebTrekkSharedPreference(getInstrumentation().getTargetContext());
        int messageReceived = getMessageReceivedNumber(preferences)+1;
        WebtrekkLogging.log("Background test. Receive message number:"+messageReceived);
        setMessageReceivedNumber(preferences, messageReceived);
    }

    private void setMessageReceivedNumber(SharedPreferences preference, int messagesReceived)
    {
        preference.edit().putInt(SUSPEND_TEST_RECEIVED_MESSAGE, messagesReceived).commit();
    }

    private int getMessageReceivedNumber(SharedPreferences preferences)
    {
        return preferences.getInt(SUSPEND_TEST_RECEIVED_MESSAGE, 0);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
    }

    public void testBeforeGoBackgroundHome()
    {
        if (!mIsExternalCall)
            return;

        RequestUrlStore store = new RequestUrlStore(getInstrumentation().getTargetContext(), 10);
        store.deleteRequestsFile();
        setMessageReceivedNumber(HelperFunctions.getWebTrekkSharedPreference(getInstrumentation().getTargetContext()), 0);

        getActivity();
        mHttpServer.setDelay(100);

        mStringNumbersToWait = MESSAGES_NUMBER;

        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < MESSAGES_NUMBER; i++)
                {
                    mWebtrekk.track();
                }
            }
        });

        try {
            Thread.sleep(DELAY_FOR_SEND);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int messageReceived = getMessageReceivedNumber(HelperFunctions.getWebTrekkSharedPreference(getInstrumentation().getTargetContext()));

        WebtrekkLogging.log("Backgroud test. Wait for message number:"+(MESSAGES_NUMBER - messageReceived));
        mHttpServer.setDelay(0);
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {

            }
        }, MESSAGES_NUMBER - messageReceived);

        mWaitMilliseconds = 20000;
        waitForTrackedURLs();
    }
}