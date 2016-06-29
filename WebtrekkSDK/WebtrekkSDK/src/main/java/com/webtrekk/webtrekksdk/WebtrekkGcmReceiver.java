package com.webtrekk.webtrekksdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.webtrekk.webtrekksdk.Modules.WebtrekkPushNotification;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

/**
 * Created by vartbaronov on 16.03.16.
 */
public class WebtrekkGcmReceiver extends BroadcastReceiver

{
    @Override
    public void onReceive (Context context, Intent intent)
    {
        WebtrekkLogging.log("Action for GCM is received:" + intent.getAction());

        String from = intent.getStringExtra("from");

        if("com.google.android.c2dm.intent.REGISTRATION".equals(intent.getAction()) ||
           "google.com/iid".equals(from) ||
           "gcm.googleapis.com/refresh".equals(from)){
            if (!intent.hasExtra("registration_id"))
               processRegistration(intent, context);
        }
        else if ("com.google.android.c2dm.intent.RECEIVE".equals(intent.getAction())) {
            new WebtrekkPushNotification(context, false).processReceivedData(intent);
        }

    }

    private void processRegistration(final Intent intent, final Context context) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                new WebtrekkPushNotification(context,
                                            intent.getBooleanExtra(WebtrekkPushNotification.TEST_MODE_KEY, false)).
                             getTokkenRequest(context);
            }
        });
    }
}
