/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Webtrekk GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Created by Arsen Vartbaronov on 17.05.16.
 */

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
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;

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

        Bundle arguments = null;
        Instrumentation instrumentation = (Instrumentation)getInstrumentation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            arguments = InstrumentationRegistry.getArguments();
        }else {
            arguments = (Bundle)getFieldValue(instrumentation, "mArguments");
        }
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
        SharedPreferences preferences = HelperFunctions.getWebTrekkSharedPreference(getInstrumentation().getTargetContext());

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
        Context context = getInstrumentation().getTargetContext();

        return context.getResources().getBoolean(R.bool.is_restricted_mode);
    }
}
