package com.webtrekk.android.tracking;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * @deprecated  Use the new Tracker Interface instead
 *    This method is expected to be removed in future Versions
 *    of this API. Please check the documentation of API Version 4
 *    on how to use the new Tracking Interface.
 */
@Deprecated
class Core {

    public static final String VERSION = "3.2";

    private static final String TAG = Core.class.getName();

    private static final String PARAMETER_ACTION_ID = "ct";
    private static final String PARAMETER_EVER_ID = "eid";
    private static final String PARAMETER_MOBILE_TIMESTAMP = "mts";
    private static final String PARAMETER_END_OF_REQUEST = "eor";
    /**
     * format:
     * <version>,<contentId>,<javascript available>,
     * <screen resolution>,<screen color depth>,
     * <cookies available>,<timestamp>,<referer>,
     * <browser size>,<java available>
     */
    private static final String PARAMETER_PIXEL = "p";
    private static final String PARAMETER_SAMPLING_RATE = "ps";
    private static final String PARAMETER_USER_AGENT = "X-WT-UA";
    private static final String PARAMETER_FIRST_APP_START = "one";
    private static final String PIXEL_VERSION = "302";
    private static final String NEW_INSTALLATION_FLAG = "1";

    private static final String INSTALL_REFERRER_PARAMS_MC = "mc";

    private static final String PREFERENCE_KEY_IS_SAMPLING = "sampling";
    private static final String PREFERENCE_KEY_SAMPLING_RATE = "samplingRate";
    private static final String PREFERENCE_KEY_OPTED_OUT = "optedOut";
    private static final String PREFERENCE_KEY_EVER_ID = "everId";
    private static final String PREFERENCES_FILE_NAME = "webtrekk-preferences";
    private static final String PREFERENCE_KEY_INSTALLATION_FLAG = "InstallationFlag";
    private static final String PREFERENCE_KEY_APP_VERSION = "appVersion";

    private static final long INITIAL_SEND_DELAY = 5 * 1000; // 5 seconds
    private static final long DEFAULT_SEND_DELAY = 5 * 60 * 1000; // 5 minutes

    private static final String PARAMETER_UPDATE_PAGE_CONTENT_ID = "update";

    private Context context;
    private WeakReference<Activity> currentActivity;
    private String everId;
    private boolean isFirstAppStart;
    private boolean isSampling;
    private boolean loggingEnabled;
    private boolean optedOut;
    //private final RequestQueue queue;
    private int samplingRate;
    private String serverUrl;
    private boolean started;
    private String trackId;
    private String userAgent;
    private String appVersion;
    private String appVersionParameter;
    // for migration and compatibility purposes
    private Tracker tracker = null;
    private WTrack wtrack;

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public Core() {
        //this.queue = new RequestQueue(this, INITIAL_SEND_DELAY, DEFAULT_SEND_DELAY);

        this.setupUserAgent();
    }

    public void activityStart(Activity activity) {
        if (activity == null) {
            this.log("activityStart: 'activity' must not be null.");
            return;
        }

        if (!this.started) {
            this.setContext(activity);
        }

        if (this.serverUrl == null) {
            this.log("activityStart: 'serverUrl' was not set.");
            return;
        }
        if (this.trackId == null) {
            this.log("activityStart: 'trackId' was not set.");
            return;
        }

        if (this.currentActivity != null) {
            Activity currentActivity = this.currentActivity.get();
            if (currentActivity == activity) {
                return;
            }
        }

        this.currentActivity = new WeakReference<Activity>(activity);

        if (!this.started) {
            this.setupSampling();
            this.getEverId();

            this.started = true;
            this.log("activityStart: Started tracking.");

            this.trackReferrer();

            if(this.appVersionParameter != null) {
                this.trackUpdate();
            }
        }
    }

    public void activityStop(Activity activity) {
        if (activity == null) {
            this.log("activityStop: 'activity' must not be null.");
            return;
        }

        if (this.currentActivity == null
                || this.currentActivity.get() != activity) {
            return;
        }

        wtrack.getRequestQueue().saveBackup();

        this.currentActivity = null;
        this.isSampling = false;

        this.started = false;

        this.log("activityStop: Stopped tracking.");
    }

