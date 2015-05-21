package com.webtrekk.android.tracking;

import android.util.Log;

import com.webtrekk.android.trackingplugin.Plugin;

/**
 * Created by user on 01/03/15.
 */
public class Tracker {
    private WTrack wtrack;


    /**
     * creates a Tracker object
     * @param wtrack
     */
    public Tracker(WTrack wtrack) {
        this.wtrack = wtrack;
    }


    /**
     * track a string message, this is for testing purposes to see if tracking works
     * @param message
     */
    public void track(String message) {
        // don't track anything if the user opted out
        if(wtrack.isOptout()) return;

        L.log("logging message: " + message + " to: " + wtrack.getWebtrekk_track_domain() + " with trackdid: " + wtrack.getWebtrekk_track_id());

    }

    /**
     * this is the default tracking method which creates an empty tracking params object
     * it only tracks the auto tracked values like lib version, resolution, page name
     */
    public void track() {
        // don't track anything if the user opted out
        if(wtrack.isOptout()) return;
        track(new TrackingParams());
    }
    /**
     * each track requests consists of an eventype defined in the enum events, and the tracking params
     * from the trackingparams class,
     * for some event types automatic tracking can be enabled in the xml config,
     *
     * @param tp the TrackingParams for the event
    */
    public void track(TrackingParams tp) {
        // don't track anything if the user opted out
        if(wtrack.isOptout()) return;

        // use the automatic name in case no activity name is given
        // for calls from class methods this must be overwritten
        if(!tp.getTparams().containsKey(TrackingParams.Params.ACTIVITY_NAME)) {
            // hack for now, reflection not possible, Thread.currentThread().getStackTrace()[2].getClassName() is slower, custom security maanger to much
            // automatically adds the name of the calling activity or class to the trackingparams
            String activity_name = new Throwable().getStackTrace()[2].getClassName();
            tp.add(TrackingParams.Params.ACTIVITY_NAME, activity_name);
        }
        tp.add(wtrack.getAuto_tracked_values());
        tp.add(TrackingParams.Params.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        TrackingRequest request;
        if(wtrack.isJson_tracking()) {
            request = new TrackingRequestJSON(tp, wtrack);
        } else {
            request = new TrackingRequestUrl(tp, wtrack);
        }

        // execute the before plugin functions
        for(Plugin p: wtrack.getPlugins()){
            p.before_request(request);
        }

        // put the request on the send queue
        if(wtrack.isJson_tracking()) {
            // maybe later
        } else {
            wtrack.getRequestQueue().addUrl(((TrackingRequestUrl)request).getURLString());
        }


        // execute the after_request plugin functions
        for(Plugin p: wtrack.getPlugins()){
            p.after_request(request);
        }
    }

    public void trackReferrer() {
        String campaign = "";
        String content = "";
        String medium = "";
        String source = "";
        String term = "";

        String referrer = ReferrerReceiver.getStoredReferrer(wtrack.getContext());
        if (referrer == null || referrer.length() == 0) {
            return;
        }

        String[] components = referrer.split("&");
        for (String component : components) {
            String parameter[] = component.split("=", 2);
            if (parameter.length < 2) {
                continue;
            }

            String key = HelperFunctions.urlDecode(parameter[0]);
            String value = HelperFunctions.urlDecode(parameter[1]);

            if ("utm_campaign".equals(key)) {
                campaign = value;
            } else if ("utm_content".equals(key)) {
                content = value;
            } else if ("utm_medium".equals(key)) {
                medium = value;
            } else if ("utm_source".equals(key)) {
                source = value;
            } else if ("utm_term".equals(key)) {
                term = value;
            }
        }
        TrackingParams tp = new TrackingParams();

        String campaignId = HelperFunctions.urlEncode(source + "." + medium + "." + content + "." + campaign);
        if(!term.isEmpty())
        {
            campaignId += ";wt_kw%3D" + HelperFunctions.urlEncode(term);
        }

        tp.add(TrackingParams.Params.INSTALL_REFERRER_PARAMS_MC, campaignId);
        tp.add(TrackingParams.Params.ACTIVITY_NAME, "app-installation");

        track(tp);
    }

    public void trackUpdate() {
        if(HelperFunctions.updated(wtrack.getContext())) {
            L.log("is update");
            TrackingParams tp = new TrackingParams();
            tp.add(TrackingParams.Params.ACTIVITY_NAME, "update");
            //tp.add(TrackingParams.Params.APP_UPDATE, "1");
            track(tp);

        }


    }


}
