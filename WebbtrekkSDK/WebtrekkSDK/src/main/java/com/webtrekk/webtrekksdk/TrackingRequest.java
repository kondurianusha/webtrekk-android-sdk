package com.webtrekk.webtrekksdk;


import java.util.Map;
import java.util.SortedMap;

import com.webtrekk.webtrekksdk.TrackingParameter.Parameter;


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
        StringBuffer url = new StringBuffer();
        SortedMap<Parameter, String> tp = trackingParameter.getDefaultParameter();
        // maybe add https here if that ssl option is set in the configs
        url.append(trackingConfiguration.getTrackDomain() + "/" + trackingConfiguration.getTrackId() + "/wt?p=" + Webtrekk.TRACKING_LIBRARY_VERSION + ",");
        url.append(HelperFunctions.urlEncode(tp.get(Parameter.ACTIVITY_NAME)) + ",0,");
        url.append(tp.get(Parameter.SCREEN_RESOLUTION) + ",");
        url.append(tp.get(Parameter.SCREEN_DEPTH) + ",0,");
        url.append(tp.get(Parameter.TIMESTAMP) + ",0,0,0");

        if(trackingParameter.containsKey(Parameter.EVERID)) {
            url.append("&" + Parameter.EVERID.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.EVERID)));
        }

        if(trackingParameter.containsKey(Parameter.ADVERTISER_ID)) {
            url.append("&" + Parameter.ADVERTISER_ID.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ADVERTISER_ID)));
        }

        if(trackingParameter.containsKey(Parameter.FORCE_NEW_SESSION)) {
            url.append(("&" + Parameter.FORCE_NEW_SESSION.toString() + "=" + tp.get(Parameter.FORCE_NEW_SESSION)));
        }

        if(trackingParameter.containsKey(Parameter.APP_FIRST_START)) {
            url.append("&" + Parameter.APP_FIRST_START.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.APP_FIRST_START)));
        }

        if(trackingParameter.containsKey(Parameter.CURRENT_TIME)) {
            url.append( "&" + Parameter.CURRENT_TIME.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.CURRENT_TIME)));
        }

        if(trackingParameter.containsKey(Parameter.TIMEZONE)) {
            url.append("&" + Parameter.TIMEZONE.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.TIMEZONE)));
        }

        if(trackingParameter.containsKey(Parameter.DEV_LANG)) {
            url.append("&" + Parameter.DEV_LANG.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.DEV_LANG)));
        }

        if(trackingParameter.containsKey(Parameter.CUSTOMER_ID)) {
            url.append("&" + Parameter.CUSTOMER_ID.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.CUSTOMER_ID)));
        }

        if(trackingParameter.containsKey(Parameter.ACTION_NAME)) {
            url.append("&" + Parameter.ACTION_NAME.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ACTION_NAME)));
        }

        if(trackingParameter.containsKey(Parameter.ORDER_TOTAL)) {
            url.append("&" + Parameter.ORDER_TOTAL.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ORDER_TOTAL)));
        }
        if(trackingParameter.containsKey(Parameter.ORDER_NUMBER)) {
            url.append("&" + Parameter.ORDER_NUMBER.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ORDER_NUMBER)));
        }
        if(trackingParameter.containsKey(Parameter.PRODUCT)) {
            url.append("&" + Parameter.PRODUCT.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.PRODUCT)));
        }
        if(trackingParameter.containsKey(Parameter.PRODUCT_COST)) {
            url.append("&" + Parameter.PRODUCT_COST.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.PRODUCT_COST)));
        }

        if(trackingParameter.containsKey(Parameter.CURRENCY)) {
            url.append("&" + Parameter.CURRENCY.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.CURRENCY)));
        }

        if(trackingParameter.containsKey(Parameter.PRODUCT_COUNT)) {
            url.append("&" + Parameter.PRODUCT_COUNT.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.PRODUCT_COUNT)));
        }
        if(trackingParameter.containsKey(Parameter.PRODUCT_STATUS)) {
            url.append("&" + Parameter.PRODUCT_STATUS.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.PRODUCT_STATUS)));
        }
        if(trackingParameter.containsKey(Parameter.VOUCHER_VALUE)) {
            url.append("&" + Parameter.VOUCHER_VALUE.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.VOUCHER_VALUE)));
        }
        if(trackingParameter.containsKey(Parameter.ADVERTISEMENT)) {
            url.append("&" + Parameter.ADVERTISEMENT.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ADVERTISEMENT)));
        }
        if(trackingParameter.containsKey(Parameter.ADVERTISEMENT_ACTION)) {
            url.append("&" + Parameter.ADVERTISEMENT_ACTION.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ADVERTISEMENT_ACTION)));
        }
        if(trackingParameter.containsKey(Parameter.INTERN_SEARCH)) {
            url.append("&" + Parameter.INTERN_SEARCH.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.INTERN_SEARCH)));
        }

