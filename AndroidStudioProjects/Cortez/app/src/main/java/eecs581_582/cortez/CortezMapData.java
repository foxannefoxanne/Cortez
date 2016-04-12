package eecs581_582.cortez;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import eecs581_582.cortez.backend.Downloader;

import static eecs581_582.cortez.backend.JSONHandler.*;

/**
 * A container class for Cortez Map Data.
 * Created by Joseph on 1/29/16.
 */
public class CortezMapData {
    public static final String TAG = CortezMapData.class.getSimpleName();

    /**
     * Name of the Cortez Map
     */
    private String cortezMapName;

    /**
     * Cortez Geofences
     */
    private HashMap<LatLng, CortezGeofence> cortezGeofences;

    /**
     * Constructor for maps that are being read from local storage.
     * @param context
     * @param fullPath
     */
    public CortezMapData(Context context, String fullPath) {
        JSONObject cortezJSONData = openMapData(fullPath);
        this.cortezMapName = getStringFromJsonObject(cortezJSONData, "mapName", context.getString(R.string.cortezMapNameDefault));
        this.cortezGeofences = setCortezGeofences(context, cortezJSONData);
    }

    /**
     * Builds a GeofenceStore object from JSON data.
     * @param context the Context from which we are requesting map data from (i.e., MapActivity.java)
     * @param jsonObject the map data to parse into Cortez Geofences
     * @return all Cortez Geofences from the map data, addressable by their LatLng coordinates.
     */
    private HashMap<LatLng, CortezGeofence> setCortezGeofences(Context context, JSONObject jsonObject) {
        try {

            // Get the geofence data from the JSON object.
            JSONArray array = jsonObject.getJSONArray("geofences");

            HashMap<LatLng, CortezGeofence> cortezGeofences = new HashMap<LatLng, CortezGeofence>(array.length());

            Resources resources = context.getResources();

            // Build each geofence and add them to the list.
            for (int i = 0; i < array.length(); i++) {
                JSONObject geofenceJson = array.getJSONObject(i);

                // Required parameters to set (MUST have been read in from JSON)
                JSONObject location = geofenceJson.getJSONObject("location");
                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");
                LatLng latlng = new LatLng(latitude, longitude);

                // Optional parameters to set (may / may not exist in JSON)
                JSONObject
                        enterTransition = geofenceJson.getJSONObject("GEOFENCE_TRANSITION_ENTER"),
                        dwellTransition = geofenceJson.getJSONObject("GEOFENCE_TRANSITION_DWELL"),
                        exitTransition = geofenceJson.getJSONObject("GEOFENCE_TRANSITION_EXIT");
                String markerTitle = getStringFromJsonObject(
                        geofenceJson,
                        "markerTitle",
                        resources.getString(R.string.geofenceMarkerTitleDefault));
                String markerSnippet = getStringFromJsonObject(
                        geofenceJson,
                        "markerSnippet",
                        resources.getString(R.string.geofenceMarkerSnippetDefault));
                int radius = getIntFromJsonObject(
                        geofenceJson,
                        "radius",
                        resources.getInteger(R.integer.geofenceRadiusDefault));
                long expirationDuration = getLongFromJsonObject(
                        geofenceJson,
                        "expirationDuration",
                        Geofence.NEVER_EXPIRE);
                int notificationResponsiveness = getIntFromJsonObject(
                        geofenceJson,
                        "notificationResponsiveness",
                        0);
                int loiteringDelay = getIntFromJsonObject(
                        dwellTransition,
                        "loiteringDelay",
                        resources.getInteger(R.integer.geofenceLoiteringDelayDefault));
                String geofenceEnterNotificationText = getStringFromJsonObject(
                        enterTransition,
                        "notificationText",
                        resources.getString(R.string.notifyGeofenceEnter));
                String geofenceDwellNotificationText = getStringFromJsonObject(
                        dwellTransition,
                        "notificationText",
                        resources.getString(R.string.notifyGeofenceDwell));
                String geofenceExitNotificationText = getStringFromJsonObject(
                        exitTransition,
                        "notificationText",
                        resources.getString(R.string.notifyGeofenceExit));
                MarkerOptions markerOptions = new MarkerOptions()
                        .visible(true)
                        .position(latlng)
                        .title(markerTitle)
                        .snippet(markerSnippet)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                CircleOptions circleOptions = new CircleOptions()
                        .visible(true)
                        .center(latlng)
                        .radius(radius)
                        .fillColor(ContextCompat.getColor(context, R.color.geofenceCircleFillDefault))
                        .strokeColor(ContextCompat.getColor(context, R.color.geofenceCircleStrokeDefault))
                        .strokeWidth(resources.getInteger(R.integer.geofenceCircleStrokeWidthDefault));

                // Media for the geofence
                JSONObject pinLink = new Downloader(context, geofenceJson.getString("pinLink")).getJsonObject();
                String geofenceInfoText = getStringFromJsonObject(
                        pinLink,
                        "pinInfo",
                        resources.getString(R.string.infoActivityWebViewDefault));

                JSONArray mediaJsonArray = pinLink.getJSONArray("media");
                ArrayList<String>
                        pics = new ArrayList<>(),
                        auds = new ArrayList<>(),
                        vids = new ArrayList<>();
                for (int j = 0; j < mediaJsonArray.length(); j++) {
                    JSONObject mediaElement = mediaJsonArray.getJSONObject(j);
                    String mediaType = mediaElement.getString("mediaType");

                    if (mediaType.equals("pic")) {
                        pics.add(mediaElement.getString("picLink"));
                    }
                    else if (mediaType.equals("aud")) {
                        auds.add(mediaElement.getString("audLink"));
                    }
                    else if (mediaType.equals("vid")) {
                        vids.add(mediaElement.getString("vidLink"));
                    }
                    else {
                        // unsupported mediaType
                    }
                }

                Log.d(TAG, "Pics: " +pics.size()
                        + ", Audios: " + auds.size()
                        + ", Videos: " + vids.size());


                Geofence geofence = new Geofence.Builder()
                        .setRequestId(cortezMapName + "@" + markerTitle + ":" + latitude + "," + longitude)
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
                        .enterNotificationText(geofenceEnterNotificationText)
                        .dwellNotificationText(geofenceDwellNotificationText)
                        .exitNotificationText(geofenceExitNotificationText)
                        .geofenceInfoText(geofenceInfoText)
                        .picLinks(pics)
                        .audLinks(auds)
                        .vidLinks(vids)
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
     * Opens Cortez JSON data from internal storage.
     * @param fullPath the absolute file path to the Cortez JSON data file (Including file name and extension)
     */
    protected JSONObject openMapData(String fullPath) {
        Log.i(TAG, "Opening Cortez Map Data...");
        Log.d(TAG, "Opening from: " + fullPath);

        File file = new File(fullPath);
        StringBuilder text = new StringBuilder();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

        } catch (IOException e) {
            // TODO: Handle the bad JSON file in some way
            e.printStackTrace();
        } finally {
            try {
                br.close();
                Log.i(TAG, "Successfully opened Cortez Map Data.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            return new JSONObject(text.toString());
        } catch (JSONException e) {
            // TODO: Handle the bad JSON file in some way
            e.printStackTrace();
        }

        return new JSONObject(); // returned on error
    }

    public HashMap<LatLng, CortezGeofence> getCortezGeofences() {
        return cortezGeofences;
    }

    public String getCortezMapName() {
        return cortezMapName;
    }
}
