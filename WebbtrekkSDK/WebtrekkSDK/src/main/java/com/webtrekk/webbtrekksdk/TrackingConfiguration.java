package com.webtrekk.webbtrekksdk;

import java.util.HashMap;
import java.util.Map;

/**
 * a global configuration class which gets its values from the xml config parser
 * all plugins, activity and other tracking details are configured here
 */
class TrackingConfiguration {

    // the current version of the configuration, used to check if a new one is available
    private int version;

    //global tracking configuration

    private String trackDomain;
    private String trackId;
    private int sampling;
    private int initialSendDelay;
    private int sendDelay;
    private int maxRequests;

    // activitylifycycle callbacks for automated activity tracking
    private boolean autoTracked;

    // auto tracking configuration
    private boolean autoTrackAppUpdate;
    private boolean autoTrackAdvertiserId;
    private boolean autoTrackAppVersionName;
    private boolean autoTrackAppVersionCode;
    private boolean autoTrackAppPreInstalled;
    private boolean autoTrackPlaystoreUsername;
    private boolean autoTrackPlaystoreMail;
    private boolean autoTrackPlaystoreGivenName;
    private boolean autoTrackPlaystoreFamilyName;
    private boolean autoTrackApiLevel;
    private boolean autoTrackScreenorientation;
    private boolean autoTrackConnectionType;
    private boolean autoTrackAdvertismentOptOut;

    // enabled plugins
    private boolean enablePluginHelloWorld;
    private boolean enableRemoteConfiguration;
    private String trackingConfigurationUrl;
    private boolean sendRequestUrlStoreSize;
    //intervall when autotracked start activity is send again
    private int resendOnStartEventTime;


    // global trackingparameter xml values
    private TrackingParameter globalTrackingParameter;

    private Map<String, ActivityConfiguration> activityConfigurations;

    // the customParameter map from the xml configuration
    private Map<String, String> customParameter;

    public TrackingConfiguration() {
        activityConfigurations = new HashMap<>();

    }

    public static class ActivityConfiguration {
        private String className;
        private String mappingName;
        private boolean isAutoTrack;
        private TrackingParameter activityTrackingParameter;

        public ActivityConfiguration(String className, String mappingName, boolean isAutoTrack, TrackingParameter tp) {
            this.className = className;
            this.mappingName = mappingName;
            this.isAutoTrack = isAutoTrack;
            this.activityTrackingParameter = tp;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMappingName() {
            return mappingName;
        }

        public void setMappingName(String mappingName) {
            this.mappingName = mappingName;
        }

        public boolean isAutoTrack() {
            return isAutoTrack;
        }

        public void setIsAutoTrack(boolean isAutoTrack) {
            this.isAutoTrack = isAutoTrack;
        }

        public TrackingParameter getActivityTrackingParameter() {
            return activityTrackingParameter;
        }

        public void setActivityTrackingParameter(TrackingParameter activityTrackingParameter) {
            this.activityTrackingParameter = activityTrackingParameter;
        }
    }

    /**
     * this method validates that the tracking configuration is valid
     *
     * @return
     */
    public boolean validateConfiguration() {
        //TODO: implement validation rules

        // check for mandatory values

        // check for details like valid url, min max values and so on
        return true;
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

    public int getInitialSendDelay() {
        return initialSendDelay;
    }

    public void setInitialSendDelay(int initialSendDelay) {
        this.initialSendDelay = initialSendDelay;
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

    public boolean isSendRequestUrlStoreSize() {
        return sendRequestUrlStoreSize;
    }

    public void setSendRequestUrlStoreSize(boolean sendRequestUrlStoreSize) {
        this.sendRequestUrlStoreSize = sendRequestUrlStoreSize;
    }

    public Map<String, String> getCustomParameter() {
        return customParameter;
    }

    public void setCustomParameter(Map<String, String> customParameter) {
        this.customParameter = customParameter;
    }
}
