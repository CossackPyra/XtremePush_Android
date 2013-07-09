package ie.imobile.extremepush.api;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.api.model.LocationItem;
import ie.imobile.extremepush.location.ProxymityAlertReceiver;
import ie.imobile.extremepush.util.LogEventsUtils;
import ie.imobile.extremepush.util.SharedPrefUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

public final class LocationsResponseHandler extends AsyncHttpResponseHandler {

    private static final String TAG = "LocationsResponseHandler";
    private static ArrayList<LocationItem> lastKnownLocations;

    private WeakReference<Context> contextHolder;

    public LocationsResponseHandler(Context context) {
        contextHolder = new WeakReference<Context>(context);
    }

    public void onSuccess(int arg0, String response) {
        Context context = contextHolder.get();
        if (PushConnector.DEBUG) Log.d(TAG, "Locations: " + response);
        if (context == null) return;

        ArrayList<LocationItem> newLocationsItems = ResponseParser.parseLocations(response);
        ArrayList<LocationItem> oldLocationsItems = null;

        String oldLocations = SharedPrefUtils.getOldLocations(context);
        if (oldLocations != null) {
            oldLocationsItems = ResponseParser.parseLocations(oldLocations);
        }

        SharedPrefUtils.setOldLocations(context, response);

        createProxymityAlerts(context, newLocationsItems, oldLocationsItems);

        if (PushConnector.DEBUG) Log.d(TAG, "Successfully obtained locations");
        if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Successfully obtained locations");

        lastKnownLocations = newLocationsItems;
        PushConnector.postInEventBus(newLocationsItems);
    }

    public void onFailure(Throwable arg0, String arg1) {
        if (PushConnector.DEBUG) Log.d(TAG, "Failed to obtaine locations");

        Context context = contextHolder.get();
        if (context == null) return;
        if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Failed to obtaine locations");
    }

    private void createProxymityAlerts(Context context, List<LocationItem> locationsItems,
            List<LocationItem> oldLocationsItems) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (oldLocationsItems != null) {
            for (LocationItem item : oldLocationsItems) {
                Intent intent = new Intent(context, ProxymityAlertReceiver.class);
                intent.putExtra(ProxymityAlertReceiver.EXTRAS_LOCATION_ID, item.id);
                intent.setAction("action" + item.id);
                locationManager.removeProximityAlert(PendingIntent.getBroadcast(context, 0, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT));
            }
        }

        for (LocationItem item : locationsItems) {
            Intent intent = new Intent(context, ProxymityAlertReceiver.class);
            intent.putExtra(ProxymityAlertReceiver.EXTRAS_LOCATION_ID, item.id);
            intent.setAction("action" + item.id);
            locationManager.addProximityAlert(item.latitude, item.longitude, item.radius, -1, PendingIntent.getBroadcast(
                    context.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT));
        }
    }

    public static ArrayList<LocationItem> getLastKnownLocations() {
        return lastKnownLocations;
    }
}
