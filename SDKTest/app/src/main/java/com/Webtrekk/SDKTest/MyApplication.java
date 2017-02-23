package com.Webtrekk.SDKTest;

import com.webtrekk.webtrekksdk.WebtrekkApplication;

/**
 * Created by user on 11/03/15.
 */

public class MyApplication extends WebtrekkApplication {
    private static  SDKInstanceManager mSDKManager = new SDKInstanceManager();

    public SDKInstanceManager getSDKManager(){
        return  mSDKManager;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mSDKManager.setup();
    }
}
