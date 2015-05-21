package com.webtrekk.android.tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLDecoder;


public class ReferrerReceiver extends BroadcastReceiver {

    private static final String TAG = BroadcastReceiver.class.getName();

    public static final String REFERRER_FILE_NAME = "webtrekk-referrer-store";
    public static final String KEY_REFERRER = "referrer";



    public static String getStoredReferrer(Context context) {
        String result = null;

        Writer writer = new StringWriter();
        Reader reader = null;
        char[] buffer = new char[1024];

        try {
            reader = new BufferedReader(new InputStreamReader(context.openFileInput(ReferrerReceiver.REFERRER_FILE_NAME), "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            result = writer.toString();

            if (!context.deleteFile(ReferrerReceiver.REFERRER_FILE_NAME)) {
                Log.d(TAG, "could not delete referrer file");
            }
        }
        catch (FileNotFoundException e) {
            // ignore
        }
        catch (IOException e) {
            Log.d(TAG, "Cannot load referrer file.", e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    // ignore
                }
            }
        }

        return result;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String campaign = intent.getStringExtra("referrer");
        if (!"com.android.vending.INSTALL_REFERRER".equals(intent.getAction()) || campaign == null) {
            return;
        }

        try {
            OutputStream output = context.openFileOutput(REFERRER_FILE_NAME, Context.MODE_PRIVATE);
            output.write(URLDecoder.decode(campaign, "UTF-8").getBytes());
            output.close();
        }
        catch (Exception e) {
            Log.e(TAG, "Cannot store referrer.", e);
        }
    }
}