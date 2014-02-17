package ie.imobile.extremepush.fragment;

import ie.imobile.extremepush.R;
import ie.imobile.extremepush.api.LogResponseHandler;
import ie.imobile.extremepush.api.XtremeRestClient;
import ie.imobile.extremepush.api.model.PushMessage;
import ie.imobile.extremepush.util.SharedPrefUtils;
import ie.imobile.extremepush.util.UrlUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.loopj.android.http.AsyncHttpResponseHandler;

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
        SharedPrefUtils.setLastPushId(getActivity(), pushMessage.pushActionId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.push_dialog_title);
        builder.setMessage(pushMessage.alert);
        if (pushMessage.url != null) {
            builder.setPositiveButton(R.string.push_dialog_view, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    XtremeRestClient.hitUrl(getActivity(), actionUrlResponseHandler,
                            SharedPrefUtils.getServerDeviceId(getActivity()), pushMessage.pushActionId);

                    if (pushMessage.openInBrowser) {
                        UrlUtils.openUrlInBrowser(getActivity(), pushMessage.url);
                    } else {
                        UrlUtils.openUrlInWebView(getActivity(), pushMessage.url);
                    }
                }
            });
        }
        builder.setNegativeButton(R.string.push_dialog_close, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        return builder.create();
    }

}