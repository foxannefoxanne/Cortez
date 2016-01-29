package eecs581_582.cortez;

import java.util.List;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
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

        // TODO: send notifications only when Cortez is running and (screen is off or Cortez is in background)
        sendNotification(this, intent);
        Log.d(TAG, "onHandleIntent");
    }

    /**
     * Sends a Notification to the user's Notification Bar when 1+ Geofences are triggered.
     * @param context the GeofenceIntentService Context
     * @param intent the Intent coming from GeofenceStore which had been fired when Geofences were triggered
     */
    private void sendNotification(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(!geofencingEvent.hasError()) {
            int transition = geofencingEvent.getGeofenceTransition();
            String notificationTitle;
            switch (transition) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    notificationTitle = getString(R.string.notifyGeofenceEnter);
                    Log.d(TAG, "Geofence Entered");
                    break;
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    notificationTitle = getString(R.string.notifyGeofenceDwell);
                    Log.d(TAG, "Dwelling in Geofence");
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    notificationTitle = getString(R.string.notifyGeofenceExit);
                    Log.d(TAG, "Geofence Exited");
                    break;
                default:
                    notificationTitle = "Geofence Unknown";
                    Log.d(TAG, "Geofence Unknown");
            }

            // Enable the Notification to take the user back to the map (MainActivity)
            Intent notificationIntent = new Intent(context, MapActivity.class);
            // If an activity other than the map (MainActivity) is running in Cortez, it will be closed
            // (stopping any running processes within that activity), and the map will be added
            // to the top of the history stack.
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(R.mipmap.logo)                            // Use Cortez logo
                    .setPriority(Notification.PRIORITY_DEFAULT)                     // Don't annoy the user
                    .setAutoCancel(true)                                            // Close when clicked
                    .setContentTitle(notificationTitle)                             // Add Notification title
                    .setContentText(getTriggeringGeofences(intent))                 // Add Notification text
                    .setDefaults(Notification.DEFAULT_ALL)                          // Use system defaults for Notification
                    .setContentIntent(pendingIntent);                               // Set the next PendingIntent to go back to the MainActivity


            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Log.d(TAG, "Sending Notification");
            notificationManager.notify(0, notificationBuilder.build());
        }

        wakeLock.release();
    }

    /**
     * Names the list of Geofences that have been triggered from the same location (Intent).
     * @param intent the Intent coming from GeofenceStore which had been fired when Geofences were triggered
     * @return a comma-separated String containing the names of triggered Geofences
     */
    private String getTriggeringGeofences(Intent intent) {
        GeofencingEvent geofenceEvent = GeofencingEvent.fromIntent(intent);
        List<Geofence> geofences = geofenceEvent
                .getTriggeringGeofences();

        String[] geofenceIds = new String[geofences.size()];

        for (int i = 0; i < geofences.size(); i++) {
            geofenceIds[i] = geofences.get(i).getRequestId();
        }

        return TextUtils.join(", ", geofenceIds);
    }
}
