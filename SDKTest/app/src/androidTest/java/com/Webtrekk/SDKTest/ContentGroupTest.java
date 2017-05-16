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
 * Created by Arsen Vartbaronov on 26.05.16.
 */

package com.Webtrekk.SDKTest;

import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Webtrekk;

public class ContentGroupTest  extends ActivityInstrumentationTestCase2Base<EmptyActivity> {
    private Webtrekk mWebtrekk;

    public ContentGroupTest() {
        super(EmptyActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_no_auto_track);
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        super.tearDown();
    }

    public void testContentGroup() {
        final String pageCat1 = "Cat1";
        final String pageCatLong = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456";

        assertEquals(256, pageCatLong.length());
        final String trunkPageCatLong = pageCatLong.substring(0, 255);
        assertEquals(255, trunkPageCatLong.length());


        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                TrackingParameter par = new TrackingParameter();
                par.add(TrackingParameter.Parameter.PAGE_CAT, "1", pageCat1);
                par.add(TrackingParameter.Parameter.PAGE_CAT, "2", pageCatLong);
                mWebtrekk.track(par);
            }
        });

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);

        assertEquals(pageCat1, HelperFunctions.urlDecode(parcel.getValue("cg1")));
        assertEquals(trunkPageCatLong, HelperFunctions.urlDecode(parcel.getValue("cg2")));
    }
}