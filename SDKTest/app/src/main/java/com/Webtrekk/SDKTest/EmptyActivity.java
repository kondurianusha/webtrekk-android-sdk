package com.Webtrekk.SDKTest;

import android.app.Activity;

/**
 * Created by vartbaronov on 14.04.16.
 */
public class EmptyActivity extends Activity {

    public boolean isStopped() {
        return mIsStopped;
    }

    public boolean isStartedToStopping() {
        return mIsStartedToStopping;
    }

    volatile boolean mIsStopped;
    volatile boolean mIsStartedToStopping;


    @Override
    protected void onStart() {
        super.onStart();
        mIsStopped = false;
        mIsStartedToStopping = false;
    }

    @Override
    protected void onStop() {
        mIsStartedToStopping = true;
        super.onStop();
        mIsStopped = true;
    }
}
