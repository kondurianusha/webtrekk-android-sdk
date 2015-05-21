package test.myapplication;

import android.app.Application;

import com.webtrekk.android.tracking.Webtrekk;
/**
 * Created by user on 11/03/15.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Webtrekk.setContext(this);
        Webtrekk.setLoggingEnabled(true);
        Webtrekk.setOptedOut(false);
        Webtrekk.setSamplingRate(0);
        Webtrekk.setSendDelay(10000);
        Webtrekk.setServerUrl("http://atracktest.nglab.org");
        Webtrekk.setTrackId("206091227999999");

        // Webtrekk.setAppVersionParameter("cs5");
    }
}
