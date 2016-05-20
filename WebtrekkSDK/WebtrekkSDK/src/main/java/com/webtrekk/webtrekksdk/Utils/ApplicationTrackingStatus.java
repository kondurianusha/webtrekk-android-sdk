package com.webtrekk.webtrekksdk.Utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by vartbaronov on 06.05.16.
 * Need to calculate status of application
 * @hide
 */
public class ApplicationTrackingStatus implements Application.ActivityLifecycleCallbacks
{
    public enum STATUS
    {
        NO_ACTIVITY_IS_RUNNING,
        FIRST_ACTIVITY_STARTED,
        ACTIVITY_IS_SHOWN,
        RETURNINIG_FROM_BACKGROUND,
        GOING_TO_BACKGROUND,
        SHUT_DOWNING
    }

    private STATUS mCurrentStatus = STATUS.NO_ACTIVITY_IS_RUNNING;
    private String mCurrentActivityName;
    private WeakReference<Activity> mCurrentActivityInstance;

    volatile private int mCurrentActivitiesCount;
    private String mPreviousActivityName;
    private String mFirstActivityName;
    private long mLastActivityVisibleTime;
    private long mReturnFromBackgroundTime;

    private boolean mIsRecreationInProgress;

    public STATUS getCurrentStatus() {
        return mCurrentStatus;
    }

    public int getCurrentActivitiesCount() {
        return mCurrentActivitiesCount;
    }

    public String getCurrentActivityName() {
        return mCurrentActivityName;
    }

    public boolean isRecreationInProgress() {
        return mIsRecreationInProgress;
    }

    /**
     *
     * @return time in seconds when application was in background. Should ba called when status is RETURNINIG_FROM_BACKGROUND
     */
    public long inactivityApplicaitonTime()
    {
        return (mReturnFromBackgroundTime - mLastActivityVisibleTime)/1000;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //TODO delete logging
        WebtrekkLogging.log("Tracking Activity Created: "+getActivityName(activity) + " instance hash:" + activity.hashCode() + (savedInstanceState != null ? " as recreation":""));
        WebtrekkLogging.log("CurrentStatus before:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);
        mIsRecreationInProgress = savedInstanceState != null;
        if (!mIsRecreationInProgress) {
            mPreviousActivityName = mCurrentActivityName;
            mCurrentActivityName = getActivityName(activity);
        }
        mCurrentActivityInstance = new WeakReference<Activity>(activity);

        if (!mIsRecreationInProgress)
            mCurrentActivitiesCount++;

        WebtrekkLogging.log("CurrentStatus after:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        WebtrekkLogging.log("Tracking Activity started: " + getActivityName(activity) + " instance hash:" + activity.hashCode());
        WebtrekkLogging.log("CurrentStatus before:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);

        //this is first start, but initialization was done in onCreate of MainActivity
        if (!mIsRecreationInProgress && mCurrentActivitiesCount == 0)
            mCurrentActivitiesCount = 1;

        if (mCurrentActivitiesCount == 1 && mCurrentStatus == STATUS.NO_ACTIVITY_IS_RUNNING) {
            mCurrentStatus = STATUS.FIRST_ACTIVITY_STARTED;
            if (mCurrentActivityName == null) {
                mCurrentActivityName = getActivityName(activity);
                mCurrentActivityInstance = new WeakReference<Activity>(activity);
            }
            mFirstActivityName = mCurrentActivityName;
        }else if (mCurrentStatus == STATUS.GOING_TO_BACKGROUND && getActivityName(activity).equals(mCurrentActivityName))
        {
            mCurrentStatus = STATUS.RETURNINIG_FROM_BACKGROUND;
            mReturnFromBackgroundTime = System.currentTimeMillis();
        }

        WebtrekkLogging.log("CurrentStatus after:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        WebtrekkLogging.log("Tracking Activity Resumed: " + getActivityName(activity) + " instance hash:" + activity.hashCode() + (mIsRecreationInProgress ? " as recreation":""));
        WebtrekkLogging.log("CurrentStatus before:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);
        mIsRecreationInProgress = false;
        mCurrentStatus = STATUS.ACTIVITY_IS_SHOWN;
        WebtrekkLogging.log("CurrentStatus after:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);
    }

    @Override
    public void onActivityPaused(Activity activity) {
      WebtrekkLogging.log("Tracking Activity Paused: " + getActivityName(activity));

        WebtrekkLogging.log("CurrentStatus before:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " instance hash:" + activity.hashCode() + " Previous Activity:"+mPreviousActivityName);
        if (activity.isFinishing() && mCurrentActivityName.equals(getActivityName(activity))) {
            mCurrentActivityName = mPreviousActivityName;
            mCurrentActivityInstance = null;
            mPreviousActivityName = null;
        }

        WebtrekkLogging.log("CurrentStatus after:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        WebtrekkLogging.log("Tracking Activity stopped: " + getActivityName(activity) + " instance hash:" + activity.hashCode() + (activity.isFinishing() ? " as finishing" : " as sleeping"));
        WebtrekkLogging.log("CurrentStatus before:"+mCurrentStatus+ " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);
        if (activity.isFinishing()) {
            //activity is finishing increase activity counter and determine applicaiton shut down
            mCurrentActivitiesCount--;
            if (mCurrentActivitiesCount == 0 || (mFirstActivityName != null && mFirstActivityName.equals(getActivityName(activity))))
                mCurrentStatus = STATUS.SHUT_DOWNING;
        }else
        {
            // if this is not finishing activity and there is no any new activity started that is go to background process
            String stoppedActivity = getActivityName(activity);
            if (stoppedActivity.equals(mCurrentActivityName) && !activity.isChangingConfigurations() && (mCurrentActivityInstance == null || activity == mCurrentActivityInstance.get()))
            {
                mCurrentStatus = STATUS.GOING_TO_BACKGROUND;
                mLastActivityVisibleTime = System.currentTimeMillis();
            }
        }
        WebtrekkLogging.log("CurrentStatus after:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        WebtrekkLogging.log("Tracking Activity SaveInstance: " + getActivityName(activity) + (outState != null ? " as recreation":""));
        WebtrekkLogging.log("CurrentStatus:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        WebtrekkLogging.log("Tracking Activity Destroyed: " + getActivityName(activity) + " instance hash:" + activity.hashCode() + (activity.isFinishing() ? " as finishing" : " as sleeping"));
        WebtrekkLogging.log("CurrentStatus before:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);

        if (activity.isFinishing() && mCurrentStatus != STATUS.SHUT_DOWNING &&
                    (mCurrentActivitiesCount == 0 || (mFirstActivityName != null && mFirstActivityName.equals(getActivityName(activity))))) {
                mCurrentStatus = STATUS.SHUT_DOWNING;
        }
        WebtrekkLogging.log("CurrentStatus after:"+mCurrentStatus + " Current Activity:"+mCurrentActivityName + " Previous Activity:"+mPreviousActivityName);
    }

    private String getActivityName(Activity activity)
    {
        return activity.getClass().getName();
    }
}
