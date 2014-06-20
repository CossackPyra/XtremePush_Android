package ie.imobile.extremepush.util;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.Locale;

public class FingerPrintManager {
    public static String createFingerprint(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        String carrierName = tm.getNetworkOperatorName();
        return generateConfigFingerprint(android.os.Build.BRAND,
                Build.VERSION.SDK_INT,
                android.os.Build.MODEL,
                context.getResources().getConfiguration().locale.getCountry(),
                carrierName,
                LibVersion.VER,
                TimeUtils.getUtcTimeZone(),
                countryCode,
                Locale.getDefault().getISO3Language());
    }

    private static String generateConfigFingerprint(Object... configs) {
        StringBuilder hashBuilder = new StringBuilder();
        for (Object config : configs) {
            hashBuilder.append(config.hashCode());
        }
        return hashBuilder.toString();
    }
}
