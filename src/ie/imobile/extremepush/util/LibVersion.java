package ie.imobile.extremepush.util;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;

public class LibVersion {
	private final static String PATTERN = "dd/MM/yy";
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat formatter = new SimpleDateFormat(PATTERN);
//	public final static String VER = "a"+formatter.format(new Date());
	public final static String VER = "a12022014";
}
