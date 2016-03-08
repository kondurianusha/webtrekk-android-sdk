// Webtrekk GmbH, www.webtrekk.com
// Library by Widgetlabs, www.widgetlabs.eu

package com.webtrekk.webtrekksdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.webtrekk.webtrekksdk.WebtrekkLogging;

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

    /* @hide */
    public static final String REFERRER_KEY_NAME = "WEBTREKK_REFERRER";
    /* @hide */
        public static final String KEY_REFERRER = "referrer";

    /**
     * @hide
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String campaign = intent.getStringExtra(KEY_REFERRER);
        if (!"com.android.vending.INSTALL_REFERRER".equals(intent.getAction()) || campaign == null) {
            return;
        }

        SharedPreferences preference = HelperFunctions.getWebTrekkSharedPreference(context);

        preference.edit().putString(REFERRER_KEY_NAME, campaign).apply();

        WebtrekkLogging.log("New referrer is received:"+campaign);
    }
}