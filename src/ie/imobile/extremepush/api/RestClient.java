package ie.imobile.extremepush.api;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.PushManager;
import ie.imobile.extremepush.util.LogEventsUtils;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public final class RestClient {

    private static final String TAG = "RestClient";
    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    private RestClient() {
    }

    public static void registerOnServer(Context context, AsyncHttpResponseHandler responseHandler, String regId) {
        String url = PushManager.serverUrl + "/push/api/deviceCreate";
        try {
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForRegistration(context, regId), "application/json",
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
            httpClient.post(null, url, RequestBuilder.buildJsonEntityForLocationCheck(serverRegId, location),
                    "application/json", responseHandler);
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
}
