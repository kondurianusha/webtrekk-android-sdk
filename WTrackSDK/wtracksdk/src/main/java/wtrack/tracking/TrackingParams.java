package wtrack.tracking;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by user on 11/03/15.
 *
 * this class deals with all the possible tracking parameters, it offers a static enum for all the valid ones
 * it also allows to return the params as url, and maybe later as json as well
 * it offers the user some helper functions for easy of use, and will be created before a tracking request is send
 * the manual information has to be added by the user, the automatic information will be added by the sdk
 */
public class TrackingParams {

    private TreeMap<Params, String> tparams;

    public TrackingParams() {
        this.tparams = new TreeMap<>();
    }

    public TrackingParams add(Params key, String value) {
        tparams.put(key, value);
        return this;
    }

    /**
     * this function adds the auto tracked values to the trackingparams map
     * it will be called during before the request is send by the sdk
     * @param auto_tracked_values
     * @return
     *
     */
    public TrackingParams add(HashMap<Params, String> auto_tracked_values) {
        for(Map.Entry<Params, String> entry : auto_tracked_values.entrySet()) {
            tparams.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public TreeMap<Params, String> getTparams() {
        return tparams;
    }

    public void setTparams(TreeMap<Params, String> tparams) {
        this.tparams = tparams;
    }



    public enum Params {
        // automatic data
        SCREEN_RESOLUTION("res"),
        SCREEN_DEPTH("depth"),
        TIMESTAMP("ts"),
        IP("ip"),
        DEV_LANG("dlang"),
        TIMEZONE("tz"),
        OS_NAME("osname"),
        OS_VERSION("osversion"),
        DEVICE("dev"),
        TRACKING_LIB_VERSION("tracklib"),
        APP_VERSION_NAME("aversion_name"),
        APP_VERSION_CODE("aversion_code"),
        APP_LANGUAGE("alang"),
        APP_UPDATE("aupdate"),
        APP_PREINSTALLED("apreinstalled"),
        APP_FIRST_START("afirst"),
        EVERID("eid"),
        PLAYSTORE_GNAME("ps_gname"),
        PLAYSTORE_SNAME("ps_sname"),
        PLAYSTORE_MAIL("ps_mail"),
        ACTIVITY_NAME("aname"),
        API_LEVEL("api"),
        ADVERTISER_ID("adid"),
        ADVERTISER_OPTOUT("adoo"),
        // we dont use a haspmap here but instead breadcrumbs like path values so e.g. /de/appname/start
        ACTIVITY_CATEGORY("acat"),
        // manual data
        BIRTHDAY("bd"),
        CAMPAIGN("cp"),
        CITY("city"),
        COUNTRY("country"),
        VOUCHER("vou"),
        CURRENCY("cur"),
        CAMPAIGN_PARAMS("cp_params"),
        ACTION_PARAMS("a_params"),
        ECOM_PARAMS("e_params"),
        PAGE_PARAMS("p_params"),
        SESSION_PARAMS("s_params"),
        CUSTOMER_ID("cid"),
        EMAIL("mail"),
        EMAIL_RID("rid"),
        NEWSLETTER("nl"),
        GNAME("gn"),
        SSL("ssl"),
        GENDER("g"),
        INTERN_SEARCH("is"),
        ACTION_NAME("a_name"),
        ORDER_NUMBER("o_number"),
        ORDER_TOTAL("o_total"),
        ZIP("zip"),
        STREET("street"),
        STREETNUMBER("streetnumber"),
        PHONE("phone"),
        PRODUCT("p"),
        PRODUCT_CATEGORY("p_cat"),
        PRODUCT_COST("p_cost"),
        PRODUCT_COUNT("p_count"),
        PRODUCT_STATUS("p_status"),
        USER_CATEGORY("u_cat");

        private final String value;

        Params(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

}
