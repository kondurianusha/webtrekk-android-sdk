package com.webtrekk.webbtrekksdk;

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
    private SortedMap<Parameter, String> tparams;
    // customer trackingparams, defined by the app
    private TreeMap<String, String> pageParams;
    private TreeMap<String, String> sessionParams;
    private TreeMap<String, String> ecomParams;
    private TreeMap<String, String> userCategories;
    private TreeMap<String, String> pageCategories;
    private TreeMap<String, String> adParams;
    private TreeMap<String, String> actionParams;
    private TreeMap<String, String> productCategories;
    private TreeMap<String, String> mediaCategories;
    private HashMap<String, Object> pluginParams;


    public TrackingParameter() {
        this.tparams = new TreeMap<>();
        this.pageParams = new TreeMap<>();
        this.sessionParams = new TreeMap<>();
        this.ecomParams = new TreeMap<>();
        this.userCategories = new TreeMap<>();
        this.pageCategories = new TreeMap<>();
        this.adParams = new TreeMap<>();
        this.actionParams = new TreeMap<>();
        this.productCategories = new TreeMap<>();
        this.mediaCategories = new TreeMap<>();
        this.pluginParams = new HashMap<>();
    }

    /*
     * this method adds a tracking param to the HashMap
     * if the key already exists it will be updated
     */
    public TrackingParameter add(Parameter key, String value) {
        tparams.put(key, value);
        return this;
    }

    /**
     * this method allows to merge two trackingparameter objects in single objects
     * @param tp
     * @return
     */
    public TrackingParameter add(TrackingParameter tp) {
        this.tparams.putAll(tp.getTparams());
        this.pageParams.putAll(tp.getPageParams());
        this.sessionParams.putAll(tp.getSessionParams());
        this.ecomParams.putAll(tp.getEcomParams());
        this.userCategories.putAll(tp.getUserCategories());
        this.pageCategories.putAll(tp.getPageCategories());
        this.adParams.putAll(tp.getAdParams());
        this.actionParams.putAll(tp.getActionParams());
        this.productCategories.putAll(tp.getProductCategories());
        this.mediaCategories.putAll(tp.getMediaCategories());
        this.pluginParams.putAll(tp.getPluginParams());
        return this;
    }
    /**
     * this method ads new plugin trackingParameter, each plugin can check by the key if it has the neccesary information
     * this can be used for example to pass the activity as reference for a plugin which needs to get resource access
     *
     * @param key
     * @Param value
     * @return this
     */
    public TrackingParameter add(String key, Object value) {
        this.pluginParams.put(key, value);
        return this;
    }

    public boolean containsKey(Parameter key) {
        return tparams.containsKey(key);
    }

    //TODO: noch mehr add Methoden für int/long/double usw, dann muss der nutzer nicht mehr manuell String.valueOf nehmen beispiel mediatracking positionen

    //TODO: maybe make the index to int, in case this is really always a number

    public TrackingParameter add(Parameter key, String index, String value) {
        switch(key) {
            case ACTION:
                this.actionParams.put(index, value);
                break;
            case PAGE:
                this.pageParams.put(index, value);
                break;
            case SESSION:
                this.sessionParams.put(index, value);
                break;
            case ECOM:
                this.ecomParams.put(index, value);
                break;
            case AD:
                this.adParams.put(index, value);
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
            tparams.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public SortedMap<Parameter, String> getTparams() {
        return tparams;
    }

    public SortedMap<String, String> getPageParams() {
        return pageParams;
    }

    public SortedMap<String, String> getSessionParams() {
        return sessionParams;
    }

    public SortedMap<String, String> getEcomParams() {
        return ecomParams;
    }

    public SortedMap<String, String> getUserCategories() {
        return userCategories;
    }

    public SortedMap<String, String> getPageCategories() {
        return pageCategories;
    }

    public SortedMap<String, String> getAdParams() {
        return adParams;
    }

    public SortedMap<String, String> getActionParams() {
        return actionParams;
    }

    public SortedMap<String, String> getProductCategories() {
        return productCategories;
    }

    public void setTparams(SortedMap<Parameter, String> tparams) {
        this.tparams = tparams;
    }

    public SortedMap<String, String> getMediaCategories() {
        return mediaCategories;
    }

    public Map<String, Object> getPluginParams() {
        return pluginParams;
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
        DEVICE("dev"),


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
        OS_NAME("osname"),
        OS_VERSION("osversion"),
        TRACKING_LIB_VERSION("tracklib"),
        APP_VERSION_NAME("aversion_name"),
        APP_VERSION_CODE("aversion_code"),
        APP_LANGUAGE("alang"),
        APP_UPDATE("aupdate"),
        APP_PREINSTALLED("apreinstalled"),
        PLAYSTORE_GNAME("ps_gname"),
        PLAYSTORE_SNAME("ps_sname"),
        PLAYSTORE_MAIL("ps_mail"),
        ACTIVITY_NAME("aname"),
        API_LEVEL("api"),
        INSTALL_REFERRER_PARAMS_MC("wt_mc"), // for the referrer tracking
        INSTALL_REFERRER_KEYWORD("wt_kw"), // for the referrer tracking
        // this are string/value pairs which are just used to pass objects/references and values to the plugins, they are not used by the tracking lib,
        PLUGIN_PARAM(""),
        SCREEN_ORIENTATION("s_o"),
        CONNECTION_TYPE("c_t"),
        ADVERTISEMENT_OPT_OUT("aoo");

        private final String value;

        Parameter(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

}
