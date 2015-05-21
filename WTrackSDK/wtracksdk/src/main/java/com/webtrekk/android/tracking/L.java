package com.webtrekk.android.tracking;

public class L {
    public static final String logTag = "WTRACK";
    public static boolean isLogging = false;

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

    public static synchronized boolean isLogging() {
        return isLogging;
    }

    public static synchronized void setIsLogging(boolean isLogging) {
        L.isLogging = isLogging;
    }
}
