package ie.imobile.extremepush.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;


//adb logcat -v time   ActivityManager:W  yourappname:D  *:W >"C:\devAndroid\log\yourappname.log"
//https://support.skype.com/ru/faq/FA12220/kak-sozdat-fajl-zurnala-na-ustrojstve-android

public class LoggingToFile {

	public void appendLog(String text) {
		File logFile = new File(Environment.getExternalStorageDirectory() + "xpush.log");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			buf.append(text);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
