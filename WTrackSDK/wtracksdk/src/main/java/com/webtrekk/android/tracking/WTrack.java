package com.webtrekk.android.tracking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import com.webtrekk.android.trackingplugin.Plugin;
import com.webtrekk.android.trackingplugin.HelloWorldPlugin;
import wtrack.wtracksdk.R;


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
    public static final String PREFERENCE_KEY_EVER_ID = "everId";
    public static final String PREFERENCE_APP_VERSIONCODE = "appVersion";
    public static final String PREFERENCE_KEY_OPTED_OUT = "optedOut";
    public static final String PREFERENCE_KEY_IS_SAMPLING = "sampling";
    public static final String PREFERENCE_KEY_SAMPLING_RATE = "samplingRate";
    public static final String PREFERENCE_KEY_INSTALLATION_FLAG = "InstallationFlag";

    public static final String TRACKING_LIBRARY_VERSION = "400";

    private static WTrack wtrack;
    private WTrackApplication app;
    private Tracker tracker;
    private WeakReference<Activity> currentActivity;
    private boolean started;

    Resources res;

    private String webtrekkTrackDomain;
    private String webtrekkTrackId;
    // TODO: sampling implementation behavior still unclear
    private int sampling;
    private boolean isSampling;
    private boolean isOptout;
    // TODO: in die xml config
    private long initialSendDelay = 0;
    private long sendDelay = 0;

    private HashMap<TrackingParams.Params, String> autoTrackedValues;
    private HashMap<String, String> activityState;

    /* synchronized queue for all tracking requests, automatically runs in background threads, handled
    * by a threadpool executor, enables caching, retry management and others, implemented in the volley lib
    *
    */
    private RequestQueue requestQueue;

    // the global application context
    private Context context;

    // the available plugins
    private ArrayList<Plugin> plugins;

    /**
     * private constructor for singleton pattern
     */
    private WTrack(Context context) {
        //cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
        //network = new BasicNetwork(new HurlStack());
        //this.requestQueue = new RequestQueue(cache, network);
        this.requestQueue = new RequestQueue(initialSendDelay, sendDelay);
        requestQueue.setContext(context);
        autoTrackedValues = new HashMap<>();
        //this.res = app.getResources();
        this.res = context.getResources();
        this.context = context;
        // create the tracker instance, the object responsible for sending the requests
        tracker = new Tracker(this);

    }


    /**
     * check if already a WTrack instance exists for the application, if not create a new one
     * otherwise return the existing
     *
     * @return
     */
    public static synchronized WTrack getInstance(Context context) {
        L.log("getting Wtrack Singleton Instance");
        if(wtrack != null) {
            return wtrack;
        }
        wtrack = new WTrack(context);
        wtrack.initFromXML();
        wtrack.collectAutomaticData();
        L.log("tracking initialized");

        return wtrack;

    }

    /**
     * this inits the basic webtrekk tracking configuration, the xml values will be overwritten by the application
     */
    private void initFromXML() {
        webtrekkTrackDomain = res.getString(R.string.webtrekk_track_domain);
        webtrekkTrackId = res.getString(R.string.webtrekk_track_id);
        sampling = res.getInteger(R.integer.webtrekk_sampling);
        initialSendDelay = res.getInteger(R.integer.initial_send_delay);
        sendDelay = res.getInteger(R.integer.send_delay);

        this.requestQueue = new RequestQueue(initialSendDelay, sendDelay);
        requestQueue.setContext(context);
        setupOptedOut();
        setupSampling();

        // load plugins, this will decided by hand during compile time of the lib to improve performance
        // each customer can that way have a lib with all the features/plugins he needs
        // he has to enable the plugin in the xml to load it here, therefore each plugin needs a unique name
        //plugins.add(new ExampleDiscountAdNotificationPlugin(this));
        plugins = new ArrayList<>();
        if(res.getBoolean(R.bool.enable_plugin_hello_world))
            plugins.add(new HelloWorldPlugin(this));
    }

    public void setupOptedOut() {
        SharedPreferences preferences = this.context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        this.isOptout = preferences.getBoolean(PREFERENCE_KEY_OPTED_OUT, false);
        L.log("optedOut = " + this.isOptout);
    }

    public void setupSampling() {
        SharedPreferences preferences = this.context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        if (preferences.getInt(PREFERENCE_KEY_SAMPLING_RATE, 0) == this.sampling) {
            this.isSampling = preferences.getBoolean(PREFERENCE_KEY_IS_SAMPLING, true);
        } else {
            this.isSampling = (this.sampling <= 0 || new Random().nextInt(this.sampling) == 0);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(PREFERENCE_KEY_IS_SAMPLING, this.isSampling);
            editor.putInt(PREFERENCE_KEY_SAMPLING_RATE, this.sampling);
            editor.commit();
        }

        L.log("isSampling = " + this.isSampling + ", samplingRate = " + this.sampling);
    }

    public void activityStart(Activity activity) {
        if(activity == null) {
            L.log("activityStart: activity must not be null");
            return;
        }
        if(!this.started) {
            setContext(activity);
        }
        if (this.webtrekkTrackDomain == null) {
            L.log("activityStart: 'track_domain' was not set.");
            return;
        }
        if(this.webtrekkTrackId == null) {
            L.log("activityStart: track_id was not set");
            return;
        }
        if(this.currentActivity != null) {
            Activity currentActivity = this.currentActivity.get();
            if(currentActivity == activity) {
                return;
            }
        }

        this.currentActivity = new WeakReference<Activity>(activity);
        if(!started) {
            this.setupSampling();
            this.getEverId();
            this.started = true;
            //TODO ? prüfen
            // this.trackReferrer();
            //TODO: auch prüfen, doof im prinzip wenn der code überall verteilt ist, die neue version ist da besser
            // this.trackUpdate();
            L.log(("activityStart: Started tracking"));

        }
    }

    public void activityStop(Activity activity) {
        if(activity == null) {
            L.log("activityStop: activity must not be null");
            return;
        }
        if (this.currentActivity == null || this.currentActivity.get() != activity) {
            return;
        }

        wtrack.getRequestQueue().saveBackup();

        this.currentActivity = null;
        this.isSampling = false;

        this.started = false;

        L.log("activityStop: Stopped tracking.");

    }

    public void setContext(Context context) {
        if(this.started) {
            L.log(("setContext: can not set context after tracking was started"));
            return;
        }

        if(context != null) {
            context = context.getApplicationContext();
        }

        this.context = context;

    }




    /**
     * collecting all static data which remains the same for all trackers and requests
     * this functions inserts all data into the autoTrackedValues HashMap for which tracking is enabled in the xml
     * this data always stays the same for a device for all trackers if configured once
     * all trackers will append this values to their tracking requests as url/json params
     * customers can decide on their own based on their xml tracking config, which values they are interested in
     */
    private void collectAutomaticData() {
        autoTrackedValues.put(TrackingParams.Params.SCREEN_RESOLUTION, HelperFunctions.getResolution(context));
        autoTrackedValues.put(TrackingParams.Params.SCREEN_DEPTH, HelperFunctions.getDepth(context));
        autoTrackedValues.put(TrackingParams.Params.TIMEZONE, HelperFunctions.getTimezone());
        String useragent = getUserAgent();
        autoTrackedValues.put(TrackingParams.Params.USERAGENT, useragent);
        autoTrackedValues.put(TrackingParams.Params.DEV_LANG, HelperFunctions.getLanguage());

        if(res.getBoolean(R.bool.auto_track_apilevel)) {
            autoTrackedValues.put(TrackingParams.Params.API_LEVEL, HelperFunctions.getAPILevel());

        }


        if(res.getBoolean(R.bool.auto_track_playstoreusername)) {
            HashMap<String, String> playstoreprofile = HelperFunctions.getUserProfile(app);
            autoTrackedValues.put(TrackingParams.Params.PLAYSTORE_SNAME, playstoreprofile.get("sname"));
            autoTrackedValues.put(TrackingParams.Params.PLAYSTORE_GNAME, playstoreprofile.get("gname"));

        }

        if(res.getBoolean(R.bool.auto_track_playstoremail)) {
            HashMap<String, String> playstoreprofile = HelperFunctions.getUserProfile(app);
            autoTrackedValues.put(TrackingParams.Params.PLAYSTORE_MAIL, playstoreprofile.get("email"));

        }
        if(res.getBoolean(R.bool.auto_track_appversion_name)) {
            autoTrackedValues.put(TrackingParams.Params.APP_VERSION_NAME, HelperFunctions.getAppVersionName(context));

        }
        if(res.getBoolean(R.bool.auto_track_appversion_code)) {
            autoTrackedValues.put(TrackingParams.Params.APP_VERSION_CODE, String.valueOf(HelperFunctions.getAppVersionCode(context)));

        }
        if(res.getBoolean(R.bool.auto_track_preinstalled)) {
            autoTrackedValues.put(TrackingParams.Params.APP_PREINSTALLED, String.valueOf(HelperFunctions.isAppPreinstalled(context)));

        }
        if(res.getBoolean(R.bool.auto_track_advertiserid)) {
            HelperFunctions.getAdvertiserID(app);
        }

        // if the app is started for the first time, the param "one" is 1 otherwise its always 0
        if(HelperFunctions.firstStart(context)) {
            autoTrackedValues.put(TrackingParams.Params.APP_FIRST_START, "1");
            // the old version sets this sharedpreference key, so we do it here as well for compatibility
            SharedPreferences.Editor sharedPreferences = this.context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).edit();
            // if the ever id is null so we set the installation flag = new  for further usage
            sharedPreferences.putString(PREFERENCE_KEY_INSTALLATION_FLAG, "1");
            sharedPreferences.commit();
        } else {
            autoTrackedValues.put(TrackingParams.Params.APP_FIRST_START, "0");
        }

        // for comatilility reasons always add the sampling rate param to the url
        autoTrackedValues.put(TrackingParams.Params.SAMPLING, String.valueOf(sampling));

        // always track the wt everid
        autoTrackedValues.put(TrackingParams.Params.EVERID, getEverId());

        // if the app was updated, send out the update request once
        // TODO: nochmal klären hier wann und wie das mitgesendet wird
        if(res.getBoolean(R.bool.auto_track_updated)) {

        }
    }

    public Tracker getTracker() {
        return tracker;
    }

    public static String getUserAgent() {
        return "Tracking Library " + TRACKING_LIBRARY_VERSION + "(" + HelperFunctions.getOSName() + ";" + HelperFunctions.getOSVersion() + ";" + HelperFunctions.getDevice() + ";" + Locale.getDefault() + ")";
    }

    public String getWebtrekkTrackDomain() {
        return webtrekkTrackDomain;
    }

    public void setWebtrekkTrackDomain(String webtrekkTrackDomain) {
        // TODO: if this.started so if tracking has already started the trackdomain can not be adjusted anymore
        this.webtrekkTrackDomain = webtrekkTrackDomain;
    }

    public String getWebtrekkTrackId() {
        return webtrekkTrackId;
    }

    public void setWebtrekkTrackId(String webtrekkTrackId) {
        // TODO: if this.started so if tracking has already started the trackid can not be adjusted anymore

        this.webtrekkTrackId = webtrekkTrackId;
    }

    public int getSampling() {
        return sampling;
    }

    public void setSampling(int sampling) {
        if (sampling < 0) {
            L.log("setSamplingRate: 'samplingRate' must not be negative.");
            return;
        }
        // TODO: if this.started so if tracking has already started the sampling rate can not be adjusted anymore
        this.sampling = sampling;
    }

    public HashMap<TrackingParams.Params, String> getAutoTrackedValues() {
        return autoTrackedValues;
    }

    public void setAutoTrackedValues(HashMap<TrackingParams.Params, String> autoTrackedValues) {
        this.autoTrackedValues = autoTrackedValues;
    }


    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public boolean isOptout() {
        return isOptout;
    }

    public void setOptout(boolean oo) {
        if (this.isOptout == oo) {
            return;
        }

        this.isOptout = oo;

        SharedPreferences preferences = this.context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCE_KEY_OPTED_OUT, isOptout).commit();

        if (isOptout) {
            this.requestQueue.clear();
        }
    }

    // all activities can place key/values in the global state, all plugins can use this information
    public void addActivityState(String key, String value) {
        this.activityState.put(key, value);
    }

    // this methods clears the activity state and is called when the activity ends
    public void clearActivityState() {
        this.activityState.clear();
    }

    public Resources getRes() {
        return res;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<Plugin> getPlugins() {
        return plugins;
    }

    public String getEverId() {
        SharedPreferences preferences = context.getSharedPreferences(WTrack.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        if (!preferences.contains(PREFERENCE_KEY_EVER_ID)) {
            preferences.edit().putString(WTrack.PREFERENCE_KEY_EVER_ID, HelperFunctions.generateEverid()).commit();
            // for compatibility reasons put the key here for new installation
            preferences.edit().putString(PREFERENCE_KEY_INSTALLATION_FLAG, "1");
        }
        return preferences.getString(PREFERENCE_KEY_EVER_ID, "");
    }

    public static boolean isLoggingEnabled() {
        return L.isLogging();
    }

    public static void setLoggingEnabled(boolean logging) {
        L.setIsLogging(logging);
    }

    public long getInitialSendDelay() {
        return initialSendDelay;
    }

    public void setInitialSendDelay(long initialSendDelay) {
        this.initialSendDelay = initialSendDelay;
    }

    public long getSendDelay() {
        return sendDelay;
    }

    public void setSendDelay(long sendDelay) {
        if (sendDelay < 1000) {
            L.log("setSendDelay: 'sendDelay' must be at least one second.");
            return;
        }
        // TODO: if this.started so if tracking has already started the send delay can not be adjusted anymore
        this.sendDelay = sendDelay;
        this.requestQueue.setSendDelay(sendDelay);
    }

    public String getCurrentActivityName() {
        return this.currentActivity.getClass().getName();
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isSampling() {
        return isSampling;
    }

    public void setIsSampling(boolean isSampling) {
        this.isSampling = isSampling;
    }
}
