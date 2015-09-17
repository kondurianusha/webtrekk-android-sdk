package com.webtrekk.webbtrekksdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * This classe downloads the xml configuration from the configured remote url,
 * it runs asynchronous in the background during application start if enabled
 *
 */
class TrackingConfigurationDownloadTask extends AsyncTask<String, Void, TrackingConfiguration> {
    private Context context;
    private Webtrekk webtrekk;
    private TrackingConfiguration trackingConfiguration;
    private String trackingConfigurationString;

    public TrackingConfigurationDownloadTask(Webtrekk webtrekk) {
        this.webtrekk = webtrekk;
        this.context = webtrekk.getContext();
    }

    /**
     * runs in the background and downloads and parses the xmlconfigration from the configured remoteurl
     *
     * @param urls
     * @return
     */
    @Override
    protected TrackingConfiguration doInBackground(String... urls) {
        try {
            InputStream stream = null;
            // Instantiate the parser
            TrackingConfigurationXmlParser trackingConfigurationParser = new TrackingConfigurationXmlParser();


            try {
                stream = downloadUrl(urls[0]);
                trackingConfigurationString = HelperFunctions.stringFromStream(stream);
                trackingConfiguration = trackingConfigurationParser.parse(trackingConfigurationString);
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (IOException e) {
            WebtrekkLogging.log("connection error, can not download xml configuration", e);
        } catch (XmlPullParserException e) {
            WebtrekkLogging.log("xml parser error, no validate xml configuration file",e);
        }
        return null;
    }

    /**
     * this method gets called when the doInBackground method is finished it replaces the current
     * TrackingConfiguration with a newer one if one was found online, and also store it in the shared prefs
     *
     * @param config
     */
    @Override
    protected void onPostExecute(TrackingConfiguration config) {
        if(config == null) {
            WebtrekkLogging.log("error getting a new valid configuration from remote url, tracking with the old config");
            return;
        }
        if(config.getVersion() > webtrekk.getTrackingConfiguration().getVersion()) {
            WebtrekkLogging.log("found a new version online, updating current version");
            // either store it as xml on the internal storage or save it as xml string in the shared prefs
            WebtrekkLogging.log("saving new trackingConfiguration to preferences");
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            sharedPrefs.edit().putString(Webtrekk.PREFERENCE_KEY_CONFIGURATION, trackingConfigurationString).commit();

            //TODO: update the current configuration only if valid and newer
            WebtrekkLogging.log("updating current trackingConfiguration");
            webtrekk.setTrackingConfiguration(trackingConfiguration);
        } else {
            WebtrekkLogging.log("local config is already up to date, doing nothing");
        }

    }


    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}