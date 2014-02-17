package ie.imobile.extremepush.api;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.api.model.EventsPushlistWrapper;
import ie.imobile.extremepush.api.model.PushmessageListItem;
import ie.imobile.extremepush.util.LogEventsUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class EventResponseHandler extends AsyncHttpResponseHandler {

    private static final String TAG = "EventResponseHandler";
    private WeakReference<Context> contextHolder;

    public EventResponseHandler(Context context) {
        contextHolder = new WeakReference<Context>(context);
    }
    
	  public void onSuccess(int arg0, String response) {
	        Context context = contextHolder.get();
          if (PushConnector.DEBUG_LOG) {
        	  LogEventsUtils.sendLogTextMessage(context, "Catch response: " + response);
          }
          
	        if (PushConnector.DEBUG) Log.d(TAG, "Events: " + response);
	        if (context == null) return;

	        ArrayList<PushmessageListItem> pushmessageListItems = ResponseParser.parseEvent(response).pushmessageList;
	        if (pushmessageListItems != null) {
	        PushConnector.postInEventBus(new EventsPushlistWrapper(pushmessageListItems));
	        }
	    }

	    @Override
	    public void onFailure(Throwable arg0, String arg1) {
	        if (PushConnector.DEBUG) Log.d(TAG, "Failed to obtaine locations");

	        Context context = contextHolder.get();
	        if (context == null) return;
	        if (PushConnector.DEBUG) LogEventsUtils.sendLogTextMessage(context, "Failed to obtaine locations");
	        if (PushConnector.DEBUG_LOG) LogEventsUtils.sendLogTextMessage(context, "Failed to obtaine locations " + arg1);
	    }
}
