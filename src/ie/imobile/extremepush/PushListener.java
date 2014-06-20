package ie.imobile.extremepush;

import ie.imobile.extremepush.api.model.PushMessage;

public interface PushListener {
    public void onPushMessage(PushMessage pm);
}
