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
 * Created by Arsen Vartbaronov on 30.05.16.
 */

package com.Webtrekk.SDKTest;

import android.content.Intent;
import android.os.Looper;

import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Webtrekk;
import com.webtrekk.webtrekksdk.WebtrekkRecommendations;

import java.util.List;

public class RecommendationsTest extends ActivityInstrumentationTestCase2Base<RecommendationActivity> {

    Webtrekk mWebtrekk;

    public RecommendationsTest(){
        super(RecommendationActivity.class);
        }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_recomendations);
    }

    public void testComplexRecommendations()
    {
        recommendTest("complexReco", "085cc2g007", null);
    }

    public void testSimpleRecommendations()
    {
        recommendTest("simpleReco", null, null);
    }

    public void testEmptyRecommendationList()
    {
        recommendTest("emptyTest", null, null, 0);
    }

    private void recommendTest(String recName, String productID, String productCat)
    {
        recommendTest(recName, productID, productCat, 1);
    }

    private void recommendTest(String recName, String productID, String productCat, int countCheck)
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(RecommendationActivity.RECOMMENDATION_NAME, recName);
        intent.putExtra(RecommendationActivity.RECOMMENDATION_PRODUCT_ID, productID);
        intent.putExtra(RecommendationActivity.RECOMMENDATION_PRODUCT_CAT, productCat);
        setActivityIntent(intent);
        RecommendationActivity activity = getActivity();
        //Wait for recommendation request done
        while (!activity.isRequestFinished()) {
            getInstrumentation().waitForIdleSync();
        }

        //Check results
        assertEquals(WebtrekkRecommendations.QueryRecommendationResult.RECEIVED_OK, getActivity().getLastResult());

        if (countCheck > 0)
          assertTrue(getActivity().getRecommendationCount() >= countCheck);
        else
          assertEquals(0, getActivity().getRecommendationCount());
        assertTrue(getActivity().isUsedUIThread());

        finishActivitySync(activity);
    }

    public void testRecoRequest()
    {
        Webtrekk webtrekk = Webtrekk.getInstance();
        WebtrekkRecommendations recommendations = webtrekk.getRecommendations();

        initWaitingForTrack(null);
        final long currentThreadID = Thread.currentThread().getId();

        assertFalse(Looper.getMainLooper().getThread() == Thread.currentThread());

        recommendations.queryRecommendation(new WebtrekkRecommendations.RecommendationCallback() {
            @Override
            public void onReceiveRecommendations(List<WebtrekkRecommendations.RecommendationProduct> products, WebtrekkRecommendations.QueryRecommendationResult result) {
                assertFalse(currentThreadID == Thread.currentThread().getId());
            }
        }, "paramTest").setProductId("productIDTest")/*.setProductCat("productCatTest")*/.call();

        String url = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(url);

        assertEquals("productIDTest", parcel.getValue("product"));
        //assertEquals("productCatTest", parcel.getValue("productCat"));
        assertEquals(HelperFunctions.getEverId(getInstrumentation().getTargetContext()), parcel.getValue("userId"));
    }
}
