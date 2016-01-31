package eecs581_582.cortez.backend;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;

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

    public static void checkYouTubeAPI(Activity activity) {
        YouTubeInitializationResult initializationResult = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(activity);
        if (initializationResult != YouTubeInitializationResult.SUCCESS) {
            if (initializationResult.isUserRecoverableError()) {
                // YouTube not installed: provide a means for doing so
                initializationResult.getErrorDialog(activity, 2).show();
            }
        }

        // YouTube is installed: movie time!
    }
}
