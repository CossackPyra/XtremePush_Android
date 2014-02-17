package ie.imobile.extremepush.util;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationClient;

import android.content.Context;
import android.os.Bundle;

public class XPushLocationClient implements ConnectionCallbacks, OnConnectionFailedListener {
	
	private LocationClient locationClient = null;
	public XPushLocationClient(final Context context) {
		int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if (checkGooglePlayServices == ConnectionResult.SUCCESS) {
			locationClient = new LocationClient(context, this, this);
			locationClient.connect();
		}
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	}
	
	@Override
	public void onConnected(Bundle arg0) {
		locationClient.getLastLocation();
	}
	
	@Override
	public void onDisconnected() {
	}

}
