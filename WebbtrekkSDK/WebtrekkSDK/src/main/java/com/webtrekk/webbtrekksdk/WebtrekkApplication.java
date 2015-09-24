package com.webtrekk.webbtrekksdk;

import android.app.Application;


/**
 * This is the main Application class, the developer can either inherit from this, or add the declared
 * variables and the getTracker function to his own custom application class
 */
public class WebtrekkApplication extends Application {
    TrackedActivityLifecycleCallbacks callbacks;
    private Webtrekk webtrekk;

    synchronized public Webtrekk getWebtrekk() {

        if(webtrekk == null) {
            webtrekk = Webtrekk.getInstance();
            webtrekk.initWebtrekk(this);
        }

        if(callbacks == null && webtrekk.getTrackingConfiguration().isAutoTracked()) {
            callbacks = new TrackedActivityLifecycleCallbacks(webtrekk);
            registerActivityLifecycleCallbacks(callbacks);
        }
        return webtrekk;
    }
}
