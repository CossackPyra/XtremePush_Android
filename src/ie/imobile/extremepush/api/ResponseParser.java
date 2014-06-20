package ie.imobile.extremepush.api;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.api.model.EventItem;
import ie.imobile.extremepush.api.model.LocationItem;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.api.model.PushmessageListItem;
import ie.imobile.extremepush.util.LogEventsUtils;
import ie.imobile.extremepush.util.SharedPrefUtils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;


public class ResponseParser {

    private static final String TAG = ResponseParser.class.getCanonicalName();

    public static String parseRegisterOnServerResponse(String response, Context context) {
        try {
            final JSONObject responseJson;
            responseJson = new JSONObject(response);
            if (PushConnector.DEBUG_LOG) 
            	LogEventsUtils.sendLogTextMessage(context, "Catch response: " + responseJson.toString(1));
            int code = responseJson.getInt("code");
            if (code == 200) {
                String regId = responseJson.getString("id");
                return regId;
            }

            String domain = responseJson.getString("domain");
	        if (!SharedPrefUtils.getServerUrl(context).equalsIgnoreCase(domain)) {
	        	SharedPrefUtils.setServerUrl(context, domain);
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
                item.id = itemObj.optString("id", "");
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
    
    public static EventItem parseEvent(String events) {
    	final ArrayList<PushmessageListItem> pushmessageList = new ArrayList<PushmessageListItem>();
    	final EventItem response = new EventItem();
        try {
        	JSONObject jsonObject = new JSONObject(events);
            Log.d(TAG, jsonObject.toString());
        		response.code = jsonObject.getInt("code");
        		response.responsMessage = jsonObject.getString("message");

        	final JSONArray resultArray = jsonObject.getJSONArray("result");
            int length = resultArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject resultObject = resultArray .getJSONObject(i);
                PushmessageListItem pushmessageListItem = new PushmessageListItem();
                	pushmessageListItem.id = resultObject.getInt("id");
                	pushmessageListItem.createTimestamp = resultObject.getString("create_time");
                	
                	final JSONObject messageJsonObject = resultObject.getJSONObject("message");
                	final PushMessage message = new PushMessage();
                		message.pushActionId = messageJsonObject.optString("id", "");
                		message.badge = messageJsonObject.optString("badge", "");
                		message.sound = messageJsonObject.optString("sound", "");
                		message.url = messageJsonObject.optString("u", "");
                		message.alert = messageJsonObject.optString("alert", "");
                	pushmessageListItem.message = message;
                	
                	pushmessageListItem.messageId = resultObject.getInt("message_id");
                	pushmessageListItem.locationId = resultObject.getString("location_id");
                	pushmessageListItem.tag = resultObject.getString("tags");
                	pushmessageListItem.read = resultObject.getBoolean("read");
                	
                	pushmessageList.add(pushmessageListItem);
            }
            response.pushmessageList = pushmessageList;
        } catch (JSONException e) {
            Log.wtf(TAG, e);
        }
        return response;
    }

    public static String parseStatisticsResponse(String response, Context context) {
        try {
            final JSONObject responseJson;
            responseJson = new JSONObject(response);
            if (PushConnector.DEBUG_LOG)
                LogEventsUtils.sendLogTextMessage(context, "Catch response: " + responseJson.toString(1));
            int code = responseJson.getInt("code");
            if (code == 200) {
                String regId = responseJson.getString("message");
                return regId;
            }
            return null;
        } catch (JSONException e) {
            Log.wtf(TAG, e);
            return null;
        }
    }
}
