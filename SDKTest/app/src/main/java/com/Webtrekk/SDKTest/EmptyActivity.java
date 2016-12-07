package com.Webtrekk.SDKTest;

import android.app.Activity;

/**
 * Created by vartbaronov on 14.04.16.
 */
public class EmptyActivity extends Activity {

    public boolean isStopped() {
        return mIsStopped;
    }

    boolean mIsStopped;


    @Override
    protected void onStart() {
        super.onStart();
        mIsStopped = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIsStopped = true;
    }
}
