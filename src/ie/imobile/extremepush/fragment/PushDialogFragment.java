package ie.imobile.extremepush.fragment;

import com.loopj.android.http.AsyncHttpResponseHandler;

import ie.imobile.extremepush.R;
import ie.imobile.extremepush.api.LogResponseHandler;
import ie.imobile.extremepush.api.RestClient;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.ui.WebViewActivity;
import ie.imobile.extremepush.util.SharedPrefUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public final class PushDialogFragment extends DialogFragment {

    private static final String EXTRAS_PUSH_MESSAGE = "extras_negative_button";

    private PushMessage pushMessage;
    private AsyncHttpResponseHandler actionUrlResponseHandler = new LogResponseHandler("ActionUrlResponseHandler");

    public static PushDialogFragment newInstance(PushMessage pushMessage) {
        PushDialogFragment fragment = new PushDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRAS_PUSH_MESSAGE, pushMessage);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pushMessage = (PushMessage) getArguments().getParcelable(EXTRAS_PUSH_MESSAGE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.push_dialog_title);
        builder.setMessage(pushMessage.alert);
        if (pushMessage.url != null) {
            builder.setPositiveButton(R.string.push_dialog_view, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    RestClient.hitUrl(getActivity(), actionUrlResponseHandler,
                            SharedPrefUtils.getServerDeviceId(getActivity()), pushMessage.pushActionId);

                    if (pushMessage.openInBrowser) {
                        openUrlInBrowser(pushMessage.url);
                    } else {
                        openUrlInWebView(pushMessage.url);
                    }
                    SharedPrefUtils.setLastPushId(getActivity(), pushMessage.pushActionId);
                }
            });
        }
        builder.setNegativeButton(R.string.push_dialog_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                SharedPrefUtils.setLastPushId(getActivity(), pushMessage.pushActionId);
            }
        });
        return builder.create();
    }

    private void openUrlInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        getActivity().startActivity(intent);
    }

    private void openUrlInWebView(String url) {
        Intent intent = new Intent(getActivity(), WebViewActivity.class).putExtra(WebViewActivity.EXTRAS_URL, url);
        getActivity().startActivity(intent);
    }
}