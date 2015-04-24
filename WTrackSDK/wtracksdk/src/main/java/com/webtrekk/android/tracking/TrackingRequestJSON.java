package com.webtrekk.android.tracking;

/**
 * this class contains the tracking event and all the tracking params, and allows to create a json of it
 * for now nothing to see here, just offer the interface for future implementations
 */
public class TrackingRequestJSON extends TrackingRequest{
    private TrackingParams params;

    public TrackingRequestJSON(TrackingParams tp, WTrack wtrack) {
        super(wtrack);
        this.params = tp;
    }
}
