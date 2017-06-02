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
 * Created by Thomas Dahlmann on 09.11.15.
 */

package com.Webtrekk.SDKTest;

import com.webtrekk.webtrekksdk.Request.RequestUrlStore;
import com.webtrekk.webtrekksdk.Webtrekk;

public class MainActivityTest extends ActivityInstrumentationTestCase2BaseMain<MainActivity> {
    private MainActivity mMainActivity;
    private Webtrekk wt;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mMainActivity = getActivity();
        wt = mMainActivity.getWebtrekk();
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        RequestUrlStore request = new RequestUrlStore(getInstrumentation().getTargetContext());
        request.deleteRequestsFile();
        super.tearDown();
    }

    public void testPreconditions() {
        //Try to add a message to add context to your assertions. These messages will be shown if
        //a tests fails and make it easy to understand why a test failed
        assertNotNull("mFirstTestActivity is null", mMainActivity);
    }

    /**
     * make sure webtrekk was initialized
     */
    public void testInitWebtrekk() {
        assertNotNull(mMainActivity.getWebtrekk());
    }


    /**
     * test that all default sdk values are overriden by the local ones form the local config
     */
    public void testAllValuesOverrideWithApplicationConfig() {

        assertTrue(wt.getTrackDomain().contains("://q3.webtrekk.net"));
        assertEquals(wt.getVersion(), 1);
        assertEquals(wt.getTrackingIDs().get(0), "123451234512345");
        assertEquals(wt.getSampling(), 0);
        assertEquals(wt.getSendDelay(), 1);
        //assertEquals(wt.getResendOnStartEventTime(), 303);
        assertEquals(wt.getMaxRequests(), 100);
        assertEquals(wt.getTrackingConfigurationUrl(), "https://d1r27qvpjiaqj3.cloudfront.net/238713152098253/34629.xml");
        assertEquals(wt.isAutoTracked(), true);
        assertEquals(wt.isAutoTrackApiLevel(), true);

    }

    /**
     * test that when a config value is invalid it chooses the default from the sdk
     */
    public void testInvalidValuesInApplicationConfig() {
        assertEquals(wt.isEnableRemoteConfiguration(), false);
    }

    /**
     * test that when an empty value is provided it also uses the default from the sdk
     */

    public void testEmptyValuesInApplicationConfig() {
        assertEquals(wt.getResendOnStartEventTime(), 30);
    }

}
