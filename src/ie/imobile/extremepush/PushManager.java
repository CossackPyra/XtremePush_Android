package ie.imobile.extremepush;

import ie.imobile.extremepush.api.DeviceUpdateHandler;
import ie.imobile.extremepush.api.EventResponseHandler;
import ie.imobile.extremepush.api.LocationsResponseHandler;
import ie.imobile.extremepush.api.LogResponseHandler;
import ie.imobile.extremepush.api.RegisterOnServerHandler;
import ie.imobile.extremepush.api.XtremeRestClient;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.config.LocationConfig;
import ie.imobile.extremepush.location.LocationReceiver;
import ie.imobile.extremepush.util.*;
import ie.imobile.extremepush.util.CoarseLocationProvider.CoarseLocationListener;

import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.locpoll.LocationPollerParameter;
import com.google.android.gcm.GCMRegistrar;

public final class PushManager {

    private static final String TAG = "PushManager";

    public static String appKey;
    public static String senderId;
    public static String serverUrl;

    private PushConnector pushConnector;

    private String regId;
    private boolean isConfigsUpdated;
    private boolean isDestroyed;
    
    public static int locationCheckTimeout = LocationConfig.LOCATION_CHECK_TIMEOUT;
    public static float locationDistance = LocationConfig.LOCATION_DISTANCE;

    private LocationAccessHelper locationAccessHelper;
    private PushListener pushListener;

