package com.webtrekk.android.tracking;

import android.app.Application;
import android.content.Context;

import java.util.HashMap;

/**
 * Created by user on 01/03/15.
 *
 * This is the abstract base class which handles the creation of the WTrack Singleton class, and the initiatilation
 * of the tracker, every application which wants to use tracking functions needs to extend this class
 */
public abstract class WTrackApplication extends Application {
    // a tracked application can have one ore more trackers identified bei their name
    HashMap<String, Tracker> trackers = new HashMap<String, Tracker>();
    WTrack wtrack;

    public WTrack getWTRack() {
        Context context = getApplicationContext();
        wtrack =  WTrack.getInstance(this, context);
        return wtrack;
    }

    public Tracker getTracker(String name) {

        if(trackers.containsKey(name)) {
            return trackers.get(name);
        }
        Tracker t = new Tracker(name, getWTRack());
        trackers.put(name, t);
        return t;
    }
}
