package com.webtrekk.webtrekksdk;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by user on 11/03/15.
 *
 * this class deals with all the possible tracking parameters, it offers a static enum for all the valid ones
 * it also allows to return the trackingParameter as url, and maybe later as json as well
 * it offers the user some helper functions for easy of use, and will be created before a tracking request is send
 * the manual information has to be added by the user, the automatic information will be added by the sdk
 */
public class TrackingParameter {
    // general tracking trackingParameter
    private SortedMap<Parameter, String> defaultParameter;
    // customer trackingparams, defined by the app
    private SortedMap<String, String> pageParameter;
    private SortedMap<String, String> sessionParameter;
    private SortedMap<String, String> ecomParameter;
    private SortedMap<String, String> userCategories;
    private SortedMap<String, String> pageCategories;
    private SortedMap<String, String> adParameter;
    private SortedMap<String, String> actionParameter;
    private SortedMap<String, String> productCategories;
    private SortedMap<String, String> mediaCategories;


    public TrackingParameter() {
        this.defaultParameter = new TreeMap<>();
        this.pageParameter = new TreeMap<>();
        this.sessionParameter = new TreeMap<>();
        this.ecomParameter = new TreeMap<>();
        this.userCategories = new TreeMap<>();
        this.pageCategories = new TreeMap<>();
        this.adParameter = new TreeMap<>();
        this.actionParameter = new TreeMap<>();
        this.productCategories = new TreeMap<>();
        this.mediaCategories = new TreeMap<>();
    }

    /*
     * this method adds a tracking param to the HashMap
     * if the key already exists it will be updated
     */
    public TrackingParameter add(Parameter key, String value) {
        defaultParameter.put(key, value);
        return this;
    }

    /**
     * this method allows to merge two trackingparameter objects in single objects
     * @param tp
     * @return
     */
    public TrackingParameter add(TrackingParameter tp) {
        if(tp == null) {
            WebtrekkLogging.log("Error: TrackingParameter object passed to add method is null");
            return this;
        }
        this.defaultParameter.putAll(tp.getDefaultParameter());
        this.pageParameter.putAll(tp.getPageParameter());
        this.sessionParameter.putAll(tp.getSessionParameter());
        this.ecomParameter.putAll(tp.getEcomParameter());
        this.userCategories.putAll(tp.getUserCategories());
        this.pageCategories.putAll(tp.getPageCategories());
        this.adParameter.putAll(tp.getAdParameter());
        this.actionParameter.putAll(tp.getActionParameter());
        this.productCategories.putAll(tp.getProductCategories());
        this.mediaCategories.putAll(tp.getMediaCategories());
        return this;
    }

    public boolean containsKey(Parameter key) {
        return defaultParameter.containsKey(key);
    }

    //TODO: noch mehr add Methoden für int/long/double usw, dann muss der nutzer nicht mehr manuell String.valueOf nehmen beispiel mediatracking positionen

    //TODO: maybe make the index to int, in case this is really always a number

    public TrackingParameter add(Parameter key, String index, String value) {
        switch(key) {
            case ACTION:
                this.actionParameter.put(index, value);
                break;
            case PAGE:
                this.pageParameter.put(index, value);
                break;
            case SESSION:
                this.sessionParameter.put(index, value);
                break;
            case ECOM:
                this.ecomParameter.put(index, value);
                break;
            case AD:
                this.adParameter.put(index, value);
                break;
            case USER_CAT:
                this.userCategories.put(index, value);
                break;
            case PAGE_CAT:
                this.pageCategories.put(index, value);
                break;
            case PRODUCT_CAT:
                this.productCategories.put(index, value);
                break;
            case MEDIA_CAT:
                this.mediaCategories.put(index, value);
                break;
            default:
                WebtrekkLogging.log( "invalid trackingparam type");
                throw new IllegalArgumentException("invalid TrackingParameter type");
        }
        return this;
    }

