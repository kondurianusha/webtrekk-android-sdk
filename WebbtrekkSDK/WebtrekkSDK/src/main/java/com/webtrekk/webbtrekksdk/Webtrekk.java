package com.webtrekk.webbtrekksdk;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The WebtrekkSDK main class, the developer/customer interacts with the SDK through this class.
 */
public final class Webtrekk {


    //name of the preference strings
    public static final String PREFERENCE_FILE_NAME = "webtrekk-preferences";
    public static final String PREFERENCE_KEY_EVER_ID = "everId";
    public static final String PREFERENCE_APP_VERSIONCODE = "appVersion";
    public static final String PREFERENCE_KEY_OPTED_OUT = "optedOut";
    public static final String PREFERENCE_KEY_IS_SAMPLING = "issampling";
    public static final String PREFERENCE_KEY_SAMPLING = "sampling";
    public static final String PREFERENCE_KEY_INSTALLATION_FLAG = "InstallationFlag";
    public static final String PREFERENCE_KEY_CONFIGURATION = "webtrekkTrackingConfiguration";
    public static final String TRACKING_LIBRARY_VERSION = "400";


    private RequestUrlStore requestUrlStore;
    private TrackingConfiguration trackingConfiguration;

    private boolean isSampling;
    private boolean isOptout;

    // the available plugins
    private ArrayList<Plugin> plugins;

    // this always contains the name of the current activity as string, important for auto naming button clicks or other inner class events
    private String currentActivityName;
    // this hashmap contains the automatically collected data which remains the same with every request
    private HashMap<TrackingParameter.Parameter, String> staticAutomaticData;
    //determins the number of currently running activitys
    private int activityCount;

    private ScheduledExecutorService timerService;
    private ExecutorService executorService;
    private Future<?> requestProcessorFuture;

    private Context context;
    // this tracking params allows to add parameters globally to all tracking requests in the configured app
    private TrackingParameter globalTrackingParameter;

    private TrackedActivityLifecycleCallbacks callbacks;

    /**
     * non public constructor to create a Webtrekk Instance as
     * makes use of the Singleton Pattern here.
     */

    Webtrekk() {
    }

    // https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
    private static class SingletonHolder {
        static final Webtrekk webtrekk = new Webtrekk();
    }

    /**
     * public method to get the singleton instance of the webtrekk object,
     * @return webtrekk instance
     */
    public static Webtrekk getInstance() {
        return SingletonHolder.webtrekk;
    }

    /**
     * this initializes the webtrekk tracking configuration, it has to be called only once when the
     * application starts, for example in the Application Class or the Main Activitys onCreate
     * @param c the application context / context of the main activity
     *
     */
    final public void initWebtrekk(final Context c) {
        if (c == null) {
            throw new IllegalArgumentException("no valid context");
        }
        if (this.context != null) {
            throw new IllegalStateException("The initWebtrekk method must be called only once");
        }
        this.context = c;

        initTrackingConfiguration();
        initOptedOut();
        initSampling();
        initStaticAutomaticData();
        initPlugins();
        initTimerService();

        this.requestUrlStore = new RequestUrlStore(context, trackingConfiguration.getMaximumRequests());
        WebtrekkLogging.log("requestUrlStore created: max requests - " + trackingConfiguration.getMaximumRequests());
        WebtrekkLogging.log("tracking initialized");

    }

    final void initTrackingConfiguration() {
        initTrackingConfiguration(null);
    }

