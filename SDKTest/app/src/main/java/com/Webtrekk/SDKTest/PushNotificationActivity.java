package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webtrekk.webtrekksdk.Webtrekk;
import com.webtrekk.webtrekksdk.WebtrekkPushNotification;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by vartbaronov on 16.03.16.
 */
public class PushNotificationActivity extends Activity {

    private volatile boolean mUseURLLink;
    private volatile String mToken;
    WebtrekkPushNotification mPushNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tokenReceiverUnRegister();
    }

    public void subscribe(View view)
    {
        tokenReceiverRegister();
        final Webtrekk webtrekk = Webtrekk.getInstance();
        //if (mPushNotification == null)
            //mPushNotification = webtrekk.getPushNotification();

        mPushNotification.start(/*new WebtrekkPushNotification.PushNotificationMessageCallback() {
            @Override
            public void onReceive(String message, Bitmap bitmap, String reference) {

                ((TextView) findViewById(R.id.push_reseive_message)).append(message + "\n");

                final ImageView image = new ImageView(PushNotificationActivity.this);
                image.setImageBitmap(bitmap);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ((LinearLayout) findViewById(R.id.push_images_root)).addView(image, layoutParams);
            }
        }*/null);
    }

    public void unsubscribe(View view)
    {
        final Webtrekk webtrekk = Webtrekk.getInstance();
        if (mPushNotification != null)
            mPushNotification.stop();
        mPushNotification = null;
        mToken = null;
        tokenReceiverUnRegister();
    }

    public void send(View view)
    {
        if (mToken != null)
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    sendPushMessage(mToken, BitmapFactory.decodeResource(getResources(), R.drawable.push_bitmap));
                }
            });
        else
            Log.e(getLocalClassName(), "No token is recieved");
    }

    public void switchCheckBox(View view)
    {
        mUseURLLink = ((CheckBox)view).isChecked();
    }


    private BroadcastReceiver mTokenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            mToken = intent.getStringExtra("token");
        }
    };

    /**
     * This is just for testing. To receive Campain installation data
     */
    private void tokenReceiverRegister()
    {
        LocalBroadcastManager.getInstance(this).registerReceiver(mTokenReceiver,
                new IntentFilter("com.webtrekk.webtrekksdk.WebtrekkPushNotification.Push"));
    }

    /**
     * This is just for testing. To receive Campain installation data
     */
    private void tokenReceiverUnRegister()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTokenReceiver);
    }

    private String convertBitmapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return Base64.encodeToString(buffer.toByteArray(), Base64.DEFAULT);
    }

    private void sendPushMessage(String token, Bitmap bitmap)
    {
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("message", "Test message");
            jData.put("reference", "com.Webtrekk.SDKTest.CDBActivityTest?par1=value1&par2=value2");

            if (!mUseURLLink)
            jData.put("bitmap_key_base64_data", convertBitmapToString(bitmap));
                else
            jData.put("bitmap_key_url_link", "https://3.downloader.disk.yandex.ru/disk/ff23c00eff977a32e9f9ee5ecb3239a282956aef753132e249a5499143f38aab/56ec6e41/c4lGANy7lcr-c94HmRTeU1-KDDeQToq6oCuk9CRmSjqIorGQ6g7qUD9npBrp09yVUOkEgAb4oQnbrJAzkWJ_kg%3D%3D?uid=0&filename=MyPhoto.jpg&disposition=inline&hash=&limit=0&content_type=image%2Fjpeg&fsize=32796&hid=09f767f8dfc5485c1e01b93b897b2124&media_type=image&tknv=v2&etag=2164b603eb7e7918ede9faade7b03706");

            // Where to send GCM message.
            jGcmData.put("to", token);
            // What to send in GCM message.
            jGcmData.put("data", jData);

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + "AIzaSyCuVwETB1UOVHCYmrf8MBFSLJdaz1_1kW0");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            System.out.println(resp);
            System.out.println("Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the GCM message.");
        } catch (IOException e) {
            System.out.println("Unable to send GCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