    /**
     * this function adds the auto tracked values to the trackingparams map
     * it will be called during before the request is send by the sdk
     * @param auto_tracked_values
     * @return
     *
     */
    public TrackingParameter add(Map<Parameter, String> auto_tracked_values) {
        for(Map.Entry<Parameter, String> entry : auto_tracked_values.entrySet()) {
            defaultParameter.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public SortedMap<Parameter, String> getDefaultParameter() {
        return defaultParameter;
    }

    public SortedMap<String, String> getPageParameter() {
        return pageParameter;
    }

    public SortedMap<String, String> getSessionParameter() {
        return sessionParameter;
    }

    public SortedMap<String, String> getEcomParameter() {
        return ecomParameter;
    }

    public SortedMap<String, String> getUserCategories() {
        return userCategories;
    }

    public SortedMap<String, String> getPageCategories() {
        return pageCategories;
    }

    public SortedMap<String, String> getAdParameter() {
        return adParameter;
    }

    public SortedMap<String, String> getActionParameter() {
        return actionParameter;
    }

    public SortedMap<String, String> getProductCategories() {
        return productCategories;
    }

    public void setDefaultParameter(SortedMap<Parameter, String> defaultParameter) {
        this.defaultParameter = defaultParameter;
    }

    public SortedMap<String, String> getMediaCategories() {
        return mediaCategories;
    }

    public void setPageParameter(SortedMap<String, String> pageParameter) {
        this.pageParameter = pageParameter;
    }

    public void setSessionParameter(SortedMap<String, String> sessionParameter) {
        this.sessionParameter = sessionParameter;
    }

    public void setEcomParameter(SortedMap<String, String> ecomParameter) {
        this.ecomParameter = ecomParameter;
    }

    public void setUserCategories(SortedMap<String, String> userCategories) {
        this.userCategories = userCategories;
    }

    public void setPageCategories(SortedMap<String, String> pageCategories) {
        this.pageCategories = pageCategories;
    }

    public void setAdParameter(SortedMap<String, String> adParameter) {
        this.adParameter = adParameter;
    }

    public void setActionParameter(SortedMap<String, String> actionParameter) {
        this.actionParameter = actionParameter;
    }

    public void setProductCategories(SortedMap<String, String> productCategories) {
        this.productCategories = productCategories;
    }

    public void setMediaCategories(SortedMap<String, String> mediaCategories) {
        this.mediaCategories = mediaCategories;
    }

    /**
     * this enum contains all valid tracking parameter and their url string identifier
     */
    public enum Parameter {
        /**
         * single value parameter
         */
        SCREEN_RESOLUTION("res"),
        SCREEN_DEPTH("depth"),

        SAMPLING("ps"), // to submit the sampling value with each request which is an integer like 10
        TIMESTAMP("ts"),
        CURRENT_TIME("mts"),
        IP_ADDRESS("X_WT_IP"), //TODO: no internal way to get the external address, discuss which is really useful here
        USERAGENT("X-WT-UA"),
        TIMEZONE("tz"),
        DEV_LANG("la"),
        EVERID("eid"),
        APP_FIRST_START("one"),
        ACTION_NAME("ct"),
        VOUCHER_VALUE("cb563"), //Gutscheinwert
        ORDER_TOTAL("ov"), // Gesamtbestellwert
        ORDER_NUMBER("oi"), // Bestellnummer
        PRODUCT("ba"), //produkt
        PRODUCT_COST("co"), // produktkosten
        CURRENCY("cr"),
        PRODUCT_COUNT("qn"), // produkt anzahl
        PRODUCT_STATUS("st"), // produkt status ( ad, view, conf)
        CUSTOMER_ID("cd"), // kundennnummer
        EMAIL("uc700"), // email
        EMAIL_RID("uc701"), // email rid
        NEWSLETTER("uc702"),
        GNAME("uc703"), // vorname
        SNAME("uc704"), // nachname
        PHONE("uc705"), // telefonnummer
        GENDER("uc705"), //geschlecht
        BIRTHDAY("uc707"), // geburtstag
        CITY("uc708"),
        COUNTRY("uc709"),
        ZIP("uc710"), // postleitzahl
        STREET("uc711"),
        STREETNUMBER("uc712"),
        INTERN_SEARCH("is"),
        ADVERTISEMENT("mc"), // Werbemittel
        ADVERTISEMENT_ACTION("mca"), // Werbemittel
        ADVERTISER_ID("geid"),

        /**
         * Media tracking parameter
         */

        MEDIA_FILE("mi"), // media datei
        MEDIA_ACTION("mk"), //play,pause,stop,pos,seek,eof
        MEDIA_POS("mt1"), //aktuelle position
        MEDIA_LENGTH("mt2"), // laenge der aktuellen media datei
        MEDIA_BANDWITH("bw"), // bandbreite der mediendatei
        MEDIA_VOLUME("vol"), // lautstaerke der mediendatei
        MEDIA_MUTED("mut"),
        MEDIA_TIMESTAMP("x"),// timestamp um caching zu umgehen

        /**
         * multiple value trackingParameter and customer trackingParameter
         */

        PAGE(""),
        SESSION(""),
        ECOM(""),
        AD(""),
        ACTION(""),
        USER_CAT(""),
        PAGE_CAT(""),
        PRODUCT_CAT(""),
        MEDIA_CAT(""),
        ACTIVITY_CAT(""),


        /**
         * unclear / TODO: remove and use as custom trackingParameter
         */
        ACTIVITY_NAME("aname"),
        INSTALL_REFERRER_PARAMS_MC("wt_mc"), // for the referrer tracking
        INSTALL_REFERRER_KEYWORD("wt_kw"), // for the referrer tracking
        FORCE_NEW_SESSION("fns");

        private final String value;

        Parameter(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }

        public static Parameter getParameterByName(String name) {
            for(Parameter p: Parameter.values()) {
                if(name.equals(p.name())) {
                    return p;
                }
            }
            return null;
        }
    }

    /**
     * this method maps any values from mapping values to all the stored parameters
     * it matches by the key of the stored parameter and the mapping value, and replaces
     * the value of the stored parameter with the value from the mappingValues map
     * this method must not modify the original object as its the configuration and key may change dynamicly
     * so the original configuration must contain the placeholders
     *
     * @param mappingValues
     */
    public TrackingParameter applyMapping(Map<String, String> mappingValues) {
        // create a new mapped trackingparameter object
        TrackingParameter mappedTrackingParameter = new TrackingParameter();
        mappedTrackingParameter.setDefaultParameter(new TreeMap<Parameter, String>(this.defaultParameter));
        mappedTrackingParameter.setActionParameter(applySingleMapping(actionParameter, mappingValues));
        mappedTrackingParameter.setAdParameter(applySingleMapping(adParameter, mappingValues));
        mappedTrackingParameter.setEcomParameter(applySingleMapping(ecomParameter, mappingValues));
        mappedTrackingParameter.setPageParameter(applySingleMapping(pageParameter, mappingValues));
        mappedTrackingParameter.setProductCategories(applySingleMapping(productCategories, mappingValues));
        mappedTrackingParameter.setSessionParameter(applySingleMapping(sessionParameter, mappingValues));
        mappedTrackingParameter.setUserCategories(applySingleMapping(userCategories, mappingValues));
        mappedTrackingParameter.setPageCategories(applySingleMapping(pageCategories, mappingValues));
        mappedTrackingParameter.setMediaCategories(applySingleMapping(mediaCategories, mappingValues));
        return mappedTrackingParameter;
    }

    private SortedMap<String, String> applySingleMapping(SortedMap<String, String> original, Map<String, String> mappingValues) {
        SortedMap<String, String> mappedValues = new TreeMap<>();
        if (!original.isEmpty()) {
            for (Map.Entry<String, String> entry : original.entrySet()) {
                String key = entry.getValue();
                if(mappingValues.containsKey(key)) {
                    //entry.setValue(mappingValues.get(entry.getValue()));
                    mappedValues.put(entry.getKey(), mappingValues.get(key));
                } else {
                    // pass empty string if no mapping value is found in the custom parameter, could also use get with default here
                    mappedValues.put(entry.getKey(), "");
                }
            }
        }
        return mappedValues;
    }

}
