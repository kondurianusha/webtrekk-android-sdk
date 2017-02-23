package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.app.Application;

import com.webtrekk.webtrekksdk.Request.RequestFactory;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by vartbaronov on 23.02.17.
 */

public class SDKInstanceManager {
    private Thread.UncaughtExceptionHandler mOldHandler;

    public void setup(){
        refreshWTInstance();
        mOldHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void release(Application application){
        Webtrekk webtrekk = Webtrekk.getInstance();
        if (webtrekk.isInitialized())
            webtrekk.stopTracking();
        Thread.setDefaultUncaughtExceptionHandler(mOldHandler);
        unregisterCallback(application);
        stopSendThread(application);
        unregisterActivityEventCallback(application);
    }

    private void refreshWTInstance() {
        Class<Webtrekk> wtClass = Webtrekk.class;
        try {
            for (Class<?> classObj : wtClass.getDeclaredClasses()) {
                if (classObj.getName().contains("SingletonHolder")) {
                    Field field = null;
                    field = classObj.getDeclaredField("webtrekk");
                    field.setAccessible(true);
                    Constructor<Webtrekk> wtConstr = wtClass.getDeclaredConstructor();
                    wtConstr.setAccessible(true);
                    field.set(null, wtConstr.newInstance());
                }
            }
        } catch (NoSuchFieldException e) {
            WebtrekkLogging.log("Can't refresh Webtrekk instance");
        } catch (NoSuchMethodException e) {
            WebtrekkLogging.log("Can't refresh Webtrekk instance");
        } catch (InstantiationException e) {
            WebtrekkLogging.log("Can't refresh Webtrekk instance");
        } catch (IllegalAccessException e) {
            WebtrekkLogging.log("Can't refresh Webtrekk instance");
        } catch (InvocationTargetException e) {
            WebtrekkLogging.log("Can't refresh Webtrekk instance");
        }
    }

    private void unregisterCallback(Application application)
    {
        if (application == null)
        {
            WebtrekkLogging.log("Error unregister callback. Application reference is null");
            return;
        }

        if (!Webtrekk.getInstance().isInitialized())
        {
            WebtrekkLogging.log("Error unregister callback. Webtrekk isn't initialized");
            return;
        }
        Webtrekk webtrekk = Webtrekk.getInstance();

        try {
            Field callbackField = Webtrekk.class.getDeclaredField("mCallbacks");
            callbackField.setAccessible(true);
            Application.ActivityLifecycleCallbacks callback = (Application.ActivityLifecycleCallbacks) callbackField.get(webtrekk);
            application.unregisterActivityLifecycleCallbacks(callback);
        } catch (NoSuchFieldException e) {
            WebtrekkLogging.log("Can't remove activity callback");
        } catch (IllegalAccessException e) {
            WebtrekkLogging.log("Can't remove activity callback");
        }
    }


    private void stopSendThread(Application application)
    {
        if (application == null)
        {
            WebtrekkLogging.log("Error unregister callback. Application reference is null");
            return;
        }

        if (!Webtrekk.getInstance().isInitialized())
        {
            WebtrekkLogging.log("Error unregister callback. Webtrekk isn't initialized");
            return;
        }
        Webtrekk webtrekk = Webtrekk.getInstance();

        RequestFactory requestFactory = (RequestFactory)returnHiddenField(webtrekk, "mRequestFactory");
        ScheduledExecutorService threadService1 = (ScheduledExecutorService)returnHiddenField(requestFactory, "mURLSendTimerService");
        ScheduledExecutorService threadService2 = (ScheduledExecutorService)returnHiddenField(requestFactory, "mFlashTimerService");
        threadService1.shutdownNow();
        threadService2.shutdownNow();
    }

    private void unregisterActivityEventCallback(Application application)
    {
        Webtrekk webtrekk = Webtrekk.getInstance();

        Application.ActivityLifecycleCallbacks callbacks = (Application.ActivityLifecycleCallbacks)returnHiddenField(webtrekk, "mCallbacks");
        if (callbacks != null)
        {
            application.unregisterActivityLifecycleCallbacks(callbacks);
        }
    }

    static private boolean isActivityStopped(Activity activity)
    {
        return (Boolean)returnHiddenField(activity, "mStopped");
    }

    static private boolean isActivityResumed(Activity activity)
    {
        return (Boolean)returnHiddenField(activity, "mResumed");
    }

    static private Object returnHiddenField(Object object, String fieldName)
    {
        Object result = null;
        try {
            Field callbackField = object.getClass().getDeclaredField(fieldName);
            callbackField.setAccessible(true);
            result = callbackField.get(object);
        } catch (NoSuchFieldException e) {
            WebtrekkLogging.log("Can't remove activity callback");
        } catch (IllegalAccessException e) {
            WebtrekkLogging.log("Can't remove activity callback");
        }

        return result;
    }
}
