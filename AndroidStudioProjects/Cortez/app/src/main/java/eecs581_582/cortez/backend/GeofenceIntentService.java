package eecs581_582.cortez.backend;

import java.util.ArrayList;

import com.google.android.gms.location.GeofencingEvent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Credit: https://github.com/the-paulus/Android-Geofence
 * Modifications by Joseph on 1/23/16.
 */
public class GeofenceIntentService extends IntentService {

    private final String TAG = this.getClass().getCanonicalName();

    public GeofenceIntentService() {
        super("GeofenceIntentService");
        Log.d(TAG, "Constructor.");
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        /**
         * TODO: I'm thinking that this is where the magic should happen for Cortez.
         * Add support for handling the desired actions of each Geofence.
         *
         * NOTE: We will need to carefully consider the design for this method,
         * due to several factors:
         *
         * 1. Multiple Geofences may trigger at the same time if they overlap
         *    (which could cause race conditions in delivering content)
         *
         * 2. The onHandleIntent method may be delayed by connection loss or power saving
         *    (so immediate handling cannot be guaranteed)
         *
         * 3. We'll need to implement a "handler" method in this class for each type of
         *    supported action in Cortez, so that the mobile device knows what to do with
         *    certain media. At this time, Cortez can only send Notifications when a Geofence is triggered.
         *
         * 4. Let's try not to annoy / spam the user with content, mkay?
         */
        Log.d(TAG, "onHandleIntent");

        // Tell MapActivity which Geofences were triggered
        Intent broadcastIntent = new Intent("TRIGGERING GEOFENCES");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        broadcastIntent.putExtra("Triggering Geofences", new ArrayList<>(geofencingEvent.getTriggeringGeofences()));
        broadcastIntent.putExtra("Geofence Transition", geofencingEvent.getGeofenceTransition());
        sendBroadcast(broadcastIntent);

        // TODO: send notifications only when Cortez is running and (screen is off or Cortez is in background)

        GeofenceIntentWorker.sendNotification(this, intent);
    }
}
