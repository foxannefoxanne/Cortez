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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

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
     * Cortez JSON Data
     */
    private JSONObject cortezJSONData;

    /**
     * Cortez Geofences
     */
    private HashMap<LatLng, CortezGeofence> cortezGeofences;

    /**
     * Constructor for debugging.
     * @param context
     */
    public CortezMapData(Context context) {
        this.cortezJSONData = setCortezJSONData(context);
        this.cortezMapName = getStringFromJsonObject(cortezJSONData, "mapName", context.getString(R.string.cortezMapNameDefault));
        this.cortezGeofences = setCortezGeofences(context, cortezJSONData);
    }

    /**
     * Constructor for maps that are being downloaded from a stream.
     * @param context
     * @param cortezJSONData
     */
    public CortezMapData(Context context, JSONObject cortezJSONData) {
        this.cortezJSONData = cortezJSONData;

        // Always save a downloaded map.
        saveMapData(context);

        this.cortezMapName = getStringFromJsonObject(cortezJSONData, "mapName", context.getString(R.string.cortezMapNameDefault));
        this.cortezGeofences = setCortezGeofences(context, cortezJSONData);
    }

    /**
     * Constructor for maps that are being read from local storage.
     * @param context
     * @param filename
     */
    public CortezMapData(Context context, String filename) {
        this.cortezJSONData = openMapData(context.getFilesDir().getPath() + "/" + filename);
        this.cortezMapName = getStringFromJsonObject(cortezJSONData, "mapName", context.getString(R.string.cortezMapNameDefault));
        this.cortezGeofences = setCortezGeofences(context, cortezJSONData);
    }

    /**
     * Sets Cortez JSON data.
     * @return a traversable JSON object containing all textual data for Cortez
     */
    private JSONObject setCortezJSONData(Context context) {
        try {
            InputStream is = context.getAssets().open("cortezSampleJson.json");
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
                String infoActivityMessage1 = getStringFromJsonObject(
                        geofenceJson,
                        "infoActivityString1",
                        resources.getString(R.string.infoActivityWebViewDefault));
                String infoActivityMessage2 = getStringFromJsonObject(
                        geofenceJson,
                        "infoActivityString2",
                        resources.getString(R.string.infoActivityTextViewDefault));

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
                        .fillColor(ContextCompat.getColor(context, R.color.geofenceCircleFillDefault))
                        .strokeColor(ContextCompat.getColor(context, R.color.geofenceCircleStrokeDefault))
                        .strokeWidth(resources.getInteger(R.integer.geofenceCircleStrokeWidthDefault));

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
                        .enterNotificationText(geofenceEnterNotificationText)
                        .dwellNotificationText(geofenceDwellNotificationText)
                        .exitNotificationText(geofenceExitNotificationText)
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
            int i = jsonObject.getInt(jsonKey);
            Log.d(TAG, "Got " + jsonKey);
            return i;
        }
        catch (JSONException e) {
            Log.w(TAG, e.getLocalizedMessage() + ", so substituting " + defaultValue);
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
            long l = jsonObject.getInt(jsonKey);
            Log.d(TAG, "Got " + jsonKey);
            return l;
        }
        catch (JSONException e) {
            Log.w(TAG, e.getLocalizedMessage() + ", so substituting " + defaultValue);
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
            String s = jsonObject.getString(jsonKey);
            Log.d(TAG, "Got " + jsonKey);
            return s;
        }
        catch (JSONException e) {
            Log.w(TAG, e.getLocalizedMessage() + ", so substituting " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Saves a copy of the Cortez JSON data to internal storage.
     * This copy can be used to load the Geofence data for the map at a later time,
     * without redundant database calls.
     */
    protected void saveMapData(Context context) {
        FileOutputStream outputStream = null;
        try {

            // TODO: This filename will need to come from somewhere external to the JSON data itself
            String filename = cortezJSONData.getString("filename");

            /*
             * Set the data to be saved in "private mode" (accessible only to Cortez).
             * It's my opinion that we should do this, because we don't want the map data
             * to be modified by any programs / persons external to the Cortez app.
             */
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);

            String fullPath = context.getFilesDir().getPath() + "/" + filename;

            Log.i(TAG, "Saving Cortez Map Data...");
            Log.d(TAG, "File path to save: " + fullPath);
            outputStream.write(cortezJSONData.toString().getBytes());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        } finally {
            try {
                outputStream.close();
                Log.i(TAG, "Successfully saved Cortez Map Data.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    protected JSONObject getCortezJSONData() {
        return cortezJSONData;
    }

    public HashMap<LatLng, CortezGeofence> getCortezGeofences() {
        return cortezGeofences;
    }

    public String getCortezMapName() {
        return cortezMapName;
    }
}
