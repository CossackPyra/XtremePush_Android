package ie.imobile.extremepush.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.api.XtremeRestClient;
import ie.imobile.extremepush.api.model.PushmessageListItem;
import ie.imobile.extremepush.util.LogEventsUtils;
import ie.imobile.extremepush.util.SharedPrefUtils;
import ie.imobile.extremepush.util.XR;

import java.text.SimpleDateFormat;
import java.util.*;

public class XPushListAdapter extends BaseAdapter implements OnClickListener {
	private ArrayList<PushmessageListItem> items;
	
	public static class ViewHolder {
		public int id;
		public TextView name;
		public TextView date;
		public TextView arrow;
		public LinearLayout bg;
	}

	private Context context;
	public XPushListAdapter(Context context) {
		this.context = context;
		this.items = new ArrayList<PushmessageListItem>();
	}
	@Override
	public int getCount() {
		return items.size();
	}

	public void addData(List<PushmessageListItem> items) {
		this.items.addAll(items);
		Collections.sort(this.items, new Comparator<PushmessageListItem>() {

			@Override
			public int compare(PushmessageListItem lhs, PushmessageListItem rhs) {
				 if (!(lhs instanceof PushmessageListItem)) {
		                return -1;
		            }
		            if (!(rhs instanceof PushmessageListItem)) {
		                return -1;
		            }

		            PushmessageListItem mli1 = (PushmessageListItem)lhs;
		            PushmessageListItem mli2 = (PushmessageListItem)rhs;

		            return Integer.valueOf(mli2.createTimestamp)-
		            		Integer.valueOf(mli1.createTimestamp);
			}
			
		});
		notifyDataSetChanged();
	}
	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	ViewHolder viewHolder;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		String time = ((PushmessageListItem) getItem(position)).createTimestamp;
		String alert = ((PushmessageListItem) getItem(position)).message.alert;
		String url = ((PushmessageListItem) getItem(position)).message.url;
		Boolean read = ((PushmessageListItem) getItem(position)).read;

		if (convertView == null){
			rowView = LayoutInflater.from(context).inflate(XR.layout.xpush_layout_item_view, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(XR.id.messageTextView);
			viewHolder.date = (TextView) rowView.findViewById(XR.id.timeTextView);
			viewHolder.arrow = (TextView) rowView.findViewById(XR.id.arrow);
			viewHolder.bg = (LinearLayout) rowView.findViewById(XR.id.bg);
			rowView.setTag(viewHolder);
		}

		viewHolder = (ViewHolder) rowView.getTag();
		if (!TextUtils.isEmpty(url)) {
			viewHolder.arrow.setVisibility(View.VISIBLE);
		} else {
			viewHolder.arrow.setVisibility(View.GONE);
		}
		
		if (!read) {
			viewHolder.bg.setBackgroundColor(Color.CYAN);
		} else {
			viewHolder.bg.setBackgroundColor(Color.WHITE);
		}
		viewHolder.name.setText(dateFormat.format(new Date(Long.valueOf(time)*1000))); 
		viewHolder.date.setText(alert);
		viewHolder.bg.setOnClickListener(this);
		return rowView;
	}
	
	@Override
	public void onClick(View v) {
		viewHolder = (ViewHolder) v.getTag();
		PushmessageListItem pushmessageListItem = (PushmessageListItem)getItem(viewHolder.id);
		if (pushmessageListItem.message != null &&!TextUtils.isEmpty(pushmessageListItem.message.url)) {
			XtremeRestClient.hitAction(context, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(Throwable arg0, String arg1) {
					super.onFailure(arg0, arg1);
				     if (PushConnector.DEBUG_LOG)
				    	 LogEventsUtils.sendLogTextMessage(context, 
				    			 "Failed to obtaine push messages " + arg1);
					   
				}

				@Override
				public void onSuccess(String arg0) {
					super.onSuccess(arg0);
				     if (PushConnector.DEBUG_LOG) 
				    	 LogEventsUtils.sendLogTextMessage(context,
				    			 "Successed to obtaine messages " + arg0);
					   
				}
				
			}, 
					SharedPrefUtils.getServerDeviceId(context),
					String.valueOf(pushmessageListItem.message.pushActionId));
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, 
					Uri.parse(pushmessageListItem.message.url));
			context.startActivity(browserIntent);				
			}
		pushmessageListItem.read = true;
		notifyDataSetChanged();	
		}
	
}
