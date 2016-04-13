// Webtrekk GmbH, www.webtrekk.com
// Library by Widgetlabs, www.widgetlabs.eu

package com.webtrekk.webtrekksdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

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

        WebtrekkLogging.log("New action for referrer is received:"+intent.getAction());

        String campaign = intent.getStringExtra(KEY_REFERRER);

        if (!"com.android.vending.INSTALL_REFERRER".equals(intent.getAction()) || campaign == null) {
            return;
        }

        SharedPreferences preference = HelperFunctions.getWebTrekkSharedPreference(context);

        preference.edit().putString(REFERRER_KEY_NAME, campaign).apply();

        WebtrekkLogging.log("New referrer is received:"+campaign);
    }
}