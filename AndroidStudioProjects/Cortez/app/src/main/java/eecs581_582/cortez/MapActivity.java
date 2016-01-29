package eecs581_582.cortez;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapActivity extends FragmentActivity {

    public static final String TAG = MapActivity.class.getSimpleName();

    /**
     * Google Map object
     */
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    /**
     * Visible Markers and Circles on the Google Map at the current CameraPosition
     */
    HashMap<LatLng, Marker> mGeofenceVisibleMarkers;
    HashMap<LatLng, Circle> mGeofenceVisibleCircles;

    /**
     * Geofence Store
     */
    private GeofenceStore mGeofenceStore;

    /**
     * Cortez Geofences
     *
     * TODO: Might change this to ArrayList<CortezGeofence> later
     */
    private HashMap<LatLng, CortezGeofence> cortezGeofences;

    /**
     * Cortez JSON Data
     */
    private JSONObject cortezJSONData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        mGeofenceStore.disconnect();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        mGeofenceStore.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        // TODO: move Google Play Services check to MainActivity.java
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            setUpMapIfNeeded();
        } else {
            GooglePlayServicesUtil.getErrorDialog(
                    GooglePlayServicesUtil.isGooglePlayServicesAvailable(this),
                    this, 0);
        }
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
        else {
            /* Map is already initialized -- move camera to latest location.
             *
             * This will be executed if either of the following conditions are met:
             *
             * 1. The app has been minimized, and then brought back to focus
             *
             * 2. A marker on the map is clicked (switching views to InfoActivity),
             *    followed by clicking the "back" button (swtiching back to MapActivity)
             */
            locate();
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
                Log.d(TAG, "Clicked Marker at " + marker.getPosition());
                Intent poi_details = new Intent(MapActivity.this, InfoActivity.class);

                String infoActivityMessage1 = cortezGeofences
                        .get(marker.getPosition())
                        .getInfoActivityMessage1();
                String infoActivityMessage2 = cortezGeofences
                        .get(marker.getPosition())
                        .getInfoActivityMessage2();

                poi_details.putExtra("title", marker.getTitle());
                poi_details.putExtra("infoActivityMessage1", infoActivityMessage1);
                poi_details.putExtra("infoActivityMessage2", infoActivityMessage2);
                startActivity(poi_details);
                return true;
            }
        });
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
     * Initializes Cortez Geofences.
     */
    private void setUpGeofences() {
        // Initialize variables
        mGeofenceVisibleMarkers = new HashMap<LatLng, Marker>();
        mGeofenceVisibleCircles = new HashMap<LatLng, Circle>();

        // Get data
        cortezJSONData = getCortezJSONData();

        // Set map title
        setTitle(getStringFromJsonObject(cortezJSONData, "mapName", getString(R.string.cortezMapNameDefault)));

        cortezGeofences = getCortezGeofences(cortezJSONData);

        // Add the geofences to the GeofenceStore object.
        ArrayList<Geofence> geofences = new ArrayList<Geofence>(cortezGeofences.size());
        for (CortezGeofence c : cortezGeofences.values()) {
            geofences.add(c.getGeofence());
        }
        mGeofenceStore = new GeofenceStore(this, geofences);
    }

    /**
     * Gets Cortez JSON data.
     * @return a traversable JSON object containing all textual data for Cortez
     */
    private JSONObject getCortezJSONData() {
        // TODO: implement in a way that downloads JSON from the database
        try {
            InputStream is = this.getAssets().open("cortezSampleJson.json");
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                stringBuilder.append(inputStr);

            return new JSONObject(stringBuilder.toString());
        }
        catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
        catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return new JSONObject(); // Returned on error
    }

    /**
     * Saves a copy of the Cortez JSON data to internal storage.
     * This copy can be used to load the Geofence data for the map at a later time,
     * without redundant database calls.
     */
    private void saveCortezJSONData() {
        FileOutputStream outputStream;
        try {
            String filename = cortezJSONData.getString("filename");
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            Log.i(TAG, "Saving Cortez JSON Data...");
            outputStream.write(cortezJSONData.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    /**
     * Builds a GeofenceStore object from JSON data.
     * @param jsonObject
     * @return
     */
    private HashMap<LatLng, CortezGeofence> getCortezGeofences(JSONObject jsonObject) {
        try {

            // Get the geofence data from the JSON object.
            JSONArray array = jsonObject.getJSONArray("geofences");

            HashMap<LatLng, CortezGeofence> cortezGeofences = new HashMap<LatLng, CortezGeofence>(array.length());

            // Build each geofence and add them to the list.
            for (int i = 0; i < array.length(); i++) {
                JSONObject geofenceJson = array.getJSONObject(i);

                // Required parameters to set (MUST have been read in from JSON)
                String markerTitle = getStringFromJsonObject(geofenceJson, "markerTitle", getString(R.string.geofenceMarkerTitleDefault));
                String markerSnippet = getStringFromJsonObject(geofenceJson, "markerSnippet", getString(R.string.geofenceMarkerSnippetDefault));
                JSONObject location = geofenceJson.getJSONObject("location");
                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");
                LatLng latlng = new LatLng(latitude, longitude);
                int loiteringDelay = Integer.parseInt(geofenceJson
                        .getJSONObject("GEOFENCE_TRANSITION_DWELL").getString("loiteringDelay"));
                String infoActivityMessage1 = geofenceJson.getString("infoActivityString1");
                String infoActivityMessage2 = geofenceJson.getString("infoActivityString2");

                // Optional parameters to set (may / may not exist in JSON)
                int radius = getIntFromJsonObject(geofenceJson, "radius", getResources().getInteger(R.integer.geofenceRadiusDefault));
                long expirationDuration = getLongFromJsonObject(geofenceJson, "expirationDuration", Geofence.NEVER_EXPIRE);
                int notificationResponsiveness = getIntFromJsonObject(geofenceJson, "notificationResponsiveness", 0);

                // TODO: modify the lines for MarkerOptions and CircleOptions when customization is supported
                MarkerOptions markerOptions = new MarkerOptions()
                        .visible(true)
                        .position(latlng)
                                // Markers will assume our default settings if not specified from JSON
                        .title(markerTitle)
                        .snippet(markerSnippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                CircleOptions circleOptions = new CircleOptions()
                        .visible(true)
                        .center(latlng)
                        .radius(radius)
                                // Circles will assume our default settings if not specified from JSON
                        .fillColor(ContextCompat.getColor(this, R.color.geofenceCircleFillDefault))
                        .strokeColor(ContextCompat.getColor(this, R.color.geofenceCircleStrokeDefault))
                        .strokeWidth(getResources().getInteger(R.integer.geofenceCircleStrokeWidthDefault));

                Geofence geofence = new Geofence.Builder()
                        .setRequestId(markerTitle)
                                // The coordinates of the center of the geofence and the radius in meters.
                        .setCircularRegion(latitude, longitude, radius)
                        .setExpirationDuration(expirationDuration)
                        .setNotificationResponsiveness(notificationResponsiveness)
                                // Required when we use the transition type of GEOFENCE_TRANSITION_DWELL
                        .setLoiteringDelay(loiteringDelay)
                        .setTransitionTypes(
                                Geofence.GEOFENCE_TRANSITION_ENTER
                                        | Geofence.GEOFENCE_TRANSITION_DWELL
                                        | Geofence.GEOFENCE_TRANSITION_EXIT).build();

                // TODO: set any new parameters for CortezGeofence here when they are supported / implemented in CortezGeofence.java
                // Add CortezGeofence
                cortezGeofences.put(latlng, new CortezGeofence.Builder(geofence, latlng)
                        .markerOptions(markerOptions)
                        .circleOptions(circleOptions)
                        .infoActivityMessage1(infoActivityMessage1)
                        .infoActivityMessage2(infoActivityMessage2)
                        .build());
            }

            return cortezGeofences;
        } catch (JSONException e) {

            // A required value for the CortezGeofence was not found in the JSONObject.
            // Check the log for a red message specifying which one.
            Log.e(TAG, e.getLocalizedMessage());

            // TODO: Handle the bad JSON file in some way
        }

        return new HashMap<>(); // Returned on error
    }

    /**
     * Attempts to get an integer value from a JSONObject key.
     * @precondition Assumes the key is directly accessible.
     * @param jsonObject the JSONObject containing the desired value
     * @param jsonKey the JSON key at which the desired value is stored
     * @param defaultValue a default value to return in case retrieval fails
     * @return the value at the specified key from a JSONObject if successful, otherwise, a default value.
     */
    private int getIntFromJsonObject(JSONObject jsonObject, String jsonKey, int defaultValue) {
        try {
            return jsonObject.getInt(jsonKey);
        }
        catch (JSONException e) {
            Log.w(TAG, e.getLocalizedMessage());
            return defaultValue;
        }
    }

    /**
     * Attempts to get a long value from a JSONObject key.
     * @precondition Assumes the key is directly accessible.
     * @param jsonObject the JSONObject containing the desired value
     * @param jsonKey the JSON key at which the desired value is stored
     * @param defaultValue a default value to return in case retrieval fails
     * @return the value at the specified key from a JSONObject if successful, otherwise, a default value.
     */
    private long getLongFromJsonObject(JSONObject jsonObject, String jsonKey, long defaultValue) {
        try {
            return jsonObject.getInt(jsonKey);
        }
        catch (JSONException e) {
            Log.w(TAG, e.getLocalizedMessage());
            return defaultValue;
        }
    }

    /**
     * Attempts to get a String value from a JSONObject key.
     * @precondition Assumes the key is directly accessible.
     * @param jsonObject the JSONObject containing the desired value
     * @param jsonKey the JSON key at which the desired value is stored
     * @param defaultValue a default value to return in case retrieval fails
     * @return the value at the specified key from a JSONObject if successful, otherwise, a default value.
     */
    private String getStringFromJsonObject(JSONObject jsonObject, String jsonKey, String defaultValue) {
        try {
            return jsonObject.getString(jsonKey);
        }
        catch (JSONException e) {
            Log.w(TAG, e.getLocalizedMessage());
            return defaultValue;
        }
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
