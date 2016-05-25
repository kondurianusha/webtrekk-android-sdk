package com.Webtrekk.SDKTest;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vartbaronov on 25.05.16.
 */
public class OldWebtrekkActivity extends Activity{

    @Override
    protected void onStart () {
        super.onStart();

        com.webtrekk.android.tracking.Webtrekk.activityStart(this);

        // Webtrekk.activityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


        // Um das Update einer Applikation zu übermitteln
        Map<String, String> parameter = new HashMap<String, String>();

        if(com.webtrekk.android.tracking.Webtrekk.isThisVersionAnUpdate()) {
            parameter.put("cs2", "1");
        }
        else {
            parameter.put("cs2", "0");
        }

        // Um die aktuelle Version der App ebenfalls zu übermitteln
        com.webtrekk.android.tracking.Webtrekk.setAppVersionParameter("cs5");
        com.webtrekk.android.tracking.Webtrekk.trackPage("app-update", parameter);
    }


    @Override
    protected void onStop() {
        com.webtrekk.android.tracking.Webtrekk.activityStop(this);

        super.onStop();
    }

}
