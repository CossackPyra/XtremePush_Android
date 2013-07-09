package ie.imobile.extremepush.util;

import ie.imobile.extremepush.PushConnector;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class CoarseLocationProvider {

    public interface CoarseLocationListener {
        void onCoarseLocationReceived(Location location);
    }

    protected static final String TAG = "CoarseLocationProvider";

    public static void requestCoarseLocation(Context context, final CoarseLocationListener coarseLocationListener) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String locationProvider = locationManager.getBestProvider(criteria, true);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if (PushConnector.DEBUG) Log.d(TAG, "onLocationChanged " + location.toString());

                coarseLocationListener.onCoarseLocationReceived(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestSingleUpdate(locationProvider, locationListener, null);
    }
}
