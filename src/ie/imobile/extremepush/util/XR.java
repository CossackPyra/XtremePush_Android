package ie.imobile.extremepush.util;

import android.content.Context;
import android.content.res.Resources;

public class XR {
    public static class string {
        public static int app_name;
        public static int gcm_error;
        public static int gcm_registered;
        public static int device_update_response_error;
        public static int server_register_error;
        public static int location_providers_dialog_positive;
        public static int location_providers_dialog_negative;
        public static int push_dialog_title;
        public static int push_dialog_view;
        public static int push_dialog_close;
        public static int ptr_pull_to_refresh;
        public static int ptr_release_to_refresh;
        public static int ptr_refreshing;
        public static int ptr_last_updated;
    }

    public static class id {
        public static int ask_location;
        public static int webview_webview;
        public static int webview_close;
        public static int webview_share;
        public static int webview_view_in_browser;
        public static int messageTextView;
        public static int timeTextView;
        public static int arrow;
        public static int bg;
        public static int pull_to_refresh_listview;
        public static int ptr_id_header;
        public static int ptr_id_text;
        public static int ptr_id_last_updated;
        public static int ptr_id_image;
        public static int ptr_id_spinner;
    }

    public static class layout {
        public static int location_dialog;
        public static int activity_webview;
        public static int xpush_layout_item_view;
        public static int ptr_header;
        public static int xpush_log_list;
    }

    public static class raw {
        public static int android_models;
    }

    public static void init(Context context) {
        String packageName = context.getPackageName();
        Resources resources = context.getResources();

        string.app_name = resources.getIdentifier("app_name", "string", packageName);
        string.gcm_error = resources.getIdentifier("gcm_error", "string", packageName);
        string.gcm_registered = resources.getIdentifier("gcm_registered", "string", packageName);
        string.device_update_response_error = resources.getIdentifier("device_update_response_error", "string", packageName);
        string.server_register_error = resources.getIdentifier("server_register_error", "string", packageName);
        string.location_providers_dialog_positive = resources.getIdentifier("location_providers_dialog_positive", "string", packageName);
        string.location_providers_dialog_negative = resources.getIdentifier("location_providers_dialog_negative", "string", packageName);
        string.push_dialog_title = resources.getIdentifier("push_dialog_title", "string", packageName);
        string.push_dialog_view = resources.getIdentifier("push_dialog_view", "string", packageName);
        string.push_dialog_close = resources.getIdentifier("push_dialog_close", "string", packageName);
        string.ptr_pull_to_refresh = resources.getIdentifier("ptr_pull_to_refresh", "string", packageName);
        string.ptr_release_to_refresh = resources.getIdentifier("ptr_release_to_refresh", "string", packageName);
        string.ptr_refreshing = resources.getIdentifier("ptr_refreshing", "string", packageName);
        string.ptr_last_updated = resources.getIdentifier("ptr_last_updated", "string", packageName);

        id.ask_location = resources.getIdentifier("ask_location", "id", packageName);
        id.webview_webview = resources.getIdentifier("webview_webview", "id", packageName);
        id.webview_close = resources.getIdentifier("webview_close", "id", packageName);
        id.webview_share = resources.getIdentifier("webview_share", "id", packageName);
        id.webview_view_in_browser = resources.getIdentifier("webview_view_in_browser", "id", packageName);
        id.messageTextView = resources.getIdentifier("messageTextView", "id", packageName);
        id.timeTextView = resources.getIdentifier("timeTextView", "id", packageName);
        id.arrow = resources.getIdentifier("arrow", "id", packageName);
        id.bg = resources.getIdentifier("bg", "id", packageName);
        id.pull_to_refresh_listview = resources.getIdentifier("pull_to_refresh_listview", "id", packageName);
        id.ptr_id_header = resources.getIdentifier("ptr_id_header", "id", packageName);
        id.ptr_id_text = resources.getIdentifier("ptr_id_text", "id", packageName);
        id.ptr_id_last_updated = resources.getIdentifier("ptr_id_last_updated", "id", packageName);
        id.ptr_id_image = resources.getIdentifier("ptr_id_image", "id", packageName);
        id.ptr_id_spinner = resources.getIdentifier("ptr_id_spinner", "id", packageName);

        layout.location_dialog = resources.getIdentifier("location_dialog", "id", packageName);
        layout.activity_webview = resources.getIdentifier("activity_webview", "id", packageName);
        layout.xpush_layout_item_view = resources.getIdentifier("xpush_layout_item_view", "id", packageName);
        layout.ptr_header = resources.getIdentifier("ptr_header", "id", packageName);
        layout.xpush_log_list = resources.getIdentifier("xpush_log_list", "id", packageName);

        raw.android_models = resources.getIdentifier("android_models", "raw", packageName);
    }
}