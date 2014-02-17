package ie.imobile.extremepush.api.model;

import java.util.ArrayList;

public class EventsPushlistWrapper {
	
	private ArrayList<PushmessageListItem> pushmessageListItems;
	public EventsPushlistWrapper(ArrayList<PushmessageListItem> pushmessageListItems) {
		this.pushmessageListItems = pushmessageListItems;
	}
	
	public ArrayList<PushmessageListItem> getEventPushlist() {
		return this.pushmessageListItems;
	}
}
