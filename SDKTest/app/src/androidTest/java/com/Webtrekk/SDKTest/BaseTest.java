package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.Webtrekk.SDKTest.SimpleHTTPServer.HttpServer;
import com.webtrekk.webtrekksdk.Webtrekk;

import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.List;
import java.util.Vector;

import static junit.framework.Assert.assertTrue;

/**
 * Created by vartbaronov on 22.06.17.
 */
public class BaseTest extends BaseWebtrekkTest{

    private List<String> mSentURLArray = new Vector<String>();
    protected volatile boolean mStringReceived;
    protected final Object mSynchronize = new Object();
    protected long mWaitMilliseconds = 12000;
    protected HttpServer mHttpServer;
    volatile long mStringNumbersToWait = 1;
    volatile private boolean mWaitWhileTimoutFinished;
    private long mStartMessageReceiveNumber;

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
    public void before() throws Exception {

        super.before();
        //refresh webtrekk instance
        if (mHttpServer == null) {
            mHttpServer = new HttpServer();
            mHttpServer.setContext(mApplication);
            mHttpServer.setNotifier(mURLReceiver);
            mHttpServer.start();
        }
    }

    @Override
    public void after() throws Exception {
        mHttpServer.stop();
        super.after();
    }

    protected void initWaitingForTrack(Runnable process)
    {
        initWaitingForTrack(process, 1);
    }

    protected void initWaitingForTrack(Runnable process, long UrlCount)
    {
        mStringNumbersToWait = UrlCount;
        mSentURLArray.clear();
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
            Thread.yield();
            InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        }
    }
}
