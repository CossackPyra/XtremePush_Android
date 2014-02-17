package ie.imobile.extremepush.util;

import ie.imobile.extremepush.api.XtremeRestClient;

import java.util.HashMap;
import java.util.Map;

import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class MonitoringUtils {
	private static final String TAG = MonitoringUtils.class.getCanonicalName();
	private static long time = 0L;
	
	public static void startSession(final Context context) {
		sendLastLog(context);
		time = System.currentTimeMillis();
	}

	public static void stopSession(final Context context, final String serverRegId) {
		SharedPrefUtils.setLastStartSessionTime(context, String.valueOf(time/1000));
		SharedPrefUtils.setLastDurationtSessionTime(context, String.valueOf((System.currentTimeMillis() - time)/1000));
		Log.d(TAG, "session duration: " +  ((System.currentTimeMillis() - time)/1000) 
				+ "sec, start at: " + time/1000);
	}

	private static void sendLastLog(final Context context) {
		String ss = SharedPrefUtils.getLastStartSessionTime(context);
		String dd = SharedPrefUtils.getLastDurationtSessionTime(context);
		if (!TextUtils.isEmpty(ss) && !TextUtils.isEmpty(dd)) {
			long s = Long.valueOf(ss);
			long d = Long.valueOf(dd);
			Map<Long, Long> timeLog = new HashMap<Long, Long>();
			timeLog.put(s, d);
			XtremeRestClient.hitDevStatistics(context,
					new AsyncHttpResponseHandler(),
					SharedPrefUtils.getServerDeviceId(context), timeLog);
		}
	}
		
}
