package com.webtrekk.webtrekksdk;

/**
 * Created by user on 12/11/15.
 */
class ActivityConfiguration {
    private String mClassName;
    private String mMappingName;
    private boolean mIsAutoTrack;
    private TrackingParameter mActivityTrackingParameter;
    private TrackingParameter mConstActivityTrackingParameter;

    public ActivityConfiguration() {
    }

    public ActivityConfiguration(String className, String mappingName, boolean isAutoTrack, TrackingParameter tp, TrackingParameter constTp) {
        mClassName = className;
        mMappingName = mappingName;
        mIsAutoTrack = isAutoTrack;
        mActivityTrackingParameter = tp;
        mConstActivityTrackingParameter = constTp;
    }

    public String getClassName() {
        return mClassName;
    }

    public void setClassName(String className) {
        mClassName = className;
    }

    public String getMappingName() {
        return mMappingName;
    }

    public void setMappingName(String mappingName) {
        mMappingName = mappingName;
    }

    public boolean isAutoTrack() {
        return mIsAutoTrack;
    }

    public void setIsAutoTrack(boolean isAutoTrack) {
        mIsAutoTrack = isAutoTrack;
    }

    public TrackingParameter getActivityTrackingParameter() {
        return mActivityTrackingParameter;
    }

    public void setActivityTrackingParameter(TrackingParameter activityTrackingParameter) {
        this.mActivityTrackingParameter = activityTrackingParameter;
    }

    public TrackingParameter getConstActivityTrackingParameter() {
        return mConstActivityTrackingParameter;
    }

    public void setConstActivityTrackingParameter(TrackingParameter constActivityTrackingParameter) {
        mConstActivityTrackingParameter = constActivityTrackingParameter;
        //validae URL if any
        if (mConstActivityTrackingParameter.containsKey(TrackingParameter.Parameter.PAGE_URL))
        {
            String url = mConstActivityTrackingParameter.getDefaultParameter().get(TrackingParameter.Parameter.PAGE_URL);
            if (!HelperFunctions.testIsValidURL(url))
            {
                WebtrekkLogging.log("Incorrece URL:"+url+" in configuration. Don't track it for pu parameter");
                mConstActivityTrackingParameter.getDefaultParameter().remove(TrackingParameter.Parameter.PAGE_URL);
            }
        }
    }

    public void setOverridenPageURL(String url)
    {
        mActivityTrackingParameter.getDefaultParameter().put(TrackingParameter.Parameter.PAGE_URL, url);
    }

    public void resetOverridenPageURL()
    {
        mActivityTrackingParameter.getDefaultParameter().remove(TrackingParameter.Parameter.PAGE_URL);
    }


}
