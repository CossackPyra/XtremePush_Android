package ie.imobile.extremepush.location;

import ie.imobile.extremepush.PushConnector;
import ie.imobile.extremepush.api.LocationsResponseHandler;
import ie.imobile.extremepush.api.XtremeRestClient;
import ie.imobile.extremepush.util.SharedPrefUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.commonsware.cwac.locpoll.LocationPollerResult;
public class LocationReceiver extends BroadcastReceiver {
	  @Override
	  public void onReceive(Context context, Intent intent) {
	    File log=
	        new File(Environment.getExternalStorageDirectory(),
	                 "LocationLog.txt");
/*		Toast
			.makeText(context,
								"Location polling every õ minutes begun",
								Toast.LENGTH_LONG)
			.show();*/

	    try {
	      BufferedWriter out=
	          new BufferedWriter(new FileWriter(log.getAbsolutePath(),
	                                            log.exists()));

	      out.write(new Date().toString());
	      out.write(" : ");
	      
	      Bundle b=intent.getExtras();
	      
	      LocationPollerResult locationResult = new LocationPollerResult(b);
	      
	      Location loc=locationResult.getLocation();
	      String msg;

	      if (loc==null) {
	        loc=locationResult.getLastKnownLocation();
	        
	        if (loc==null) {
	          msg=locationResult.getError();
	        }
	        else {
	          msg="TIMEOUT, lastKnown="+loc.toString();
	        }
	      }
	      else {
	        msg=loc.toString();
	      }

	      if (msg==null) {
	        msg="Invalid broadcast received!";
	      }

	      out.write(msg);
	      out.write("\n");
	      out.close();
	      

          if (PushConnector.DEBUG) Log.d("", "Coarse location received:" + loc.toString());
          XtremeRestClient.locationCheck(new LocationsResponseHandler(context),
                  SharedPrefUtils.getServerDeviceId(context), loc);

	    }
	    catch (IOException e) {
	      Log.e(getClass().getName(), "Exception appending to log file", e);
	    }
	  }
	}