package ie.imobile.extremepush.api;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.PushManager;
import ie.imobile.extremepush.R;
import ie.imobile.extremepush.config.ConnectionConfig;
import ie.imobile.extremepush.util.CoarseLocationProvider;
import ie.imobile.extremepush.util.CoarseLocationProvider.CoarseLocationListener;
import ie.imobile.extremepush.util.ExponentialDelay;
import ie.imobile.extremepush.util.FingerPrintManager;
import ie.imobile.extremepush.util.LogEventsUtils;
import ie.imobile.extremepush.util.ReconnectDelay;
import ie.imobile.extremepush.util.SharedPrefUtils;

public class DeviceUpdateHandler extends AsyncHttpResponseHandler {

    private static final String TAG = "DeviceUpdateHandler";

    private WeakReference<Context> contextHolder;
    private Handler mHandler;
    private int mNumberOfRetries;
    private Long mTimeout;
    private Runnable mRunnable;
    private int mCurrentIteration;
    private String mRegId;
    private ReconnectDelay mReconnectDelay;

    public DeviceUpdateHandler(Context context, String regId, Long timeOut, int numberOfRetries) {
        init(context, regId, timeOut, numberOfRetries);
    }
    public DeviceUpdateHandler(Context context, String regId) {
        init(context, regId, ConnectionConfig.SERVER_CONNECTION_TIMEOUT,
                ConnectionConfig.SERVER_CONNECTION_RETRIES);
    }

    private void init(Context context, String regId, Long timeOut, int numberOfRetries) {
        contextHolder = new WeakReference<Context>(context);
        mCurrentIteration = 0;
        mHandler = new Handler();
        mTimeout = timeOut;
        mRegId = regId;
        mNumberOfRetries = numberOfRetries;
        mReconnectDelay = new ExponentialDelay();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Context context = contextHolder.get();
                if (context == null) return; // No iterations context has been lost.
                XtremeRestClient.hitDeviceUpdate(context, DeviceUpdateHandler.this, mRegId);

            }
        };
    }

    public void onSuccess(int arg0, String response) {

        final Context context = contextHolder.get();
        if (context == null) return;

        if (PushConnector.DEBUG_LOG) {
            LogEventsUtils.sendLogTextMessage(context, "Response:" + response);
        }

        final String deviceFingerprint = FingerPrintManager.createFingerprint(context);
        if (!TextUtils.equals(deviceFingerprint, SharedPrefUtils.getDeviceFingerprint(context))) {
            if (PushConnector.DEBUG) Log.d(TAG, "Device fingerprint update");
            SharedPrefUtils.setDeviceFingerprint(context, FingerPrintManager.createFingerprint(context));
        }
        SharedPrefUtils.setDeviceFingerprint(context, deviceFingerprint);
        GCMRegistrar.setRegisteredOnServer(context, true);
        CoarseLocationProvider.requestCoarseLocation(context, new CoarseLocationListener() {
                @Override
                public void onCoarseLocationReceived(Location location) {
                    XtremeRestClient.locationCheck(new LocationsResponseHandler(context),
                            SharedPrefUtils.getServerDeviceId(context), location);
                }
            }, PushManager.locationCheckTimeout, PushManager.locationDistance);
    }

    private boolean needToResend(Context context, int arg0) {
        switch (arg0) {
            case HttpStatus.SC_BAD_GATEWAY:
            case HttpStatus.SC_SERVICE_UNAVAILABLE:
            case HttpStatus.SC_GATEWAY_TIMEOUT:
                if (mCurrentIteration < mNumberOfRetries)
                    mHandler.postDelayed(mRunnable, mTimeout);
                mCurrentIteration++;
                if (PushConnector.DEBUG) Log.d(TAG,
                        context.getString(R.string.device_update_response_error) + ":" + arg0);
                if (PushConnector.DEBUG_LOG) LogEventsUtils.sendLogTextMessage(context,
                        context.getString(R.string.device_update_response_error) + ":" + arg0);
                return true;
            default:
                return false;
        }
    }

    public void onFailure(Throwable arg0, String error) {
        Context context = contextHolder.get();
        if (context == null) return;

        if (PushConnector.DEBUG) Log.d(TAG, context.getString(R.string.device_update_response_error) + ":" + error);
        if (PushConnector.DEBUG_LOG) LogEventsUtils.sendLogTextMessage(context, context.getString(R.string.device_update_response_error) + ":" + error);

        final JSONObject responseJson;
        int code = -1;
        try {
            responseJson = new JSONObject(error);
            code = responseJson.getInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        switch (code) {
            case HttpStatus.SC_BAD_GATEWAY:
            case HttpStatus.SC_SERVICE_UNAVAILABLE:
            case HttpStatus.SC_GATEWAY_TIMEOUT:
                if (mCurrentIteration < mNumberOfRetries) {
                    if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Delayed registration on" + " : " +
                            mReconnectDelay.getDelay(mTimeout, mCurrentIteration) / 1000 + " seconds.");
                    mHandler.postDelayed(mRunnable, mReconnectDelay.getDelay(mTimeout, mCurrentIteration));
                    mCurrentIteration++;
                }
        }
    }
}
