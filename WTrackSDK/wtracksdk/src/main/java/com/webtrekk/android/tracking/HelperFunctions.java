package com.webtrekk.android.tracking;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;


/**
 * Created by user on 01/03/15.
 */
public class HelperFunctions {

    /**
     * returns a string of the display resolution, like 400x300
     * @param context
     * @return
     */
    public static String getResolution(Context context) {
        String resolution = "";
        try {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(dm);

            resolution = String.format("%sx%s", dm.widthPixels, dm.heightPixels);
        } catch (Exception e) {
            Log.w("WTrack", "Cannot grab resolution", e);
        }
        return resolution;
    }

    /**
     * gets the screendepth, as its 32 default, return this value
     * @param context
     * @return
     */
    public static String getDepth(Context context) {
        // since android 2.3
        return "32";
        //WindowManager wm = (WindowManager) app.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //int depth = wm.getDefaultDisplay().getPixelFormat();
        //return String.format("%s", depth);
    }

    /**
     * returns the current timestamp in ms as String
     * @return
     */
    public static String getTimestamp() {
        Long ts = System.currentTimeMillis()/1000;
        return ts.toString();
    }

    /**
     * returns the current formatted date and time
     * @return
     */
    public static String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ").format(new Date());
    }

    /**
     * returns the default language of the device as human readable String
     * @return
     */
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * returns the UTC Offset of the current timezone at the current time, in hours
     * @return
     */
    public static String getTimezone() {
        return "" + (TimeZone.getDefault().getRawOffset()/1000/60/60);
    }

    /**
     * returns the name of the os, defualt android is its an android lib
     * @return
     */
    public static String getOSName() {
        return "Android";
    }

    /**
     * returns the release version of the os, as human readable String like : Lollipop
     * @return
     */
    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * returns the API level, like 22 for Lollipop
     * @return
     */
    public static String getAPILevel() {
        return String.format("%s", Build.VERSION.SDK_INT);
    }

    /**
     * returns the manufactures device string
     * @return
     */
    public static String getDevice() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    /**
     * returns the current country, based on the default locale of the device as country code
     * @return
     */
    public static String getCountry() {
        return Locale.getDefault().getCountry();
    }

    /**
     * returns the user agent string of the default browser
     * @return
     */
    public static String getUserAgent() {
        return System.getProperty("http.agent");
    }

    /**
     * reads the contact information of the user like email, name, sname
     * important: you need to set read_profile/read_contacts permissions for this to work
     * @param context
     * @return
     */
    public static HashMap<String, String> getUserProfile(Context context) {
        // TODO: this method needs special permissions, so i would at least add an opt out possibility here
        // this method only works on android > 4.0, for lower versions use accountmanager

        String[] profilequery = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.Contacts.Data.MIMETYPE
        };
        final ContentResolver content = context.getContentResolver();
        final Cursor cursor = content.query(
                // Retrieves data rows for the device user's 'profile' contact
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                        profilequery,

                // Selects only email addresses or names
                ContactsContract.Contacts.Data.MIMETYPE + "=? OR "
                        + ContactsContract.Contacts.Data.MIMETYPE + "=?",
                new String[]{
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                },
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
        );

        HashMap<String, String> result = new HashMap<>();
        String mime_type;
        while (cursor.moveToNext()) {
            // get the typ, 4.column in the query result
            mime_type = cursor.getString(4);
            if (mime_type.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)){
                // this automaticly stores the first found email in the result, but its sorted by primary, so if a user has a primary one, its the default
                result.put("email", cursor.getString(0));
            }
            else if (mime_type.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
                result.put("gname", cursor.getString(2));
                result.put("sname", cursor.getString(3));
            }
        }
        cursor.close();
        return result;
    }

    /**
     * generates the everid, a unique identifier for the tracking
     * @return
     */
    public static String generateEverid() {
        String everId = "";
        long date = System.currentTimeMillis() / 1000;
        Random random = new Random();

        everId = "6" + String.format(
                "%010d%08d",
                date,
                Long.valueOf(random.nextInt(100000000))
        );

        return everId;
    }

    /**
     * returns the version of the application
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        // android studio has this as static var in buildconfig, later switch
        // return BuildConfig.VERSION_NAME
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("WTrack", e.getMessage());
            return "";
        }
    }

    /**
     * returns the version code of the playstore from the app
     * each version code of an uploaded app in the playstore must be higher than the previous one
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        // android studio has this as static var in buildconfig, later switch
        // return BuildConfig.VERSION_CODE
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("WTrack", e.getMessage());
            return -1;
        }
    }

    public static void setAppVersionCode(int versionCode, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(WTrack.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(WTrack.PREFERENCE_APP_VERSIONCODE, versionCode).commit();
    }

    /**
     * this method checks if the application was updated, therefore it writes its version_code in a shared preference
     * and in case its a higher number on the next start, it was updated
     * @param context
     * @return
     */
    public static boolean updated(Context context) {
        // this also automaticly handles the case when there is an update between the tracking lib
        int current_version = getAppVersionCode(context);
        int stored_version = context.getSharedPreferences(WTrack.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE).getInt(WTrack.PREFERENCE_APP_VERSIONCODE, -1);
        if(!firstStart(context) && current_version > stored_version ) {
            return true;
        }
        return false;
    }

    /**
     * returns true when the app is started for the first time,
     * it detects the first start when there is no everid set by the application
     *
     * @param context
     * @return
     */
    public static boolean firstStart(Context context) {
        // if no everid is set, this is the first start
        SharedPreferences preferences = context.getSharedPreferences(WTrack.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.contains(WTrack.PREFERENCE_KEY_EVER_ID);
    }

    /**
     * this is true when its a system app or was preinstalled on the phone
     * @param context
     * @return
     */
    public static boolean isAppPreinstalled(Context context) {
        ApplicationInfo info = context.getApplicationInfo();
        if (info != null && info.flags != 0) {
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNewInstallation(Context context) {
            SharedPreferences preferences = context.getSharedPreferences(WTrack.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
            String installationFlag = preferences.getString(WTrack.PREFERENCE_KEY_INSTALLATION_FLAG, null);
            return (installationFlag != null && installationFlag.equals("1"));

    }

    /**
     * reads the advertiser id and advertiser opt out values for the user,
     * needs the appropiate permissions and playstore lib linked, at least the ad parts
     * works by passing in a reference as it runs in a seperate thread to avoid lags in the main thread,
     *
     * @param app
     */
    public static synchronized void getAdvertiserID(final WTrackApplication app) {
        // check if playservice sdk is available on that device, TODO: define alternative handling here
        // TODO: define default handling when this values can not be read, maybe cache them in Shared preferences if that is allowed due to opt out
        if( GooglePlayServicesUtil.isGooglePlayServicesAvailable(app) == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Info adInfo = null;
                    try {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(app);
                        app.getWTRack().getAuto_tracked_values().put(TrackingParams.Params.ADVERTISER_ID, adInfo.getId());
                        app.getWTRack().getAuto_tracked_values().put(TrackingParams.Params.ADVERTISER_OPTOUT, String.valueOf(adInfo.isLimitAdTrackingEnabled()));

                    } catch (IOException e) {
                        // Unrecoverable error connecting to Google Play services (e.g.,
                        // the old version of the service doesn't support getting AdvertisingId).

                    } catch (GooglePlayServicesNotAvailableException e) {
                        // Google Play services is not available entirely.
                    } catch (GooglePlayServicesRepairableException e) {
                        // maybe will work with another try, recheck
                    } catch (NullPointerException e) {
                        // adinfo was null or could not get the id/optout setting
                    }
                }
            }).start();


        }
    }

    /**
     * url encodes the given string as utf8
     * replaces special strings like " " with %20
     * @param string
     * @return
     */
    public static String urlEncode(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }

        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String urlDecode(String string) {
        if (string == null || string.length() == 0) {
            return string;
        }

        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


}