    /**
     * initializes the tracking configuration, either from xml, or shared prefs, or remote
     * always takes the newest version, and stores it in the shared preferences for future reference
     * in case it can not get a new remote config and remote config is enabled, the old config is used
     *
     * @param configurationString for unit testing only
     */
    final void initTrackingConfiguration(final String configurationString) {

            // always parse the local raw config version first, this is fallback, default and also the way to fix broken online configs
            //TODO: this could me more elegant by only parsing it when its a new app version which needs to be set anyway
            String trackingConfigurationString;
            if (configurationString == null) {
                try {
                    trackingConfigurationString = HelperFunctions.stringFromStream(context.getResources().openRawResource(R.raw.webtrekk_config));
                } catch (IOException e) {
                    throw new IllegalStateException("can not load xml configuration file, invalid state");
                }
            } else {
                trackingConfigurationString = configurationString;
            }

        try {
            trackingConfiguration = new TrackingConfigurationXmlParser().parse(trackingConfigurationString);
        } catch (Exception e) {
            throw new IllegalStateException("invalid xml configuration file, invalid state");
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        // second check if a newer remote config version is stored locally
        if(sharedPrefs.contains(Webtrekk.PREFERENCE_KEY_CONFIGURATION)) {
            WebtrekkLogging.log("found trackingConfiguration in preferences");
            // in this case we already have a configuration xml stored
            // parse the existing one and check if an update is online available
            trackingConfigurationString = sharedPrefs.getString(Webtrekk.PREFERENCE_KEY_CONFIGURATION, null);
            TrackingConfiguration sharedPreferencetrackingConfiguration = null;
            try {
                sharedPreferencetrackingConfiguration = new TrackingConfigurationXmlParser().parse(trackingConfigurationString);
                if(sharedPreferencetrackingConfiguration.getVersion() > trackingConfiguration.getVersion()) {
                    // in this case there is a newer, so replace th old one
                    trackingConfiguration = sharedPreferencetrackingConfiguration;
                }
            } catch (IOException e) {
                WebtrekkLogging.log("ioexception parsing the configuration string", e);
            } catch(XmlPullParserException e) {
                WebtrekkLogging.log("exception parsing the configuration string", e);
            }


        }
        // third check online for newer versions
        //TODO: maybe store just the version number locally in preferences might reduce some parsing
        //new TrackingConfigurationDownloadTask(this).execute(trackingConfiguration.getTrackingConfigurationUrl());

        WebtrekkLogging.log("saving trackingConfiguration to preferences");
        sharedPrefs.edit().putString(Webtrekk.PREFERENCE_KEY_CONFIGURATION, trackingConfigurationString).commit();



        if(trackingConfiguration != null) {
            WebtrekkLogging.log("xml trackingConfiguration value: trackid - " + trackingConfiguration.getTrackId());
            WebtrekkLogging.log("xml trackingConfiguration value: trackdomain - " + trackingConfiguration.getTrackDomain());
            WebtrekkLogging.log("xml trackingConfiguration value: send_delay - " + trackingConfiguration.getSendDelay());
            WebtrekkLogging.log("xml trackingConfiguration value: initial_send_delay - " + trackingConfiguration.getInitialSendDelay());

            for(TrackingConfiguration.ActivityConfiguration cfg : trackingConfiguration.getActivityConfigurations().values()) {
                WebtrekkLogging.log("xml trackingConfiguration activity for: " + cfg.getClassName() + " mapped to: " + cfg.getMappingName() + " autotracked: " + cfg.isAutoTrack());
            }
        } else {
            WebtrekkLogging.log("error loading the configuration - can not initialize tracking");
            throw new IllegalStateException("could not get valid configuration, invalid state");
        }

        WebtrekkLogging.log("tracking configuration initialized");

    }

    /**
     * load the enabled plugins, this will decided by hand during compile time of the lib to improve performance
     * each customer can that way have a lib with all the features/plugins he needs
     * he has to enable the plugin in the xml to load it here, therefore each plugin needs a unique name
     */
    void initPlugins() {
        plugins = new ArrayList<>();
        if(trackingConfiguration.isHelloWorldPluginEnabed()){
            plugins.add(new HelloWorldPlugin(this));
            WebtrekkLogging.log("loaded plugin: HelloWorldPlugin");
        }
        WebtrekkLogging.log("all plugins loaded");
    }

    /**
     * starts the timer service, it executes after initial send delay for the first time, and then
     * every sendDelay seconds, it processes the stored requests in a separate thread
     */
    void initTimerService() {
        // start the timer service
        timerService = Executors.newSingleThreadScheduledExecutor();
        timerService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                onSendIntervalOver();
            }
        }, trackingConfiguration.getInitialSendDelay(), trackingConfiguration.getSendDelay(), TimeUnit.SECONDS);
        WebtrekkLogging.log("timer service started");
    }

    /**
     * initializes the opt out based on the shared preference settings
     */
    void initOptedOut() {
        SharedPreferences preferences = this.context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        this.isOptout = preferences.getBoolean(PREFERENCE_KEY_OPTED_OUT, false);
        WebtrekkLogging.log("optedOut = " + this.isOptout);
    }

    /**
     * initializes the sampling, which means that if a sampling value X is configured, only
     * every X user data will be tracked, sampling will be stores in the shared prefs once initialized
     * it can be reset with changing the xml config
     */
    void initSampling() {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        if(preferences.contains(PREFERENCE_KEY_IS_SAMPLING)) {
            // key exists so set sampling value and return
            isSampling = preferences.getBoolean(PREFERENCE_KEY_IS_SAMPLING, false);
            // check if the sampling value is unchanged, if so return
            if(preferences.getInt(PREFERENCE_KEY_SAMPLING, -1) == trackingConfiguration.getSampling()) {
                return;
            }
        }
        // from here on they sampling either changed or is missing, so reinitialize it
        SharedPreferences.Editor editor = preferences.edit();
        // calculate if the device is sampling
        if(trackingConfiguration.getSampling()>0) {
            isSampling = new Random().nextInt(trackingConfiguration.getSampling()) == 0;
        } else {
            isSampling = false;
        }
        // store the preference keys if the device is sampling and the sampling value
        editor.putBoolean(PREFERENCE_KEY_IS_SAMPLING, isSampling);
        editor.putInt(PREFERENCE_KEY_SAMPLING, trackingConfiguration.getSampling());
        editor.commit();
        WebtrekkLogging.log("isSampling = " + this.isSampling + ", samplingRate = " + trackingConfiguration.getSampling());
    }

    /**
     * collecting all static data which remains the same for all requests
     * this functions inserts all data into the staticAutomaticData HashMap for which tracking is enabled in the xml
     * this data always stays the same for a device for all trackers if configured once
     * all trackers will append this values to their tracking requests as url/json trackingParameter
     * customers can decide on their own based on their xml tracking config, which values they are interested in
     */
    void initStaticAutomaticData() {
        // collect all static device information which remain the same for all requests
        staticAutomaticData = new HashMap<>();

        staticAutomaticData.put(TrackingParameter.Parameter.SCREEN_RESOLUTION, HelperFunctions.getResolution(context));
        staticAutomaticData.put(TrackingParameter.Parameter.SCREEN_DEPTH, HelperFunctions.getDepth(context));
        staticAutomaticData.put(TrackingParameter.Parameter.TIMEZONE, HelperFunctions.getTimezone());
        staticAutomaticData.put(TrackingParameter.Parameter.USERAGENT, HelperFunctions.getUserAgent());
        staticAutomaticData.put(TrackingParameter.Parameter.DEV_LANG, HelperFunctions.getLanguage());

        if(trackingConfiguration.isAutoTrackApiLevel()) {
            staticAutomaticData.put(TrackingParameter.Parameter.API_LEVEL, HelperFunctions.getAPILevel());

        }

        if(trackingConfiguration.isAutoTrackPlaystoreUsername()) {
            Map<String, String> playstoreprofile = HelperFunctions.getUserProfile(context);
            staticAutomaticData.put(TrackingParameter.Parameter.PLAYSTORE_SNAME, playstoreprofile.get("sname"));
            staticAutomaticData.put(TrackingParameter.Parameter.PLAYSTORE_GNAME, playstoreprofile.get("gname"));

        }

        if(trackingConfiguration.isAutoTrackPlaystoreEmail()) {
            Map<String, String> playstoreprofile = HelperFunctions.getUserProfile(context);
            staticAutomaticData.put(TrackingParameter.Parameter.PLAYSTORE_MAIL, playstoreprofile.get("email"));

        }

        if(trackingConfiguration.isAutoTrackAppversionName()) {
            staticAutomaticData.put(TrackingParameter.Parameter.APP_VERSION_NAME, HelperFunctions.getAppVersionName(context));

        }
        if(trackingConfiguration.isAutoTrackAppversionCode()) {
            staticAutomaticData.put(TrackingParameter.Parameter.APP_VERSION_CODE, String.valueOf(HelperFunctions.getAppVersionCode(context)));

        }
        if(trackingConfiguration.isAutoTrackAppPreinstalled()) {
            staticAutomaticData.put(TrackingParameter.Parameter.APP_PREINSTALLED, String.valueOf(HelperFunctions.isAppPreinstalled(context)));

        }
        if(trackingConfiguration.isAutoTrackAdvertiserId()) {
            initAdvertiserId();
        }

        // if the app is started for the first time, the param "one" is 1 otherwise its always 0
        if(HelperFunctions.firstStart(context)) {
            staticAutomaticData.put(TrackingParameter.Parameter.APP_FIRST_START, "1");
        } else {
            staticAutomaticData.put(TrackingParameter.Parameter.APP_FIRST_START, "0");
        }

        // for comatilility reasons always add the sampling rate param to the url
        staticAutomaticData.put(TrackingParameter.Parameter.SAMPLING, ""+trackingConfiguration.getSampling());

        // always track the wt everid
        staticAutomaticData.put(TrackingParameter.Parameter.EVERID, HelperFunctions.generateEverid());

        // if the app was updated, send out the update request once
        if(trackingConfiguration.isAutoTrackAppUpdate() && HelperFunctions.updated(context)) {
            staticAutomaticData.put(TrackingParameter.Parameter.APP_UPDATE, "1");
        }
        WebtrekkLogging.log("collected static automatic data");
    }

    /**
     * reads the advertiser id and advertiser opt out values for the user,
     * needs the appropiate permissions and playstore lib linked, at least the ad parts
     * works by passing in a reference as it runs in a seperate thread to avoid lags in the main thread,
     */
    private synchronized void initAdvertiserId() {
        // check if playservice sdk is available on that device, TODO: define alternative handling here
        // TODO: define default handling when this values can not be read, maybe cache them in Shared preferences if that is allowed due to opt out
        if( GooglePlayServicesUtil.isGooglePlayServicesAvailable(context) == 0) {
            Thread advertiserIdThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AdvertisingIdClient.Info adInfo = null;
                    try {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                        Webtrekk.getInstance().getStaticAutomaticData().put(TrackingParameter.Parameter.ADVERTISER_ID, adInfo.getId());
                        Webtrekk.getInstance().getStaticAutomaticData().put(TrackingParameter.Parameter.ADVERTISEMENT_OPT_OUT, String.valueOf(adInfo.isLimitAdTrackingEnabled()));
                        //app.getTracker()..getAutoTrackedValues().put(TrackingParams.Params.ADVERTISER_ID, adInfo.getId());
                        //app.getWTRack().getAutoTrackedValues().put(TrackingParams.Params.ADVERTISER_OPTOUT, String.valueOf(adInfo.isLimitAdTrackingEnabled()));

                    } catch (IOException e) {
                        // Unrecoverable error connecting to Google Play services (e.g.,
                        // the old version of the service doesn't support getting AdvertisingId).

                    } catch (GooglePlayServicesNotAvailableException e) {
                        // Google Play services is not available entirely.
                    } catch (GooglePlayServicesRepairableException e) {
                        // maybe will work with another try, recheck
                    } catch (NullPointerException e) {
                        // adinfo was null or could not get the id/optout setting
                    }
                }
            });
            try {
                advertiserIdThread.start();
            } catch (Exception e){
                WebtrekkLogging.log("error getting the advertiser id");
            }


        }
    }


    /**
     * this method collects the automated data which may change with every request
     * @return
     */
    Map<TrackingParameter.Parameter, String> initDynamicAutomaticData() {
        HashMap<TrackingParameter.Parameter, String> dynamicAutomaticData = new HashMap<>();
        dynamicAutomaticData.put(TrackingParameter.Parameter.SCREEN_ORIENTATION, HelperFunctions.getOrientation(context));
        dynamicAutomaticData.put(TrackingParameter.Parameter.CONNECTION_TYPE, HelperFunctions.getConnectionString(context));
        return dynamicAutomaticData;
    }



    /**
     * this functions enables the automatic activity tracking in case its enabled in the configuration
     * @param app application object of the tracked app, can either be a custom one or required by getApplication()
     */
    void initAutoTracking(Application app){
        if(callbacks == null && trackingConfiguration.isAutoTracking()) {
            callbacks = new TrackedActivityLifecycleCallbacks(this);
            app.registerActivityLifecycleCallbacks(callbacks);
        }
    }



    /**
     * this method immediately stops tracking, for example when a user opted out
     * tracking will be in invalid state, until init is called again
     */
    public synchronized  void stopTracking() {
        if(requestUrlStore != null) {
            requestUrlStore.clear();
        }
        activityCount = 0;
    }

    /**
     * checks if logging is enabled
     * @return boolean if logging is enabled
     */
    public static boolean isLoggingEnabled() {
        return WebtrekkLogging.isLogging();
    }

    /**
     * enables the logging for all SDK log outputs
     * @param logging enables/disables the webtrekk logging
     */
    public static void setLoggingEnabled(boolean logging) {
        WebtrekkLogging.setIsLogging(logging);
    }

    public boolean isSampling() {
        return isSampling;
    }

    /**
     * returns the context with which the webtrekk instance was initialized, this can not be changed
     *
     * @return context
     */
    public Context getContext() {
        return context;
    }

    public String getCurrentActivityName() {
        return this.currentActivityName;
    }

    public void setCurrentActivityName(String currentActivityName) {
        this.currentActivityName = currentActivityName;
    }


    /**
     * this is the default tracking method which creates an empty tracking trackingParameter object
     * it only tracks the auto tracked values like lib version, resolution, page name
     */
    public void track() {
        track(new TrackingParameter());
    }

    /**
     * this method gets called when auto tracking is enabled and one of the lifycycle methods is called
     * @param name
     */
    public void autoTrackActivity(String name) {
        //TODO: check call start/stopActivity here as well
        if(!trackingConfiguration.getActivityConfigurations().containsKey(name)) {
            // this activity is not configured
            //TODO: this basicly makes autoTrack obsolete
            return;
        }
        TrackingConfiguration.ActivityConfiguration actConfig = trackingConfiguration.getActivityConfigurations().get(name);
        TrackingParameter tp = new TrackingParameter();
        tp.add(staticAutomaticData);
        //TODO: this values need to be in a custom customer defined parameter as webtrekk has none for it
        tp.add(initDynamicAutomaticData());
        tp.add(TrackingParameter.Parameter.TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        TrackingRequest request;

        // apply mapping
        tp.add(TrackingParameter.Parameter.ACTIVITY_NAME, actConfig.getMappingName());

        request = new TrackingRequest(tp, trackingConfiguration);
        addRequest(request);
    }

    /**
     * allows tracking of a requests with a custom set of trackingparams
     *
     * @param tp the TrackingParams for the request
     * @throws IllegalStateException when the SDK has not benn initialized, activity was not started or the trackingParameter are invalid
     */
    public void track(final TrackingParameter tp) {
        if (requestUrlStore == null || trackingConfiguration == null) {
            throw new IllegalStateException("webtrekk has not been initialized");
        }
        if (activityCount == 0) {
            throw new IllegalStateException("no running activity, call startActivity first");
        }
        if(tp == null) {
            throw new IllegalStateException("TrackingParams is null");
        }

        // use the automatic name in case no activity name is given
        // for calls from class methods this must be overwritten
        //if(!tp.getTparams().containsKey(TrackingParams.Params.ACTIVITY_NAME)) {
        // hack for now, reflection not possible, Thread.currentThread().getStackTrace()[2].getClassName() is slower, custom security maanger to much
        // automatically adds the name of the calling activity or class to the trackingparams
        //String activity_name = new Throwable().getStackTrace()[2].getClassName();
        //tp.add(TrackingParams.Params.ACTIVITY_NAME, activity_name);
        //}
        // other way was cooler, but requirements where to allow setting it always manually
        tp.add(TrackingParameter.Parameter.ACTIVITY_NAME, currentActivityName);
        tp.add(staticAutomaticData);
        //TODO: this values need to be in a custom customer defined parameter as webtrekk has none for it?
        tp.add(initDynamicAutomaticData());
        // add the global tracking parameter, this overrides any custom values which may be set
        if(globalTrackingParameter!=null) {
            tp.add(globalTrackingParameter);
        }
        tp.add(TrackingParameter.Parameter.TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        TrackingRequest request = new TrackingRequest(tp, trackingConfiguration);
        addRequest(request);
    }

    /**
     * Stores the generated URLS of the requests in the local RequestUrlStore until they are send
     * by the timer function. It is also responsible for executing the plugins before and after the request
     * the plugins will be always executed no matter if the user opted out or not
     *
     * @param request the Tracking Request
     */
    private void addRequest(TrackingRequest request)  {
        //TODO: plugins have no caching actually, they get executed when the event happens, make sure this is the desired behaviour
        // execute the before plugin functions
        for(Plugin p: plugins){
            p.before_request(request);
        }
        // only track when not opted out, but always execute the plugins
        if(!isOptout()) {
            requestUrlStore.add(request.getUrlString());
        }

        // execute the after_request plugin functions
        for(Plugin p: plugins){
            p.after_request(request);
        }
    }

    /**
     * sends an update track event when the app was updated
     */
    public void trackUpdate() {
        if(HelperFunctions.updated(context)) {
            WebtrekkLogging.log("is update");
            TrackingParameter tp = new TrackingParameter();
            tp.add(TrackingParameter.Parameter.ACTIVITY_NAME, "update");
            //tp.add(TrackingParams.Params.APP_UPDATE, "1");
            track(tp);
        }
    }


    /**
     * this developer has to call this function each time a new activity starts, except when he uses auto tracking
     * best place to call this is during the activitys onStart method, it also allows overriding the
     * current activity name, which gets tracked
     *
     * @param ActivityName a string containing the name of the activity
     */
    public void startActivity(String ActivityName) {
        if (requestUrlStore == null || trackingConfiguration == null) {
            throw new IllegalStateException("webtrekk has not been initialized");
        }
        activityCount++;
        this.currentActivityName = ActivityName;
        if(activityCount == 1) {
            onFirstActivityStart();
        }
    }

    /**
     * this method gets called when the first activity of the application has started
     * it loads the old requests from the backupfile and trys to send them
     *
     */
    private void onFirstActivityStart() {
        requestUrlStore.loadRequestsFromFile();
        // remove the old backupfile after the requests are loaded into memory/requestUrlStore
        requestUrlStore.deleteRequestsFile();
        onSendIntervalOver();
    }

    /**
     * this has to be called in every Activitys onStop method, that way the SDk can track the current
     * open activities and knows when to exit
     */
    public void stopActivity() {
        if (requestUrlStore == null || trackingConfiguration == null) {
            throw new IllegalStateException("webtrekk has not been initialized");
        }
        if (activityCount == 0) {
            throw new IllegalStateException("activity has not been started yet, call startActivity");
        }
        activityCount--;
        if(activityCount == 0) {
            onLastActivityStop();
        }
    }

    /**
     * this method gets called when the last activtiy closes, it trys so send the remaining requests
     * and store the rest if sending fails
     */
    private void onLastActivityStop() {
        onSendIntervalOver();
        requestUrlStore.saveRequestsToFile();
    }

    /**
     * this method gets called whenever the send delay is over, it executes the requesthandler in a
     * new thread
     */
    void onSendIntervalOver() {
        WebtrekkLogging.log("onSendIntervalOver: activity count: " + activityCount + " request urls: " + requestUrlStore.size());
        if(requestUrlStore.size() > 0  && (requestProcessorFuture == null || requestProcessorFuture.isDone())) {
            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
            }
            requestProcessorFuture = executorService.submit(new RequestProcessor(requestUrlStore));
        }
    }

    void setContext(Context context) {
        this.context = context;
    }

    Map<TrackingParameter.Parameter, String> getStaticAutomaticData() {
        return staticAutomaticData;
    }


    public boolean isOptout() {
        return isOptout;
    }

    /**
     * this method is for the opt out switcht, when called it will set the shared preferences of opt out
     * and also stops tracking in case the user opts out
     * @param oo boolean value indicating if the user opted out or not
     */
    public void setOptout(boolean oo) {
        if (this.isOptout == oo) {
            return;
        }
        this.isOptout = oo;

        SharedPreferences preferences = this.context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(PREFERENCE_KEY_OPTED_OUT, isOptout).commit();
    }

    /**
     * allows to set global tracking parameter which will be added to all requests
     * @return
     */
    public TrackingParameter getGlobalTrackingParameter() {
        return globalTrackingParameter;
    }

    public void setGlobalTrackingParameter(TrackingParameter globalTrackingParameter) {
        this.globalTrackingParameter = globalTrackingParameter;
    }


    public String getEverId() {
        return HelperFunctions.getEverId(context);
    }
    /**
     * for unit testing only
     * @param requestUrlStore
     */
    void setRequestUrlStore(RequestUrlStore requestUrlStore) {
        this.requestUrlStore = requestUrlStore;
    }

    /**
     * for unit testing only
     * @return
     */
    RequestUrlStore getRequestUrlStore() {
        return requestUrlStore;
    }
    /**
     * for unit testing only
     * @return
     */
    int getActivityCount() {
        return activityCount;
    }
    /**
     * for unit testing only
     * @return
     */
    ScheduledExecutorService getTimerService() {
        return timerService;
    }
    /**
     * for unit testing only
     * @return
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }
    /**
     * for unit testing only
     * @return
     */
    public Future<?> getRequestProcessorFuture() {
        return requestProcessorFuture;
    }
    /**
     * for unit testing only
     * @return
     */
    List<Plugin> getPlugins() {
        return plugins;
    }

    /**
     * for unittesting only
     * @return
     */
    TrackingConfiguration getTrackingConfiguration() {
        return trackingConfiguration;
    }

    /**
     * for unit testing only
     * @param trackingConfiguration
     */
    void setTrackingConfiguration(TrackingConfiguration trackingConfiguration) {
        this.trackingConfiguration = trackingConfiguration;
    }
}
