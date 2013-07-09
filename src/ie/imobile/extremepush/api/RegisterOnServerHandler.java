package ie.imobile.extremepush.api;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.R;
import ie.imobile.extremepush.util.CoarseLocationProvider;
import ie.imobile.extremepush.util.CoarseLocationProvider.CoarseLocationListener;
import ie.imobile.extremepush.util.LogEventsUtils;
import ie.imobile.extremepush.util.SharedPrefUtils;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.AsyncHttpResponseHandler;

public final class RegisterOnServerHandler extends AsyncHttpResponseHandler {

    private static final String TAG = "RegisterOnServerHandler";

    private WeakReference<Context> contextHolder;

    public RegisterOnServerHandler(Context context) {
        contextHolder = new WeakReference<Context>(context);
    }

    public void onSuccess(int arg0, String response) {
        final Context context = contextHolder.get();
        if (context == null) return;

        if (PushConnector.DEBUG) Log.d(TAG, "Response: " + response);

        String serverRegId = ResponseParser.parseRegisterOnServerResponse(response);

        if (serverRegId != null) {
            if (PushConnector.DEBUG) Log.d(TAG, " RegId: " + serverRegId);
            if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Registred on server with id: "
                    + serverRegId);

            GCMRegistrar.setRegisteredOnServer(context, true);
            SharedPrefUtils.setServerDeviceId(context, serverRegId);
            CoarseLocationProvider.requestCoarseLocation(context, new CoarseLocationListener() {

                @Override
                public void onCoarseLocationReceived(Location location) {
                    RestClient.locationCheck(new LocationsResponseHandler(context),
                            SharedPrefUtils.getServerDeviceId(context), location);
                }
            });
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
        GCMRegistrar.unregister(context);
    }

}
