package eecs581_582.cortez.backend;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import eecs581_582.cortez.R;
import eecs581_582.cortez.frontend.MapActivity;

/**
 * Created by Joseph on 2/22/16.
 */
public class GeofenceIntentWorker {

    public static String TAG = GeofenceIntentWorker.class.getSimpleName();

    /**
     * Sends a Notification to the user's Notification Bar when 1+ Geofences are triggered.
     * @param context the GeofenceIntentService Context
     * @param intent the Intent coming from GeofenceMonitor which had been fired when Geofences were triggered
     */
    protected static void sendNotification(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if(!geofencingEvent.hasError()) {
            sendNotificationHelper(context, geofencingEvent);
        }

        wakeLock.release();
    }

    private static void sendNotificationHelper(Context context, GeofencingEvent geofencingEvent) {

        /*
         * TODO: If Cortez is currently running (foreground or background),
         * allow the Notification to take the user to MapActivity on click.
         *
         * Otherwise, take the user to LauncherActivity on click (to avoid a crash).
         */
//        Intent notificationIntent = new Intent(context, MapActivity.class);
        // If an activity other than the map (MapActivity) is running in Cortez, it will be closed
        // (stopping any running processes within that activity), and the map will be added
        // to the top of the history stack.
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.logo)                                                // Use Cortez logo
                .setPriority(Notification.PRIORITY_DEFAULT)                                 // Don't annoy the user
                .setAutoCancel(true)                                                        // Close when clicked
                .setContentTitle(getNotificationTitle(context, geofencingEvent))            // Add Notification title
                .setContentText(getTriggeringGeofences(geofencingEvent))                    // Add Notification text
                .setDefaults(Notification.DEFAULT_ALL);                                     // Use system defaults for Notification
//                .setContentIntent(pendingIntent);                                           // Set the next PendingIntent to go back to the MapActivity


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());

        Log.d(TAG, "Sending Notification");
        notificationManager.notify(0, notificationBuilder.build());
    }

    private static String getNotificationTitle(Context context, GeofencingEvent geofencingEvent) {
        int transition = geofencingEvent.getGeofenceTransition();
        String notificationTitle;
        switch (transition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                notificationTitle = context.getString(R.string.notifyGeofenceEnter);
                Log.d(TAG, "Geofence(s) Entered: " + getTriggeringGeofences(geofencingEvent));
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                notificationTitle = context.getString(R.string.notifyGeofenceDwell);
                Log.d(TAG, "Dwelling in Geofence(s): " + getTriggeringGeofences(geofencingEvent));
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                notificationTitle = context.getString(R.string.notifyGeofenceExit);
                Log.d(TAG, "Geofence(s) Exited: " + getTriggeringGeofences(geofencingEvent));
                break;
            default:
                notificationTitle = "Geofence(s) Unknown";
                Log.d(TAG, "Geofence(s) Unknown");
        }

        return notificationTitle;
    }

    /**
     * Names the list of Geofences that have been triggered from the same location (Intent).
     * @param geofencingEvent the GeofencingEvent which contains Geofences that were triggered
     * @return a comma-separated String containing the names of triggered Geofences
     */
    private static String getTriggeringGeofences(GeofencingEvent geofencingEvent) {
        List<Geofence> geofences = geofencingEvent
                .getTriggeringGeofences();

        String[] geofenceIds = new String[geofences.size()];

        for (int i = 0; i < geofences.size(); i++) {
            String tmp = geofences.get(i).getRequestId();
            geofenceIds[i] = tmp.substring((tmp.indexOf("@") + 1 ), tmp.indexOf(":"));
        }

        return TextUtils.join(", ", geofenceIds);
    }
}
