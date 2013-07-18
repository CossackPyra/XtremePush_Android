package ie.imobile.extremepush;

import ie.imobile.extremepush.api.LocationsResponseHandler;
import ie.imobile.extremepush.api.LogResponseHandler;
import ie.imobile.extremepush.api.RegisterOnServerHandler;
import ie.imobile.extremepush.api.RestClient;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.util.CoarseLocationProvider;
import ie.imobile.extremepush.util.CoarseLocationProvider.CoarseLocationListener;
import ie.imobile.extremepush.util.LocationAccessHelper;
import ie.imobile.extremepush.util.PushMessageDisplayHelper;
import ie.imobile.extremepush.util.SharedPrefUtils;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

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

    private LocationAccessHelper locationAccessHelper;

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context activity, Intent intent) {
            Bundle extras = intent.getExtras();
            String action = intent.getAction();

            if (PushConnector.DEBUG) Log.d(TAG, "Receive broadcast");
            if (action.equals(GCMIntentService.ACTION_REGISTER_ON_SERVER)) {
                RestClient.registerOnServer(pushConnector.getActivity(),
                        new RegisterOnServerHandler(pushConnector.getActivity()),
                        extras.getString(GCMIntentService.EXTRAS_REG_ID));
            } else if (action.equals(GCMIntentService.ACTION_MESSAGE)) {
                PushMessage pushMessage = extras.getParcelable(GCMIntentService.EXTRAS_PUSH_MESSAGE);
                PushMessageDisplayHelper.displayPushMessage(pushConnector.getActivity(), pushConnector.getFragmentManager(),
                        pushMessage, pushConnector.isResumed());
                if (PushConnector.DEBUG) Log.d(TAG, "ReceiveMessage" + pushMessage);
            }
        }
    };

    PushManager(PushConnector pushConnector, String serverUrl, String appKey, String senderId) {
        this.pushConnector = pushConnector;
        PushManager.appKey = appKey;
        PushManager.senderId = senderId;
        PushManager.serverUrl = serverUrl;

    }

    public void init() {
        isConfigsUpdated = isConfigsUpdated();
        if (PushConnector.DEBUG) {
            Log.d(TAG, "Configs updated: " + isConfigsUpdated);
            updateConfigs(pushConnector.getActivity(), serverUrl, appKey, senderId);
        }

        SharedPrefUtils.setMainActivityUris(pushConnector.getActivity());

        initHelpers(pushConnector.getActivity());

        checkIntent();

        setupGCM();

        locationAccessHelper.checkLocationProviders();
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
        PushMessage pushMessage;

        if (extras != null && (pushMessage = extras.getParcelable(GCMIntentService.EXTRAS_PUSH_MESSAGE)) != null) {
            PushMessageDisplayHelper.displayPushMessage(pushConnector.getActivity(), pushConnector.getFragmentManager(),
                    pushMessage, true);
        }
    }

    void hitTag(String tag) {
        if (isDestroyed) throw new IllegalStateException("PushManager already destoyed");
        RestClient.hitTag(pushConnector.getActivity(), new LogResponseHandler("hitTag"),
                SharedPrefUtils.getServerDeviceId(pushConnector.getActivity()), tag);
    }

    private void updateConfigs(Activity activity, String serverUrl, String appKey, String senderId) {
        SharedPrefUtils.setAppKey(activity, appKey);
        SharedPrefUtils.setSenderId(activity, senderId);
        SharedPrefUtils.setServerUrl(activity, serverUrl);
    }

    private boolean isConfigsUpdated() {
        Activity activity = pushConnector.getActivity();

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
        PushMessage pushMessage;

        if (intent != null && (extras = intent.getExtras()) != null
                && (pushMessage = extras.getParcelable(GCMIntentService.EXTRAS_PUSH_MESSAGE)) != null) {
            PushMessageDisplayHelper.displayPushMessage(pushConnector.getActivity(), pushConnector.getActivity()
                    .getSupportFragmentManager(), pushMessage, pushConnector.isResumed());
        }
    }

    private void setupGCM() {
        Activity activity = pushConnector.getActivity();
        final Context appContext = activity.getApplicationContext();

        GCMRegistrar.checkDevice(appContext);
        GCMRegistrar.checkManifest(appContext);

        regId = GCMRegistrar.getRegistrationId(appContext);
        if (PushConnector.DEBUG) Log.d(TAG, "GCM id:" + regId);

        if (regId.length() == 0) {
            GCMRegistrar.register(appContext, senderId);
        } else {
            if (GCMRegistrar.isRegisteredOnServer(appContext) && !isConfigsUpdated) {
                if (PushConnector.DEBUG) Log.d(TAG, "Device is already registered on server");
                CoarseLocationProvider.requestCoarseLocation(activity, new CoarseLocationListener() {

                    @Override
                    public void onCoarseLocationReceived(Location location) {
                        if (PushConnector.DEBUG) Log.d(TAG, "Coarse location received:" + location.toString());
                        RestClient.locationCheck(new LocationsResponseHandler(appContext),
                                SharedPrefUtils.getServerDeviceId(appContext), location);
                    }
                });
            } else {
                if (PushConnector.DEBUG) Log.d(TAG, "Register on server from PushManager with regId: " + regId);
                RestClient.registerOnServer(appContext, new RegisterOnServerHandler(appContext), regId);
            }
        }
    }

    private static String generateConfigFingerprint(Object... configs) {
        StringBuilder hashBuilder = new StringBuilder();
        for (Object config : configs) {
            hashBuilder.append(config.hashCode());
        }
        return hashBuilder.toString();
    }

}
