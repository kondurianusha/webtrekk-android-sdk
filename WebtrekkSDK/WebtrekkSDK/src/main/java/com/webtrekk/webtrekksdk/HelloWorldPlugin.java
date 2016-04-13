package com.webtrekk.webtrekksdk;


import com.webtrekk.webtrekksdk.Request.TrackingRequest;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

/**
 * example plugin class to demonstrate how to implement plugins
 */
public class HelloWorldPlugin extends Plugin {

    public HelloWorldPlugin(Webtrekk webtrekk) {
        super(webtrekk);
    }


    @Override
    public void before_request(TrackingRequest request) {
        WebtrekkLogging.log("plugin hello world: before_request");
    }

    @Override
    public void after_request(TrackingRequest request) {
        WebtrekkLogging.log("plugin hello world: after_request");
    }

}
