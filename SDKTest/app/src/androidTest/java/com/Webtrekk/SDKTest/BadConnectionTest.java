package com.Webtrekk.SDKTest;

import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.IOException;

/**
 * Created by vartbaronov on 24.05.16.
 */
public class BadConnectionTest extends ActivityInstrumentationTestCase2Base<EmptyActivity> {

    private Webtrekk mWebtrekk;
    private static final int TRACKING_CALLS_STACK = 1000;


    public BadConnectionTest() {
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
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            WebtrekkLogging.log("Sleep interruction");
        }

        //stop http server - emulator connection brakes
        mHttpServer.stop();
        WebtrekkLogging.log("Stop HTTP Server");

        //wait sometime
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            WebtrekkLogging.log("Sleep interruction");
        }

        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                try {
                    WebtrekkLogging.log("Start HTTP Server");
                    synchronized (mHttpServer) {
                        mHttpServer.start();
                    }
                } catch (IOException e) {
                    WebtrekkLogging.log("testLostConnection. Can't start server one more time");
                }
            }
        }, TRACKING_CALLS_STACK - (mHttpServer.getCurrentRequestNumber() - messageReeivedCounter));


        mWaitMilliseconds = 70000;
        waitForTrackedURLs();
    }

    public void testSlowConnection(){
        final int delay = 30*1000;

        // make significant delay in response
        WebtrekkLogging.log("Setup HTTP request delay");
        mHttpServer.setBeforeDelay(delay);

        try {

            // do some track
            initWaitingForTrack(new Runnable() {
                @Override
                public void run() {
                    mWebtrekk.track();
                }
            });

            //Wait for 2 seconds to make SDK start to send message.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                WebtrekkLogging.log("Sleep interruction");
            }

            //make sure you can close activity in less then 5 sec (stop is called)

            EmptyActivity activity = (EmptyActivity) getActivity();

            activity.finish();

            // due to difference time when onStop start to process
            // on a test server and a local machine at first wait while onStart is started
            // to call

            try {
                WebtrekkLogging.log("start to wait activity stop procedure");

                // wait while on start is called
                while (!activity.isStartedToStopping()) {
                    Thread.yield();
                }

                WebtrekkLogging.log("on stoping is started");

                // just wait 5 seconds to check ANR not happened
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                WebtrekkLogging.log("Sleep interruction");
            }

            // check that activity is stopped if not this is ANR
            assertTrue(activity.isStopped());

            // wait for finishing activity
            ActivityInstrumentationTestCase2BaseMain.finishActivitySync(activity, getInstrumentation(), false);
            setActivity(null);
        }finally {
            //change delay to zero and cancel current delay
            WebtrekkLogging.log("Cancel HTTP request delay.");
            mHttpServer.setBeforeDelay(0);
            mHttpServer.stopBeforeDelay();
        }

        //start activity again
        getActivity();

        // receive tracks
        waitForTrackedURLs();

    }
}