package com.webtrekk.webtrekksdk;



/**
 * Created by user on 08/04/15.
 */

// abstract base class for all plugins, maybe switch to interface later when more information are avail
abstract class Plugin {
    Webtrekk webtrekk;

    public Plugin(Webtrekk webtrekk) {
        this.webtrekk = webtrekk;
    }

    // this function is called by the tracker for all requests before the request is made
    public abstract void before_request(TrackingRequest request);

    // this function is called by the tracker for all requests and all plugins after the requst is made
    public abstract void after_request(TrackingRequest request);


}