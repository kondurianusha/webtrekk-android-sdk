package com.webtrekk.webtrekksdk;

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
 * Created by vartbaronov on 10.11.17.
 */

import android.content.Context;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.webtrekk.webtrekksdk.Configuration.TrackingConfiguration;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class ProductListTracker {

    public static class ParameterBuilder{
        private final TrackingParameter mParameter = new TrackingParameter();

        public ParameterBuilder(int position, @NonNull String productdId){
            mParameter.add(TrackingParameter.Parameter.PRODUCT, productdId);
            mParameter.add(TrackingParameter.Parameter.PRODUCT_POSITION, Integer.toString(position));
        }

        @NonNull
        public ParameterBuilder setCost(float cost){
            mParameter.add(TrackingParameter.Parameter.PRODUCT_COST, Float.toString(cost));
            return this;
        }

        @NonNull
        public ParameterBuilder setEcommerce(int index, String value){
            mParameter.add(TrackingParameter.Parameter.ECOM, Integer.toString(index), value);
            return this;
        }

        @NonNull
        public TrackingParameter getResult(){
            return mParameter;
        }
    }

    public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener{

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(@NonNull Context context, final RecyclerView recyclerView){
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, MotionEvent e) {
            if (mGestureDetector.onTouchEvent(e)){
                mApplyPendingAction.run();
                clearPendingEvents();
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    }

    /**
     * Simple inteface for application to provide additional information about items in list.
     * It is mandatory at lease provide productId
     */
    public interface ProductListItemCallback{
        @NonNull
        TrackingParameter getItem(int position);
    }

    final private SortedMap<Integer, TrackingParameter> mProductItems = new TreeMap<>();
    final private SortedMap<Integer, TrackingParameter> mPendingProductItems = new TreeMap<>();
    private RecyclerView.OnScrollListener mScrollListener;
    private Handler mDelayHandler;
    final private Runnable mApplyPendingAction = new Runnable() {
        @Override
        public void run() {
            if (!mPendingProductItems.isEmpty()) {
                mProductItems.putAll(mPendingProductItems);
                WebtrekkLogging.log(mPendingProductItems.size() + " products add to tracking");
                WebtrekkLogging.log("Products positions: " + Arrays.toString(mPendingProductItems.keySet().toArray()));
                mPendingProductItems.clear();
            }
        }
    };

    private RecyclerView.OnItemTouchListener mOnTouсhEventListener;
    private final TrackingConfiguration mTrackingConfiguration;


    ProductListTracker(TrackingConfiguration configuration){
        mTrackingConfiguration = configuration;
    }


    /**
     * sends all product tracking list requests to server.
     */
    public void send(){
        //Check if there is anything to send
        if (mProductItems.isEmpty()){
            return;
        }

        TrackingParameter mergedParameters = constructTrackingParameter();
        Webtrekk webtrekk = Webtrekk.getInstance();

        for (TrackingParameter item: mProductItems.values()){
            TrackingParameter parameters = mergedParameters.mergeProducts(item, mTrackingConfiguration);

            // parameters value is more then 255
            if (parameters == null){
                webtrekk.track(mergedParameters);
                mergedParameters = constructTrackingParameter();
                // we assume that one product list won't have field with length more then 255
                mergedParameters = mergedParameters.mergeProducts(item, mTrackingConfiguration);
            } else{
                mergedParameters = parameters;
            }
        }

        webtrekk.track(mergedParameters);

        mProductItems.clear();
    }

    @NonNull
    private TrackingParameter constructTrackingParameter(){
        TrackingParameter parameter = new TrackingParameter();
        parameter.add(TrackingParameter.Parameter.ACTION_NAME, "webtrekk_ignore");
        parameter.add(TrackingParameter.Parameter.PRODUCT_STATUS, "list");
        return parameter;
    }

    @NonNull
    public void track(@NonNull TrackingParameter parameters){

        String positionS = parameters.getDefaultParameter().get(TrackingParameter.Parameter.PRODUCT_POSITION);

        if (positionS == null){
            WebtrekkLogging.log("Error: No position in parameters. Track won't be done");
            return;
        }

        Integer position = null;

        try {
            position = Integer.valueOf(positionS);
        }catch (NumberFormatException e){}

        if (position == null){
            WebtrekkLogging.log("Error: Incorrect position value in parameters. Track won't be done");
            return;
        }

        mProductItems.put(position, parameters);
    }

    public void registerView(@NonNull RecyclerView view, @NonNull final ProductListItemCallback itemCallback){
        registerView(view, 2000, itemCallback);
    }

    public void registerView(@NonNull RecyclerView view, @IntRange(from=0) final long timeoutMilliseconds, @NonNull final ProductListItemCallback itemCallback){

        final RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        //we not support others layout managers then LinearLayoutManager
        if (!(layoutManager instanceof LinearLayoutManager)) {
            WebtrekkLogging.log("Error: not LinearLayouManager isn't supported");
            return;
        }

        mDelayHandler = new Handler();
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    initPendingList(recyclerView, timeoutMilliseconds, itemCallback);
                }else{
                    clearPendingEvents();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //it equals zero on the first list start in that case start pending
                if (dy == 0){
                    initPendingList(recyclerView, timeoutMilliseconds, itemCallback);
                }
            }
        };

        view.addOnScrollListener(mScrollListener);

        mOnTouсhEventListener = new RecyclerItemClickListener(view.getContext(), view);
        view.addOnItemTouchListener(mOnTouсhEventListener);
    }

    public void unregisterView(@NonNull RecyclerView view){
        if (mScrollListener != null){
            view.removeOnScrollListener(mScrollListener);
        }

        if (mOnTouсhEventListener != null) {
            view.removeOnItemTouchListener(mOnTouсhEventListener);
        }

        clearPendingEvents();

        //send all items
        send();
        mDelayHandler = null;
        mOnTouсhEventListener = null;
        mScrollListener = null;
    }

    private void clearPendingEvents(){
        mPendingProductItems.clear();
        if (mDelayHandler != null) {
            mDelayHandler.removeCallbacks(mApplyPendingAction);
        }
    }

    private void initPendingList(@NonNull RecyclerView view, long timeOut,
                                 ProductListItemCallback itemCallback){
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)view.getLayoutManager();
        final int firstCompletelyItem = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        final int lastCompletelyItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

        for (int position = firstCompletelyItem; position <= lastCompletelyItem; position++){
            if (!mProductItems.containsKey(position)) {
                mPendingProductItems.put(position, itemCallback.getItem(position));
            }
        }

        mDelayHandler.postDelayed(mApplyPendingAction, timeOut);
    }
}

