package com.webtrekk.webbtrekksdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * this class provides global functions to override the activitylivecycle callbacks
 * it calls the original function but also sends a track request before
 */
class TrackedActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private WebtrekkApplication app;

    public TrackedActivityLifecycleCallbacks(WebtrekkApplication app) {
        this.app = app;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        WebtrekkLogging.log("Tracking Activity Created: " + activity.getLocalClassName());
        app.getWebtrekk().autoTrackActivity(activity.getLocalClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        WebtrekkLogging.log("Tracking Activity Destroyed: " + activity.getLocalClassName());
        app.getWebtrekk().autoTrackActivity(activity.getLocalClassName());
    }
    @Override
    public void onActivityPaused(Activity activity) {
        WebtrekkLogging.log("Tracking Activity Paused: " + activity.getLocalClassName());
        app.getWebtrekk().autoTrackActivity(activity.getLocalClassName());
    }
    @Override
    public void onActivityResumed(Activity activity) {
        WebtrekkLogging.log("Tracking Activity Resumed: " + activity.getLocalClassName());
        app.getWebtrekk().autoTrackActivity(activity.getLocalClassName());
    }
    @Override
    public void onActivitySaveInstanceState (Activity activity, Bundle outState) {
        WebtrekkLogging.log("Tracking Activity saved: " + activity.getLocalClassName());
        //app.getTracker().autoTrackActivity(activity.getLocalClassName());
    }
    @Override
    public void onActivityStarted(Activity activity) {
        WebtrekkLogging.log("Tracking Activity started: " + activity.getLocalClassName());
        app.getWebtrekk().autoTrackActivity(activity.getLocalClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        WebtrekkLogging.log("Tracking Activity stopped: " + activity.getLocalClassName());
        app.getWebtrekk().autoTrackActivity(activity.getLocalClassName());
    }
}
