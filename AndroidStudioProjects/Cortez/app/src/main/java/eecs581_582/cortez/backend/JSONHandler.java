package eecs581_582.cortez.backend;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joseph on 3/21/16.
 */
public class JSONHandler {
    public static final String TAG = JSONHandler.class.getSimpleName();

    /**
     * Attempts to get an integer value from a JSONObject key.
     * @precondition Assumes the key is directly accessible.
     * @param jsonObject the JSONObject containing the desired value
     * @param jsonKey the JSON key at which the desired value is stored
     * @param defaultValue a default value to return in case retrieval fails
     * @return the value at the specified key from a JSONObject if successful, otherwise, a default value.
     */
    public static int getIntFromJsonObject(JSONObject jsonObject, String jsonKey, int defaultValue) {
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
    public static long getLongFromJsonObject(JSONObject jsonObject, String jsonKey, long defaultValue) {
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
    public static String getStringFromJsonObject(JSONObject jsonObject, String jsonKey, String defaultValue) {
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
}
