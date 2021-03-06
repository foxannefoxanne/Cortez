package eecs581_582.cortez.backend;

import java.util.ArrayList;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Monitors all Geofence objects.
 * Credit: https://github.com/the-paulus/Android-Geofence
 * Modifications by Joseph on 1/28/16.
 */
public class GeofenceMonitor implements ConnectionCallbacks,
        OnConnectionFailedListener, ResultCallback<Status>, LocationListener {

    private final String TAG = this.getClass().getSimpleName();

    /**
     * Context
     */
    private Context mContext;

    /**
     * Google API client object.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Geofencing PendingIntent
     */
    private PendingIntent mPendingIntent;

    /**
     * List of geofences to monitor.
     */
    private ArrayList<Geofence> mGeofences;

    /**
     * Geofence request.
     */
    private GeofencingRequest mGeofencingRequest;

    /**
     * Location Request object.
     */
    private LocationRequest mLocationRequest;

    /**
     * Constructs a new GeofenceMonitor.
     *
     * @param context The context to use.
     * @param geofences List of geofences to monitor.
     */
    public GeofenceMonitor(Context context, ArrayList<Geofence> geofences) {

        mContext = context;

        mGeofences = geofences;

        mPendingIntent = null;

        // Build a new GoogleApiClient, specify that we want to use LocationServices
        // by adding the API to the client, specify the connection callbacks are in
        // this class as well as the OnConnectionFailed method.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // This is purely optional and has nothing to do with geofencing.
        // I added this as a way of debugging.
        // Define the LocationRequest.
        mLocationRequest = new LocationRequest();
        // We want a location update every 10 seconds.
        mLocationRequest.setInterval(10000);
        // We want the location to be as accurate as possible.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        removePreviousGeofences();
        connectLocationListener();
    }

    /**
     * Connects the Google API client.
     */
    public void connectLocationListener() {
        Log.d(TAG, "Connected the Google API client");
        mGoogleApiClient.connect();
    }

    /**
     * Disconnects the Google API client.
     */
    public void disconnectLocationListener() {
        Log.d(TAG, "Disconnected the Google API client");
        mGoogleApiClient.disconnect();
    }

    /**
     * Removes all geofences from the most recently opened map in Cortez.
     * This seems to be a necessary evil to prevent Cortez from monitoring
     * geofences from several maps at the same time (i.e., a bug).
     */
    private void removePreviousGeofences() {
        // TODO: These steps should prevent Cortez from monitoring any Geofences from the last opened map:

        // 1. Create a temporary List<String> from the last opened map by reading Geofences_LastRun.txt

        // 2. Do LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, List<String>)
    }

    /**
     * Adds all geofences that the monitor is keeping track of.
     */
    public void beginMonitoring() {
        /*
         *  If, for some reason, there are no geofences associated with this map,
         *  we'll continue monitoring user location, but NOT attempt to monitor geofences.
         */
        if (!mGeofences.isEmpty()) {
            Log.d(TAG, "Started monitoring geofences");
            mGeofencingRequest = new GeofencingRequest.Builder().addGeofences(
                    mGeofences).build();

            mPendingIntent = createRequestPendingIntent();

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            // This is for debugging only and does not affect
            // geofencing.
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

            // Submitting the request to monitor geofences.
            PendingResult<Status> pendingResult = LocationServices.GeofencingApi
                    .addGeofences(mGoogleApiClient, mGeofencingRequest,
                            mPendingIntent);

            // Set the result callbacks listener to this class.
            pendingResult.setResultCallback(this);
        }
    }

    /**
     * Removes all geofences that the monitor is keeping track of.
     */
    public void stopMonitoring() {
        Log.d(TAG, "Stopped monitoring geofences");
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                // This is the same pending intent that was used in addGeofences().
                mPendingIntent
        ).setResultCallback(this); // Result processed in onResult().
    }

    @Override
    public void onResult(Status result) {
        if (result.isSuccess()) {
            Log.d(TAG, "Result Success!");
        } else if (result.hasResolution()) {
            // TODO Handle resolution
        } else if (result.isCanceled()) {
            Log.d(TAG, "Result Canceled");
        } else if (result.isInterrupted()) {
            Log.d(TAG, "Result Interrupted");
        } else {

        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed.");
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // We're connected, now we need to create a GeofencingRequest with
        // the geofences we have stored.
        Log.d(TAG, "Connected");
        beginMonitoring();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Connection suspended.");
    }

    /**
     * This creates a PendingIntent that is to be fired when geofence transitions
     * take place. In this instance, we are using an IntentService to handle the
     * transitions.
     *
     * @return A PendingIntent that will handle geofence transitions.
     */
    private PendingIntent createRequestPendingIntent() {
        if (mPendingIntent == null) {
            Log.d(TAG, "Creating PendingIntent");
            Intent intent = new Intent(mContext, GeofenceIntentService.class);
            mPendingIntent = PendingIntent.getService(mContext, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return mPendingIntent;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location Information\n"
                + "==========\n"
                + "Provider:\t" + location.getProvider() + "\n"
                + "Lat & Long:\t" + location.getLatitude() + ", "
                + location.getLongitude() + "\n"
                + "Altitude:\t" + location.getAltitude() + "\n"
                + "Bearing:\t" + location.getBearing() + "\n"
                + "Speed:\t\t" + location.getSpeed() + "\n"
                + "Accuracy:\t" + location.getAccuracy() + "\n");
    }
}
