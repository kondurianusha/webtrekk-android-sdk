package com.webtrekk.webtrekksdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.webtrekk.webtrekksdk.Utils.ApplicationTrackingStatus;

/**
 * this class provides global functions to override the activitylivecycle callbacks
 * it calls the original function but also sends a track request before
 * @hide
 */
class TrackedActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    final private Webtrekk mWebtrekk;
    private final ApplicationTrackingStatus mApplicationStatus = new ApplicationTrackingStatus();
    // timestamp when the last request was send, this is usefull for resending onStart track events

    public TrackedActivityLifecycleCallbacks(Webtrekk webtrekk) {
        mWebtrekk = webtrekk;
        mWebtrekk.setApplicationStatus(mApplicationStatus);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mApplicationStatus.onActivityCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mApplicationStatus.onActivityDestroyed(activity);
        mWebtrekk.destroy();
    }
    @Override
    public void onActivityPaused(Activity activity) {
        mApplicationStatus.onActivityPaused(activity);
    }
    @Override
    public void onActivityResumed(Activity activity) {
        mApplicationStatus.onActivityResumed(activity);
    }
    @Override
    public void onActivitySaveInstanceState (Activity activity, Bundle outState) {
        mApplicationStatus.onActivitySaveInstanceState(activity, outState);
    }
    @Override
    public void onActivityStarted(Activity activity) {
        mApplicationStatus.onActivityStarted(activity);
        mWebtrekk.startActivity();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mApplicationStatus.onActivityStopped(activity);
        mWebtrekk.stopActivity();
    }
}
