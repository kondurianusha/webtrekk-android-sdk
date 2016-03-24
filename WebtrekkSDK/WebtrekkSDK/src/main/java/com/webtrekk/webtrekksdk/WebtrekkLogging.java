package com.webtrekk.webtrekksdk;

/**
 * custom logging class for the sdk defining the logTag and allows a global isLogging switch
 *
 */
class WebtrekkLogging {
    public static final String logTag = "WebtrekkSDK";
    public static boolean isLogging = true;

    static void log(String message) {
        if(isLogging) {
            android.util.Log.d(logTag, message);
        }
    }

    static void log(String message, Throwable t) {
        if(isLogging) {
            android.util.Log.d(logTag, message, t);
        }
    }

    public static synchronized boolean isLogging() {
        return isLogging;
    }

    /**
     * enables/disables logging for the sdk
     *
     * @param isLogging
     */
    public static synchronized void setIsLogging(boolean isLogging) {
        WebtrekkLogging.isLogging = isLogging;
    }
}
