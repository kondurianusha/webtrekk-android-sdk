package com.webtrekk.webtrekksdk;

import android.content.Context;
import android.content.SharedPreferences;

import com.webtrekk.webtrekksdk.TrackingParameter.Parameter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by vartbaronov on 24.02.16.
 * Class is manage user parameters as part of Cross Device Bridge implementation.
 * Class should be used for customer to set user parameter, It can Save/Restore user data from settings
 */
public class WebtrekkUserParameters {

    private final Map<Parameter, String> mParameters = new HashMap<Parameter, String>();
    private final SortedMap<String, String> mCustomParameters  = new TreeMap<String, String>();
    static final Parameter ALL_CDB_PAR[] = {Parameter.CDB_EMAIL_MD5, Parameter.CDB_EMAIL_SHA,
            Parameter.CDB_PHONE_MD5, Parameter.CDB_PHONE_SHA, Parameter.CDB_ADDRESS_MD5, Parameter.CDB_ADDRESS_SHA,
            Parameter.CDB_ANDROID_ID, Parameter.CDB_IOS_ADD_ID, Parameter.CDB_WIN_AD_ID, Parameter.CDB_FACEBOOK_ID,
            Parameter.CDB_TWITTER_ID, Parameter.CDB_GOOGLE_PLUS_ID, Parameter.CDB_LINKEDIN_ID};
    static final int CUSTOM_PAR_BASE_INDEX = 50;
    private static String LAST_CBD_REQUEST_DATE = "LAST_CBD_REQUEST_DATE";
    private static final long DATE_DELIMETER = 1000*60*60*24;

    /**
     * Set Email should be normalized - lower case only and no whitespaces
     * To remove parameter use null
     * @param email
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setEmail(String email) {
        putMd5ShaPair(Parameter.CDB_EMAIL_MD5, Parameter.CDB_EMAIL_SHA, normalizeEmail(email));
        return this;
    }

    /**
     * Set Phone should be normalized - lower case only, no whitespaces, no non-number characters
     * @param phone phone number
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setPhone(String phone)
    {
        putMd5ShaPair(Parameter.CDB_PHONE_MD5, Parameter.CDB_PHONE_SHA, normalizePhone(phone));
       return this;
    }

    /**
     * Set address should be in form prename|surename|zipcode|street|house number
     * @param address
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setAddress(String address)
    {
        //Validate if address is incorrect

        if (address.matches("(.+\\|(.)+){4}")) {
            putMd5ShaPair(Parameter.CDB_ADDRESS_MD5, Parameter.CDB_ADDRESS_SHA, normalizeAddress(address));
        }else
          WebtrekkLogging.log("Address isn't added. Format is incorrect, use this one: prename|surename|zipcode|street|house number");
        return this;
    }

    /**
     * set android ID
     * @param androidId
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setAndroidId(String androidId) {
        mParameters.put(Parameter.CDB_ANDROID_ID, androidId);
        return this;
    }

    /**
     * set iOS id
     * @param iOSId
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setiOSId(String iOSId) {
        mParameters.put(Parameter.CDB_IOS_ADD_ID, iOSId);
        return this;
    }

    /**
     * set WindowsID
     * @param windowsId
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setWindowsId(String windowsId) {
        mParameters.put(Parameter.CDB_WIN_AD_ID, windowsId);
        return this;
    }

    /**
     * set Facebook ID
     * @param facebookID
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setFacebookID(String facebookID) {
        mParameters.put(Parameter.CDB_FACEBOOK_ID, HelperFunctions.makeSha256(facebookID));
        return this;
    }

    /**
     * set Twitter ID
     * @param twitterID
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setTwitterID(String twitterID)
    {
        mParameters.put(Parameter.CDB_TWITTER_ID, HelperFunctions.makeSha256(twitterID));
        return this;
    }

    /**
     * set Google Plus ID
     * @param googlePlusID
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setGooglePlusID(String googlePlusID)
    {
        mParameters.put(Parameter.CDB_GOOGLE_PLUS_ID, HelperFunctions.makeSha256(googlePlusID));
        return this;
    }

    /**
     * set LinkedIn ID
     * @param liknedInID
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setLiknedInID(String liknedInID)
    {
        mParameters.put(Parameter.CDB_LINKEDIN_ID, HelperFunctions.makeSha256(liknedInID));
        return this;
    }

    /**
     * set Custom User Parameters
     * @param id id of custom parameter should be id > 0 && id < 30
     * @param value custom user value
     * @return instance of to WebtrekkUserParameters
     */
    public WebtrekkUserParameters setCustom(int id, String value)
    {
        if (id > 0 && id < 30)
        {
            mCustomParameters.put(new Integer(CUSTOM_PAR_BASE_INDEX+id).toString(), value);
        }else
        {
            WebtrekkLogging.log("Custom parameter isn't set as id is incorrect please use id > 0 && id < 30 ");
        }

        return this;
    }


