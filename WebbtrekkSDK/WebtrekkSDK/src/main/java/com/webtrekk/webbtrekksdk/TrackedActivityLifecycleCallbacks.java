package com.webtrekk.webbtrekksdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * this class provides global functions to override the activitylivecycle callbacks
 * it calls the original function but also sends a track request before
 */
class TrackedActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private Webtrekk webtrekk;

    public TrackedActivityLifecycleCallbacks(Webtrekk webtrekk) {
        this.webtrekk = webtrekk;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        WebtrekkLogging.log("Tracking Activity Created: " + activity.getLocalClassName());
        webtrekk.autoTrackActivity(activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        WebtrekkLogging.log("Tracking Activity Destroyed: " + activity.getLocalClassName());
        webtrekk.autoTrackActivity(activity.getLocalClassName());
    }
    @Override
    public void onActivityPaused(Activity activity) {
        WebtrekkLogging.log("Tracking Activity Paused: " + activity.getLocalClassName());
        webtrekk.autoTrackActivity(activity.getLocalClassName());
    }
    @Override
    public void onActivityResumed(Activity activity) {
        WebtrekkLogging.log("Tracking Activity Resumed: " + activity.getLocalClassName());
        webtrekk.autoTrackActivity(activity.getLocalClassName());
    }
    @Override
    public void onActivitySaveInstanceState (Activity activity, Bundle outState) {
        WebtrekkLogging.log("Tracking Activity saved: " + activity.getLocalClassName());
        //app.getTracker().autoTrackActivity(activity.getLocalClassName());
    }
    @Override
    public void onActivityStarted(Activity activity) {
        WebtrekkLogging.log("Tracking Activity started: " + activity.getLocalClassName());
        webtrekk.autoTrackActivity(activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        WebtrekkLogging.log("Tracking Activity stopped: " + activity.getLocalClassName());
        webtrekk.autoTrackActivity(activity.getLocalClassName());
    }
}
