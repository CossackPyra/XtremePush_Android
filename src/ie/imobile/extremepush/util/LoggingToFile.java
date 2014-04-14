package ie.imobile.extremepush.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


//adb logcat -v time   ActivityManager:W  yourappname:D  *:W >"C:\devAndroid\log\yourappname.log"
//https://support.skype.com/ru/faq/FA12220/kak-sozdat-fajl-zurnala-na-ustrojstve-android

public class LoggingToFile {

	public void appendLog(String text) {
		File logFile = new File("sdcard/log.file");
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
