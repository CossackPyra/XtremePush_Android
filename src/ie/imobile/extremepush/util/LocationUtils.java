package ie.imobile.extremepush.util;

import android.location.LocationManager;

public final class LocationUtils {
    private LocationUtils() {
    }

    public static boolean isLocationProvidersEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
