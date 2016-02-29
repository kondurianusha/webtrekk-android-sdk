package com.webtrekk.webtrekksdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * this class provides global functions to override the activitylivecycle callbacks
 * it calls the original function but also sends a track request before
 */
class TrackedActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private Webtrekk webtrekk;
    // timestamp when the last request was send, this is usefull for resending onStart track events
    long lastRequestTimestamp;
    boolean isPaused;

    public TrackedActivityLifecycleCallbacks(Webtrekk webtrekk) {
        this.webtrekk = webtrekk;
        isPaused = false;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }
    @Override
    public void onActivityPaused(Activity activity) {
        WebtrekkLogging.log("Tracking Activity Paused: " + activity.getClass().getName());
        // this means the application is no longer in the foreground so save timestamp when this happened
        lastRequestTimestamp = System.currentTimeMillis();
        isPaused = true;
    }
    @Override
    public void onActivityResumed(Activity activity) {
        WebtrekkLogging.log("Tracking Activity Resumed: " + activity.getClass().getName());
        if(isPaused) {
            if((System.currentTimeMillis() - lastRequestTimestamp)/1000 > webtrekk.getTrackingConfiguration().getResendOnStartEventTime()) {
                // in this case more than resendOnStartEventTime seconds have passed since the last request, so send the onStart Event again
                webtrekk.autoTrackActivity();
            }
            isPaused = false;
        }
    }
    @Override
    public void onActivitySaveInstanceState (Activity activity, Bundle outState) {
    }
    @Override
    public void onActivityStarted(Activity activity) {
        WebtrekkLogging.log("Tracking Activity started: " + activity.getClass().getName());
        webtrekk.startActivity(activity.getClass().getName());
        webtrekk.autoTrackActivity();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        WebtrekkLogging.log("Tracking Activity stopped: " + activity.getClass().getName());
        webtrekk.stopActivity();
    }
}
