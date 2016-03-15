package com.webtrekk.webtrekksdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.JsonReader;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vartbaronov on 03.03.16.
 * Class is responsible for calling thread that is get Advertizing ID and processing referrer id. It is extracts click id and sends install request.
 *
 * @hide
 */
class Campaign extends Thread
{
    private final String mTrackID;
    private final boolean mFirstStart;
    private final Context mContext;

    private String mMediaCode;
    private final Runnable mStopNotification;

    private static final String ADV_ID = "INSTALL_SETTINGS_ADV_ID";
    private static final String MEDIA_CODE = "INSTALL_SETTINGS_MEDIA_CODE";
    private static final String OPT_OUT = "INSTALL_SETTINGS_OPT_OUT";
    private static final String FIRST_START_INITIATED = "FIRST_START_INITIATED";
    private static final String CAMPAIN_MEDIA_CODE_DEFINED_MESSAGE = "com.Webtrekk.CampainMediaMessage";


    Campaign(Context context, String trackID, boolean isFirstStart,
             Runnable stopNotification) {

        mContext = context;
        //if it is not first start check if thread was interrupted on first start.
        //It is case when application was closed just after first start.
        mFirstStart = isFirstStart ? isFirstStart : getFirstStartInitiated(context, true);
        mTrackID = trackID;
        mStopNotification = stopNotification;
    }

    /**
     * @hide
     * Starts thread for collecting Campain data
     * @param context context
     * @param trackID track id
     * @param isFirstStart if this is first start
     * @param stopNotification notify about that work is done. It is required to free instance of this class for GC
     * @return instance of Campain class. you need it to interrupt process if application is closed.
     */
    static Campaign start(Context context, String trackID, boolean isFirstStart,
                          Runnable stopNotification)
    {
        if (trackID == null || trackID.isEmpty())
        {
            WebtrekkLogging.log("Track ID is received to Campain server is null. Campain can't be tracked");
            return null;
        }

        Campaign service = new Campaign(context, trackID, isFirstStart, stopNotification);
        service.start();
        return service;
    }

    /** @hide */
    private String getStoredReferrer(Context context) {
        String result = null;

        SharedPreferences preferences = HelperFunctions.getWebTrekkSharedPreference(context);

        result = preferences.getString(ReferrerReceiver.REFERRER_KEY_NAME, null);

        return result;
    }

    /** @hide */
    //Parse refferer and get media code
    private String getGoogleAnalyticMediaCode(String referrer)
    {
        String campaign = "";
        String content = "";
        String medium = "";
        String source = "";
        String term = "";

        String[] components = referrer.split("&");
        for (String component : components) {
            String parameter[] = component.split("=", 2);
            if (parameter.length < 2) {
                continue;
            }

            String key = HelperFunctions.urlDecode(parameter[0]);
            String value = HelperFunctions.urlDecode(parameter[1]);

            if ("utm_campaign".equals(key)) {
                campaign = value;
            } else if ("utm_content".equals(key)) {
                content = value;
            } else if ("utm_medium".equals(key)) {
                medium = value;
            } else if ("utm_source".equals(key)) {
                source = value;
            } else if ("utm_term".equals(key)) {
                term = value;
            }
        }

        if (campaign.isEmpty() && medium.isEmpty() && content.isEmpty() && source.isEmpty())
            return null;

        String campaignId = "wt_mc%3D" + HelperFunctions.urlEncode(source + "." + medium + "." + content + "." + campaign);
        if(!term.isEmpty()) {
            campaignId += ";wt_kw%3D" + HelperFunctions.urlEncode(term);
        }

        return campaignId;

    }

    /** @hide */
    private String getClickID(String referrer)
    {
        Pattern pattern = Pattern.compile("(^|&)wt_clickid=([^&#=]*)([#&]|$)");
        Matcher matcher = pattern.matcher(referrer);

        if (matcher.find())
        {
            return matcher.group(2);
        } else
            return null;
    }

