package com.webtrekk.webtrekksdk;

import android.app.Application;


/**
 * This is the main Application class, the developer can either inherit from this, or add the declared
 * variables and the getTracker function to his own custom application class
 */
public class WebtrekkApplication extends Application {
    private Webtrekk webtrekk;

    synchronized public Webtrekk getWebtrekk() {

        if(webtrekk == null) {
            webtrekk = Webtrekk.getInstance();
            webtrekk.initWebtrekk(this);
        }

        return webtrekk;
    }
}
