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

package com.webtrekk.webtrekksdk.Modules;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.widget.RemoteViews;

//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.iid.InstanceID;
import com.webtrekk.webtrekksdk.R;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Arsen Vartbaronov on 16.03.16.
 * Class is responsible for processing push notification messages to have it work you should
 * create instance of this class from {@link Webtrekk#getPushNotification()} method.
 * Call start to start receive push notification message and stop to stop receive push
 * notification message.
 */
public class WebtrekkPushNotification {

    private static final String SENDER_ID = "813020662430";
    private static final String BITMAP_KEY_BASE64_DATA = "bitmap_key_base64_data";
    private static final String BITMAP_KEY_URL_LINK = "bitmap_key_url_link";
    private static final String BITMAP_KEY_BITMAP = "bitmap_key_bitmap";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_REFERENCE = "reference";


    private final Context mContext;
    private final Boolean mIsTestMode;
    private boolean mIsNotificationStarted;
    public static final String TEST_MODE_KEY = "WebtrekkTestMode";
    static final String WEBTREKK_PUSH_MESSAGE_RECEIVE_MESSAGE_NOTIFICATION = "WEBTREKK_PUSH_MESSAGE_RECEIVE_MESSAGE_NOTIFICATION";
    PushNotificationMessageCallback mCallback;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra(KEY_MESSAGE);
            Bitmap bitmap = intent.getParcelableExtra(BITMAP_KEY_BITMAP);
            String reference = intent.getStringExtra(KEY_REFERENCE);

            if (mCallback != null)
                mCallback.onReceive(message, bitmap, reference);
            else
                putToAndroidNotificationTray(message, bitmap, reference);
        }
    };

    public WebtrekkPushNotification(Context context, boolean isTestMode)
    {
        mContext = context;
        mIsTestMode = isTestMode;
    }

    /**
     * Push notification interface to receive push messages
     */
    public interface PushNotificationMessageCallback {
        /**
         * Method to receive messag data. Include image and message text.
         * Method will be called in main thread.
         * @param message received message
         * @param image received image. This parameter can be null.
         * @param reference - reference to deep link. Might be null
         */
        void onReceive(String message, Bitmap image, String reference);
    }

    /**
     * Start to receive push notification message. use callback to receive message.
     * See {@link WebtrekkPushNotification.PushNotificationMessageCallback} for detalis
     * You should always call stop after that. Please don't call start twice.
     * @param callback callback to receive push message.
     * @return return true if notification has been started.
     */
    public boolean start(PushNotificationMessageCallback callback){

/*
        if (!HelperFunctions.isGooglePlayAvailable(mContext))
        {
            WebtrekkLogging.log("Can't start push notification. Google Play isn't available on device");
            return false;
        }

        if (mIsNotificationStarted) {
            WebtrekkLogging.log("Push notification is already started.");
            return true;
        }

        mCallback = callback;

        //subscribe on local message notification to receive message data
        subscribeOnMessageNotification();
        Intent intent = new Intent("com.google.android.c2dm.intent.REGISTRATION");

        if (mIsTestMode)
            intent.putExtra(TEST_MODE_KEY, true);

        //send registration request
        mContext.sendBroadcast(intent);

        mIsNotificationStarted = true;
*/

        return true;
    }


        /**
         * stop receive notification from server. Must be called after start.
         */
    public void stop()
    {
/*
        unSubscribeOnMessageNotification();
        mCallback = null;
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                deleteToken();
            }
        });
        mIsNotificationStarted = false;
*/
    }


    public void getTokkenRequest(Context context)
    {
/*
        try {
            InstanceID instanceID = InstanceID.getInstance(context);
            // You should try to get token several times
            String token = instanceID.getToken(SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            WebtrekkLogging.log("Refresh token for new:" + token);
            //We should send token to our server here
            if (mIsTestMode)
               sendTestMessage(token, context);
        } catch (IOException e) {
            WebtrekkLogging.log("Can't provide push notification. Token can't be created with exception:" + e.getMessage());
        }
*/
    }

    void deleteToken()
    {
/*
        try {
            InstanceID instanceID = InstanceID.getInstance(mContext);
            // You should try to delete token several times
            instanceID.deleteToken(SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            WebtrekkLogging.log("Delete current token");
        } catch (IOException e) {
            WebtrekkLogging.log("Can't delete token. Token can't be deleted with exception:" + e.getMessage());
        }
*/
    }


    /**
     * Send test messsage with token. It is just for test.
     * @param token
     * @param context
     */
    private void sendTestMessage(String token, Context context) {
        Intent intent = new Intent("com.webtrekk.webtrekksdk.Modules.WebtrekkPushNotification.Push");

        intent.putExtra("token", token);

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void subscribeOnMessageNotification()
    {
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                new IntentFilter(WEBTREKK_PUSH_MESSAGE_RECEIVE_MESSAGE_NOTIFICATION));
    }

    private void unSubscribeOnMessageNotification()
    {
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiver);
    }

    /**
     * Called from gcm receiver when message is arrived. Process message, upload bitmap and resend
     * as local broadcast.
     * @param intent
     */

    public void processReceivedData(final Intent intent)
    {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                String bitmapStr;
                Intent newIntent = new Intent(WEBTREKK_PUSH_MESSAGE_RECEIVE_MESSAGE_NOTIFICATION);
                // process bitmap as Base64
                if (intent.hasExtra(BITMAP_KEY_BASE64_DATA)) {
                    bitmapStr = intent.getStringExtra(BITMAP_KEY_BASE64_DATA);
                    byte[] bitmapArr = Base64.decode(bitmapStr, Base64.DEFAULT);
                    newIntent.putExtra(BITMAP_KEY_BITMAP, BitmapFactory.decodeByteArray(bitmapArr, 0, bitmapArr.length));
                    //process bitmap as link to URL
                } else if (intent.hasExtra(BITMAP_KEY_URL_LINK)) {
                    bitmapStr = intent.getStringExtra(BITMAP_KEY_URL_LINK);
                    try {
                        InputStream is = (InputStream) new URL(bitmapStr).getContent();
                        newIntent.putExtra(BITMAP_KEY_BITMAP, BitmapFactory.decodeStream(is));
                    } catch (IOException e) {
                        WebtrekkLogging.log("Can't load bitmap from path:" + bitmapStr + "Process without bitmap");
                    }
                } else
                    WebtrekkLogging.log("Key with bitmap isn't found process with text only");

                newIntent.putExtra(KEY_MESSAGE, intent.getStringExtra(KEY_MESSAGE));
                newIntent.putExtra(KEY_REFERENCE, intent.getStringExtra(KEY_REFERENCE));

                LocalBroadcastManager.getInstance(mContext).sendBroadcast(newIntent);
            }
        });
   }

    private void putToAndroidNotificationTray(String message, Bitmap bitmap, String reference)
    {

        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.custom_notification);
        contentView.setImageViewBitmap(R.id.notifiation_image, bitmap);
        contentView.setTextViewText(R.id.notification_title, mContext.getApplicationInfo().nonLocalizedLabel);
        contentView.setTextViewText(R.id.notification_text, message);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(mContext.getApplicationInfo().icon)
                        .setContent(contentView)
                        .setContentText(message);

        Intent intent = null;
        String[] arr = reference.split("[?]");
        String namespace = null;

        try {
        if (arr.length > 2) // error
            WebtrekkLogging.log("incorrect format. No intent is going to be added");
        else if (arr.length == 2) // process parameters
        {
            namespace = arr[0];
            intent = new Intent(mContext, Class.forName(namespace));
            String[] parameters = arr[1].split("&");

            for (String parameter: parameters)
            {
                String[] keyValue = parameter.split("=");
                intent.putExtra(keyValue[0], keyValue[1]);
            }
        }else if (arr.length == 1) // no parameters
        {
            intent = new Intent(mContext, Class.forName(reference));
        }
        } catch (ClassNotFoundException e) {
            WebtrekkLogging.log("Class not found for notification. Error:"+e.getMessage());
        }

        if (intent != null) {
            builder.setContentIntent(PendingIntent.getActivity(mContext, 0, intent, 0));

            NotificationManager mNotificationManager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(0, builder.build());
        }
    }

}