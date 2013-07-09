package ie.imobile.extremepush;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMReceiver extends GCMBroadcastReceiver {

    private static final String TAG = "GCMReceiver";

    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        if (PushConnector.DEBUG) Log.d(TAG, "received push");
        return GCMIntentService.class.getName();
    }
}
