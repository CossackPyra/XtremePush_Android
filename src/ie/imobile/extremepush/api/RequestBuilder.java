package ie.imobile.extremepush.api;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.util.LibVersion;
import ie.imobile.extremepush.util.SharedPrefUtils;
import ie.imobile.extremepush.util.TimeUtils;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

public final class RequestBuilder {

    private static final String TAG = "RequestBuilder";

    private RequestBuilder() {
    }

    static StringEntity buildJsonEntityForRegistration(Context context, String regId) throws UnsupportedEncodingException,
            JSONException {
        JSONObject jsonEntity = new JSONObject();

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String carrierName = manager.getNetworkOperatorName();
        final String libVer = LibVersion.VER;
        final String countryCode = manager.getSimCountryIso();
        
        String deviceId;
        if (manager.getDeviceId() != null) {
            deviceId = manager.getDeviceId();
        } else {
            deviceId = Secure.getString(context.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        }
        
        final PackageManager packageManager = context.getPackageManager();
        String versionName = null;
        if (packageManager != null) {
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                versionName = packageInfo.versionName + " " + packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                versionName = "none";
            }
        }
        
        jsonEntity.put("id", SharedPrefUtils.getServerDeviceId(context));
        jsonEntity.put("appkey", SharedPrefUtils.getAppKey(context));
        jsonEntity.put("token", regId);
        
        jsonEntity.put("type", "android");
        jsonEntity.put("name", android.os.Build.BRAND);
        
        jsonEntity.put("device_id", deviceId);
        jsonEntity.put("device_os", Build.VERSION.SDK_INT);
        jsonEntity.put("device_type", android.os.Build.MODEL);

        jsonEntity.put("environment", "production");
        
        jsonEntity.put("country", manager.getSimCountryIso());
        
        jsonEntity.put("carrier_name", manager.getSimOperatorName());

        jsonEntity.put("timezone", TimeUtils.getUtcTimeZone());
        jsonEntity.put("bundle_version", versionName);
        jsonEntity.put("lib_version", libVer);
        
        jsonEntity.put("language", Locale.getDefault().getLanguage());


        String jsonString = jsonEntity.toString();
    
        if (PushConnector.DEBUG) Log.d(TAG, "EntityForRegistration: " + jsonString);

        return new StringEntity(jsonString, HTTP.UTF_8);
    }

    static StringEntity buildJsonEntityForLocationCheck(String serverRegId, Location location)
            throws UnsupportedEncodingException, JSONException {
        JSONObject jsonEntity = new JSONObject();

        jsonEntity.put("id", serverRegId);

        JSONObject locationObject = new JSONObject();
        locationObject.put("latitude", location.getLatitude());
        locationObject.put("longitude", location.getLongitude());

        jsonEntity.put("location", locationObject);
        String jsonString = jsonEntity.toString();

        if (PushConnector.DEBUG) Log.d(TAG, "EntityForLocationCheck" + jsonString);

        return new StringEntity(jsonString, HTTP.UTF_8);
    }

    static StringEntity buildJsonEntityForLocationHit(String serverRegId, String locationId)
            throws UnsupportedEncodingException, JSONException {
        JSONObject jsonEntity = new JSONObject();

        jsonEntity.put("id", serverRegId);
        jsonEntity.put("location_id", locationId);

        String jsonString = jsonEntity.toString();

        if (PushConnector.DEBUG) Log.d(TAG, "EntityForLocationHit " + jsonString);

        return new StringEntity(jsonString, HTTP.UTF_8);
    }

    static StringEntity buildJsonEntityForLocationExit(String serverRegId, String locationId)
            throws UnsupportedEncodingException, JSONException {
        JSONObject jsonEntity = new JSONObject();

        jsonEntity.put("id", serverRegId);
        jsonEntity.put("location_id", locationId);

        String jsonString = jsonEntity.toString();

        if (PushConnector.DEBUG) Log.d(TAG, "EntityForLocationExit " + jsonString);

        return new StringEntity(jsonString, HTTP.UTF_8);
    }
    
    static StringEntity buildJsonEntityForPushList(String serverRegId, String offset, String limit)
            throws UnsupportedEncodingException, JSONException {
        JSONObject jsonEntity = new JSONObject();

        jsonEntity.put("id", serverRegId);
        jsonEntity.put("offset", offset);
        jsonEntity.put("limit", limit);

        String jsonString = jsonEntity.toString();

        if (PushConnector.DEBUG) Log.d(TAG, "buildJsonEntityForPushList " + jsonString);

        return new StringEntity(jsonString, HTTP.UTF_8);
    }

    
    static StringEntity buildJsonEntityForPushAction(String serverRegId, String pushActionId) throws JSONException,
            UnsupportedEncodingException {
        JSONObject jsonEntity = new JSONObject();

        jsonEntity.put("id", serverRegId);
        jsonEntity.put("action_id", pushActionId);

        String jsonString = jsonEntity.toString();

        if (PushConnector.DEBUG) Log.d(TAG, "EntityForPushAction" + jsonString);

        return new StringEntity(jsonString, HTTP.UTF_8);
    }

    static StringEntity buildJsonEntityForHitTag(String serverRegId, String tag) throws JSONException,
            UnsupportedEncodingException {
        JSONObject jsonEntity = new JSONObject();

        jsonEntity.put("id", serverRegId);
        jsonEntity.put("tag", tag);

        String jsonString = jsonEntity.toString();

        if (PushConnector.DEBUG) Log.d(TAG, "EntityForHitTag" + jsonString);

        return new StringEntity(jsonString, HTTP.UTF_8);
    }
    
    static StringEntity buildJsonEntityForImpressionTag(String serverRegId, String tag) throws JSONException,
	    UnsupportedEncodingException {
		JSONObject jsonEntity = new JSONObject();
		
		jsonEntity.put("id", serverRegId);
		jsonEntity.put("impression", tag);
		jsonEntity.put("device_count", "device_count");
		
		String jsonString = jsonEntity.toString();
		
		if (PushConnector.DEBUG) Log.d(TAG, "buildJsonEntityForImpressionTag " + jsonString);
		
		return new StringEntity(jsonString, HTTP.UTF_8);
	}
	    
    public static StringEntity buildJsonEntityForHitStatistics(Context context, String serverRegId, Map<Long, Long> log) throws JSONException,
	    UnsupportedEncodingException {
	JSONObject jsonEntity = new JSONObject();
    final PackageManager packageManager = context.getPackageManager();
    String versionName = null;
    if (packageManager != null) {
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName + " " + packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "none";
        }
    }
    
	jsonEntity.put("id", serverRegId);
	
	JSONObject jsonSessionsEntity = new JSONObject();
	for (Entry<Long, Long> entry: log.entrySet()) {
		jsonSessionsEntity.put("start", entry.getKey());
		jsonSessionsEntity.put("length", entry.getValue());
	}
	jsonEntity.put("sessions", jsonSessionsEntity);
    jsonEntity.put("bundle_version", versionName);
    jsonEntity.put("lib_version", LibVersion.VER);
	
	String jsonString = jsonEntity.toString();
	
	if (PushConnector.DEBUG) Log.d(TAG, "Entity For hit statictic" + jsonString);
	
	return new StringEntity(jsonString, HTTP.UTF_8);
}
}
