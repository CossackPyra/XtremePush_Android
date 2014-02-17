package ie.imobile.extremepush.api.model;

public class PushmessageListItem {

	public int id;
	public String createTimestamp;
	public PushMessage message;
	public int messageId;
	public String locationId;
	public String tag;
	public boolean read;
	
	
	@Override
	public String toString() {
		return "locationId: " + locationId + " createTimestamp: " + createTimestamp
				+ " messageId: " + messageId + " message: " + message.toString();
	}
}
