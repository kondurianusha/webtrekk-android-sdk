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
 * Created by Arsen Vartbaronov on 09.05.16.
 */

package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.TouchUtils;
import android.util.Log;
import android.widget.Button;

import com.webtrekk.webtrekksdk.Webtrekk;

public class PageAutoTrackingTest extends ActivityInstrumentationTestCase2Base<MainActivity> {

    Webtrekk mWebtrekk;

    public PageAutoTrackingTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication);
    }


    public void testActivityChanging() {

        setActivityInitialTouchMode(true);

        Instrumentation instrumentation = getInstrumentation();

        Instrumentation.ActivityMonitor pageActivityMonitor = instrumentation.addMonitor(PageExampleActivity.class.getName(), null, false);
        Instrumentation.ActivityMonitor nextPageActivityMonitor = instrumentation.addMonitor(NextPageExampleActivity.class.getName(), null, false);

        //track main activity
        initWaitingForTrack(null);
        Activity mainActivity = getActivity();

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);
        assertTrue(parcel.getValue("p").contains("Startseite"));

        //track first page activity
        initWaitingForTrack(null);

        Button pageExampleButton = (Button) mainActivity.findViewById(R.id.button_page);
        TouchUtils.clickView(this, pageExampleButton);

        Activity pageActivity = getInstrumentation().waitForMonitorWithTimeout(pageActivityMonitor, 5);
        assertNotNull(pageActivity);
        instrumentation.removeMonitor(pageActivityMonitor);

        URL = waitForTrackedURL();
        parcel.parseURL(URL);
        assertTrue(parcel.getValue("p").contains("Seite"));

        //track next page activity
        initWaitingForTrack(null);

        Button nextPageButton = (Button) pageActivity.findViewById(R.id.button_next_page);
        TouchUtils.clickView(this, nextPageButton);

        URL = waitForTrackedURL();
        parcel.parseURL(URL);
        assertTrue(parcel.getValue("p").contains("NextPage"));

        Activity nextPageActivity = getInstrumentation().waitForMonitorWithTimeout(nextPageActivityMonitor, 5);
        assertNotNull(nextPageActivity);
        instrumentation.removeMonitor(nextPageActivityMonitor);

        //track first page activity
        initWaitingForTrack(null);

        finishActivitySync(nextPageActivity);

        URL = waitForTrackedURL();
        parcel.parseURL(URL);
        assertTrue(parcel.getValue("p").contains("Seite"));

        //track main activity
        initWaitingForTrack(null);

        finishActivitySync(pageActivity);

        URL = waitForTrackedURL();
        parcel.parseURL(URL);
        assertTrue(parcel.getValue("p").contains("Startseite"));

        finishActivitySync(mainActivity);
    }
}