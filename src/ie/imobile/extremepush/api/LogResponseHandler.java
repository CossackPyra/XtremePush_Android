package ie.imobile.extremepush.api;

import ie.imobile.extremepush.PushConnector;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

public final class LogResponseHandler extends AsyncHttpResponseHandler {

    private String tag = "LogResponseHandler";

    public LogResponseHandler() {
    }

    public LogResponseHandler(String tag) {
        this.tag = tag;
    }

    public void onSuccess(int arg0, String response) {
        if (PushConnector.DEBUG) Log.d(tag, "Success: " + response);
        
        
    }

    public void onFailure(Throwable arg0, String error) {
        if (PushConnector.DEBUG) Log.d(tag, "Fail: " + error);
    }
}