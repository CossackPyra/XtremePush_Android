package ie.imobile.extremepush.util;

import ie.imobile.extremepush.PushConnector;
import android.content.Context;
import android.content.Intent;

public class LogEventsUtils {

    public static void sendLogTextMessage(Context context, String message) {
        context.sendBroadcast(new Intent(PushConnector.ACTION_EVENT_MESSAGE).putExtra(PushConnector.EXTRAS_EVENT_MESSAGE,
                message));
    }

}
