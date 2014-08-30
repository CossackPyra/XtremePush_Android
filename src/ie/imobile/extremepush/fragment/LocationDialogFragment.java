package ie.imobile.extremepush.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import ie.imobile.extremepush.util.LocationAccessHelper;
import ie.imobile.extremepush.util.XR;

public final class LocationDialogFragment extends DialogFragment {

    private OnButtonClickListener buttonClickListener;

    public void setButtonClickListener(OnButtonClickListener listener) {
        buttonClickListener = listener;
    }
	public static LocationDialogFragment newInstance() {
		LocationDialogFragment fragment = new LocationDialogFragment();
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(XR.layout.location_dialog, null);
        final CheckBox checkBox = (CheckBox) dialogView.findViewById(XR.id.ask_location);
        return builder.setView(dialogView)
            .setPositiveButton(XR.string.location_providers_dialog_positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        if (buttonClickListener != null)
                            buttonClickListener.onPositiveButtonClicked();
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity()
                                .startActivityForResult(
                                        intent,
                                        LocationAccessHelper.START_LOCATION_ACTIVITY_CODE);
                    }
                })
            .setNegativeButton(XR.string.location_providers_dialog_negative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (buttonClickListener != null && checkBox.isChecked())
                        buttonClickListener.onNegativeButtonClicked();
                    dismiss();
                }
            }).create();
	}

    public interface OnButtonClickListener {
        public void onPositiveButtonClicked();
        public void onNegativeButtonClicked();
    }
}
