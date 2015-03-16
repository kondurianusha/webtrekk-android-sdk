package wtrack.tracking;

import android.util.Log;

import com.android.volley.Request;

/**
 * Created by user on 01/03/15.
 */
public class Tracker {
    protected final String name;
    private WTrack wtrack;

    public Tracker(String name, WTrack wtrack) {
        this.name = name;
        this.wtrack = wtrack;
    }

    public void track(String message) {
        // don't track anything if the user opted out
        if(wtrack.isOptout()) return;

        Log.i(WTrack.LOGTAG, "logging message: " + message + " to: " + wtrack.getWebtrekk_track_domain() + " with trackdid: " + wtrack.getWebtrekk_track_id());

    }
    /**
     * each track requests consists of an eventype defined in the enum events, and the tracking params
     * from the trackingparams class,
     * for some event types automatic tracking can be enabled in the xml config,
     *
     * @param e the event type which to track
     * @param tp the TrackingParams for the event
    */
    public void track(Events e, TrackingParams tp) {
        // hack for now, reflection not possible, Thread.currentThread().getStackTrace()[2].getClassName() is slower, custom security maanger to much
        // automatically adds the name of the calling activity or class to the trackingparams
        String activity_name = new Throwable().getStackTrace()[2].getClassName();
        tp.add(TrackingParams.Params.ACTIVITY_NAME, activity_name);
        TrackingRequest request;
        if(wtrack.isJson_tracking()) {
            request = new TrackingRequestJSON(e, tp, wtrack);
        } else {
            request = new TrackingRequestUrl(e, tp, wtrack);
        }
        Log.d(WTrack.LOGTAG, "track_url: " + request.getURLString());
        Request r = request.getRequest();
        wtrack.getRequestQueue().add(r);

    }

    public void track(Events e) {
        track(e, new TrackingParams());
    }

    public enum Events {
        // the app was installed
        APP_INSTALLED("app_installed"),
        // the app was started
        APP_STARTET("app_started"),
        // the app was updated
        APP_UPDATED("app_updated"),
        // the app is run for the first time
        APP_FIRST("app_first"),
        // activity is started
        ACTIVITY("activity"),
        // user interaction e.g. with buttons, forms, urls, items
        ACTION("action"),
        // an exception happened
        EXCEPTION("exception"),
        // user impression, this is when a user has seen some ad/media and can be used to measure conversions
        IMPRESSION("impression"),
        // conversion, can be fired when the user orders something or enters his data in some form, so whenever a conversion goal is reached
        CONVERSION("conversion");

        private final String value;

        Events(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

}
