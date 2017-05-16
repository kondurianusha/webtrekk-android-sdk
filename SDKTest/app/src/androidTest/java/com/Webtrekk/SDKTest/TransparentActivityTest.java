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
 * Created by Arsen Vartbaronov on 27.05.16.
 */

package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.content.Intent;

import com.webtrekk.webtrekksdk.Webtrekk;

public class TransparentActivityTest extends ActivityInstrumentationTestCase2Base<EmptyActivity> {

    Webtrekk mWebtrekk;

    public TransparentActivityTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testTransparentActivity()
    {
        initWaitingForTrack(null);
        getActivity();

        waitForTrackedURL();

        initWaitingForTrack(null);
        Intent newActivityIntent = new Intent(getActivity(), TransparentActivity.class);
        newActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Activity newActivity = getInstrumentation().startActivitySync(newActivityIntent);

        waitForTrackedURL();

        initWaitingForTrack(null);
        finishActivitySync(newActivity);

        waitForTrackedURL(true);

        finishActivitySync(getActivity());
    }
}