package com.webtrekk.webtrekksdk.Request;


import java.util.Map;
import java.util.SortedMap;

import com.webtrekk.webtrekksdk.TrackingConfiguration;
import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.TrackingParameter.Parameter;
import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;


/**
 * this class contains the tracking event and the trackingparams, and handles the creation of an url string
 * which is send as get request to the configured track domain of the customer
 */
public class TrackingRequest {


    public final TrackingParameter mTrackingParameter;
    final private TrackingConfiguration trackingConfiguration;
    final private RequestType mRequestType;

    public enum RequestType
    {
        GENERAL,
        CDB,
        INSTALL
    }

    /**
     * creates a new tracking request object with default GENERAL type request
     *
     * @param tp the TrackingParameter for the TrackingRequest
     * @param trackingConfiguration the tracking configuration of the webtrekk object
     */
    public TrackingRequest(TrackingParameter tp, TrackingConfiguration trackingConfiguration) {
        mTrackingParameter = tp;
        this.trackingConfiguration = trackingConfiguration;
        mRequestType = RequestType.GENERAL;
    }

    /**
     * creates a new tracking request object
     *
     * @param tp the TrackingParameter for the TrackingRequest
     * @param trackingConfiguration the tracking configuration of the webtrekk object
     * @param type of request
     */
    public TrackingRequest(TrackingParameter tp, TrackingConfiguration trackingConfiguration, RequestType type) {
        mTrackingParameter = tp;
        this.trackingConfiguration = trackingConfiguration;
        mRequestType = type;
    }

    /**
     * get address and track id part of request. It is common for all requests
     */

    private String getBaseURLPart()
    {
       return  trackingConfiguration.getTrackDomain() + "/" + trackingConfiguration.getTrackId() + "/wt?";
    }

    /**
     * add to url bufer parameters defined in keys array with appropriate value in trackingParameters.
     * @param trackingParameter
     * @param url
     * @param keys
     */
    private void addParametersArray(TrackingParameter trackingParameter, StringBuffer url, Parameter keys[],
                                    boolean isAmpToFirstParameter)
    {
        SortedMap<Parameter, String> tp = trackingParameter.getDefaultParameter();
        boolean isAmp = isAmpToFirstParameter;

        for (Parameter key:keys)
        {
            if(trackingParameter.containsKey(key)) {
                url.append((isAmp ? "&" : "") + key.toString() + "=" + HelperFunctions.urlEncode(tp.get(key)));

                if (!isAmp)
                    isAmp = true;
            }
        }
    }

    /**
     * The same as {@link #addParametersArray(TrackingParameter, StringBuffer, Parameter[], boolean)}
     * @param trackingParameter
     * @param url
     * @param keys
     */
    private void addParametersArray(TrackingParameter trackingParameter, StringBuffer url, Parameter keys[])
    {
        addParametersArray(trackingParameter, url, keys, true);
    }

