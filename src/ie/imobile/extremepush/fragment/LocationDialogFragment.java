package ie.imobile.extremepush.fragment;

import ie.imobile.extremepush.R;
import ie.imobile.extremepush.util.LocationAccessHelper;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;

public final class LocationDialogFragment extends DialogFragment {

	public static LocationDialogFragment newInstance() {
		LocationDialogFragment fragment = new LocationDialogFragment();
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setMessage(R.string.location_providers_dialog_message)
				.setPositiveButton(R.string.location_providers_dialog_positive,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Intent intent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								getActivity()
										.startActivityForResult(
												intent,
												LocationAccessHelper.START_LOCATION_ACTIVITY_CODE);
							}
						}).create();
	}
}
