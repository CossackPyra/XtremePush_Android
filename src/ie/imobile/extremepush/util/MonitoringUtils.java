package ie.imobile.extremepush.util;

import android.os.Environment;
import android.text.TextUtils;
import ie.imobile.extremepush.api.ResponseParser;
import ie.imobile.extremepush.api.XtremeRestClient;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.Context;
import android.util.Log;

public class MonitoringUtils {
	private static final String TAG = MonitoringUtils.class.getCanonicalName();
	private static long time = 0L;
	
	public static void startSession(final Context context) {
		sendLastLog(context);
		time = System.currentTimeMillis();
	}

	public static void stopSession() {
        Map<Long, Long> map = readSessionMap();
        map.put(time/1000, (System.currentTimeMillis() - time)/1000);
        writeSessionMap(map);
        Log.d(TAG, "session duration: " +  ((System.currentTimeMillis() - time)/1000)
				+ "sec, start at: " + time/1000);
	}

    private static void sendLastLog(final Context context) {
        Map<Long, Long> timeLog = readSessionMap();
        XtremeRestClient.hitDevStatistics(context,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int arg, String response) {
                        String message = ResponseParser.parseStatisticsResponse(response, context);
                        if (message != null && TextUtils.equals(message, "Success"))
                            writeSessionMap(new HashMap<Long, Long>());
                    }
                },
                SharedPrefUtils.getServerDeviceId(context), timeLog);
	}

    private static Map<Long, Long> readSessionMap() {
        Map<Long, Long> map = new HashMap<Long, Long>();
        try {
            File sessionsFile=
                    new File(Environment.getExternalStorageDirectory(),
                            "SessionsLog.txt");
            if (!sessionsFile.exists()) return map;
            FileInputStream log = new FileInputStream(sessionsFile);
            ObjectInputStream iStream = new ObjectInputStream(log);
            map = (Map<Long, Long>) iStream.readObject();
            if (map == null)
                map = new HashMap<Long, Long>();
            iStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }

    private static void writeSessionMap(Map<Long, Long> list) {
        try {
            File sessionsFile=
                    new File(Environment.getExternalStorageDirectory(),
                            "SessionsLog.txt");
            FileOutputStream log = new FileOutputStream(sessionsFile);
            ObjectOutputStream oStream = new ObjectOutputStream(log);
            oStream.writeObject(list);
            oStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
