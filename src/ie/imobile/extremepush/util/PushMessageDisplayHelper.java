package ie.imobile.extremepush.util;

import android.support.v4.app.FragmentTransaction;
import com.loopj.android.http.AsyncHttpResponseHandler;
import ie.imobile.extremepush.api.LogResponseHandler;
import ie.imobile.extremepush.api.XtremeRestClient;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.fragment.PushDialogFragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;

public final class PushMessageDisplayHelper {

    private static AsyncHttpResponseHandler pushActionResponseHandler = new LogResponseHandler();

    public static void displayPushMessage(Context context, FragmentManager fm, PushMessage pushMessage, boolean isVisible,
            boolean fromNotification) {

        // avoid double showing
        if (pushMessage == null || pushMessage.pushActionId == null
                || pushMessage.pushActionId.equals(SharedPrefUtils.getLastPushId(context))) return;

    	if (fromNotification && pushMessage.url != null) {
    		SharedPrefUtils.setLastPushId(context, pushMessage.pushActionId);

            String serverDeviceId = SharedPrefUtils.getServerDeviceId(context);
            if (pushMessage.pushActionId != null && serverDeviceId != null) {
                XtremeRestClient.hitAction(context, pushActionResponseHandler, serverDeviceId, pushMessage.pushActionId);
            }

    		if (pushMessage.openInBrowser) {
    			UrlUtils.openUrlInBrowser(context, pushMessage.url);
        	} else {
        		UrlUtils.openUrlInWebView(context, pushMessage.url);
        	}
    	} else {
	        if (!isVisible) {
	            PushDialogFragment fragment = PushDialogFragment.newInstance(pushMessage);
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(fragment, "100500");
                ft.commitAllowingStateLoss();
	        }
    	}

    }
}
