package ie.imobile.extremepush.api.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class PushMessage implements Parcelable {

    public String pushActionId;
    public String alert;
    public String badge;
    public String sound;
    public String url;
    public boolean openInBrowser;

    public PushMessage() {
    }

    private PushMessage(Parcel in) {
        pushActionId = in.readString();
        alert = in.readString();
        badge = in.readString();
        sound = in.readString();
        url = in.readString();
        openInBrowser = in.readByte() == 1 ? true : false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pushActionId);
        dest.writeString(alert);
        dest.writeString(badge);
        dest.writeString(sound);
        dest.writeString(url);
        dest.writeByte((byte) (openInBrowser ? 1 : 0));
    }

    public static final Parcelable.Creator<PushMessage> CREATOR = new Parcelable.Creator<PushMessage>() {
        public PushMessage createFromParcel(Parcel in) {
            return new PushMessage(in);
        }

        public PushMessage[] newArray(int size) {
            return new PushMessage[size];
        }
    };

    @Override
    public String toString() {
        return "PushMessage [pushActionId=" + pushActionId + ", alert=" + alert + ", badge=" + badge + ", sound=" + sound
                + ", url=" + url + ", openInBrowser=" + openInBrowser + "]";
    }

}