    private boolean showDialog = true;
    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context activity, Intent intent) {
            Bundle extras = intent.getExtras();
            String action = intent.getAction();

            if (PushConnector.DEBUG) Log.d(TAG, "Receive broadcast");

            regId = extras.getString(GCMIntentService.EXTRAS_REG_ID);
            if (action.equals(GCMIntentService.ACTION_REGISTER_ON_SERVER)) {
                XtremeRestClient.registerOnServer(pushConnector.getActivity(),
                        new RegisterOnServerHandler(pushConnector.getActivity(), regId));
            } else if (action.equals(GCMIntentService.ACTION_MESSAGE)) {
                PushMessage pushMessage = extras.getParcelable(GCMIntentService.EXTRAS_PUSH_MESSAGE);
                boolean fromNotification = extras.getBoolean(GCMIntentService.EXTRAS_FROM_NOTIFICATION);
                PushMessageDisplayHelper.displayPushMessage(pushConnector.getActivity(),
                		pushConnector.getFragmentManager(),
                        pushMessage, pushConnector.isResumed(), fromNotification, showDialog);
                if (PushConnector.DEBUG) Log.d(TAG, "ReceiveMessage" + pushMessage);
            }
        }
    };

    PushManager(PushConnector pushConnector, String serverUrl, String appKey, String senderId, int locationCheckTimeout, float locationDistance) {
        this.pushConnector = pushConnector;
        this.appKey = appKey;
        this.senderId = senderId;
        this.serverUrl = serverUrl;
        this.locationCheckTimeout = locationCheckTimeout;
        this.locationDistance = locationDistance;

    }

    public void init() {
        isConfigsUpdated = isConfigsUpdated();
        if (PushConnector.DEBUG) {
            Log.d(TAG, "Configs updated: " + isConfigsUpdated);
            updateConfigs(pushConnector.getActivity(), serverUrl, appKey, senderId);
        }

        SharedPrefUtils.setMainActivityUris(pushConnector.getActivity());

        initHelpers(pushConnector.getActivity());

        locationAccessHelper.checkLocationProviders();
    }

    public void setLocationCheckTimeout(int min) {
    	locationCheckTimeout = min;
    }
    
    void onStart() {
    	setupGCM();

        Intent activityStateIntent = new Intent(GCMIntentService.ACTIVITY_IN_BACKGROUND);
        activityStateIntent.putExtra(GCMIntentService.ACTIVITY_IN_BACKGROUND, false);
        pushConnector.getActivity().sendBroadcast(activityStateIntent);

        final Context ctx = pushConnector.getActivity().getApplicationContext();
        if (TextUtils.equals(createFingerpring(ctx), SharedPrefUtils.getDeviceFingerprint(ctx))
                || !GCMRegistrar.isRegistered(ctx) || TextUtils.isEmpty(SharedPrefUtils.getServerDeviceId(ctx))) return;
        if (PushConnector.DEBUG) Log.d(TAG, "Device fingerprint update");
        XtremeRestClient.hitDeviceUpdate(ctx, new DeviceUpdateHandler(ctx, regId), regId);

    }

    void onStop() {

        Intent activityStateIntent = new Intent(GCMIntentService.ACTIVITY_IN_BACKGROUND);
        activityStateIntent.putExtra(GCMIntentService.ACTIVITY_IN_BACKGROUND, true);
        pushConnector.getActivity().sendBroadcast(activityStateIntent);

    }

    void onResume() {
//    	checkConf(pushConnector.getActivity());
    }
    
    void onDestroy() {
        if (PushConnector.DEBUG) Log.d(TAG, "onDestroy");
        isDestroyed = true;

        pushConnector.getActivity().unregisterReceiver(messageReceiver);
        pushConnector = null;
    }

    void onAttach(Activity activity) {

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(GCMIntentService.ACTION_MESSAGE);
        filter.addAction(GCMIntentService.ACTION_REGISTER_ON_SERVER);
        pushConnector.getActivity().registerReceiver(messageReceiver, filter);
        onNewIntent(pushConnector.getActivity().getIntent());
    }

    void onDetach() {
        if (pushConnector != null) pushConnector.getActivity().unregisterReceiver(messageReceiver);
    }

    void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PushConnector.DEBUG) Log.d(TAG, "onActivityResult");
        if (isDestroyed) throw new IllegalStateException("PushManager already destoyed");
        locationAccessHelper.onActivityResult(requestCode, resultCode, data);
    }

    void onNewIntent(Intent intent) {
        if (isDestroyed) throw new IllegalStateException("PushManager already destoyed");
        if (intent == null) return;

        Bundle extras = intent.getExtras();

        if (extras == null) return;

        PushMessage pushMessage = extras.getParcelable(GCMIntentService.EXTRAS_PUSH_MESSAGE);
        boolean fromNotification = extras.getBoolean(GCMIntentService.EXTRAS_FROM_NOTIFICATION);

        if (pushListener != null)
            pushListener.onPushMessage(pushMessage);

        PushMessageDisplayHelper.displayPushMessage(pushConnector.getActivity(), pushConnector.getActivity()
                .getSupportFragmentManager(), pushMessage, pushConnector.isResumed(), fromNotification, showDialog);
    }

    void hitTag(String tag) {
        if (isDestroyed) throw new IllegalStateException("PushManager already destoyed");
        XtremeRestClient.hitTag(pushConnector.getActivity(), new LogResponseHandler("hitTag"),
                SharedPrefUtils.getServerDeviceId(pushConnector.getActivity()), tag);
    }
    
    void hitImpression(String tag) {
        if (isDestroyed) throw new IllegalStateException("PushManager already destoyed");
        XtremeRestClient.hitImpression(pushConnector.getActivity(), new LogResponseHandler("hitImpression"),
                SharedPrefUtils.getServerDeviceId(pushConnector.getActivity()), tag);
    }
    
    void getPushlist(String offset, String limit) {
        if (isDestroyed) throw new IllegalStateException("PushManager already destoyed");
        XtremeRestClient.hitPushList(pushConnector.getActivity(),
        		new EventResponseHandler(pushConnector.getActivity()), 
        		SharedPrefUtils.getServerDeviceId(pushConnector.getActivity()), offset, limit);
    }

    private void updateConfigs(Activity activity, String serverUrl, String appKey, String senderId) {
        SharedPrefUtils.setAppKey(activity, appKey);
        SharedPrefUtils.setSenderId(activity, senderId);
        if (TextUtils.isEmpty(SharedPrefUtils.getServerUrl(activity))) {
        	SharedPrefUtils.setServerUrl(activity, serverUrl);
        }
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    private boolean isConfigsUpdated() {
        final Activity activity = pushConnector.getActivity();

        String newFingerprint = generateConfigFingerprint(appKey, senderId, serverUrl);
        String oldFingerprint = SharedPrefUtils.getConfigsFingerprint(activity);

        if (oldFingerprint == null) {
            SharedPrefUtils.setConfigsFingerpirnt(activity, newFingerprint);
            return false;
        }

        if (!oldFingerprint.equals(newFingerprint)) {
            SharedPrefUtils.setConfigsFingerpirnt(activity, newFingerprint);
            return true;
        }

        return false;
    }

    private void initHelpers(Activity activity) {
        locationAccessHelper = new LocationAccessHelper(pushConnector);
    }

    private void checkIntent() {
        Intent intent = pushConnector.getActivity().getIntent();
        Bundle extras;

        if (intent == null || (extras = intent.getExtras()) == null) return;

        PushMessage pushMessage = extras.getParcelable(GCMIntentService.EXTRAS_PUSH_MESSAGE);
        boolean fromNotification = extras.getBoolean(GCMIntentService.EXTRAS_FROM_NOTIFICATION);

        PushMessageDisplayHelper.displayPushMessage(pushConnector.getActivity(), pushConnector.getActivity()
                .getSupportFragmentManager(), pushMessage, pushConnector.isResumed(), fromNotification, showDialog);
    }

    public String getGCMToken() {
    	return regId;
    }
    
    public String getDeviceID() {
    	return SharedPrefUtils.getServerDeviceId(pushConnector.getActivity().getApplicationContext());
    }
    
    private void setupGCM() {
        final Activity activity = pushConnector.getActivity();
        final Context appContext = activity.getApplicationContext();

		try {
			GCMRegistrar.checkDevice(appContext);
			GCMRegistrar.checkManifest(appContext);
		} catch (UnsupportedOperationException e) {
			Log.e(TAG, "Device does not have package com.google.android.gsf");
		}

        regId = GCMRegistrar.getRegistrationId(appContext);
        if (PushConnector.DEBUG) Log.d(TAG, "GCM id:" + regId);

        if (!GCMRegistrar.isRegistered(appContext)) {
            GCMRegistrar.register(appContext, senderId);
        } else {
            if (GCMRegistrar.isRegisteredOnServer(appContext) && !isConfigsUpdated) {
                if (PushConnector.DEBUG) Log.d(TAG, "Device is already registered on server");

                CoarseLocationProvider.requestCoarseLocation(activity, new CoarseLocationListener() {
                    @Override
                    public void onCoarseLocationReceived(Location location) {
                        if (PushConnector.DEBUG && location != null) Log.d(TAG, "Coarse location received:" + location.toString() 
                        		+ " with device id: " + SharedPrefUtils.getServerDeviceId(appContext));
                        XtremeRestClient.locationCheck(new LocationsResponseHandler(appContext),
                                SharedPrefUtils.getServerDeviceId(appContext), location);
                    }
                }, locationCheckTimeout, locationDistance);
            } else {
                if (PushConnector.DEBUG) Log.d(TAG, "Register on server from PushManager");
                XtremeRestClient.registerOnServer(appContext, new RegisterOnServerHandler(appContext, regId));
                String devicefinferpring = createFingerpring(appContext);
                SharedPrefUtils.setDeviceFingerprint(appContext, devicefinferpring);
                
            }
        }
        
        if (LocationUtils.isLocationEnabled(appContext))
            createLocationPoller(appContext);
    }

    private void createLocationPoller(Context context) {
        final AlarmManager mgr=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        final Intent i = new Intent(context, LocationPoller.class);
        final Bundle bundle = new Bundle();
        final LocationPollerParameter parameter = new LocationPollerParameter(bundle);
            parameter.setIntentToBroadcastOnCompletion(new Intent(context, LocationReceiver.class));
            parameter.setProviders(new String[] {LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER});
        i.putExtras(bundle);

        final PendingIntent pi=PendingIntent.getBroadcast(context, 0, i, 0);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP,
                                            System.currentTimeMillis()+10000,
                                            1000*60*locationCheckTimeout,
                                            pi);
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

    public void setPushListener(PushListener pushListener) {
        this.pushListener = pushListener;
    }
}
