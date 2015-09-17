package com.webtrekk.webbtrekksdk;


import java.util.Map;
import java.util.TreeMap;

import com.webtrekk.webbtrekksdk.TrackingParameter.Parameter;


/**
 * this class contains the tracking event and the trackingparams, and handles the creation of an url string
 * which is send as get request to the configured track domain of the customer
 */
public class TrackingRequest {

    protected TrackingParameter trackingParameter;
    private TrackingConfiguration trackingConfiguration;

    /**
     * creates a new tracking request object
     *
     * @param tp the TrackingParameter for the TrackingRequest
     * @param trackingConfiguration the tracking configuration of the webtrekk object
     */
    public TrackingRequest(TrackingParameter tp, TrackingConfiguration trackingConfiguration) {
        trackingParameter = tp;
        this.trackingConfiguration = trackingConfiguration;
    }

    /**
     * creates a URL String from the given Request which can be send to the server/stores in the urlStore
     *
     * @return returns the URL as String with all the TrackingParameter url encoded
     */
    public String getUrlString() {
        String url = "";
        TreeMap<Parameter, String> tp = trackingParameter.getTparams();
        // maybe add https here if that ssl option is set in the configs
        url += trackingConfiguration.getTrackDomain() + "/" + trackingConfiguration.getTrackId() + "/wt?p=" + Webtrekk.TRACKING_LIBRARY_VERSION + ",";
        url += HelperFunctions.urlEncode(tp.get(Parameter.ACTIVITY_NAME)) + ",0,";
        url += tp.get(Parameter.SCREEN_RESOLUTION) + ",";
        url += tp.get(Parameter.SCREEN_DEPTH) + ",0,";
        url += tp.get(Parameter.TIMESTAMP) + ",0,0,0";

        // always add mts param for server side request ordering
        url += "&mts=" + trackingParameter.getTparams().get(Parameter.TIMESTAMP);

        if(trackingParameter.containsKey(Parameter.DEVICE)) {
            url += "&" + Parameter.DEVICE.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.DEVICE));
        }
        if(trackingParameter.containsKey(Parameter.SAMPLING)) {
            url += "&" + Parameter.SAMPLING.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.SAMPLING));
        }
        if(trackingParameter.containsKey(Parameter.CURRENT_TIME)) {
            url += "&" + Parameter.CURRENT_TIME.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.CURRENT_TIME));
        }
        if(trackingParameter.containsKey(Parameter.IP_ADDRESS)) {
            url += "&" + Parameter.IP_ADDRESS.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.IP_ADDRESS));
        }
        if(trackingParameter.containsKey(Parameter.USERAGENT)) {
            url += "&" + Parameter.USERAGENT.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.USERAGENT));
        }
        if(trackingParameter.containsKey(Parameter.TIMEZONE)) {
            url += "&" + Parameter.TIMEZONE.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.TIMEZONE));
        }
        if(trackingParameter.containsKey(Parameter.DEV_LANG)) {
            url += "&" + Parameter.DEV_LANG.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.DEV_LANG));
        }
        if(trackingParameter.containsKey(Parameter.EVERID)) {
            url += "&" + Parameter.EVERID.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.EVERID));
        }
        if(trackingParameter.containsKey(Parameter.APP_FIRST_START)) {
            url += "&" + Parameter.APP_FIRST_START.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.APP_FIRST_START));
        }
        if(trackingParameter.containsKey(Parameter.ACTION_NAME)) {
            url += "&" + Parameter.ACTION_NAME.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ACTION_NAME));
        }
        if(trackingParameter.containsKey(Parameter.VOUCHER_VALUE)) {
            url += "&" + Parameter.VOUCHER_VALUE.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.VOUCHER_VALUE));
        }
        if(trackingParameter.containsKey(Parameter.ORDER_TOTAL)) {
            url += "&" + Parameter.ORDER_TOTAL.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ORDER_TOTAL));
        }
        if(trackingParameter.containsKey(Parameter.ORDER_NUMBER)) {
            url += "&" + Parameter.ORDER_NUMBER.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ORDER_NUMBER));
        }
        if(trackingParameter.containsKey(Parameter.PRODUCT)) {
            url += "&" + Parameter.PRODUCT.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.PRODUCT));
        }
        if(trackingParameter.containsKey(Parameter.PRODUCT_COST)) {
            url += "&" + Parameter.PRODUCT_COST.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.PRODUCT_COST));
        }
        if(trackingParameter.containsKey(Parameter.CURRENCY)) {
            url += "&" + Parameter.CURRENCY.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.CURRENCY));
        }
        if(trackingParameter.containsKey(Parameter.PRODUCT_COUNT)) {
            url += "&" + Parameter.PRODUCT_COUNT.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.PRODUCT_COUNT));
        }
        if(trackingParameter.containsKey(Parameter.PRODUCT_STATUS)) {
            url += "&" + Parameter.PRODUCT_STATUS.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.PRODUCT_STATUS));
        }
        if(trackingParameter.containsKey(Parameter.CUSTOMER_ID)) {
            url += "&" + Parameter.CUSTOMER_ID.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.CUSTOMER_ID));
        }
        if(trackingParameter.containsKey(Parameter.EMAIL)) {
            url += "&" + Parameter.EMAIL.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.EMAIL));
        }
        if(trackingParameter.containsKey(Parameter.EMAIL_RID)) {
            url += "&" + Parameter.EMAIL_RID.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.EMAIL_RID));
        }
        if(trackingParameter.containsKey(Parameter.NEWSLETTER)) {
            url += "&" + Parameter.NEWSLETTER.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.NEWSLETTER));
        }
        if(trackingParameter.containsKey(Parameter.GNAME)) {
            url += "&" + Parameter.GNAME.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.GNAME));
        }
        if(trackingParameter.containsKey(Parameter.SNAME)) {
            url += "&" + Parameter.SNAME.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.SNAME));
        }
        if(trackingParameter.containsKey(Parameter.PHONE)) {
            url += "&" + Parameter.PHONE.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.PHONE));
        }
        if(trackingParameter.containsKey(Parameter.GENDER)) {
            url += "&" + Parameter.GENDER.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.GENDER));
        }
        if(trackingParameter.containsKey(Parameter.BIRTHDAY)) {
            url += "&" + Parameter.BIRTHDAY.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.BIRTHDAY));
        }
        if(trackingParameter.containsKey(Parameter.CITY)) {
            url += "&" + Parameter.CITY.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.CITY));
        }
        if(trackingParameter.containsKey(Parameter.COUNTRY)) {
            url += "&" + Parameter.COUNTRY.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.COUNTRY));
        }
        if(trackingParameter.containsKey(Parameter.ZIP)) {
            url += "&" + Parameter.ZIP.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ZIP));
        }
        if(trackingParameter.containsKey(Parameter.STREET)) {
            url += "&" + Parameter.STREET.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.STREET));
        }
        if(trackingParameter.containsKey(Parameter.STREETNUMBER)) {
            url += "&" + Parameter.STREETNUMBER.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.STREETNUMBER));
        }
        if(trackingParameter.containsKey(Parameter.INTERN_SEARCH)) {
            url += "&" + Parameter.INTERN_SEARCH.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.INTERN_SEARCH));
        }
        if(trackingParameter.containsKey(Parameter.ADVERTISEMENT)) {
            url += "&" + Parameter.ADVERTISEMENT.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ADVERTISEMENT));
        }
        if(trackingParameter.containsKey(Parameter.ADVERTISEMENT_ACTION)) {
            url += "&" + Parameter.ADVERTISEMENT_ACTION.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ADVERTISEMENT_ACTION));
        }
        if(trackingParameter.containsKey(Parameter.ADVERTISER_ID)) {
            url += "&" + Parameter.ADVERTISER_ID.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ADVERTISER_ID));
        }

        //if action trackingParameter are given, append them to the url as well
        if (!trackingParameter.getActionParams().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getActionParams().entrySet()) {
                url += "&ck" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if ad trackingParameter are given, append them to the url as well
        if (!trackingParameter.getAdParams().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getAdParams().entrySet()) {
                url += "&cc" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if ecom trackingParameter are given, append them to the url as well
        if (!trackingParameter.getEcomParams().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getEcomParams().entrySet()) {
                url += "&cb" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if page category trackingParameter are given, append them to the url as well
        if (!trackingParameter.getPageCategories().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getPageCategories().entrySet()) {
                url += "&cg" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if product category trackingParameter are given, append them to the url as well
        if (!trackingParameter.getProductCategories().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getProductCategories().entrySet()) {
                url += "&ca" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if session trackingParameter are given, append them to the url as well
        if (!trackingParameter.getSessionParams().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getSessionParams().entrySet()) {
                url += "&cs" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if user category trackingParameter are given, append them to the url as well
        if (!trackingParameter.getUserCategories().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getUserCategories().entrySet()) {
                url += "&uc" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        //if action trackingParameter are given, append them to the url as well
        if (!trackingParameter.getPageParams().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getPageParams().entrySet()) {
                url += "&cp" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue());
            }
        }
        // append eor to make shure the request is valid
        url += "&eor=1";
        return url;
    }

}
