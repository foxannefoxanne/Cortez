package eecs581_582.cortez.frontend;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import eecs581_582.cortez.CortezGeofence;
import eecs581_582.cortez.CortezMapData;
import eecs581_582.cortez.backend.Constants;
import eecs581_582.cortez.backend.GeofenceMonitor;
import eecs581_582.cortez.R;

/* ********************************************************************
 *  MapActivity is the screen that integrates with the Google Maps API
 *  detects the user's location, pulls Map data from the database, and
 *  plots it on the GMaps interface as Markers.
 *
 *  - Selection of those Markers should take the user to the
 *  InfoActivity
 *  - Selection of the Help button in the context menu should take the
 *  user to the HelpActivity
 */

public class MapActivity extends FragmentActivity {

    public static final String TAG = MapActivity.class.getSimpleName();

    /**
     * Google Map object
     */
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    /**
     * BroadcastReceiver listens for any Intents that are sent from any IntentService
     * that match an action in its corresponding IntentFilter.
     *
     * Currently, this is only set to receive an Intent with the action "TRIGGERING GEOFENCES."
     * These Intents only come from GeofenceIntentService, and they will contain an ArrayList of
     * all Geofences that the user has just triggered a transition for (enter / dwell / exit).
     */
    private BroadcastReceiver broadcastReceiver;

    /**
     * Visible Markers and Circles on the Google Map at the current CameraPosition
     */
    HashMap<LatLng, Marker> mGeofenceVisibleMarkers;
    HashMap<LatLng, Circle> mGeofenceVisibleCircles;

    /**
     * Geofence Monitor
     */
    private GeofenceMonitor mGeofenceMonitor;

    /**
     * Cortez Map Data
     */
    private CortezMapData cortezMapData;

    /**
     * Cortez Geofences
     */
    private HashMap<LatLng, CortezGeofence> cortezGeofences;

    /**
     * Cortez Geofences that are currently being triggered
     */
    private ArrayList<Geofence> triggeringGeofences;

    /**
     * The most recent geofence transition type that was received
     */
    private int lastTriggeredGeofenceTransition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        // Set the BroadcastReceiver to listen for any Intent with "TRIGGERING GEOFENCES" as its action
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("TRIGGERING GEOFENCES");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /*
                 * Hold onto pertinent info about the most recently triggered geofences,
                 * so we can decide whether to display relevant media in InfoActivity.
                 */
                triggeringGeofences = (ArrayList) intent.getParcelableArrayListExtra("Triggering Geofences");
                lastTriggeredGeofenceTransition = intent.getIntExtra("Geofence Transition", -1);

                String s = "";
                for (Geofence g : triggeringGeofences) {
                    s += g.toString() + "\n";
                }
                Log.d(TAG, "Received Triggering Geofences:\n"
                        + "==============================\n"
                        + s);
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        setUpMapIfNeeded();

