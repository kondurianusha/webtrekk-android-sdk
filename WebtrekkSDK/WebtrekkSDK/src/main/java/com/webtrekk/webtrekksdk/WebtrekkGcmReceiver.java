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
 * Created by Arsen Vartbaronov on 16.03.16.
 */

package com.webtrekk.webtrekksdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.webtrekk.webtrekksdk.Modules.WebtrekkPushNotification;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

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
