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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends FragmentActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

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
     */
    private HashMap<LatLng, CortezGeofence> cortezGeofences;

    /**
     * Cortez JSON Data
     */
    private JSONObject cortezJSONData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables
        mGeofenceVisibleMarkers = new HashMap<LatLng, Marker>();
        mGeofenceVisibleCircles = new HashMap<LatLng, Circle>();

        cortezJSONData = getCortezJSONData();
        try {
            setTitle(cortezJSONData.getString("mapName"));
        } catch (JSONException e) {}

        cortezGeofences = getCortezGeofences(cortezJSONData);

        // Add the geofences to the GeofenceStore object.
        ArrayList<Geofence> geofences = new ArrayList<Geofence>(cortezGeofences.size());
        for (CortezGeofence c : cortezGeofences.values()) {
            geofences.add(c.getGeofence());
        }
        mGeofenceStore = new GeofenceStore(this, geofences);
    }

    @Override
    protected void onStart() {
        Log.v(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        mGeofenceStore.disconnect();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop");
        mGeofenceStore.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            setUpMapIfNeeded();
        } else {
            GooglePlayServicesUtil.getErrorDialog(
                    GooglePlayServicesUtil.isGooglePlayServicesAvailable(this),
                    this, 0);
        }
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
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
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
            Log.v(TAG, "Saving Cortez JSON Data...");
            outputStream.write(cortezJSONData.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
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
                String requestId = geofenceJson.getString("displayName");

                JSONObject location = geofenceJson.getJSONObject("location");
                double latitude = Double.parseDouble(location.getString("lat"));
                double longitude = Double.parseDouble(location.getString("lng"));
                LatLng latlng = new LatLng(latitude, longitude);

                int radius = Integer.parseInt(geofenceJson.getString("radius"));
                long expirationDuration = Long.parseLong(geofenceJson.getString("expirationDuration"));
                int notificationResponsiveness = Integer.parseInt(geofenceJson.getString("notificationResponsiveness"));
                int loiteringDelay = Integer.parseInt(geofenceJson
                        .getJSONObject("GEOFENCE_TRANSITION_DWELL").getString("loiteringDelay"));

                String infoActivityMessage1 = geofenceJson.getString("infoActivityString1");
                String infoActivityMessage2 = geofenceJson.getString("infoActivityString2");

                boolean displayGeofenceMarker = Boolean.parseBoolean(geofenceJson.getString("displayGeofenceMarker"));
                boolean displayGeofenceCircle = Boolean.parseBoolean(geofenceJson.getString("displayGeofenceCircle"));

                // TODO: modify the lines for MarkerOptions and CircleOptions when customization is supported
                MarkerOptions markerOptions = new MarkerOptions()
                        .visible(displayGeofenceMarker)
                        .position(latlng)
                        .title(!requestId.isEmpty() ? requestId : getString(R.string.markerTest))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                CircleOptions circleOptions = new CircleOptions()
                        .visible(displayGeofenceCircle)
                        .center(latlng)
                        .radius(radius)
                        .fillColor(0x4000ffff)
                        .strokeColor(0x4000ffff).strokeWidth(2);

                Geofence geofence = new Geofence.Builder()
                        .setRequestId(requestId)
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
            e.printStackTrace();
        }

        return new HashMap<>(); // Returned on error
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
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.v(TAG, "Clicked Marker at " + marker.getPosition());
                Intent poi_details = new Intent(MainActivity.this, InfoActivity.class);

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

        // Hide labels.
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setIndoorEnabled(false);
        mMap.setMyLocationEnabled(true);

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

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition position) {
                    displayGeofences();
                }
        });
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
                        mGeofenceVisibleMarkers.put(key, mMap.addMarker(c.getGeofenceMarkerOptions()));
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
