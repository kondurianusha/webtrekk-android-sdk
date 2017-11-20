package com.Webtrekk.SDKTest;

import android.support.test.filters.LargeTest;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.Webtrekk.SDKTest.ProductList.ProductItem;
import com.Webtrekk.SDKTest.ProductList.ProductListActivity;
import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Webtrekk;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.Espresso.pressBack;
import static java.lang.Thread.sleep;


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
 * Created by Arsen Vartbaronov on 16.11.17.
 */
@RunWith(WebtrekkClassRunner.class)
@LargeTest
public class ProductListTrackingTest extends WebtrekkBaseMainTest {
    private Webtrekk mWebtrekk;

    @Rule
    public final WebtrekkTestRule<ProductListActivity> mActivityRule =
            new WebtrekkTestRule<>(ProductListActivity.class, this);

    @Override
    public void before() throws Exception {
        super.before();
        mWaitMilliseconds = 5000;
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_no_auto_track);
    }

    @Override
    @After
    public void after() throws Exception {
        super.after();
    }

    @Test
    public void manualTrackingTest(){

    }

    @Test
    public void integrationWithRecyclingViewNothingSendTest(){
        try {
            sleep(1000);
            onView(withId(R.id.productListRecyclerView)).perform(swipeUp());
            sleep(1000);
            initWaitingForTrack(null, true);
            mActivityRule.getActivity().unregisterTracking();

            //check for nothing to send
            waitForTrackedURL();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void integrationWithRecyclingViewSendOnFirstPageTest(){
        try {
            sleep(2100);
            initWaitingForTrack(null);
            mActivityRule.getActivity().unregisterTracking();

            //check that there request sent
            waitForTrackedURL();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void validateCorrectDataToSend(){
        try {
            RecyclerView recyclerView = mActivityRule.getActivity().getRecyclerView();
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            Set<Integer> positionsTracked = new HashSet<>();
            sleep(1000);
            onView(withId(R.id.productListRecyclerView)).perform(swipeUp());
            sleep(4000);
            addToHash(linearLayoutManager.findFirstCompletelyVisibleItemPosition(),
                    linearLayoutManager.findLastCompletelyVisibleItemPosition(), positionsTracked);

            onView(withId(R.id.productListRecyclerView)).perform(swipeUp());
            sleep(1000);

            onView(withId(R.id.productListRecyclerView)).perform(swipeUp());
            sleep(4000);

            final int lastItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            addToHash(linearLayoutManager.findFirstCompletelyVisibleItemPosition(),
                    lastItem, positionsTracked);

            initWaitingForTrack(null);
            mActivityRule.getActivity().unregisterTracking();

            //check that there request sent
            List<String> urls = waitForTrackedURLs(new StopListenForURLCallback() {
                @Override
                public boolean stop(String url) {
                    URLParsel parcel = new URLParsel();

                    parcel.parseURL(url);
                    final String[] positionsToCheck =
                            parcel.getDecodedValue(TrackingParameter.Parameter.PRODUCT_POSITION.toString()).split(";");

                    return Integer.valueOf(positionsToCheck[positionsToCheck.length - 1]) == lastItem;
                }
            });

            Set<Integer> positionsResult = new HashSet<>();
            Set<Float> costResult = new HashSet<>();
            Set<String> ecom1Result = new HashSet<>();
            Set<String> ecom2Result = new HashSet<>();

            //fill what should be tracked
            Set<Float> costTracked = new HashSet<>();
            Set<String> ecom1Tracked = new HashSet<>();
            Set<String> ecom2Tracked = new HashSet<>();

            for (int index: positionsTracked) {
                final ProductItem item = ProductItem.Handler.getProductItemByIndex(index);
                costTracked.add(item.getCost());
                ecom1Tracked.add(item.getEcomParameters()[0]);
                ecom2Tracked.add(item.getEcomParameters()[1]);
            }



            for (int i = 0; i < urls.size(); i++){
                URLParsel parcel = new URLParsel();

                parcel.parseURL(urls.get(i));

                positionsResult.addAll(fromStringToInt(Arrays.asList(parcel.getDecodedValue("plp").split(";"))));
                costResult.addAll(fromStringToFloat(Arrays.asList(parcel.getDecodedValue("co").split(";"))));
                ecom1Result.addAll(Arrays.asList(parcel.getDecodedValue("cb1").split(";")));
                ecom2Result.addAll(Arrays.asList(parcel.getDecodedValue("cb2").split(";")));
            }

            //positions
            assertTrue(positionsResult.containsAll(positionsTracked) && positionsTracked.containsAll(positionsResult));
            //cost
            assertTrue(costResult.containsAll(costTracked) && costTracked.containsAll(costResult));
            //ecom
            assertTrue(ecom1Result.containsAll(ecom1Tracked) && ecom1Tracked.containsAll(ecom1Result));
            assertTrue(ecom2Result.containsAll(ecom2Tracked) && ecom2Tracked.containsAll(ecom2Result));




        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addToHash(int min, int max, Set<Integer> set){
        for (int nummer = min; nummer <= max; nummer++){
            set.add(nummer);
        }
    }

    private Set<Integer> fromStringToInt(Collection<String> set){
        Set<Integer> toSet = new HashSet<>();
        for (String value: set){
            toSet.add(Integer.valueOf(value));
        }
        return toSet;
    }

    private Set<Float> fromStringToFloat(Collection<String> set){
        Set<Float> toSet = new HashSet<>();
        for (String value: set){
            toSet.add(Float.valueOf(value));
        }
        return toSet;
    }
}
