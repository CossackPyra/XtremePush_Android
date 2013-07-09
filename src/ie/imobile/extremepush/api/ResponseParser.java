package ie.imobile.extremepush.api;

import ie.imobile.extremepush.api.model.LocationItem;
import ie.imobile.extremepush.api.model.PushMessage;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class ResponseParser {

    private static final String TAG = "ResponseParser";

    public static String parseRegisterOnServerResponse(String response) {
        try {
            JSONObject responseJson;
            responseJson = new JSONObject(response);

            int code = responseJson.getInt("code");
            if (code == 200) {
                String regId = responseJson.getString("id");
                return regId;
            }
            return null;
        } catch (JSONException e) {
            Log.wtf(TAG, e);
            return null;
        }
    }

    public static PushMessage parsePushMessage(String push) {
        PushMessage pushMessage = new PushMessage();

        try {
            JSONObject messageObj = new JSONObject(push);

            pushMessage.alert = messageObj.optString("alert", null);
            pushMessage.badge = messageObj.optString("badge", null);
            pushMessage.openInBrowser = messageObj.optInt("b", 0) == 0 ? false : true;
            pushMessage.pushActionId = messageObj.optString("id", null);
            pushMessage.sound = messageObj.optString("sound", null);
            pushMessage.url = messageObj.optString("u", null);
        } catch (JSONException e) {
            Log.wtf(TAG, e);
            return null;
        }

        return pushMessage;
    }

    public static ArrayList<LocationItem> parseLocations(String locations) {
        ArrayList<LocationItem> locationsItems = new ArrayList<LocationItem>();
        try {
            JSONArray locationsArray = new JSONArray(locations);

            int length = locationsArray.length();

            for (int i = 0; i < length; i++) {
                JSONObject itemObj = locationsArray.getJSONObject(i);
                LocationItem item = new LocationItem();
                item.id = itemObj.getString("id");
                item.latitude = itemObj.getDouble("latitude");
                item.longitude = itemObj.getDouble("longitude");
                item.radius = (float) itemObj.getDouble("radius");
                item.title = itemObj.getString("title");

                locationsItems.add(item);
            }
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }

        return locationsItems;
    }
}
