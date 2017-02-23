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
import com.webtrekk.webtrekksdk.Request.RequestFactory;
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
    private SDKInstanceManager mSDKManager = new SDKInstanceManager();
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
        mSDKManager.setup();
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
        mSDKManager.release(mApplication);
        super.tearDown();
    }

    protected void deleteErrorHandlerFile(Context context)
    {
        File loadFile = new File(context.getFilesDir().getPath() + File.separator+"exception.txt");
        loadFile.delete();
    }

    protected void finishActivitySync(Activity activity)
    {
        finishActivitySync(activity, getInstrumentation());
    }

    static public void finishActivitySync(Activity activity, Instrumentation instrumentation)
    {
        ActivityInstrumentationTestCase2BaseMain.finishActivitySync(activity, instrumentation, true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    static public void finishActivitySync(Activity activity, Instrumentation instrumentation, boolean doFinish)
    {
        if (doFinish)
            activity.finish();
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

}
