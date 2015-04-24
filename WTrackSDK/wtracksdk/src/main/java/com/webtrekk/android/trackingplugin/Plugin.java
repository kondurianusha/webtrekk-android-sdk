package com.webtrekk.android.trackingplugin;

import java.util.HashMap;

import com.webtrekk.android.tracking.TrackingParams;
import com.webtrekk.android.tracking.TrackingRequest;
import com.webtrekk.android.tracking.WTrack;

/**
 * Created by user on 08/04/15.
 */

// abstract base class for all plugins, maybe switch to interface later when more information are avail
public abstract class Plugin {
    WTrack wtrack;

    public Plugin(WTrack wtrack) {
        this.wtrack = wtrack;
    }

    // this function is called by the tracker for all requests before the request is made
    public abstract void before_request(TrackingRequest request);

    // this function is called by the tracker for all requests and all plugins after the requst is made
    public abstract void after_request(TrackingRequest request);


}