package com.webtrekk.android.tracking;

import android.net.Uri;
import android.util.Log;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * this class contains the tracking event and the trackingparams, and handles the creation of an url string
 * which is send as get request to the configured track domain of the customer
 */
public class TrackingRequestUrl extends TrackingRequest{

    protected String urlstring;

    public TrackingRequestUrl(TrackingParams tp, WTrack wtrack) {
        super(wtrack);
        params = tp;
        createUrlString();
    }

    public void createUrlString() {
        String url = "";
        // maybe add https here if that ssl option is set in the configs
        url += "http://" + webtrekk_track_domain + "/" + webtrekk_track_id + "/wt?p=" + WTrack.TRACKING_LIBRARY_VERSION + ",";
        url += HelperFunctions.urlEncode(params.getTparams().get(TrackingParams.Params.ACTIVITY_NAME)) + ",0,";
        url += params.getTparams().get(TrackingParams.Params.SCREEN_RESOLUTION) + ",";
        url += params.getTparams().get(TrackingParams.Params.SCREEN_DEPTH) + ",0,";
        url += params.getTparams().get(TrackingParams.Params.TIMESTAMP) + ",0,0,0";

        // always add mts param for server side request ordering
        url += "&mts=" + params.getTparams().get(TrackingParams.Params.TIMESTAMP);

        params.getTparams().remove(TrackingParams.Params.TRACKING_LIB_VERSION);
        params.getTparams().remove(TrackingParams.Params.ACTIVITY_NAME);
        params.getTparams().remove(TrackingParams.Params.SCREEN_RESOLUTION);
        params.getTparams().remove(TrackingParams.Params.SCREEN_DEPTH);
        params.getTparams().remove(TrackingParams.Params.TIMESTAMP);


        // iterate through all the collected tracking params and append them as url parameter

        for (Map.Entry<TrackingParams.Params, String> entry : params.getTparams().entrySet()) {
            url += "&" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
        }
        //if action params are given, append them to the url as well
        if (!params.getAction_params().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getAction_params().entrySet()) {
                url += "&ck" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if ad params are given, append them to the url as well
        if (!params.getAd_params().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getAd_params().entrySet()) {
                url += "&cc" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if ecom params are given, append them to the url as well
        if (!params.getEcom_params().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getEcom_params().entrySet()) {
                url += "&cb" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if page category params are given, append them to the url as well
        if (!params.getPage_categories().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getPage_categories().entrySet()) {
                url += "&cg" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if product category params are given, append them to the url as well
        if (!params.getProduct_categories().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getProduct_categories().entrySet()) {
                url += "&ca" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if session params are given, append them to the url as well
        if (!params.getSession_params().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getSession_params().entrySet()) {
                url += "&cs" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if user category params are given, append them to the url as well
        if (!params.getUser_categories().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getUser_categories().entrySet()) {
                url += "&uc" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if action params are given, append them to the url as well
        if (!params.getPage_params().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getPage_params().entrySet()) {
                url += "&cp" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        // append eor to make shure the request is valid
        url += "&eor=1";
        this.urlstring = url;
    }

/*    @Override
    public TrackingRequestUrl getRequest() {
        createUrlString();
        return new StringRequest(Request.Method.GET, urlstring,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // it worked, but nothing to do here for now
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // simply log error for now, maybe add retry handling later or writing to disk
                Log.e(WTrack.LOGTAG, "Error: " + error.toString() +" msg: " + error.getMessage());
            }
        });
    }*/



    public String getURLString() {
        return urlstring;
    }
}
