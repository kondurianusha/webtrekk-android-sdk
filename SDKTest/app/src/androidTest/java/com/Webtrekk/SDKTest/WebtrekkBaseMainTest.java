/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Webtrekk GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Created by Arsen Vartbaronov on 23.06.17.
 */

package com.Webtrekk.SDKTest;

import android.support.test.InstrumentationRegistry;

import com.Webtrekk.SDKTest.SimpleHTTPServer.HttpServer;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.util.List;
import java.util.Vector;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.Subject;

import static junit.framework.Assert.assertTrue;

/**
 * Created by vartbaronov on 22.06.17.
 */
public class WebtrekkBaseMainTest extends WebtrekkBaseSDKTest {

    private List<String> mSentURLArray = new Vector<String>();
    protected volatile boolean mStringsReceived;
    protected final Object mSynchronize = new Object();
    protected long mWaitMilliseconds = 12000;
    protected HttpServer mHttpServer;
    volatile long mStringNumbersToWait = 1;
    volatile private boolean mWaitWhileTimoutFinished;
    private long mStartMessageReceiveNumber;
    private Subject<String> mSubject;

    private Observer<String> mObserver = new Observer<String>() {
        @Override
        public void onSubscribe(@NonNull Disposable disposable) {

        }

        @Override
        public void onNext(@NonNull String url) {
            mSentURLArray.add(url);

            if (mStringNumbersToWait >= mSentURLArray.size()) {
                mStringsReceived = true;
                if (!mWaitWhileTimoutFinished) {
                    synchronized (mSynchronize) {
                        mSynchronize.notifyAll();
                    }
                }
            }

        }

        @Override
        public void onError(@NonNull Throwable throwable) {

        }

        @Override
        public void onComplete() {

        }
    };

    @Override
    public void before() throws Exception {
        super.before();
        //refresh webtrekk instance
        if (mHttpServer == null) {
            mHttpServer = new HttpServer();
            mHttpServer.setContext(mApplication);
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
        mSubject = mHttpServer.getSubject();
        mSubject.subscribe(mObserver);
        mStringNumbersToWait = UrlCount;
        mSentURLArray.clear();
        mStringsReceived = false;

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
            while (!mStringsReceived) {
                try {
                    mSynchronize.wait(mWaitMilliseconds);
                    if (isNoTrackCheck)
                        break;
                } catch (InterruptedException e) {
                    assertTrue(false);
                }
            }
            if (!isNoTrackCheck) {
                assertTrue(mStringsReceived);
                assertEquals(mStringNumbersToWait, mSentURLArray.size());
            }else {
                assertFalse(mStringsReceived);
            }
            mSubject.onComplete();
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