        // TODO: Write the geofences for the current map into a private file called Geofences_LastRun.txt
        // This file will remind Cortez which geofences the monitor should remove when the app starts again,
        // which will prevent Cortez from monitoring geofences from several maps at once.
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        if (this.isFinishing()) {
            mGeofenceMonitor.stopMonitoring();
            mGeofenceMonitor.disconnectLocationListener();
            unregisterReceiver(broadcastReceiver);
            NotificationManagerCompat
                    .from(getApplicationContext())
                    .cancel(0);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        Log.d(TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_help: {
                // This means you have selected the Help option
                Log.d(TAG, "Help button selected");
                Intent intent = new Intent(this, HelpActivity.class);
                intent.putExtra(getString(R.string.action_help), Constants.Caller.MAP_ACTIVITY);
                startActivity(intent);
                return true;
            }
            case R.id.action_settings : {
                // TODO: Implement SettingsActivity before un-commenting these lines.
                // This means you have selected the Settings option
                Log.d(TAG, "Settings button selected");
//                Intent intent = new Intent(this, SettingsActivity.class);
//                intent.putExtra(getString(R.string.action_settings), Constants.Caller.MAP_ACTIVITY);
//                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Hide labels.
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setIndoorEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Build Cortez Geofences and place them on the map.
        setUpGeofences();

        // Display the map from the user's initial location upon starting the app.
        locate();

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                displayGeofences();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String logMessage = "Clicked Marker entitled \"" + marker.getTitle()
                        + "\" at " + marker.getPosition();

                CortezGeofence tmp = cortezGeofences.get(marker.getPosition());

                String geofenceInfoText = tmp.getGeofenceInfoText();


                Intent outgoingIntent;
                if (triggeringGeofences == null) {
                    logMessage += ", but no geofences have been triggered yet.";
                    outgoingIntent = new Intent(MapActivity.this, InfoActivity.class);
                }
                else if (triggeringGeofences.contains(tmp.getGeofence())) {
                    switch(lastTriggeredGeofenceTransition) {
                        case Geofence.GEOFENCE_TRANSITION_ENTER: {
                            logMessage += ", and the user HAS RECENTLY ENTERED the geofence.";
                            outgoingIntent = new Intent(MapActivity.this, MediaSelectActivity.class);
                            outgoingIntent.putExtra("picLinks", tmp.getGeofencePicLinks());
                            outgoingIntent.putExtra("audLinks", tmp.getGeofenceAudioLinks());
                            outgoingIntent.putExtra("vidLinks", tmp.getGeofenceVideoLinks());
                            break;
                        }
                        case Geofence.GEOFENCE_TRANSITION_DWELL: {
                            logMessage += ", and the user IS DWELLING IN the geofence.";
                            outgoingIntent = new Intent(MapActivity.this, MediaSelectActivity.class);
                            outgoingIntent.putExtra("picLinks", tmp.getGeofencePicLinks());
                            outgoingIntent.putExtra("audLinks", tmp.getGeofenceAudioLinks());
                            outgoingIntent.putExtra("vidLinks", tmp.getGeofenceVideoLinks());
                            break;
                        }
                        case Geofence.GEOFENCE_TRANSITION_EXIT: {
                            logMessage += ", but the user HAS JUST LEFT the geofence.";
                            outgoingIntent = new Intent(MapActivity.this, InfoActivity.class);
                            break;
                        }
                        default: {
                            logMessage += ", but we have no idea what's going on... derp.";
                            outgoingIntent = new Intent(MapActivity.this, InfoActivity.class);
                        }
                    }
                }
                else {
                    logMessage += ", but the user IS NOT in the geofence.";
                    outgoingIntent = new Intent(MapActivity.this, InfoActivity.class);
                }

                Log.d(TAG, logMessage);

                outgoingIntent.putExtra("title", marker.getTitle());
                outgoingIntent.putExtra("geofenceInfoText", geofenceInfoText);

                startActivity(outgoingIntent);
                return true;
            }
        });

//        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//            @Override
//            public boolean onMyLocationButtonClick() {
//
//                return false;
//            }
//        });
    }

    /**
     * Finds the user's Location and moves the Camera to that Location at a fixed height.
     */
    private void locate() {
        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location myLocation = locationManager.getLastKnownLocation(provider);

        if (myLocation != null) {

            // Get latitude of the current location
            double latitude = myLocation.getLatitude();

            // Get longitude of the current location
            double longitude = myLocation.getLongitude();

            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        }
    }

    /**
     * Initializes Geofences for the app.
     */
    private void setUpGeofences() {
        // Initialize variables
        mGeofenceVisibleMarkers = new HashMap<LatLng, Marker>();
        mGeofenceVisibleCircles = new HashMap<LatLng, Circle>();

        // Open Cortez map data from local storage
        cortezMapData = new CortezMapData(this, getIntent().getStringExtra("CortezMapData"));

        // Set the Google Map title to match the title from Cortez map data
        setTitle(cortezMapData.getCortezMapName());

        // Get Cortez Geofences
        cortezGeofences = cortezMapData.getCortezGeofences();

        // Send the Cortez Geofences as an ArrayList to GeofenceMonitor
        ArrayList<Geofence> geofences = new ArrayList<Geofence>(cortezGeofences.size());
        for (CortezGeofence c : cortezGeofences.values()) {
            geofences.add(c.getGeofence());
        }
        mGeofenceMonitor = new GeofenceMonitor(this, geofences);
    }

    /**
     * Updates the GoogleMap to display the Geofence Markers / Circles in the viewable area.
     * Removes any Markers / Circles that are not in the viewable area for increased performance.
     *
     * Credit: https://discgolfsoftware.wordpress.com
     */
    private void displayGeofences() {
        if (this.mMap != null) {
            //This is the current user-viewable region of the map
            LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

            //Loop through all the items that are available to be placed on the map
            for (LatLng key : cortezGeofences.keySet()) {

                CortezGeofence c = cortezGeofences.get(key);

                //If the item is within the the bounds of the screen
                if (bounds.contains(c.getGeofenceCoordinates())) {
                    //If the item isn't already being displayed
                    if (!mGeofenceVisibleMarkers.containsKey(key)) {
                        //Add the Marker / Circle to the Map and keep track of it with the HashMap
                        Marker marker = mMap.addMarker(c.getGeofenceMarkerOptions());
                        mGeofenceVisibleMarkers.put(key, marker);
                        marker.showInfoWindow();
                        mGeofenceVisibleCircles.put(key, mMap.addCircle(c.getGeofenceCircleOptions()));
                    }
                }

                //If the marker / circle is off screen
                else {
                    //If the location was previously on screen
                    if (mGeofenceVisibleMarkers.containsKey(key)) {
                        //1. Remove the Marker / Circle from the GoogleMap
                        mGeofenceVisibleMarkers.get(key).remove();
                        mGeofenceVisibleCircles.get(key).remove();

                        //2. Remove the reference to the Marker / Circle from the HashMap
                        mGeofenceVisibleMarkers.remove(key);
                        mGeofenceVisibleCircles.remove(key);
                    }
                }
            }
        }
    }
}