    public Context getContext() {
        return this.context;
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public String getEverId() {
        return wtrack.getEverId();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    private String getAppVersion() {
        String appVersion = this.appVersion;
        if (appVersion == null) {
            if (this.context == null) {
                this.log("getAppVersion: 'context' was not set.");
                return null;
            }

            SharedPreferences preferences = this.context.getSharedPreferences(
                    PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
            appVersion = preferences
                    .getString(PREFERENCE_KEY_APP_VERSION, null);
            this.appVersion = appVersion;
        }

        return appVersion;
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    private void setAppVersionFromPackage() {
        if (this.context == null) {
            this.log("setAppVersionFromPackage: 'context' was not set.");
        }

        else {
            String appVersion = null;
            appVersion = this.getPackageActualVersion();

            if (appVersion != null )
            {
                SharedPreferences preferences = this.context.getSharedPreferences(
                        PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);

                preferences.edit()
                        .putString(PREFERENCE_KEY_APP_VERSION, appVersion)
                        .commit();
            }
            this.appVersion = appVersion;
        }
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    private String getPackageActualVersion()
    {
        String actualVersion = null;
        if (this.context == null) {
            this.log("getPackageActualVersion: 'context' was not set.");
        }
        else
        {
            PackageManager manager = this.context.getPackageManager();
            PackageInfo pinfo;
            try {
                pinfo = manager
                        .getPackageInfo(this.context.getPackageName(), 0);
                actualVersion = pinfo.versionName;

            } catch (NameNotFoundException e) {
                this.log("getPackageActualVersion: Error reading the App Version."
                        + e.getMessage());
            }
        }

        return actualVersion;
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public boolean isThisVersionAnUpdate()
    {
        // this doesnt work as expected, but left untouched for now, TODO: check in detail if its not comatible with the new one
        boolean isUpdate = false;
        String appVersion = this.getAppVersion();
        // lookup for the Version Information in prefrences
        // no information yet
        if (appVersion == null)
        {
            this.setAppVersionFromPackage();
            //  we should look for previous installations
            if (this.isNewInstallation())
            {
                isUpdate = false;
            }
            else
            {
                isUpdate = true;
            }
        }
        // there exist information about the version in prefrences
        else{
            // compare this to the actual version of the package
            String actualVersion = this.getPackageActualVersion();
            if (appVersion != null && appVersion.equals(actualVersion))
            {
                isUpdate = false;
            }
            else
            {
                isUpdate = true;
                this.setAppVersionFromPackage();
            }
        }

        return isUpdate;
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    private boolean isNewInstallation()
    {
        return HelperFunctions.isNewInstallation(wtrack.getContext());
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public boolean isThisAppPreInstalled()
    {
        return HelperFunctions.isAppPreinstalled(wtrack.getContext());
    }


    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public int getSamplingRate() {
        return wtrack.getSampling();
    }
    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public long getSendDelay() {
        return wtrack.getSendDelay();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public String getServerUrl() {
        return wtrack.getWebtrekk_track_domain();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public String getTrackId() {
        return wtrack.getWebtrekk_track_id();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public String getUserAgent() {
        return WTrack.getUserAgent();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public boolean isLoggingEnabled() {
        return WTrack.isLoggingEnabled();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public boolean isOptedOut() {
        return wtrack.isOptout();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public void log(String message) {
        L.log(message);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    public void log(String message, Throwable throwable) {
            L.log(message, throwable);
    }


    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void setContext(Context context) {
        if (this.started) {
            this.log("setContext: Cannot set context after tracking was started.");
            return;
        }

        if (context != null) {
            context = context.getApplicationContext();
        }

        this.context = context;
        // create wtrack object here when the context is given
        wtrack =  WTrack.getInstance(context);

        if (context != null) {
            this.setupOptedOut();
        }
    }
    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void setLoggingEnabled(boolean loggingEnabled) {
        WTrack.setLoggingEnabled(loggingEnabled);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void setOptedOut(boolean optedOut) {
        wtrack.setOptout(optedOut);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void setSamplingRate(int samplingRate) {
        wtrack.setSampling(samplingRate);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void setSendDelay(long sendDelay) {
        wtrack.setSendDelay(sendDelay);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void setServerUrl(String serverUrl) {
        wtrack.setWebtrekk_track_domain(serverUrl);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void setTrackId(String trackId) {
        wtrack.setWebtrekk_track_id(trackId);
    }

    public void setAppVersionParameter(String appVersionParameter){
        this.appVersionParameter = appVersionParameter;
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    private void setupOptedOut() {
        wtrack.setupOptedOut();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    private void setupSampling() {
        wtrack.setupSampling();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    private void setupUserAgent() {
        // this data is in the auto data hashmap so do nothing here anymore
        return;
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void trackAction(String pageId, String actionId) {
        this.trackAction(pageId, actionId, null);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void trackAction(String pageId, String actionId,
                            Map<String, String> data) {
        if (pageId == null) {
            this.log("trackAction: 'pageId' must not be null.");
            return;
        }
        if (actionId == null) {
            this.log("trackAction: 'actionId' must not be null.");
            return;
        }

        data = data != null ? data : new HashMap<String, String>();
        data.put(PARAMETER_ACTION_ID, actionId);

        this.trackPage(pageId, data);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void trackEvent(Map<String, String> data) {
        if (!this.started) {
            this.log("trackEvent: Cannot track event as tracking is not started. Did you forget to call activityStart()?");
            return;
        }

        if (this.optedOut || !this.isSampling) {
            return;
        }

        Map<String, String> parameters = data != null ? new HashMap<String, String>(data) : new HashMap<String, String>();
        parameters.put(PARAMETER_EVER_ID, this.everId);
        parameters.put(PARAMETER_SAMPLING_RATE, Integer.toString(this.samplingRate));
        parameters.put(PARAMETER_USER_AGENT, this.userAgent);

        if (this.appVersion == null)
        {
            this.appVersion = getAppVersion();
        }
        if (this.appVersionParameter != null)
        {
            parameters.put(this.appVersionParameter, this.appVersion);
        }


        if (this.isFirstAppStart)
        {
            parameters.put(PARAMETER_FIRST_APP_START, "1");
            this.isFirstAppStart = false;
        }
        else {
            parameters.put(PARAMETER_FIRST_APP_START, "0");
        }


        boolean appendedParameters = false;

        StringBuilder url = new StringBuilder();
        url.append(this.serverUrl);
        if (!this.serverUrl.endsWith("/")) {
            url.append('/');
        }
        url.append(this.trackId);
        url.append("/wt");

        String pixel = parameters.get(PARAMETER_PIXEL);
        if (pixel != null) {
            // pixel parameter must come first if present
            // pixel parameter must not be URL-encoded - its parts are already
            // encoded

            parameters.remove(PARAMETER_PIXEL);

            url.append('?');
            url.append(HelperFunctions.urlEncode(PARAMETER_PIXEL));
            url.append('=');
            url.append(pixel);

            appendedParameters = true;
        }

        for (String parameterName : parameters.keySet()) {
            String parameterValue = parameters.get(parameterName);
            if (parameterValue == null) {
                continue;
            }

            if (appendedParameters) {
                url.append('&');
            } else {
                url.append('?');
                appendedParameters = true;
            }

            url.append(HelperFunctions.urlEncode(parameterName));
            url.append('=');
            url.append(HelperFunctions.urlEncode(parameterValue));
        }

        // UTC(GMT) Timestamp in Millisekunden (vergangene Zeit in Millisekunden seit dem 1. Januar 1970, 0:00 Uhr UTC)
        url.append("&" + PARAMETER_MOBILE_TIMESTAMP + "=" + System.currentTimeMillis());
        url.append("&" + PARAMETER_END_OF_REQUEST + "=1");


        // basicly we have two choices here either, create a TrackingParam object for this, or directly add the old url from here
        // to the requests queue, this works at least until plugins are used
        // second one is easier, as it requires no changes to this code
        wtrack.getRequestQueue().addUrl(url.toString());
    }

    public MediaSession trackMedia(String mediaId, int duration,
                                   int initialPosition) {
        return this.trackMedia(mediaId, duration, initialPosition, null);
    }

    public MediaSession trackMedia(String mediaId, int duration,
                                   int initialPosition, MediaCategories categories) {
        if (mediaId == null) {
            this.log("trackMedia: 'mediaId' must not be null.");
            return null;
        }
        if (duration < 0) {
            this.log("trackMedia: 'duration' must not be negative.");
            return null;
        }
        if (initialPosition < 0) {
            this.log("trackMedia: 'initialPosition' must not be negative.");
            return null;
        }
        if (!this.started) {
            this.log("trackMedia: Cannot track event as tracking is not started. Did you forget to call activityStart()?");
            return null;
        }

        return new MediaSession(this, mediaId, duration, initialPosition,
                categories);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void trackPage(String pageId) {
        this.trackPage(pageId, null);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public void trackPage(String pageId, Map<String, String> data) {
        if (pageId == null) {
            this.log("trackPage: 'pageId' must not be null.");
            return;
        }

        String pixel = PIXEL_VERSION + "," + HelperFunctions.urlEncode(pageId) + ",0,0,0,0," + System.currentTimeMillis();

        data = data != null ? data : new HashMap<String, String>();
        data.put(PARAMETER_PIXEL, pixel);
        this.trackEvent(data);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    private void trackUpdate(){
        wtrack.getTracker().trackUpdate();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    private void trackReferrer() {
        wtrack.getTracker().trackReferrer();
    }

}

