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
    private String trackingConfigurationUrl;
    private String trackDomain;
    private String trackId;
    private int sampling;
    private int initialSendDelay;
    private int sendDelay;
    private int maximumRequests;

    // auto tracking configuration
    boolean isAutoTrackLanguage;
    boolean isAutoTrackApiLevel;
    boolean isAutoTrackPlaystoreUsername;
    boolean isAutoTrackPlaystoreEmail;
    boolean isAutoTrackAppversionName;
    boolean isAutoTrackAppversionCode;
    boolean isAutoTrackAppPreinstalled;
    boolean isAutoTrackAppUpdate;
    boolean isAutoTrackAdvertiserId;
    boolean isAutoTrackAdvertisingOptOut;

    // enabled plugins
    boolean isHelloWorldPluginEnabed;

    // activitylifycycle callbacks for automated activity tracking
    boolean isAutoTracking;


    private Map<String, ActivityConfiguration> activityConfigurations;

    public TrackingConfiguration() {
        activityConfigurations = new HashMap<>();
    }

    public static class ActivityConfiguration {
        private String className;
        private String mappingName;
        private boolean isAutoTrack;

        public ActivityConfiguration(String className, String mappingName, boolean isAutoTrack) {
            this.className = className;
            this.mappingName = mappingName;
            this.isAutoTrack = isAutoTrack;
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

    public int getMaximumRequests() {
        return maximumRequests;
    }

    public void setMaximumRequests(int maximumRequests) {
        this.maximumRequests = maximumRequests;
    }

    public boolean isAutoTrackLanguage() {
        return isAutoTrackLanguage;
    }

    public void setIsAutoTrackLanguage(boolean isAutoTrackLanguage) {
        this.isAutoTrackLanguage = isAutoTrackLanguage;
    }

    public boolean isAutoTrackApiLevel() {
        return isAutoTrackApiLevel;
    }

    public void setIsAutoTrackApiLevel(boolean isAutoTrackApiLevel) {
        this.isAutoTrackApiLevel = isAutoTrackApiLevel;
    }

    public boolean isAutoTrackPlaystoreUsername() {
        return isAutoTrackPlaystoreUsername;
    }

    public void setIsAutoTrackPlaystoreUsername(boolean isAutoTrackPlaystoreUsername) {
        this.isAutoTrackPlaystoreUsername = isAutoTrackPlaystoreUsername;
    }

    public boolean isAutoTrackPlaystoreEmail() {
        return isAutoTrackPlaystoreEmail;
    }

    public void setIsAutoTrackPlaystoreEmail(boolean isAutoTrackPlaystoreEmail) {
        this.isAutoTrackPlaystoreEmail = isAutoTrackPlaystoreEmail;
    }

    public boolean isAutoTrackAppversionName() {
        return isAutoTrackAppversionName;
    }

    public void setIsAutoTrackAppversionName(boolean isAutoTrackAppversionName) {
        this.isAutoTrackAppversionName = isAutoTrackAppversionName;
    }

    public boolean isAutoTrackAppversionCode() {
        return isAutoTrackAppversionCode;
    }

    public void setIsAutoTrackAppversionCode(boolean isAutoTrackAppversionCode) {
        this.isAutoTrackAppversionCode = isAutoTrackAppversionCode;
    }

    public boolean isAutoTrackAppPreinstalled() {
        return isAutoTrackAppPreinstalled;
    }

    public void setIsAutoTrackAppPreinstalled(boolean isAutoTrackAppPreinstalled) {
        this.isAutoTrackAppPreinstalled = isAutoTrackAppPreinstalled;
    }

    public boolean isAutoTrackAppUpdate() {
        return isAutoTrackAppUpdate;
    }

    public void setIsAutoTrackAppUpdate(boolean isAutoTrackAppUpdate) {
        this.isAutoTrackAppUpdate = isAutoTrackAppUpdate;
    }

    public boolean isAutoTrackAdvertiserId() {
        return isAutoTrackAdvertiserId;
    }

    public void setIsAutoTrackAdvertiserId(boolean isAutoTrackAdvertiserId) {
        this.isAutoTrackAdvertiserId = isAutoTrackAdvertiserId;
    }

    public boolean isAutoTrackAdvertisingOptOut() {
        return isAutoTrackAdvertisingOptOut;
    }

    public void setIsAutoTrackAdvertisingOptOut(boolean isAutoTrackAdvertisingOptOut) {
        this.isAutoTrackAdvertisingOptOut = isAutoTrackAdvertisingOptOut;
    }

    public Map<String, ActivityConfiguration> getActivityConfigurations() {
        return activityConfigurations;
    }

    public void setActivityConfigurations(Map<String, ActivityConfiguration> activityConfigurations) {
        this.activityConfigurations = activityConfigurations;
    }

    public boolean isHelloWorldPluginEnabed() {
        return isHelloWorldPluginEnabed;
    }

    public void setIsHelloWorldPluginEnabed(boolean isHelloWorldPluginEnabed) {
        this.isHelloWorldPluginEnabed = isHelloWorldPluginEnabed;
    }

    public String getTrackingConfigurationUrl() {
        return trackingConfigurationUrl;
    }

    public void setTrackingConfigurationUrl(String trackingConfigurationUrl) {
        this.trackingConfigurationUrl = trackingConfigurationUrl;
    }

    public boolean isAutoTracking() {
        return isAutoTracking;
    }

    public void setIsAutoTracking(boolean isAutoTracking) {
        this.isAutoTracking = isAutoTracking;
    }
}
