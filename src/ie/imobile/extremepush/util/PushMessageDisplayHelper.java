package ie.imobile.extremepush.util;

import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.fragment.PushDialogFragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;

public final class PushMessageDisplayHelper {

    public static void displayPushMessage(Context context, FragmentManager fm, PushMessage pushMessage, boolean isVisible,
            boolean fromNotification) {

        // avoid double showing
        if (pushMessage.pushActionId.equals(SharedPrefUtils.getLastPushId(context))) return;

    	if (fromNotification && pushMessage.url != null) {
    		SharedPrefUtils.setLastPushId(context, pushMessage.pushActionId);
    		if (pushMessage.openInBrowser) {
    			UrlUtils.openUrlInBrowser(context, pushMessage.url);
        	} else {
        		UrlUtils.openUrlInWebView(context, pushMessage.url);
        	}	
    	} else {
	        if (!isVisible) {
	            PushDialogFragment fragment = PushDialogFragment.newInstance(pushMessage);
	            fragment.show(fm, null);
	        } 
    	}
        
    }
}
