package ie.imobile.extremepush;

import ie.imobile.extremepush.api.LocationsResponseHandler;
import ie.imobile.extremepush.api.model.LocationItem;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import ie.imobile.extremepush.util.MonitoringUtils;
import ie.imobile.extremepush.util.SharedPrefUtils;

public class PushConnector extends Fragment {

    private static final String TAG = PushConnector.class.getCanonicalName();
    private static final String FRAGMENT_TAG = "PushConnector";
    private static final String EXTRAS_SERVER_URL = "extras_server_url";
    private static final String EXTRAS_APP_KEY = "extras_app_key";
    private static final String EXTRAS_SENDER_ID = "extras_sender_id";
    private static final String TIMEOUT = "time_out";
    private static final String LOCALDIST = "locationDistance";

    // Constants for events logging
    public static final String ACTION_EVENT_MESSAGE = "ie.imobile.extremepush.action_event_message";
    public static final String EXTRAS_EVENT_MESSAGE = "extras_message";

    public static boolean DEBUG = true;
    public static boolean DEBUG_LOG = true;

    private static final Bus BUS = new Bus();

    private String serverUrl;
    private String appKey;
    private String senderId;
    private int locationCheckTimeout;
    private float locationDistance;
    private PushManager pushManager;

    private static String SERVER_URL = "https://xtremepush.com";

    public static PushConnector init(FragmentManager fm, String appKey, String GOOGLE_PROJECT_NUMBER, int locationCheckTimeout, float locationDistance) {
        PushConnector pushConnector = (PushConnector) fm.findFragmentByTag(FRAGMENT_TAG);
        if (pushConnector != null) return pushConnector;
        
        pushConnector = newInstance(SERVER_URL, appKey, GOOGLE_PROJECT_NUMBER, locationCheckTimeout, locationDistance);

        FragmentTransaction ft = fm.beginTransaction();
        	ft.add(pushConnector, FRAGMENT_TAG);
        ft.commitAllowingStateLoss();

        return pushConnector;
    }
    
    public static PushConnector init(FragmentManager fm, String appKey, String GOOGLE_PROJECT_NUMBER) {
        PushConnector pushConnector = (PushConnector) fm.findFragmentByTag(FRAGMENT_TAG);
        if (pushConnector != null) return pushConnector;
        
        pushConnector = newInstance(SERVER_URL, appKey, GOOGLE_PROJECT_NUMBER, 60, 2000);

        FragmentTransaction ft = fm.beginTransaction();
        	ft.add(pushConnector, FRAGMENT_TAG);
        ft.commitAllowingStateLoss();

        return pushConnector;
    }

    public static PushConnector init(FragmentManager fm, String appKey, String senderId, String serverUrl) {
    	if (serverUrl != null) {
    		SERVER_URL = serverUrl;
    	}
        return init(fm, appKey, senderId);
    }
    
    public static PushConnector init(FragmentManager fm, String appKey, String senderId, String serverUrl, boolean debug) {
        DEBUG_LOG = debug;
        return init(fm, appKey, senderId, serverUrl);
    }
    
    private static PushConnector newInstance(String serverUrl, String appKey, String senderId, int locationCheckTimeout, float locationDistance) {
        PushConnector fragment = new PushConnector();

        Bundle args = new Bundle();
	        args.putString(EXTRAS_SERVER_URL, serverUrl);
	        args.putString(EXTRAS_APP_KEY, appKey);
	        args.putString(EXTRAS_SENDER_ID, senderId);
	        args.putInt(TIMEOUT, locationCheckTimeout);
	        args.putFloat(LOCALDIST, locationDistance);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (pushManager == null) {
            parseArgs();
            pushManager = new PushManager(this, serverUrl, appKey, senderId, locationCheckTimeout, locationDistance);
            return;
        }
        pushManager.onAttach(activity);
    }

    private String getGCMTokken() {
    	return pushManager.getGCMToken();
    }
    
    private String getDeviceID() {
    	return pushManager.getDeviceID();
    }
    
    public HashMap<String, String> getDeviceInfo() {
    	HashMap<String, String> infopair = new HashMap<String, String>(2);
    		infopair.put("deviceToken", getGCMTokken());
    		infopair.put("XPushDeviceID", getDeviceID());
    	return infopair;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PushConnector.DEBUG) Log.d(TAG, "onCreate() v1.1");

        setRetainInstance(true);
        pushManager.init();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pushManager.onActivityCreated(savedInstanceState);
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	pushManager.onStart();
        MonitoringUtils.startSession(getActivity());
    }
    
    @Override
    public void onResume() {
        super.onResume();
        BUS.register(this);
        pushManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        BUS.unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        pushManager.onStop();
        MonitoringUtils.stopSession();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pushManager.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        pushManager.onDetach();
    }

    private void parseArgs() {
        Bundle args = getArguments();
        if (args == null)
            throw new IllegalStateException("You need to create PushConnector through "
                    + "newInstance(String serverUrl, String appKey, String senderId)");

        this.serverUrl = args.getString(EXTRAS_SERVER_URL);
        this.appKey = args.getString(EXTRAS_APP_KEY);
        this.senderId = args.getString(EXTRAS_SENDER_ID);
        this.locationCheckTimeout = args.getInt(TIMEOUT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pushManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onNewIntent(Intent intent) {
        pushManager.onNewIntent(intent);
    }

    public void hitTag(String tag) {
        pushManager.hitTag(tag);
    }

    public void hitImpression(String tag) {
        pushManager.hitImpression(tag);
    }
    
    public void getPushlist(final int offset, int limit) {
    	pushManager.getPushlist(String.valueOf(offset), String.valueOf(limit));
    }

    public void setShowAlertDialog(boolean showDialog) {
        pushManager.setShowDialog(showDialog);
    }

    public static void postInEventBus(Object event) {
        BUS.post(event);
    }

    public static void registerInEventBus(Object obj) {
        BUS.register(obj);
    }

    public static void unregisterInEventBus(Object obj) {
        BUS.unregister(obj);
    }

    public static void locationEnabled(Context aContext, boolean aLocationEnabled) {
        SharedPrefUtils.setLocationEnabled(aContext, aLocationEnabled);
    }

    @Produce
    public ArrayList<LocationItem> produceLocations() {
        return LocationsResponseHandler.getLastKnownLocations();
    }

    public void setPushListener(PushListener pushListener) {
        pushManager.setPushListener(pushListener);
    }
}
