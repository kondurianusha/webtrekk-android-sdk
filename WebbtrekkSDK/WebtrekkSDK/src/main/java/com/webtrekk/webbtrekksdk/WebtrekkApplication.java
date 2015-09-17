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

        if(callbacks == null) {
            callbacks = new TrackedActivityLifecycleCallbacks(this);
            registerActivityLifecycleCallbacks(callbacks);
        }

        if(webtrekk == null) {
            webtrekk = Webtrekk.getInstance();
            webtrekk.initWebtrekk(this);
        }
        return webtrekk;
    }
}
