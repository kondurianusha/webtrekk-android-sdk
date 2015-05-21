package com.webtrekk.android.tracking;


import java.util.Map;

/**
 * this class contains the tracking event and the trackingparams, and handles the creation of an url string
 * which is send as get request to the configured track domain of the customer
 */
public class TrackingRequestUrl extends TrackingRequest{

    protected String urlString;

    public TrackingRequestUrl(TrackingParams tp, WTrack wtrack) {
        super(wtrack);
        params = tp;
        createUrlString();
    }

    public void createUrlString() {
        String url = "";
        // maybe add https here if that ssl option is set in the configs
        url += webtrekk_track_domain + "/" + webtrekk_track_id + "/wt?p=" + WTrack.TRACKING_LIBRARY_VERSION + ",";
        url += HelperFunctions.urlEncode(params.getTparams().get(TrackingParams.Params.ACTIVITY_NAME)) + ",0,";
        url += params.getTparams().get(TrackingParams.Params.SCREEN_RESOLUTION) + ",";
        url += params.getTparams().get(TrackingParams.Params.SCREEN_DEPTH) + ",0,";
        url += params.getTparams().get(TrackingParams.Params.TIMESTAMP) + ",0,0,0";

        // always add mts param for server side request ordering
        url += "&mts=" + params.getTparams().get(TrackingParams.Params.TIMESTAMP);
        //TODO: hier ne bessere lösung finden, mit ner copy arbeiten oder so
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
        if (!params.getActionParams().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getActionParams().entrySet()) {
                url += "&ck" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if ad params are given, append them to the url as well
        if (!params.getAdParams().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getAdParams().entrySet()) {
                url += "&cc" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if ecom params are given, append them to the url as well
        if (!params.getEcomParams().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getEcomParams().entrySet()) {
                url += "&cb" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if page category params are given, append them to the url as well
        if (!params.getPageCategories().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getPageCategories().entrySet()) {
                url += "&cg" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if product category params are given, append them to the url as well
        if (!params.getProductCategories().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getProductCategories().entrySet()) {
                url += "&ca" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if session params are given, append them to the url as well
        if (!params.getSessionParams().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getSessionParams().entrySet()) {
                url += "&cs" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if user category params are given, append them to the url as well
        if (!params.getUserCategories().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getUserCategories().entrySet()) {
                url += "&uc" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if action params are given, append them to the url as well
        if (!params.getPageParams().isEmpty()) {
            for (Map.Entry<String, String> entry : params.getPageParams().entrySet()) {
                url += "&cp" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        // append eor to make shure the request is valid
        url += "&eor=1";
        this.urlString = url;
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }
}