    /**
     * @hide
     * Main thread
     */
    @Override
    public void run() {

        try{
            WebtrekkLogging.log("starting campain thread. Getting advertiying ID");
            AdvertisingIdClient.Info adInfo = null;
            String advID = null;
            boolean isLimitAdEnabled = false;

            //get adv ID

            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);

                advID = adInfo.getId();
                isLimitAdEnabled = adInfo.isLimitAdTrackingEnabled();

                WebtrekkLogging.log("advertiserId: " + advID);

            } catch (IOException e) {
                // Unrecoverable error connecting to Google Play services (e.g.,
                // the old version of the service doesn't support getting AdvertisingId).
                WebtrekkLogging.log("Unrecoverable error connecting to Google Play services", e);

            } catch (GooglePlayServicesNotAvailableException e) {
                // Google Play services is not available entirely.
                WebtrekkLogging.log("GooglePlayServicesNotAvailableException", e);
            } catch (GooglePlayServicesRepairableException e) {
                // maybe will work with another try, recheck
            } catch (NullPointerException e) {
                // adinfo was null or could not get the id/optout setting
                WebtrekkLogging.log("Unrecoverable error connecting to Google Play services", e);
            }

            //if this is first start wait for referrer for 30 seconds.
            final long waitForReferrerDelay = 30000;
            long timeToEndListener = System.currentTimeMillis() + waitForReferrerDelay;
            String referrer = null;

