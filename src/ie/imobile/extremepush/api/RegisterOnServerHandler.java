package ie.imobile.extremepush.api;

import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.R;
import ie.imobile.extremepush.util.*;
import ie.imobile.extremepush.util.CoarseLocationProvider.CoarseLocationListener;

import java.lang.ref.WeakReference;
import java.util.Locale;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.AsyncHttpResponseHandler;

public final class RegisterOnServerHandler extends AsyncHttpResponseHandler {

    private static final String TAG = "RegisterOnServerHandler";
    private final String regId;

    private WeakReference<Context> contextHolder;

    public RegisterOnServerHandler(Context context, String aRegId) {
        contextHolder = new WeakReference<Context>(context);
        regId = aRegId;
    }

    public void onSuccess(int arg0, String response) {
        final Context context = contextHolder.get();
        if (context == null) return;
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

            String devicefinferpring = null;
            if (!TextUtils.equals(createFingerpring(context), SharedPrefUtils.getDeviceFingerprint(context))) {
                if (PushConnector.DEBUG) Log.d(TAG, "Device fingerprint update");
                devicefinferpring = createFingerpring(context);
            }
            SharedPrefUtils.setServerDeviceId(context, serverRegId);
            final String finalDevicefinferpring = devicefinferpring == null ? SharedPrefUtils.getDeviceFingerprint(context) : devicefinferpring;
            XtremeRestClient.hitDeviceUpdate(context, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(String response) {
                    super.onSuccess(response);
                    if (PushConnector.DEBUG_LOG) {
                        LogEventsUtils.sendLogTextMessage(context, "Response:" + response );
                    }
                    SharedPrefUtils.setDeviceFingerpirnt(context, finalDevicefinferpring);
                    GCMRegistrar.setRegisteredOnServer(context, true);
                    CoarseLocationProvider.requestCoarseLocation(context, new CoarseLocationListener() {

                                                                      @Override
                                                                      public void onCoarseLocationReceived(Location location) {
                            XtremeRestClient.locationCheck(new LocationsResponseHandler(context),
                                    SharedPrefUtils.getServerDeviceId(context), location);
                    }
                }, 60, 2000);
                }

            },
            regId);
        } else {
            if (PushConnector.DEBUG) Log.d(TAG, context.getString(R.string.server_register_error));
            
            GCMRegistrar.unregister(context);
            SharedPrefUtils.setServerDeviceId(context, null);
        }
    }

    public void onFailure(Throwable arg0, String error) {
        Context context = contextHolder.get();
        if (context == null) return;

        if (PushConnector.DEBUG) Log.d(TAG, context.getString(R.string.server_register_error) + ":" + error);
        if (PushConnector.DEBUG_LOG) LogEventsUtils.sendLogTextMessage(context, context.getString(R.string.server_register_error) + ":" + error);
        GCMRegistrar.unregister(context);
    }

    public static String createFingerpring(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        String carrierName = tm.getNetworkOperatorName();
        return generateConfigFingerprint(android.os.Build.BRAND,
                Build.VERSION.SDK_INT,
                android.os.Build.MODEL,
                context.getResources().getConfiguration().locale.getCountry(),
                carrierName,
                LibVersion.VER,
                TimeUtils.getUtcTimeZone(),
                countryCode,
                Locale.getDefault().getISO3Language());
    }

    private static String generateConfigFingerprint(Object... configs) {
        StringBuilder hashBuilder = new StringBuilder();
        for (Object config : configs) {
            hashBuilder.append(config.hashCode());
        }
        return hashBuilder.toString();
    }

}
