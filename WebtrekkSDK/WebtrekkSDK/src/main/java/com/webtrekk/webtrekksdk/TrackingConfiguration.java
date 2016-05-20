package com.webtrekk.webtrekksdk;

import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * a global configuration class which gets its values from the xml config parser
 * all plugins, activity and other tracking details are configured here
 */
public class TrackingConfiguration {
    // the current version of the configuration, used to check if a new one is available
    private int version = 0;

    //global tracking configuration

    private String trackDomain;
    private String trackId;
    private int sampling = 0;
    private int sendDelay = 300;
    private int maxRequests = 5000;

    // activitylifycycle callbacks for automated activity tracking
    private boolean autoTracked = true;

    //special test mode
    private boolean mTestMode;

    // auto tracking configuration
    private boolean autoTrackAppUpdate = true;
    private boolean autoTrackAdvertiserId = true;
    private boolean autoTrackAppVersionName = true;
    private boolean autoTrackAppVersionCode = true;
    private boolean autoTrackAppPreInstalled = true;
    private boolean autoTrackPlaystoreUsername = false;
    private boolean autoTrackPlaystoreMail = false;
    private boolean autoTrackPlaystoreGivenName = false;
    private boolean autoTrackPlaystoreFamilyName = false;
    private boolean autoTrackApiLevel = true;
    private boolean autoTrackScreenorientation = true;
    private boolean autoTrackConnectionType = true;
    private boolean autoTrackAdvertismentOptOut= true;

    // enabled plugins
    private boolean enablePluginHelloWorld;
    private boolean enableRemoteConfiguration = false;
    private String trackingConfigurationUrl;
    private boolean autoTrackRequestUrlStoreSize = true;
    //intervall when autotracked start activity is send again
    private int resendOnStartEventTime = 30;
    private boolean mErrorLogEnable = false;
    private int mErrorLogLevel = 3;


    // global trackingparameter xml values
    private TrackingParameter globalTrackingParameter;
    //global contant trackingparameter
    private TrackingParameter constGlobalTrackingParameter;

    private Map<String, ActivityConfiguration> activityConfigurations;

    // the customParameter map from the xml configuration
    private Map<String, String> customParameter;

    public TrackingConfiguration() {
        activityConfigurations = new HashMap<String, ActivityConfiguration>();
        customParameter = new HashMap<String, String>();
    }

