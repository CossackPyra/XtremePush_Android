package ie.imobile.extremepush.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public final class SharedPrefUtils {

    private static final String SHARED_PREF_NAME = "gcmlib_pref";
    private static final String SHARED_FINGERPRINT = "fingerprint";
    private static final String SHARED_SERVER_SERVER_DEVICE_ID = "server_device_id";
    private static final String SHARED_APP_KEY = "server_app_key";
    private static final String SHARED_SENDER_ID = "sender_id";
    private static final String SHARED_SERVER_URL = "server_url";
    private static final String SHARED_OLD_LOCATIONS = "old_locations";
    private static final String SHARED_MAIN_ACTIVITY = "main_activity";
    private static final String SHARED_LAST_PUSH_ID = "last_push_id";

    public SharedPrefUtils() {
    }

    public static void setConfigsFingerpirnt(Context context, String fingerprint) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_FINGERPRINT, fingerprint);
        editor.commit();
    }

    public static String getConfigsFingerprint(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(SHARED_FINGERPRINT, null);
    }

    public static void setServerDeviceId(Context context, String serverDeviceId) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_SERVER_SERVER_DEVICE_ID, serverDeviceId);
        editor.commit();
    }

    public static String getServerDeviceId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(SHARED_SERVER_SERVER_DEVICE_ID, null);
    }

    public static void setAppKey(Context context, String appKey) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_APP_KEY, appKey);
        editor.commit();
    }

    public static String getAppKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(SHARED_APP_KEY, null);
    }

    public static void setSenderId(Context context, String senderId) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_SENDER_ID, senderId);
        editor.commit();
    }

    public static String getSenderId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(SHARED_SENDER_ID, null);
    }

    public static void setServerUrl(Context context, String serverUrl) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_SERVER_URL, serverUrl);
        editor.commit();
    }

    public static String getServerUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(SHARED_SERVER_URL, null);
    }

    public static void setOldLocations(Context context, String locations) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_OLD_LOCATIONS, locations);
        editor.commit();
    }

    public static String getOldLocations(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(SHARED_OLD_LOCATIONS, null);
    }

    public static void setMainActivityUris(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_MAIN_ACTIVITY, activity.getClass().getName());
        editor.commit();
    }

    public static String getMainActivityName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(SHARED_MAIN_ACTIVITY, null);
    }

    public static void setLastPushId(Context context, String id) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_LAST_PUSH_ID, id);
        editor.commit();
    }

    public static String getLastPushId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(SHARED_LAST_PUSH_ID, null);
    }

}
