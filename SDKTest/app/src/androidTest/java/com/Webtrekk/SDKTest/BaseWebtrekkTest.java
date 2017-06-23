package com.Webtrekk.SDKTest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;

import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import junit.framework.Assert;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by vartbaronov on 22.06.17.
 */

public class BaseWebtrekkTest extends Assert implements CustomTestRule.TestAdapter {
    protected Application mApplication;
    private SDKInstanceManager mSDKManager = new SDKInstanceManager();
    protected boolean mIsErrorHandlerTest;
    protected boolean mIsExternalCall;
    static private String IS_EXTERNAL = "external";
    protected boolean mIsCDBTestRequest;
    public static String mTestName;

    public void before() throws Exception {

        //super.setUp();
        //refresh webtrekk instance
        mSDKManager.setup();
        mApplication = (Application)InstrumentationRegistry.getTargetContext().getApplicationContext();
        if (!mIsErrorHandlerTest)
            deleteErrorHandlerFile(mApplication);
        if (!mIsCDBTestRequest)
            deleteCDBRepeatRequestInfo();

        Bundle arguments = null;
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            arguments = InstrumentationRegistry.getArguments();
        }else {
            arguments = (Bundle)getFieldValue(instrumentation, "mArguments");
        }
        if (arguments.size() > 0)
            WebtrekkLogging.log("Receive arguments for test:"+arguments);
        mIsExternalCall = arguments.getString(IS_EXTERNAL) != null;
    }

    public void after() throws Exception {
        mSDKManager.release(mApplication);
    }

    protected void deleteErrorHandlerFile(Context context)
    {
        File loadFile = new File(context.getFilesDir().getPath() + File.separator+"exception.txt");
        loadFile.delete();
    }

    static public void finishActivitySync(Activity activity, boolean doFinish)
    {
        finishActivitySync(activity, InstrumentationRegistry.getInstrumentation(), doFinish);
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
        boolean isDestroyed = false;

        while (!isDestroyed && !finishTimeout) {
            instrumentation.waitForIdleSync();
            finishTimeout = (System.currentTimeMillis() - currentTime) > 140000;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                isDestroyed = activity.isDestroyed();
            }else {
                isDestroyed = (Boolean)callMethod(null, activity.getWindow(), "isDestroyed", null);
            }
        }

        if (finishTimeout) {
            WebtrekkLogging.log("finishActivitySync: finished by timeout. Hash:" + activityHash);
        }
    }

    private void deleteCDBRepeatRequestInfo()
    {
        SharedPreferences preferences = HelperFunctions.getWebTrekkSharedPreference(mApplication);

        if (preferences.contains("LAST_CBD_REQUEST_DATE"))
            preferences.edit().remove("LAST_CBD_REQUEST_DATE").apply();
    }

    static private Object getFieldValue(Object instance, String valueName) {
        Field field = null;
        try {
            field = instance.getClass().getDeclaredField(valueName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    static private <T extends Object> T callMethod(String className, Object classInstance, String methodName, Class[] argumentsTypes, Object... argumentsValues){
        try {
            Class classObj = classInstance == null ? Class.forName(className) : classInstance.getClass();

            Method method = classObj.getMethod(methodName, argumentsTypes);

            return (T) method.invoke(classInstance, argumentsValues);
        }catch (InvocationTargetException e) {
            return null;
        } catch (Exception e){
            return null;
        }
    }

    protected boolean isRestrictedMode(){
        Context context = mApplication;

        return context.getResources().getBoolean(R.bool.is_restricted_mode);
    }


    protected void cleanConfigPreference()
    {
        SharedPreferences sharedPrefs = HelperFunctions.getWebTrekkSharedPreference(mApplication);
        sharedPrefs.edit().remove(Webtrekk.PREFERENCE_KEY_CONFIGURATION).apply();
    }

}
