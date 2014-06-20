package ie.imobile.extremepush.util;

import android.location.LocationManager;
import ie.imobile.extremepush.util.SharedPrefUtils;
import android.content.Context;
import android.content.pm.PackageManager;

public final class LocationUtils {
    private LocationUtils() {
    }

    public static boolean isLocationEnabled(Context context) {
    	String coarsePermissionStr = "android.permission.ACCESS_COARSE_LOCATION";
    	String finePermissionStr = "android.permission.ACCESS_FINE_LOCATION";
    	int coarseRes = context.checkCallingOrSelfPermission(coarsePermissionStr);
    	int fineRes = context.checkCallingOrSelfPermission(finePermissionStr);
    	return (coarseRes == PackageManager.PERMISSION_GRANTED) && 
    	    (fineRes == PackageManager.PERMISSION_GRANTED) && SharedPrefUtils.getLocationEnabled(context);
    }
    public static boolean isLocationProvidersEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
