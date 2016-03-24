package com.webtrekk.webtrekksdk;

/**
 * Created by user on 12/11/15.
 */
class ActivityConfiguration {
    private String className;
    private String mappingName;
    private boolean isAutoTrack;
    private TrackingParameter activityTrackingParameter;
    private TrackingParameter constActivityTrackingParameter;

    public ActivityConfiguration() {
    }

    public ActivityConfiguration(String className, String mappingName, boolean isAutoTrack, TrackingParameter tp, TrackingParameter constTp) {
        this.className = className;
        this.mappingName = mappingName;
        this.isAutoTrack = isAutoTrack;
        this.activityTrackingParameter = tp;
        this.constActivityTrackingParameter = constTp;
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

    public TrackingParameter getConstActivityTrackingParameter() {
        return constActivityTrackingParameter;
    }

    public void setConstActivityTrackingParameter(TrackingParameter constActivityTrackingParameter) {
        this.constActivityTrackingParameter = constActivityTrackingParameter;
    }
}
