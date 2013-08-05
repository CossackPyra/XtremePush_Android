package ie.imobile.extremepush.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    public static String getUtcTimeZone() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
        DateFormat date = new SimpleDateFormat("Z", Locale.getDefault());
        return date.format(currentLocalTime);
    }
}
