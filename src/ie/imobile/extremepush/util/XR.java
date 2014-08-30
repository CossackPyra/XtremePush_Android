package ie.imobile.extremepush.util;

import android.content.Context;
import android.content.res.Resources;

public class XR {
    public class string {
        public int app_name;
        public int gcm_error;
        public int gcm_registered;
        public int device_update_response_error;
        public int server_register_error;
        public int location_providers_dialog_positive;
        public int location_providers_dialog_negative;
        public int push_dialog_title;
        public int push_dialog_view;
        public int push_dialog_close;
        public int ptr_pull_to_refresh;
        public int ptr_release_to_refresh;
        public int ptr_refreshing;
        public int ptr_last_updated;
    }

    public class id {
        public int ask_location;
        public int webview_webview;
        public int webview_close;
        public int webview_share;
        public int webview_view_in_browser;
        public int messageTextView;
        public int timeTextView;
        public int arrow;
        public int bg;
        public int pull_to_refresh_listview;
        public int ptr_id_header;
        public int ptr_id_text;
        public int ptr_id_last_updated;
        public int ptr_id_image;
        public int ptr_id_spinner;
    }

    public class layout {
        public int location_dialog;
        public int activity_webview;
        public int xpush_layout_item_view;
        public int ptr_header;
        public int xpush_log_list;
    }

    public class raw {
        public int android_models;
    }


    public static string string;
    public static raw raw;
    public static layout layout;
    public static id id;

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

        raw.android_models = resources.getIdentifier("android_models", "id", packageName);
    }
}