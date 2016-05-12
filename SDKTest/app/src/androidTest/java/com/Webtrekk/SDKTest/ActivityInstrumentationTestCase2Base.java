package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.test.ActivityInstrumentationTestCase2;

import com.webtrekk.webtrekksdk.Modules.ExceptionHandler;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by vartbaronov on 22.04.16.
 */
public abstract class ActivityInstrumentationTestCase2Base<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    private volatile String mSendedURL;
    private volatile boolean mStringReceived;
    private final Object mSynchronize = new Object();

    public ActivityInstrumentationTestCase2Base(Class<T> activityClass) {
        super(activityClass);
    }

    private BroadcastReceiver mURLReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSendedURL = intent.getStringExtra("URL");
            mStringReceived = true;

            synchronized (mSynchronize)
            {
                mSynchronize.notifyAll();
            }
        }
    };

    private void URLReceiverRegister()
    {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mURLReceiver,
                new IntentFilter("com.webtrekk.webtrekksdk.TEST_URL"));
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //refresh webtrekk instance
        refreshWTInstance();
        URLReceiverRegister();
    }

    @Override
    public void tearDown() throws Exception {
        URLReceiverUnRegister();
        super.tearDown();
    }

    private void URLReceiverUnRegister()
    {
        LocalBroadcastManager.getInstance(getInstrumentation().getTargetContext()).unregisterReceiver(mURLReceiver);
    }

    protected void initWaitingForTrack(Runnable process)
    {
        mSendedURL = null;
        mStringReceived = false;

        synchronized (Webtrekk.getInstance()) {

            new Thread(process).start();
        }
    }

    protected String waitForTrackedURL()
    {
        synchronized (mSynchronize) {
            while (!mStringReceived) {
                try {
                    mSynchronize.wait(5000);
                } catch (InterruptedException e) {
                    assertTrue(false);
                }
                assertTrue(mStringReceived);
            }
        }
        return mSendedURL;
    }

    public static void refreshWTInstance() {
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

    protected void callStartActivity(String actName, Webtrekk wtInstance)
    {
        try {
            Method methodStartActivity = wtInstance.getClass().getDeclaredMethod("startActivity", String.class, boolean.class);
            Field actCount = wtInstance.getClass().getDeclaredField("mActivityCount");

            methodStartActivity.setAccessible(true);
            actCount.setAccessible(true);
            actCount.set(wtInstance, 1);
            methodStartActivity.invoke(wtInstance, actName, false);
        } catch (NoSuchMethodException e) {
            WebtrekkLogging.log("Can't invoke startActivity method");
        } catch (InvocationTargetException e) {
            WebtrekkLogging.log("Can't invoke startActivity method");
        } catch (IllegalAccessException e) {
            WebtrekkLogging.log("Can't invoke startActivity method");
        } catch (NoSuchFieldException e) {
            WebtrekkLogging.log("Can't invoke startActivity method");
        }

    }
}