    /**
     * fills url buffer with parameters defined in key/value map. Add sufix to each parameter name
     * @param map
     * @param sufix
     * @param url
     */
    private void addKeyMap(SortedMap<String, String> map, String sufix, StringBuffer url)
    {
        if (!map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                url.append(sufix + entry.getKey().toString() + "=" + HelperFunctions.urlEncode(entry.getValue()));
            }
        }
    }

    /**
     * this is interface for classes-factories that generate URL parameters.
     */
    private interface URLFactory
    {
        String getPValue(TrackingParameter trackingParameter);
        void getTrackingPart(TrackingParameter trackingParameter, StringBuffer url);
        String getBasePart();
        boolean isEORAppend();
    }


    private class CDBRequest implements URLFactory
    {
        // arrays of all keys for CDB request
        final Parameter KEYZ[] = {Parameter.EVERID, Parameter.CDB_EMAIL_MD5, Parameter.CDB_EMAIL_SHA,
                Parameter.CDB_PHONE_MD5, Parameter.CDB_PHONE_SHA, Parameter.CDB_ADDRESS_MD5, Parameter.CDB_ADDRESS_SHA,
                Parameter.CDB_ANDROID_ID, Parameter.CDB_IOS_ADD_ID, Parameter.CDB_WIN_AD_ID, Parameter.CDB_FACEBOOK_ID,
                Parameter.CDB_TWITTER_ID, Parameter.CDB_GOOGLE_PLUS_ID, Parameter.CDB_LINKEDIN_ID};

        /**
         * this method is generated p parameter for URL for specific implementation
         * @param trackingParameter
         * @return
         */
        @Override
        public String getPValue(TrackingParameter trackingParameter) {
            SortedMap<Parameter, String> tp = trackingParameter.getDefaultParameter();

            return "p=" + Webtrekk.TRACKING_LIBRARY_VERSION + ",0";
        }

        /**
         * Fills url buffer based on tracking parameters. use some help function.
         * @param trackingParameter
         * @param url
         */
        @Override
        public void getTrackingPart(TrackingParameter trackingParameter, StringBuffer url) {
            addParametersArray(trackingParameter, url, KEYZ);
            addKeyMap(trackingParameter.getCustomUserParameters(), "&cdb", url);
        }

        @Override
        public String getBasePart() {
            return getBaseURLPart();
        }

        @Override
        public boolean isEORAppend() {
            return true;
        }
    }



    private class GeneralRequest implements URLFactory
    {
        // arrays of all keys for General request
        final Parameter KEYZ[] = {Parameter.EVERID, Parameter.ADVERTISER_ID, Parameter.FORCE_NEW_SESSION,
        Parameter.APP_FIRST_START, Parameter.CURRENT_TIME, Parameter.TIMEZONE, Parameter.DEV_LANG, Parameter.CUSTOMER_ID,
        Parameter.ACTION_NAME, Parameter.ORDER_TOTAL, Parameter.ORDER_NUMBER, Parameter.PRODUCT, Parameter.PRODUCT_COST,
        Parameter.CURRENCY, Parameter.PRODUCT_COUNT, Parameter.PRODUCT_STATUS, Parameter.VOUCHER_VALUE, Parameter.ADVERTISEMENT,
        Parameter.ADVERTISEMENT_ACTION, Parameter.INTERN_SEARCH, Parameter.EMAIL, Parameter.EMAIL_RID, Parameter.NEWSLETTER,
        Parameter.GNAME, Parameter.SNAME, Parameter.PHONE, Parameter.GENDER, Parameter.BIRTHDAY, Parameter.CITY, Parameter.COUNTRY,
        Parameter.ZIP, Parameter.STREET, Parameter.STREETNUMBER, Parameter.MEDIA_FILE, Parameter.MEDIA_ACTION,
        Parameter.MEDIA_POS, Parameter.MEDIA_LENGTH, Parameter.MEDIA_BANDWITH, Parameter.MEDIA_VOLUME,
        Parameter.MEDIA_MUTED, Parameter.MEDIA_TIMESTAMP, Parameter.SAMPLING, Parameter.IP_ADDRESS, Parameter.USERAGENT,
        Parameter.PAGE_URL};

        /**
         * this method is generate p parameter for URL for specific implementation
         * @param trackingParameter
         * @return
         */
        @Override
        public String getPValue(TrackingParameter trackingParameter)
        {
            SortedMap<Parameter, String> tp = trackingParameter.getDefaultParameter();


            return "p=" + Webtrekk.TRACKING_LIBRARY_VERSION + "," +
                    HelperFunctions.urlEncode(tp.get(Parameter.ACTIVITY_NAME)) + ",0,"+
                    tp.get(Parameter.SCREEN_RESOLUTION) + ","+
                    tp.get(Parameter.SCREEN_DEPTH) + ",0,"+
                    tp.get(Parameter.TIMESTAMP) + ",0,0,0";
        }

        /**
         * Fills url buffer based on tracking parameters. use some help function.
         * @param trackingParameter
         * @param url
         */
        @Override
        public void getTrackingPart(TrackingParameter trackingParameter, StringBuffer url)
        {
            addParametersArray(trackingParameter, url, KEYZ);

            //if ecom trackingParameter are given, append them to the url as well
            addKeyMap(trackingParameter.getEcomParameter(), "&cb", url);

            //if ad trackingParameter are given, append them to the url as well
            addKeyMap(trackingParameter.getAdParameter(), "&cc", url);

            //if action trackingParameter are given, append them to the url as well
            addKeyMap(trackingParameter.getPageParameter(), "&cp", url);

            //if session trackingParameter are given, append them to the url as well
            addKeyMap(trackingParameter.getSessionParameter(), "&cs", url);

            //if action trackingParameter are given, append them to the url as well
            addKeyMap(trackingParameter.getActionParameter(), "&ck", url);

            //if product category trackingParameter are given, append them to the url as well
            addKeyMap(trackingParameter.getProductCategories(), "&ca", url);

            //if page category trackingParameter are given, append them to the url as well
            addKeyMap(trackingParameter.getPageCategories(), "&cg", url);

            //if user category trackingParameter are given, append them to the url as well
            addKeyMap(trackingParameter.getUserCategories(), "&uc", url);

            //if media category trackingParameter are given, append them to the url as well
            addKeyMap(trackingParameter.getMediaCategories(), "&mg", url);
        }

        @Override
        public String getBasePart() {
            return getBaseURLPart();
        }

        @Override
        public boolean isEORAppend() {
            return true;
        }
    }

    private class InstallRequest implements URLFactory
    {
        final Parameter KEYZ[] = {Parameter.INST_TRACK_ID, Parameter.INST_AD_ID, Parameter.INST_CLICK_ID,
                Parameter.USERAGENT};

        @Override
        public String getPValue(TrackingParameter trackingParameter) {
            return "";
        }

        @Override
        public void getTrackingPart(TrackingParameter trackingParameter, StringBuffer url)
        {
            addParametersArray(trackingParameter, url, KEYZ, false);
        }

        @Override
        public String getBasePart() {
            return "http://appinstall.webtrekk.net/appinstall/v1/install?";
        }

        @Override
        public boolean isEORAppend() {
            return false;
        }
    }


    /**
     * creates a URL String from the given Request which can be send to the server/stores in the urlStore
     *
     * @return returns the URL as String with all the TrackingParameter url encoded
     */
    public String getUrlString() {

        StringBuffer url = new StringBuffer();
        URLFactory urlFactory = null;

        switch (mRequestType)
        {
            case GENERAL:
                urlFactory = new GeneralRequest();
                break;
            case CDB:
                urlFactory = new CDBRequest();
                break;
            case INSTALL:
                urlFactory = new InstallRequest();
                break;
        }

        if (urlFactory == null)
        {
            WebtrekkLogging.log("urlFactory is null. Non supported mRequestType");
            return null;
        }

        url.append(urlFactory.getBasePart());
        url.append(urlFactory.getPValue(mTrackingParameter));
        urlFactory.getTrackingPart(mTrackingParameter, url);

        if (urlFactory.isEORAppend())
           url.append("&eor=1");
        return url.toString();
    }

    public TrackingParameter getTrackingParameter() {
        return mTrackingParameter;
    }

    public void setTrackingParameter(TrackingParameter trackingParameter) {
        trackingParameter = trackingParameter;
    }

    public TrackingConfiguration getTrackingConfiguration() {
        return trackingConfiguration;
    }

    public void setTrackingConfiguration(TrackingConfiguration trackingConfiguration) {
        trackingConfiguration = trackingConfiguration;
    }
}
