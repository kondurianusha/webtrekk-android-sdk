package com.Webtrekk.SDKTest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestRunner;

import com.Webtrekk.SDKTest.SimpleHTTPServer.HttpServer;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by vartbaronov on 17.05.16.
 */
public class ActivityInstrumentationTestCase2BaseMain<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
    protected Application mApplication;
    private Thread.UncaughtExceptionHandler mOldHandler;
    protected boolean mIsErrorHandlerTest;
    protected boolean mIsExternalCall;
    static private String IS_EXTERNAL = "external";
    protected boolean mIsCDBTestRequest;

    public ActivityInstrumentationTestCase2BaseMain(Class<T> activityClass) {
        super(activityClass);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void setUp() throws Exception {

        super.setUp();
        //refresh webtrekk instance
        refreshWTInstance();
        mOldHandler = Thread.getDefaultUncaughtExceptionHandler();
        mApplication = (Application)getInstrumentation().getTargetContext().getApplicationContext();
        if (!mIsErrorHandlerTest)
            deleteErrorHandlerFile(mApplication);
        if (!mIsCDBTestRequest)
            deleteCDBRepeatRequestInfo();

        Bundle arguments = ((InstrumentationTestRunner)getInstrumentation()).getArguments();
        if (arguments.size() > 0)
            WebtrekkLogging.log("Receive arguments for test:"+arguments);
        mIsExternalCall = arguments.getString(IS_EXTERNAL) != null;
    }

    @Override
    public void tearDown() throws Exception {
        Webtrekk webtrekk = Webtrekk.getInstance();
        if (webtrekk.isInitialized())
            webtrekk.stopTracking();
        Thread.setDefaultUncaughtExceptionHandler(mOldHandler);
        unregisterCallback();
        getInstrumentation().waitForIdleSync();
        stopSendThread();
        super.tearDown();
    }

    protected void deleteErrorHandlerFile(Context context)
    {
        File loadFile = new File(context.getFilesDir().getPath() + File.separator+"exception.txt");
        loadFile.delete();
    }

    protected void refreshWTInstance() {
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

    protected void unregisterCallback()
    {
        if (mApplication == null)
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
            mApplication.unregisterActivityLifecycleCallbacks(callback);
        } catch (NoSuchFieldException e) {
            WebtrekkLogging.log("Can't remove activity callback");
        } catch (IllegalAccessException e) {
            WebtrekkLogging.log("Can't remove activity callback");
        }
    }

    protected void finishActivitySync(Activity activity)
    {
        finishActivitySync(activity, getInstrumentation(), true);
    }

    protected void finishActivitySync(Activity activity, boolean killApplication)
    {
        ActivityInstrumentationTestCase2BaseMain.finishActivitySync(activity, getInstrumentation(), killApplication);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    static public void finishActivitySync(Activity activity, Instrumentation instrumentation, boolean killApplication)
    {   activity.finish();
        //give activity one minute to finish
        long currentTime = System.currentTimeMillis();
        boolean finishTimeout = false;
        int activityHash = activity.hashCode();
        while (!activity.isDestroyed() && !finishTimeout) {
            instrumentation.waitForIdleSync();
            finishTimeout = (System.currentTimeMillis() - currentTime) > 140000;
        }

        if (finishTimeout) {
            WebtrekkLogging.log("finishActivitySync: finished by timeout. Hash:" + activityHash);
        }
    }

    private void deleteCDBRepeatRequestInfo()
    {
        SharedPreferences preferences = HelperFunctions.getWebTrekkSharedPreference(getInstrumentation().getTargetContext());

        if (preferences.contains("LAST_CBD_REQUEST_DATE"))
            preferences.edit().remove("LAST_CBD_REQUEST_DATE").apply();
    }

    private void stopSendThread()
    {
        if (mApplication == null)
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
            Field callbackField = Webtrekk.class.getDeclaredField("mTimerService");
            callbackField.setAccessible(true);
            ScheduledExecutorService threadService = (ScheduledExecutorService) callbackField.get(webtrekk);
            threadService.shutdownNow();
        } catch (NoSuchFieldException e) {
            WebtrekkLogging.log("Can't remove activity callback");
        } catch (IllegalAccessException e) {
            WebtrekkLogging.log("Can't remove activity callback");
        }
    }


}
