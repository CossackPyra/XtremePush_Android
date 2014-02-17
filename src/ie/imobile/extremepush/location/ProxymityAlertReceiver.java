package ie.imobile.extremepush.location;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.api.LogResponseHandler;
import ie.imobile.extremepush.api.XtremeRestClient;
import ie.imobile.extremepush.util.SharedPrefUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

public final class ProxymityAlertReceiver extends BroadcastReceiver {

    private static final String TAG = "ProxymityAlertReceiver";
    public static final String ACTION_PROXIMITY_ALERT = "com.the_roberto.gcmlib.proximity_alert";
    public static final String EXTRAS_LOCATION_ID = "extras_location_id";

    private AsyncHttpResponseHandler hitLocationResponseHandler = new LogResponseHandler(TAG);

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String locationId = extras.getString(EXTRAS_LOCATION_ID);
        if (PushConnector.DEBUG) Log.d(TAG, "locationId:" + locationId);

        final Boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
        if (entering) {
            XtremeRestClient.hitLocation(context, hitLocationResponseHandler, SharedPrefUtils.getServerDeviceId(context),
                    locationId);
        } else {
            XtremeRestClient.locationExit(context, hitLocationResponseHandler, SharedPrefUtils.getServerDeviceId(context),
                    locationId);
        }
    }
}
