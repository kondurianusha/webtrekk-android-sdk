package com.webtrekk.webtrekksdk.Utils;

/**
 * custom logging class for the sdk defining the logTag and allows a global isLogging switch
 *
 */
public class WebtrekkLogging {
    public static final String logTag = "WebtrekkSDK";
    volatile public static boolean isLogging = true;

    public static void log(String message) {
        if(isLogging) {
            android.util.Log.d(logTag, message);
        }
    }

    public static void log(String message, Throwable t) {
        if(isLogging) {
            android.util.Log.d(logTag, message, t);
        }
    }

    public static boolean isLogging() {
        return isLogging;
    }

    /**
     * enables/disables logging for the sdk
     *
     * @param isLogging
     */
    public static void setIsLogging(boolean isLogging) {
        WebtrekkLogging.isLogging = isLogging;
    }
}
