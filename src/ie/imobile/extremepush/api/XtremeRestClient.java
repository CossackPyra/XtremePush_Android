package ie.imobile.extremepush.api;

import com.loopj.android.http.JsonHttpResponseHandler;
import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.PushManager;
import ie.imobile.extremepush.util.LogEventsUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONException;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public final class XtremeRestClient {

    private static final String TAG = XtremeRestClient.class.getCanonicalName();
    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    private static String AGENT = "Android " + Build.VERSION.SDK_INT + " " + Build.BRAND;
    private XtremeRestClient() {
    }

    public static void registerOnServer(Context context, AsyncHttpResponseHandler responseHandler) {
        String url = PushManager.serverUrl + "/push/api/deviceCreate";
        try {
            httpClient.setTimeout(0);
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForRegistration(context), "application/json",
                    responseHandler);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Sent request to: " + url);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }

    public static void hitDeviceUpdate(Context context, AsyncHttpResponseHandler responseHandler, String regId) {
        String url = PushManager.serverUrl + "/push/api/deviceUpdate";
        try {
            httpClient.setTimeout(0);
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForUpdate(context, regId), "application/json",
                    responseHandler);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Sent request to: " + url + " with regId: "
                    + regId);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }
    
    public static void locationCheck(AsyncHttpResponseHandler responseHandler, String serverRegId, Location location) {
        String url = PushManager.serverUrl + "/push/api/locationsCheck";
        try {
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForLocationCheck(serverRegId, location),
                    "application/json", responseHandler);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }
    
    public static void locationExit(Context context, AsyncHttpResponseHandler responseHandler, String serverRegId, String locationId) {
        String url = PushManager.serverUrl + "/push/api/locationExit";
        try {
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForLocationExit(serverRegId, locationId),
                    "application/json", responseHandler);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Sent request to: " + url
                    + " with locationId: " + locationId);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }
    
    
    public static void hitPushList(Context context, AsyncHttpResponseHandler responseHandler, String serverRegId, String offset, String limit) {
        String url = PushManager.serverUrl + "/push/api/pushList";
        try {
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForPushList(serverRegId, offset, limit),
                    "application/json", responseHandler);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Sent request to: " + url
                    + " with serverRegId: " + serverRegId);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }
    
    public static void hitLocation(Context context, AsyncHttpResponseHandler responseHandler, String serverRegId,
            String locationId) {
        String url = PushManager.serverUrl + "/push/api/locationHit";
        try {
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForLocationHit(serverRegId, locationId),
                    "application/json", responseHandler);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Sent request to: " + url
                    + " with locationId: " + locationId);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }

    public static void hitAction(Context context, AsyncHttpResponseHandler responseHandler, String serverRegId,
            String hitActionId) {
        String url = PushManager.serverUrl + "/push/api/actionHit";
        try {
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForPushAction(serverRegId, hitActionId),
                    "application/json", responseHandler);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Sent request to: " + url
                    + " with actionId: " + hitActionId);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }

    public static void hitUrl(Context context, AsyncHttpResponseHandler responseHandler, String serverRegId,
            String hitUrlActionId) {
        String url = PushManager.serverUrl + "/push/api/urlHit";
        try {
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForPushAction(serverRegId, hitUrlActionId),
                    "application/json", responseHandler);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Sent request to: " + url);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }

    public static void hitTag(Context context, AsyncHttpResponseHandler responseHandler, String serverRegId, String tag) {
        String url = PushManager.serverUrl + "/push/api/tagHit";
        try {
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForHitTag(serverRegId, tag), "application/json",
                    responseHandler);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Sent request to: " + url + " with tag: "
                    + tag);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }
    
    public static void hitImpression(Context context, AsyncHttpResponseHandler responseHandler, String serverRegId, String tag) {
        String url = PushManager.serverUrl + "/push/api/impressionHit";
        try {
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForImpressionTag(serverRegId, tag), "application/json",
                    responseHandler);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Sent request to: " + url + " with tag: "
                    + tag);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }
    
    public static void hitDevStatistics(Context context, AsyncHttpResponseHandler responseHandler,
    		String serverRegId, Map<Long, Long> map) {
        String url = PushManager.serverUrl + "/push/api/deviceStatistics";
        try {
        	httpClient.setUserAgent(AGENT);
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForHitStatistics(context, serverRegId, map), "application/json",
                    responseHandler);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Sent request to: " + url);
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, e);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
    }
}
