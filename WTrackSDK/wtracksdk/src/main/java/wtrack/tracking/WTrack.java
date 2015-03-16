package wtrack.tracking;

import android.content.res.Resources;
import android.util.Log;

import com.android.volley.RequestQueue;

import java.util.HashMap;

import wtrack.wtracksdk.R;
import static wtrack.tracking.TrackingParams.Params;


/**
 * Created by user on 01/03/15.
 *
 * This class is a singleton which will be instantiated only once by every tracked application
 * it holds global configuration variables and a reference to the application
 * also is it responsible for global application overrides, like the activity methods
 * this class contains also all the device details which are shared between all the trackers
 * the tracking app has two tracking scopes, global tracking vars which are stored here, and are used for
 * all trackers, and a tracker specific scope which are only valid for the scope of the activity/screen
 */
public class WTrack {

    public static final String PREFERENCE_FILE_NAME = "webtrekk-preferences";
    public static final String LOGTAG = "WTrack";
    public static final String PREFERENCE_KEY_EVER_ID = "EVERID";
    public static final String PREFERENCE_APP_VERSIONCODE = "APP_VERSIONCODE";

    private static HashMap<WTrackApplication, WTrack> apps = new HashMap<WTrackApplication, WTrack>();
    private WTrackApplication app;

    Resources res;

    private String webtrekk_track_domain;
    private String webtrekk_track_id;
    // TODO: sampling implementation behavior still unclear
    private int sampling;
    private boolean json_tracking;
    private boolean optout;

    private HashMap<TrackingParams.Params, String> auto_tracked_values;

    /* synchronized queue for all tracking requests, automatically runs in background threads, handled
    * by a threadpool executor, enables caching, retry management and others, implemented in the volley lib
    *
    */
    private RequestQueue requestQueue;

    /**
     * private constructor for singleton pattern
     */
    private WTrack(WTrackApplication app) {
        this.app = app;
        this.res = app.getResources();
        auto_tracked_values = new HashMap<>();
    }


    /**
     * check if already a WTrack instance exists for the application, if not create a new one
     * otherwise return the existing
     *
     * @param app
     * @return
     */
    public static synchronized WTrack getInstance(WTrackApplication app) {
        WTrack wtrack = apps.get(app);
        if(wtrack != null) {
            return wtrack;
        }
        wtrack = new WTrack(app);
        wtrack.initFromXML();
        wtrack.collectAutomaticData();
        wtrack.sendInitialRequests();
        apps.put(app, wtrack);
        Log.d(LOGTAG, "tracking initialized");
        return wtrack;

    }

    /**
     * this inits the basic webtrekk tracking configuration, the xml values will be overwritten by the application
     */
    private void initFromXML() {
        webtrekk_track_domain = res.getString(R.string.webtrekk_track_domain);
        webtrekk_track_id = res.getString(R.string.webtrekk_track_id);
        sampling = res.getInteger(R.integer.webtrekk_sampling);
        json_tracking = res.getBoolean(R.bool.json_tracking);
    }