            if (mFirstStart) {
                //Wait for referrer max 1 minute
                WebtrekkLogging.log("Start waiting for referrer");
                setFirstStartInitiated();
                while (System.currentTimeMillis() < timeToEndListener) {
                    if ((referrer = getStoredReferrer(mContext)) != null)
                        break;

                    try {
                        //ask each 5 seconds for referer;
                        sleep(5000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                WebtrekkLogging.log("Referrer is received and readed:"+referrer);
            }
            String clickID = null;
            String googleMediaCode = null;

            // for non first start referrer is always null
            if (referrer != null)
            {
                // is it referrer from Webtrekk
                clickID = getClickID(referrer);

                if (clickID != null)
                {
                    WebtrekkLogging.log("Click ID:" + clickID);
                }else
                {
                    googleMediaCode = getGoogleAnalyticMediaCode(referrer);
                    if (googleMediaCode != null) {
                        SaveCodeAndAdID(googleMediaCode, advID, isLimitAdEnabled);
                        campaignNotificationMessage(googleMediaCode, advID);
                        //delete first start
                        getFirstStartInitiated(mContext, true);
                    }
                }
            }

            String webtrekkMediaCode = null;

            // if this is webtrekk referrer get media code.
            if (googleMediaCode == null) {
                if (mFirstStart) {
                    // if thread isn't interrupted. Request for media code
                    if (!isInterrupted())
                        webtrekkMediaCode = requestMediaCode(advID, clickID, HelperFunctions.getUserAgent());
                    else
                        return;
                }

                SaveCodeAndAdID(webtrekkMediaCode, advID, isLimitAdEnabled);
                campaignNotificationMessage(webtrekkMediaCode, advID);
                //delete first start
                getFirstStartInitiated(mContext, true);
            }
        }finally {

            if (mStopNotification != null)
                mStopNotification.run();
        }

    }

    /*
    Request media code with Install request. Provides all infromation in request it can.
    Sends request anyway as userAgent should be present.
     */
    private String requestMediaCode(String advID, String clickID, String userAgent)
    {
        RequestProcessor requestProcessor = new RequestProcessor(null);

        TrackingParameter tp = new TrackingParameter();


        tp.add(TrackingParameter.Parameter.INST_TRACK_ID, mTrackID);

        if (advID != null && !advID.isEmpty())
            tp.add(TrackingParameter.Parameter.INST_AD_ID , advID);

        if (clickID != null && !clickID.isEmpty())
            tp.add(TrackingParameter.Parameter.INST_CLICK_ID, clickID);

        if (userAgent != null)
            tp.add(TrackingParameter.Parameter.USERAGENT, userAgent);

        final TrackingRequest tr = new TrackingRequest(tp, null, TrackingRequest.RequestType.INSTALL);

        try {

            String installURL = tr.getUrlString();
            WebtrekkLogging.log("Install URL:" + installURL);

            requestProcessor.sendRequest(new URL(installURL), new RequestProcessor.ProcessOutputCallback() {
                @Override
                public void process(int statusCode, HttpURLConnection connection) {
                    JsonReader jsonReader = null;
                    String mediaCodeRaw = null;
                    try
                    {

                    if (statusCode == 200) {
                        InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                        jsonReader = new JsonReader(reader);

                        jsonReader.beginObject();

                        while (jsonReader.hasNext()) {
                            final String name = jsonReader.nextName();
                            if (name.equals("mediacode")) {
                                mediaCodeRaw = jsonReader.nextString();
                            }else
                                jsonReader.skipValue();
                        }

                        jsonReader.endObject();

                        if (mediaCodeRaw != null)
                        {
                            WebtrekkLogging.log("Media code is received:"+mediaCodeRaw);
                            String mediaRawArray[] = mediaCodeRaw.split("=");

                            if (mediaRawArray.length == 2)
                                mMediaCode = mediaRawArray[1];
                            else
                                WebtrekkLogging.log("Incorrect media code in response:"+mediaCodeRaw);

                        }else
                            WebtrekkLogging.log("Media code isn't defined from response.");
                    }else
                        WebtrekkLogging.log("Install request failed with status code:"+statusCode);
                    } catch (IOException e) {
                        WebtrekkLogging.log("Incorrect install Get response:"+e.getMessage());
                    }
                    finally{

                        if (jsonReader != null) {
                            try {
                                jsonReader.close();
                            } catch (IOException e) {
                            }
                        }
                    }

                }
            });

        } catch (MalformedURLException e) {
            WebtrekkLogging.log("Error constructing INSTALL URL:" + e.getMessage());
        }

        return  mMediaCode;
    }

    /**
     Save all information to settings. So it can be used in future and in case something happened with application.
     */
    private void SaveCodeAndAdID(String mediaCode, String advertizingID, boolean isOptOut)
    {
        SharedPreferences.Editor editor = HelperFunctions.getWebTrekkSharedPreference(mContext).edit();

        WebtrekkLogging.log("Campain data is saved mediaCode:" + mediaCode + " advertizingID:" + advertizingID + " isOptOut:" + isOptOut);

        if (mediaCode != null)
            editor.putString(MEDIA_CODE, mediaCode);
        if (advertizingID != null)
            editor.putString(ADV_ID, advertizingID);
        editor.putBoolean(OPT_OUT, isOptOut);
        editor.apply();
    }

    /**
     * save if thread was interrupted just after first start. So we can process referrer one more time after second start
     */
    private void setFirstStartInitiated()
    {
        SharedPreferences.Editor editor = HelperFunctions.getWebTrekkSharedPreference(mContext).edit();

        editor.putBoolean(FIRST_START_INITIATED, true).apply();
    }

    /**
     * {@hide}
     * get if thread was interrupted see {@link #setFirstStartInitiated()} delete flag from settings
     * @return
     */
    static boolean getFirstStartInitiated(Context context, boolean deleteFlag)
    {
        SharedPreferences preferences = HelperFunctions.getWebTrekkSharedPreference(context);

        boolean result = preferences.getBoolean(FIRST_START_INITIATED, false);

        if (deleteFlag)
            preferences.edit().remove(FIRST_START_INITIATED).apply();

        return result;
    }

    static String getAdvId(Context context)
    {
        return getAndRemoveInstallSpecificCode(context, ADV_ID, false);
    }

    static String getMediaCode(Context context)
    {
        return getAndRemoveInstallSpecificCode(context, MEDIA_CODE, true);
    }

    static boolean getOptOut(Context context)
    {
        SharedPreferences preferences = HelperFunctions.getWebTrekkSharedPreference(context);
        return preferences.getBoolean(OPT_OUT, false);
    }

    /**
     * help funtion that get information from setting and optionally remove it
     * @param context
     * @param key
     * @param remove
     * @return
     */
    static private String getAndRemoveInstallSpecificCode(Context context, String key, boolean remove)
    {
        String value;
        SharedPreferences preferences = HelperFunctions.getWebTrekkSharedPreference(context);
        value = preferences.getString(key, null);
        if (value != null && remove)
          preferences.edit().remove(key).apply();
        return value;
    }

    /**
     * Some test message for test application
     * @param mediaCode
     * @param advID
     */
    private void campaignNotificationMessage(String mediaCode, String advID)
    {
        if (!mFirstStart)
            return;
        Intent intent = new Intent(CAMPAIN_MEDIA_CODE_DEFINED_MESSAGE);

        intent.putExtra(MEDIA_CODE, mediaCode);
        intent.putExtra(ADV_ID, advID);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}
