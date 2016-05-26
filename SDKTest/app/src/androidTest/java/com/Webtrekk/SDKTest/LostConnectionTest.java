package com.Webtrekk.SDKTest;

import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.IOException;

/**
 * Created by vartbaronov on 24.05.16.
 */
public class LostConnectionTest  extends ActivityInstrumentationTestCase2Base<EmptyActivity> {

    private Webtrekk mWebtrekk;
    private static final int TRACKING_CALLS_STACK = 1000;


    public LostConnectionTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_connection_broken_request);
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        super.tearDown();
    }

    public void testLostConnection()
    {
        long messageReeivedCounter = mHttpServer.getCurrentRequestNumber();

        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < TRACKING_CALLS_STACK; i++)
                {
                    mWebtrekk.track();
                }
            }
        });

        //Wait for some message starts to send.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            WebtrekkLogging.log("Sleep interruction");
        }

        WebtrekkLogging.log("Stop HTTP Server");
        //stop http server - emulator connection brakes
        mHttpServer.stop();

        //wait sometime
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            WebtrekkLogging.log("Sleep interruction");
        }

        initWaitingForTrack (null, TRACKING_CALLS_STACK - (mHttpServer.getCurrentRequestNumber() - messageReeivedCounter));

        try {
            mHttpServer.start();
            WebtrekkLogging.log("Start HTTP Server");
        } catch (IOException e) {
            WebtrekkLogging.log("testLostConnection. Can't start server one more time");
        }

        mWaitMilliseconds = 70000;
        waitForTrackedURLs();
    }
}