////////////////////////////////////

        if(trackingParameter.containsKey(Parameter.EMAIL)) {
            url.append("&" + Parameter.EMAIL.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.EMAIL)));
        }
        if(trackingParameter.containsKey(Parameter.EMAIL_RID)) {
            url.append("&" + Parameter.EMAIL_RID.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.EMAIL_RID)));
        }
        if(trackingParameter.containsKey(Parameter.NEWSLETTER)) {
            url.append("&" + Parameter.NEWSLETTER.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.NEWSLETTER)));
        }
        if(trackingParameter.containsKey(Parameter.GNAME)) {
            url.append("&" + Parameter.GNAME.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.GNAME)));
        }
        if(trackingParameter.containsKey(Parameter.SNAME)) {
            url.append("&" + Parameter.SNAME.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.SNAME)));
        }
        if(trackingParameter.containsKey(Parameter.PHONE)) {
            url.append("&" + Parameter.PHONE.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.PHONE)));
        }
        if(trackingParameter.containsKey(Parameter.GENDER)) {
            url.append("&" + Parameter.GENDER.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.GENDER)));
        }
        if(trackingParameter.containsKey(Parameter.BIRTHDAY)) {
            url.append("&" + Parameter.BIRTHDAY.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.BIRTHDAY)));
        }
        if(trackingParameter.containsKey(Parameter.CITY)) {
            url.append("&" + Parameter.CITY.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.CITY)));
        }
        if(trackingParameter.containsKey(Parameter.COUNTRY)) {
            url.append("&" + Parameter.COUNTRY.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.COUNTRY)));
        }
        if(trackingParameter.containsKey(Parameter.ZIP)) {
            url.append("&" + Parameter.ZIP.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.ZIP)));
        }
        if(trackingParameter.containsKey(Parameter.STREET)) {
            url.append("&" + Parameter.STREET.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.STREET)));
        }
        if(trackingParameter.containsKey(Parameter.STREETNUMBER)) {
            url.append("&" + Parameter.STREETNUMBER.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.STREETNUMBER)));
        }



        // media tracking
        if(trackingParameter.containsKey(Parameter.MEDIA_FILE)) {
            url.append("&" + Parameter.MEDIA_FILE.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.MEDIA_FILE)));
        }
        if(trackingParameter.containsKey(Parameter.MEDIA_ACTION)) {
            url.append("&" + Parameter.MEDIA_ACTION.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.MEDIA_ACTION)));
        }
        if(trackingParameter.containsKey(Parameter.MEDIA_POS)) {
            url.append("&" + Parameter.MEDIA_POS.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.MEDIA_POS)));
        }
        if(trackingParameter.containsKey(Parameter.MEDIA_LENGTH)) {
            url.append("&" + Parameter.MEDIA_LENGTH.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.MEDIA_LENGTH)));
        }
        if(trackingParameter.containsKey(Parameter.MEDIA_BANDWITH)) {
            url.append("&" + Parameter.MEDIA_BANDWITH.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.MEDIA_BANDWITH)));
        }
        if(trackingParameter.containsKey(Parameter.MEDIA_VOLUME)) {
            url.append("&" + Parameter.MEDIA_VOLUME.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.MEDIA_VOLUME)));
        }
        if(trackingParameter.containsKey(Parameter.MEDIA_MUTED)) {
            url.append("&" + Parameter.MEDIA_MUTED.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.MEDIA_MUTED)));
        }
        if(trackingParameter.containsKey(Parameter.MEDIA_TIMESTAMP)) {
            url.append("&" + Parameter.MEDIA_TIMESTAMP.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.MEDIA_TIMESTAMP)));
        }
        //if ecom trackingParameter are given, append them to the url as well
        if (!trackingParameter.getEcomParameter().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getEcomParameter().entrySet()) {
                url.append("&cb" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue()));
            }
        }
        //if ad trackingParameter are given, append them to the url as well
        if (!trackingParameter.getAdParameter().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getAdParameter().entrySet()) {
                url.append("&cc" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue()));
            }
        }
        //if action trackingParameter are given, append them to the url as well
        if (!trackingParameter.getPageParameter().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getPageParameter().entrySet()) {
                url.append("&cp" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue()));
            }
        }
        //if session trackingParameter are given, append them to the url as well
        if (!trackingParameter.getSessionParameter().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getSessionParameter().entrySet()) {
                url.append("&cs" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue()));
            }
        }
        //if action trackingParameter are given, append them to the url as well
        if (!trackingParameter.getActionParameter().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getActionParameter().entrySet()) {
                url.append("&ck" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue()));
            }
        }

        //if product category trackingParameter are given, append them to the url as well
        if (!trackingParameter.getProductCategories().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getProductCategories().entrySet()) {
                url.append( "&ca" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue()));
            }
        }
        //if page category trackingParameter are given, append them to the url as well
        if (!trackingParameter.getPageCategories().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getPageCategories().entrySet()) {
                url.append("&cg" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue()));
            }
        }

        //if user category trackingParameter are given, append them to the url as well
        if (!trackingParameter.getUserCategories().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getUserCategories().entrySet()) {
                url.append("&uc" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue()));
            }
        }

        //if media category trackingParameter are given, append them to the url as well
        if (!trackingParameter.getMediaCategories().isEmpty()) {
            for (Map.Entry<String, String> entry : trackingParameter.getMediaCategories().entrySet()) {
                url.append("&mg" + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue()));
            }
        }


        if(trackingParameter.containsKey(Parameter.SAMPLING)) {
            url.append("&" + Parameter.SAMPLING.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.SAMPLING)));
        }

        if(trackingParameter.containsKey(Parameter.USERAGENT)) {
            url.append("&" + Parameter.USERAGENT.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.USERAGENT)));
        }

        if(trackingParameter.containsKey(Parameter.IP_ADDRESS)) {
            url.append("&" + Parameter.IP_ADDRESS.toString() + "=" + HelperFunctions.urlEncode(tp.get(Parameter.IP_ADDRESS)));
        }

        // append eor to make shure the request is valid
        url.append("&eor=1");
        return url.toString();
    }



}
