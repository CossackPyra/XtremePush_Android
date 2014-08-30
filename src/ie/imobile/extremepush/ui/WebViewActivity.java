package ie.imobile.extremepush.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import ie.imobile.extremepush.util.XR;

public class WebViewActivity extends Activity {

    public static final String EXTRAS_URL = "extras_url";

    private WebView webView;
    private ImageButton closeButton;
    private ImageButton shareButton;
    private ImageButton openInBrowser;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(XR.layout.activity_webview);

        parseIntent();

        initViews();

        setupViews();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        url = intent.getExtras().getString(EXTRAS_URL);
    }

    private void initViews() {
        webView = (WebView) findViewById(XR.id.webview_webview);
        closeButton = (ImageButton) findViewById(XR.id.webview_close);
        shareButton = (ImageButton) findViewById(XR.id.webview_share);
        openInBrowser = (ImageButton) findViewById(XR.id.webview_view_in_browser);
    }

    private void setupViews() {
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
        closeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shareButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                intent.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(intent, "Share URL"));
            }
        });

        openInBrowser.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                finish();
            }
        });
    }
}
