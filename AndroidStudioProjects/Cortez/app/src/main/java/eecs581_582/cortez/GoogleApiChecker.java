package eecs581_582.cortez;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * This class acts as a sentinel against using Cortez on a system without the required Google API's.
 * Created by Joseph on 1/29/16.
 */
public class GoogleApiChecker {

    /**
     * Error code for displaying the Google Play Services update message
     */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Google API Availability (for checking Google Play Services)
     */
    private static GoogleApiAvailability googleApiAvailability;


    public static void checkPlayServices(Activity activity) {
        googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(result != ConnectionResult.SUCCESS) {
            // Google Play Services not installed / updated: provide a means for doing so
            if(googleApiAvailability.isUserResolvableError(result)) {
                googleApiAvailability.getErrorDialog(activity, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
        }

        // Google Play Services is installed: we're golden.
    }
}