    /**
     * collecting all static data which remains the same for all trackers and requests
     * this functions inserts all data into the auto_tracked_values HashMap for which tracking is enabled in the xml
     * this data always stays the same for a device for all trackers if configured once
     * all trackers will append this values to their tracking requests as url/json params
     * customers can decide on their own based on their xml tracking config, which values they are interested in
     */
    private void collectAutomaticData() {
        if(res.getBoolean(R.bool.auto_track_resolution)) {
            auto_tracked_values.put(Params.SCREEN_RESOLUTION, HelperFunctions.getResolution(app));

        }
        if(res.getBoolean(R.bool.auto_track_depth)) {
            auto_tracked_values.put(Params.SCREEN_DEPTH, HelperFunctions.getResolution(app));

        }
        if(res.getBoolean(R.bool.auto_track_language)) {
            auto_tracked_values.put(Params.DEV_LANG, HelperFunctions.getLanguage());

        }
        if(res.getBoolean(R.bool.auto_track_timezone)) {
            auto_tracked_values.put(Params.TIMEZONE, HelperFunctions.getTimezone());

        }
        if(res.getBoolean(R.bool.auto_track_osname)) {
            auto_tracked_values.put(Params.OS_NAME, HelperFunctions.getOSName());

        }
        if(res.getBoolean(R.bool.auto_track_osversion)) {
            auto_tracked_values.put(Params.OS_VERSION, HelperFunctions.getOSVersion());

        }
        if(res.getBoolean(R.bool.auto_track_apilevel)) {
            auto_tracked_values.put(Params.API_LEVEL, HelperFunctions.getAPILevel());

        }
        if(res.getBoolean(R.bool.auto_track_device)) {
            auto_tracked_values.put(Params.DEVICE, HelperFunctions.getDevice());

        }
        if(res.getBoolean(R.bool.auto_track_trackinglib_version)) {
            auto_tracked_values.put(Params.TRACKING_LIB_VERSION, res.getString(R.string.version));

        }
        if(res.getBoolean(R.bool.auto_track_app_language)) {
            auto_tracked_values.put(Params.APP_LANGUAGE, res.getConfiguration().locale.getDisplayLanguage());

        }
        HashMap<String, String> playstoreprofile = HelperFunctions.getUserProfile(app);
        if(res.getBoolean(R.bool.auto_track_playstoreusername)) {
            auto_tracked_values.put(Params.PLAYSTORE_SNAME, playstoreprofile.get("sname"));

        }
        if(res.getBoolean(R.bool.auto_track_playstoreusername)) {
            auto_tracked_values.put(Params.PLAYSTORE_GNAME, playstoreprofile.get("gname"));

        }
        if(res.getBoolean(R.bool.auto_track_playstoremail)) {
            auto_tracked_values.put(Params.PLAYSTORE_MAIL, playstoreprofile.get("email"));

        }
        if(res.getBoolean(R.bool.auto_track_appversion_name)) {
            auto_tracked_values.put(Params.APP_VERSION_NAME, HelperFunctions.getAppVersionName(app));

        }
        if(res.getBoolean(R.bool.auto_track_appversion_code)) {
            auto_tracked_values.put(Params.APP_VERSION_CODE, String.valueOf(HelperFunctions.getAppVersionCode(app)));

        }
        if(res.getBoolean(R.bool.auto_track_preinstalled)) {
            auto_tracked_values.put(Params.APP_PREINSTALLED, String.valueOf(HelperFunctions.isAppPreinstalled(app)));

        }
        if(res.getBoolean(R.bool.auto_track_advertiserid)) {
            HelperFunctions.getAdvertiserID(app);

        }
    }

    /**
     * this function sends out initial tracking requests which will be send only once
     * each automatic request needs to be enabled in the tracking config
     * the customer has to specify a default tracker in the xml, for this to work, otherwise the first
     * configured tracker will be used for automated requests
     */
    public void sendInitialRequests() {
        // if the app is started for the first time, send out the first start request once
        if(res.getBoolean(R.bool.auto_track_firststart)) {
            if(HelperFunctions.firstStart(app)) {
                // app is started the first time, so send out request with appropiate event type
                // TrackingParams tp = new TrackingParams();
                // track(Events.FIRST_START, tp);
            }

        }
        // if the app was updated, send out the update request once
        if(res.getBoolean(R.bool.auto_track_updated)) {
            if(HelperFunctions.updated(app)) {
                // app is updated, so send out request with appropiate event type
                // TrackingParams tp = new TrackingParams();
                // track(Events.UPDATED, tp);
            }
        }
    }

    public String getWebtrekk_track_domain() {
        return webtrekk_track_domain;
    }

    public void setWebtrekk_track_domain(String webtrekk_track_domain) {
        this.webtrekk_track_domain = webtrekk_track_domain;
    }

    public String getWebtrekk_track_id() {
        return webtrekk_track_id;
    }

    public void setWebtrekk_track_id(String webtrekk_track_id) {
        this.webtrekk_track_id = webtrekk_track_id;
    }

    public int getSampling() {
        return sampling;
    }

    public void setSampling(int sampling) {
        this.sampling = sampling;
    }

    public HashMap<Params, String> getAuto_tracked_values() {
        return auto_tracked_values;
    }

    public void setAuto_tracked_values(HashMap<Params, String> auto_tracked_values) {
        this.auto_tracked_values = auto_tracked_values;
    }

    public boolean isJson_tracking() {
        return json_tracking;
    }

    public void setJson_tracking(boolean json_tracking) {
        this.json_tracking = json_tracking;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public boolean isOptout() {
        return optout;
    }

    public void setOptout(boolean optout) {
        this.optout = optout;
    }
}
