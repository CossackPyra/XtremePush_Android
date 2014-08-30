package ie.imobile.extremepush.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.otto.Subscribe;
import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.api.EventResponseHandler;
import ie.imobile.extremepush.api.XtremeRestClient;
import ie.imobile.extremepush.api.model.EventsPushlistWrapper;
import ie.imobile.extremepush.api.model.PushmessageListItem;
import ie.imobile.extremepush.util.LogEventsUtils;
import ie.imobile.extremepush.util.PullToRefreshListView;
import ie.imobile.extremepush.util.PullToRefreshListView.OnRefreshListener;
import ie.imobile.extremepush.util.SharedPrefUtils;
import ie.imobile.extremepush.util.XR;


public class XPushLogActivity extends Activity implements OnScrollListener{
    
    PullToRefreshListView listView;
    XPushListAdapter adapter;
	int offset = 0;
	int limit = 12;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(XR.layout.xpush_log_list);
        listView = (PullToRefreshListView) findViewById(XR.id.pull_to_refresh_listview);
        
        listView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
            	offset +=limit;            
                getXPushList(offset, limit);
                }
        });
        getXPushList(0, 12);
        
        adapter = new XPushListAdapter(this);
		listView.setAdapter(adapter);

		listView.setOnScrollListener(this);
    }
    
    private void getXPushList(int o, int l) {
        XtremeRestClient.hitPushList(XPushLogActivity.this, 
        		new EventResponseHandler(XPushLogActivity.this), 
        		SharedPrefUtils.getServerDeviceId(XPushLogActivity.this), 
        		String.valueOf(o), String.valueOf(l)); 
    }
    
    @Subscribe
    public void consumeEventList(EventsPushlistWrapper pushmessageListItems) {
    	adapter.addData(pushmessageListItems.getEventPushlist());
    	listView.onRefreshComplete();
    }


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == 0) {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int half = visibleItemCount / 2;
		int to = firstVisibleItem + half;

		for (int i = firstVisibleItem; i < to; i++) {
			final PushmessageListItem item = (PushmessageListItem) adapter.getItem(i);

			if (item.message.pushActionId != null 
					&& !item.read) {
				XtremeRestClient.hitAction(XPushLogActivity.this, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						super.onFailure(arg0, arg1);
					     if (PushConnector.DEBUG_LOG)
					    	 LogEventsUtils.sendLogTextMessage(XPushLogActivity.this, 
					    			 "Failed to obtaine push messages " + arg1);
						   
					}

					@Override
					public void onSuccess(String arg0) {
						super.onSuccess(arg0);
					     if (PushConnector.DEBUG_LOG) 
					    	 LogEventsUtils.sendLogTextMessage(XPushLogActivity.this,
					    			 "Successed to obtaine messages " + arg0);
							item.read = true;
					}
					
				}, 
						SharedPrefUtils.getServerDeviceId(XPushLogActivity.this),
						String.valueOf(item.message.pushActionId));			
			}
		}
		
		if (firstVisibleItem+visibleItemCount==totalItemCount) {
			for (int i = firstVisibleItem; i < totalItemCount; i++) {
				try {
					final PushmessageListItem item = (PushmessageListItem) adapter.getItem(i);
			
					if (item.message.pushActionId != null && !item.read) {
						XtremeRestClient.hitAction(XPushLogActivity.this,
								new AsyncHttpResponseHandler() {
	
									@Override
									public void onFailure(Throwable arg0, String arg1) {
										super.onFailure(arg0, arg1);
									     if (PushConnector.DEBUG_LOG)
									    	 LogEventsUtils.sendLogTextMessage(XPushLogActivity.this, 
									    			 "Failed to obtaine push messages " + arg1);
									}
			
									@Override
									public void onSuccess(String arg0) {
										super.onSuccess(arg0);
									     if (PushConnector.DEBUG_LOG) 
									    	 LogEventsUtils.sendLogTextMessage(XPushLogActivity.this,
									    			 "Successed to obtaine messages " + arg0);
											item.read = true;
									}
									
								}, 
								SharedPrefUtils.getServerDeviceId(XPushLogActivity.this),
								String.valueOf(item.message.pushActionId));			
					}
				} catch (IndexOutOfBoundsException e) {
								
				}
			}
		}
//		adapter.notifyDataSetChanged();
	}

}