    private String normalizeEmail(String email)
    {

        return email.toLowerCase().replace(" ", "");
    }

    private String normalizePhone(String phone)
    {
        String ret = phone.toLowerCase().replaceAll("[\\D\\s]", "");

        WebtrekkLogging.log("normalized phone: "+ ret);

        return ret;
    }

    private String normalizeAddress(String address)
    {
        String ret = address.toLowerCase().
                replace("ä", "ae").
                replace("ö", "oe").
                replace("ü", "ue").
                replace("ß", "ss").
                replaceAll("[\\s_-]", "").
                replaceAll("str(\\.)?(\\s|\\|)+", "strasse|");

        WebtrekkLogging.log("normalized address: " + ret);
        return ret;
    }

    private void putMd5ShaPair(Parameter md5Key, Parameter sha256Key, String value)
    {
          mParameters.put(md5Key, HelperFunctions.makeMd5(value));
          mParameters.put(sha256Key, HelperFunctions.makeSha256(value));
    }

    /**
     * Save to settings and clear map from null values  return false if nothing to send
     * @param context
     */
    boolean saveToSettings(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(Webtrekk.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        //Save standard parameters and remove nulls
        saveToSettingAndRemoveNullItemsProcess(context, editor, mParameters, "");

        //Save custom parameters and remove nulls
        saveToSettingAndRemoveNullItemsProcess(context, editor, mCustomParameters, "cdb");

        editor.apply();

        return !mCustomParameters.isEmpty() || !mParameters.isEmpty();
    }

    /**
     * save parameters and remove nulls
     * @param context
     * @param editor
     * @param map
     * @param keyPrefix
     * @param <T>
     */
    private <T> void saveToSettingAndRemoveNullItemsProcess(Context context, SharedPreferences.Editor editor, Map<T, String> map, String keyPrefix)
    {
        for (Iterator<Map.Entry<T, String>> iterator = map.entrySet().iterator(); iterator.hasNext();)
        {
            Map.Entry<T, String> entry = iterator.next();
            String key = keyPrefix + entry.getKey();

            if (entry.getValue() != null)
                editor.putString(key, entry.getValue());
            else
            {
                editor.remove(key);
                iterator.remove();
            }
        }
    }

    /**
     * restore instace from settings returns false if no item in setting
     * @param context
     * @return
     */
    boolean restoreFromSettings(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(Webtrekk.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        for (Parameter par: ALL_CDB_PAR)
        {
            if (preferences.contains(par.toString()))
              mParameters.put(par, preferences.getString(par.toString(), ""));
        }

        for (int i = 1; i < 30; i++ )
        {
            String key = new Integer(CUSTOM_PAR_BASE_INDEX+i).toString();
            String prefKey = "cdb"+key;

            if (preferences.contains(prefKey))
              mCustomParameters.put(key,preferences.getString(prefKey, ""));
        }
        return !mCustomParameters.isEmpty() || !mParameters.isEmpty();
    }

    Map<Parameter, String> getParameters()
    {
        return  mParameters;
    }

    SortedMap<String, String> getCustomParameters()
    {
        return  mCustomParameters;
    }

    boolean isAnyNotNullValue()
    {
        for(String value:mParameters.values())
        {
            if (value != null)
                return true;
        }

        for(String value:mCustomParameters.values())
        {
            if (value != null)
                return true;
        }

        return false;
    }


    /**
     * define if CDB request need repeat
     * @param context
     * @return
     */

    static boolean needUpdateCDBRequest(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(Webtrekk.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        if (preferences.contains(LAST_CBD_REQUEST_DATE)) {
            long dates = preferences.getLong(LAST_CBD_REQUEST_DATE, 0);

            return dates < getCurrentDateCounter();
        }else
            return false;
    }

    /**
     * update date of CDB request
     * @param context
     */
    static void updateCDBRequestDate(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(Webtrekk.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        preferences.edit().putLong(LAST_CBD_REQUEST_DATE, getCurrentDateCounter()).apply();
    }

    static long getCurrentDateCounter()
    {
        return (long)System.currentTimeMillis()/DATE_DELIMETER;
    }
}
