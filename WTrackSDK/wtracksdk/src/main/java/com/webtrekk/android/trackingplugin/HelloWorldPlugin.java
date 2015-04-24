package com.webtrekk.android.trackingplugin;

import android.util.Log;

import java.util.HashMap;

import com.webtrekk.android.tracking.TrackingParams;
import com.webtrekk.android.tracking.TrackingRequest;
import com.webtrekk.android.tracking.WTrack;

/**
 * Created by user on 14/04/15.
 */
public class HelloWorldPlugin extends Plugin {

    public HelloWorldPlugin(WTrack wtrack) {
        super(wtrack);
    }


    @Override
    public void before_request(TrackingRequest request) {
        Log.d(WTrack.LOGTAG, "plugin hello world: before_request");
    }

    @Override
    public void after_request(TrackingRequest request) {
        Log.d(WTrack.LOGTAG, "plugin hello world: after_request");
    }

}
