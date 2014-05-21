package ie.imobile.extremepush.util;

import ie.imobile.extremepush.fragment.LocationDialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.Fragment;

public final class LocationAccessHelper implements LocationDialogFragment.OnButtonClickListener {

    public static final int START_LOCATION_ACTIVITY_CODE = 1;
    private Fragment fragment;

    public LocationAccessHelper(Fragment fragment) {
        this.fragment = fragment;
    }

    public boolean checkLocationProviders() {
        LocationManager locationManager = (LocationManager) fragment.getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        if (!LocationUtils.isLocationProvidersEnabled(locationManager) &&
                SharedPrefUtils.getPromptTurnLocation(fragment.getActivity())) {
            showEnableLocationProvidersDialog();
            return false;
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == START_LOCATION_ACTIVITY_CODE) {
            checkLocationProviders();
        }
    }

    private void showEnableLocationProvidersDialog() {
        LocationDialogFragment dialogFrahment = LocationDialogFragment.newInstance();
        dialogFrahment.setCancelable(false);
        dialogFrahment.setButtonClickListener(this);
        dialogFrahment.show(fragment.getFragmentManager(), null);
    }

    @Override
    public void onPositiveButtonClicked() {}

    @Override
    public void onNegativeButtonClicked() {
        SharedPrefUtils.setPromptTurnLocation(fragment.getActivity(), false);
    }
}
