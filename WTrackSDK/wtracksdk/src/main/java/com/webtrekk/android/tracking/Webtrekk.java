package com.webtrekk.android.tracking;

import android.app.Activity;
import android.content.Context;
import java.util.Map;

/**
 * @deprecated  Use the new Tracker Interface instead
 *    This method is expected to be removed in future Versions
 *    of this API. Please check the documentation of API Version 4
 *    on how to use the new Tracking Interface.
 */
@Deprecated
public class Webtrekk {

    private static final Core core = new Core();



    private Webtrekk() {
        //static class
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void activityStart(Activity activity) {
        core.activityStart(activity);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void activityStop(Activity activity) {
        core.activityStop(activity);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static Context getContext() {
        return core.getContext();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static boolean isThisVersionAnUpdate()
    {
        return core.isThisVersionAnUpdate();
    }
    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static boolean isThisAppPreInstalled()
    {
        return core.isThisAppPreInstalled();
    }
    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void setAppVersionParameter(String appVersionParameter) {
        core.setAppVersionParameter(appVersionParameter);
    }
    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static String getEverId() {
        return core.getEverId();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static int getSamplingRate() {
        return core.getSamplingRate();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static long getSendDelay() {
        return core.getSendDelay();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static String getServerUrl() {
        return core.getServerUrl();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static String getTrackId() {
        return core.getTrackId();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static String getVersion() {
        return Core.VERSION;
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static boolean isLoggingEnabled() {
        return core.isLoggingEnabled();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static boolean isOptedOut() {
        return core.isOptedOut();
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void setContext(Context context) {
        core.setContext(context);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void setLoggingEnabled(boolean loggingEnabled) {
        core.setLoggingEnabled(loggingEnabled);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void setOptedOut(boolean isOptedOut) {
        core.setOptedOut(isOptedOut);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void setSamplingRate(int samplingRate) {
        core.setSamplingRate(samplingRate);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void setSendDelay(long sendDelay) {
        core.setSendDelay(sendDelay);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void setServerUrl(String serverUrl) {
        core.setServerUrl(serverUrl);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void setTrackId(String trackId) {
        core.setTrackId(trackId);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void trackAction(String pageId, String actionId) {
        core.trackAction(pageId, actionId);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void trackAction(String pageId, String actionId, Map<String,String> data) {
        core.trackAction(pageId, actionId, data);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void trackPage(String pageId) {
        core.trackPage(pageId);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static void trackPage(String pageId, Map<String,String> data) {
        core.trackPage(pageId, data);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static MediaSession trackMedia(String mediaId, int duration, int initialPosition) {
        return core.trackMedia(mediaId, duration, initialPosition);
    }

    /**
     * @deprecated  Use the new Tracker Interface instead
     *    This method is expected to be removed in future Versions
     *    of this API. Please check the documentation of API Version 4
     *    on how to use the new Tracking Interface.
     */
    @Deprecated
    public static MediaSession trackMedia(String mediaId, int duration, int initialPosition, MediaCategories categories) {
        return core.trackMedia(mediaId, duration, initialPosition, categories);
    }
}