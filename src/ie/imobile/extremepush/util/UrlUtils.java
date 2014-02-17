package ie.imobile.extremepush.util;

import ie.imobile.extremepush.ui.WebViewActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UrlUtils {

    public static void openUrlInBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    public static void openUrlInWebView(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class)
        	.putExtra(WebViewActivity.EXTRAS_URL, url);
        context.startActivity(intent);
    }
}