    /**
     * this method validates that the tracking configuration is valid
     *
     * @return
     */
    public boolean validateConfiguration() {
        //TODO: implement validation rules
        boolean valid = true;
        if(sendDelay < 10 && !mTestMode) {
            WebtrekkLogging.log("invalid sendDelay Value");
            valid = false;
        }
        if(sampling < 0) {
            WebtrekkLogging.log("invalid sampling Value");
            valid = false;
        }
        if(maxRequests < 100) {
            WebtrekkLogging.log("invalid maxRequests Value");
            valid = false;
        }

        // check for mandatory values
        if(trackId == null || trackId.isEmpty() || trackId.length() < 5) {
            WebtrekkLogging.log("missing value: trackId");
            valid = false;
        }
        // make sure a trackdomain is configured
        if(trackDomain == null || trackDomain.isEmpty() || trackDomain.length() < 5) {
            WebtrekkLogging.log("missing value: trackId");
            valid = false;
        }
        //try if its a valid url
        try {
            new URL(trackDomain);
        } catch (MalformedURLException e) {
            WebtrekkLogging.log("invalid trackDomain Value");
            valid = false;
        }


        // check for details like valid url, min max values and so on
        return valid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTrackDomain() {
        return trackDomain;
    }

    public void setTrackDomain(String trackDomain) {
        this.trackDomain = trackDomain;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public int getSampling() {
        return sampling;
    }

    public void setSampling(int sampling) {
        this.sampling = sampling;
    }

    public int getSendDelay() {
        return sendDelay;
    }

    public void setSendDelay(int sendDelay) {
        this.sendDelay = sendDelay;
    }

    public int getResendOnStartEventTime() {
        return resendOnStartEventTime;
    }

    public void setResendOnStartEventTime(int resendOnStartEventTime) {
        this.resendOnStartEventTime = resendOnStartEventTime;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }



    public Map<String, ActivityConfiguration> getActivityConfigurations() {
        return activityConfigurations;
    }

    public void setActivityConfigurations(Map<String, ActivityConfiguration> activityConfigurations) {
        this.activityConfigurations = activityConfigurations;
    }


    public String getTrackingConfigurationUrl() {
        return trackingConfigurationUrl;
    }

    public void setTrackingConfigurationUrl(String trackingConfigurationUrl) {
        this.trackingConfigurationUrl = trackingConfigurationUrl;
    }

    public boolean isAutoTracked() {
        return autoTracked;
    }

    public void setIsAutoTracking(boolean isAutoTracking) {
        this.autoTracked = isAutoTracking;
    }

    public void setTestMode(boolean value){
        mTestMode = value;
    }

    public boolean isTestMode(){
        return mTestMode;
    }

    public boolean isErrorLogEnable() {return mErrorLogEnable;}
    public void setErrorLogEnable(boolean value) {mErrorLogEnable = value;}

    public int getErrorLogLevel() {return mErrorLogLevel;}
    public void setErrorLogLevel(int value) {mErrorLogLevel = value;}

    public TrackingParameter getGlobalTrackingParameter() {
        return globalTrackingParameter;
    }

    public void setGlobalTrackingParameter(TrackingParameter globalTrackingParameter) {
        this.globalTrackingParameter = globalTrackingParameter;
    }

    public void setAutoTracked(boolean autoTracked) {
        this.autoTracked = autoTracked;
    }

    public boolean isAutoTrackAppUpdate() {
        return autoTrackAppUpdate;
    }

    public void setAutoTrackAppUpdate(boolean autoTrackAppUpdate) {
        this.autoTrackAppUpdate = autoTrackAppUpdate;
    }

    public boolean isAutoTrackAdvertiserId() {
        return autoTrackAdvertiserId;
    }

    public void setAutoTrackAdvertiserId(boolean autoTrackAdvertiserId) {
        this.autoTrackAdvertiserId = autoTrackAdvertiserId;
    }

    public boolean isAutoTrackAppVersionName() {
        return autoTrackAppVersionName;
    }

    public void setAutoTrackAppVersionName(boolean autoTrackAppVersionName) {
        this.autoTrackAppVersionName = autoTrackAppVersionName;
    }

    public boolean isAutoTrackAppVersionCode() {
        return autoTrackAppVersionCode;
    }

    public void setAutoTrackAppVersionCode(boolean autoTrackAppVersionCode) {
        this.autoTrackAppVersionCode = autoTrackAppVersionCode;
    }

    public boolean isAutoTrackAppPreInstalled() {
        return autoTrackAppPreInstalled;
    }

    public void setAutoTrackAppPreInstalled(boolean autoTrackAppPreInstalled) {
        this.autoTrackAppPreInstalled = autoTrackAppPreInstalled;
    }

    public boolean isAutoTrackPlaystoreUsername() {
        return autoTrackPlaystoreUsername;
    }

    public void setAutoTrackPlaystoreUsername(boolean autoTrackPlaystoreUsername) {
        this.autoTrackPlaystoreUsername = autoTrackPlaystoreUsername;
    }

    public boolean isAutoTrackPlaystoreMail() {
        return autoTrackPlaystoreMail;
    }

    public void setAutoTrackPlaystoreMail(boolean autoTrackPlaystoreMail) {
        this.autoTrackPlaystoreMail = autoTrackPlaystoreMail;
    }

    public boolean isAutoTrackPlaystoreGivenName() {
        return autoTrackPlaystoreGivenName;
    }

    public void setAutoTrackPlaystoreGivenName(boolean autoTrackPlaystoreGivenName) {
        this.autoTrackPlaystoreGivenName = autoTrackPlaystoreGivenName;
    }

    public boolean isAutoTrackPlaystoreFamilyName() {
        return autoTrackPlaystoreFamilyName;
    }

    public void setAutoTrackPlaystoreFamilyName(boolean autoTrackPlaystoreFamilyName) {
        this.autoTrackPlaystoreFamilyName = autoTrackPlaystoreFamilyName;
    }

    public boolean isAutoTrackApiLevel() {
        return autoTrackApiLevel;
    }

    public void setAutoTrackApiLevel(boolean autoTrackApiLevel) {
        this.autoTrackApiLevel = autoTrackApiLevel;
    }

    public boolean isAutoTrackScreenorientation() {
        return autoTrackScreenorientation;
    }

    public void setAutoTrackScreenorientation(boolean autoTrackScreenorientation) {
        this.autoTrackScreenorientation = autoTrackScreenorientation;
    }

    public boolean isAutoTrackConnectionType() {
        return autoTrackConnectionType;
    }

    public void setAutoTrackConnectionType(boolean autoTrackConnectionType) {
        this.autoTrackConnectionType = autoTrackConnectionType;
    }

    public boolean isAutoTrackAdvertismentOptOut() {
        return autoTrackAdvertismentOptOut;
    }

    public void setAutoTrackAdvertismentOptOut(boolean autoTrackAdvertismentOptOut) {
        this.autoTrackAdvertismentOptOut = autoTrackAdvertismentOptOut;
    }

    public boolean isEnablePluginHelloWorld() {
        return enablePluginHelloWorld;
    }

    public void setEnablePluginHelloWorld(boolean enablePluginHelloWorld) {
        this.enablePluginHelloWorld = enablePluginHelloWorld;
    }

    public boolean isEnableRemoteConfiguration() {
        return enableRemoteConfiguration;
    }

    public void setEnableRemoteConfiguration(boolean enableRemoteConfiguration) {
        this.enableRemoteConfiguration = enableRemoteConfiguration;
    }

    public boolean isAutoTrackRequestUrlStoreSize() {
        return autoTrackRequestUrlStoreSize;
    }

    public void setAutoTrackRequestUrlStoreSize(boolean autoTrackRequestUrlStoreSize) {
        this.autoTrackRequestUrlStoreSize = autoTrackRequestUrlStoreSize;
    }

    public Map<String, String> getCustomParameter() {
        return customParameter;
    }

    public void setCustomParameter(Map<String, String> customParameter) {
        this.customParameter = customParameter;
    }

    public TrackingParameter getConstGlobalTrackingParameter() {
        return constGlobalTrackingParameter;
    }

    public void setConstGlobalTrackingParameter(TrackingParameter constGlobalTrackingParameter) {
        this.constGlobalTrackingParameter = constGlobalTrackingParameter;
    }
}
