package com.Webtrekk.SDKTest;

import android.app.Application;

import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

/**
 * Created by vartbaronov on 25.05.16.
 */
public class OldWebtrekkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WebtrekkLogging.log("Old webtrekk init");
        com.webtrekk.android.tracking.Webtrekk.setContext(this);
        com.webtrekk.android.tracking.Webtrekk.setLoggingEnabled(true);
        com.webtrekk.android.tracking.Webtrekk.setOptedOut(false);
        com.webtrekk.android.tracking.Webtrekk.setSamplingRate(0);
        com.webtrekk.android.tracking.Webtrekk.setSendDelay(10000);
        com.webtrekk.android.tracking.Webtrekk.setServerUrl("https://widgetlabsgmbh01.wt-eu02.net");
        com.webtrekk.android.tracking.Webtrekk.setTrackId("164398353394712");
    }
}
