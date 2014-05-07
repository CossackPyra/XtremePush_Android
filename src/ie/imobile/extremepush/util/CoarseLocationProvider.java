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

    protected static final String TAG = CoarseLocationProvider.class.getCanonicalName();

    public static void requestCoarseLocation(Context context, final CoarseLocationListener coarseLocationListener,
    		long minTime, float minDistance) {
        
    	final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
	        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	        criteria.setAltitudeRequired(false);
	        criteria.setBearingRequired(false);
	        criteria.setCostAllowed(true);
	        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String locationProvider = locationManager.getBestProvider(criteria, true);

        LocationListener locationListener = new LocationListener() {
        	
        	@Override
            public void onLocationChanged(Location location) {
                if (PushConnector.DEBUG) Log.d(TAG, "onLocationChanged " + location.toString());
//                coarseLocationListener.onCoarseLocationReceived(location);
            }
        	@Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        	@Override
            public void onProviderEnabled(String provider) {}
        	@Override
            public void onProviderDisabled(String provider) {}
        };
        
        
        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;

        Location location = null; // location
        
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
        	Log.e(TAG, "providers are not aviable");
        } else {
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        minTime,
                        minDistance, locationListener);
                Log.d(TAG, "Network");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            Log.d(TAG, latitude + " " + longitude);
                        }
                }
            }
            if (isGPSEnabled) {
                if (location == null) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            minTime,
                            minDistance, locationListener);
                    Log.d(TAG, "GPS Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        
                    }
                }
            }
            coarseLocationListener.onCoarseLocationReceived(location);
        }
    }
}
