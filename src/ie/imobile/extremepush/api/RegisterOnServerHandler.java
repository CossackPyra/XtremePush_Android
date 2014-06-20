package ie.imobile.extremepush.api;

import android.os.Handler;
import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.R;
import ie.imobile.extremepush.config.ConnectionConfig;
import ie.imobile.extremepush.util.*;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.HttpStatus;

public final class RegisterOnServerHandler extends AsyncHttpResponseHandler {

    private static final String TAG = "RegisterOnServerHandler";
    private String regId;

    private WeakReference<Context> contextHolder;
    private Handler mHandler;
    private int mNumberOfRetries;
    private Long mTimeout;
    private Runnable mRunnable;
    private int mCurrentIteration;

    public RegisterOnServerHandler(Context context, String aRegId, Long timeOut, int numberOfRetries) {
        init(context, aRegId, timeOut, numberOfRetries);
    }
    public RegisterOnServerHandler(Context context, String aRegId) {
        init(context, aRegId, ConnectionConfig.SERVER_CONNECTION_TIMEOUT,
                ConnectionConfig.SERVER_CONNECTION_RETRIES);
    }

    private void init(Context context, String aRegId, Long timeOut, int numberOfRetries) {
        contextHolder = new WeakReference<Context>(context);
        regId = aRegId;
        mCurrentIteration = 0;
        mHandler = new Handler();
        mTimeout = timeOut;
        mNumberOfRetries = numberOfRetries;
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Context context = contextHolder.get();
                if (context == null) return; // No iterations context has been lost.
                    XtremeRestClient.registerOnServer(context, RegisterOnServerHandler.this);

            }
        };
    }

    public void onSuccess(int arg0, String response) {

        final Context context = contextHolder.get();
        if (context == null) return;
        if (needToResend(context, arg0)) return;
        if (PushConnector.DEBUG_LOG) {
        	LogEventsUtils.sendLogTextMessage(context, "Response:" + response );
        }

        if (PushConnector.DEBUG) Log.d(TAG, "Response: " + response);

        String serverRegId = null;
        if (response != null)
            serverRegId = ResponseParser.parseRegisterOnServerResponse(response, context);

        if (serverRegId != null) {
            if (PushConnector.DEBUG) Log.d(TAG, " RegId: " + serverRegId);
            if (PushConnector.DEBUG_LOG) LogEventsUtils.sendLogTextMessage(context, "Registred on server with id: "
                    + serverRegId);

            SharedPrefUtils.setServerDeviceId(context, serverRegId);
            XtremeRestClient.hitDeviceUpdate(context, new DeviceUpdateHandler(context, regId), regId);
        } else {
            if (PushConnector.DEBUG) Log.d(TAG, context.getString(R.string.server_register_error));
            
            GCMRegistrar.unregister(context);
            SharedPrefUtils.setServerDeviceId(context, null);
        }
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
                        context.getString(R.string.server_register_response_error) + ":" + arg0);
                if (PushConnector.DEBUG_LOG) LogEventsUtils.sendLogTextMessage(context,
                        context.getString(R.string.server_register_response_error) + ":" + arg0);
                return true;
            default:
                return false;
        }
    }

    public void onFailure(Throwable arg0, String error) {
        Context context = contextHolder.get();
        if (context == null) return;

        if (PushConnector.DEBUG) Log.d(TAG, context.getString(R.string.server_register_error) + ":" + error);
        if (PushConnector.DEBUG_LOG) LogEventsUtils.sendLogTextMessage(context, context.getString(R.string.server_register_error) + ":" + error);
        GCMRegistrar.unregister(context);
    }


}
