package ie.imobile.extremepush.api;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.util.SharedPrefUtils;
import ie.imobile.extremepush.util.TimeUtils;

import java.io.UnsupportedEncodingException;
import java.util.TimeZone;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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
        String carrierName = manager.getNetworkOperatorName();

        String deviceId;
        if (manager.getDeviceId() != null) {
            deviceId = manager.getDeviceId();
        } else {
            deviceId = Secure.getString(context.getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        }
        jsonEntity.put("appkey", SharedPrefUtils.getAppKey(context));
        jsonEntity.put("token", regId);
        jsonEntity.put("type", "android");
        jsonEntity.put("device_os", Build.VERSION.SDK_INT);
        jsonEntity.put("device_id", deviceId);
        jsonEntity.put("device_type", android.os.Build.MODEL);
        jsonEntity.put("environment", "production");
        jsonEntity.put("country", context.getResources().getConfiguration().locale.getCountry());
        jsonEntity.put("network", carrierName);
        jsonEntity.put("timezone", TimeUtils.getUtcTimeZone());

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

        if (PushConnector.DEBUG) Log.d(TAG, "EntityForLocationHit" + jsonString);

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
}
