package eecs581_582.cortez.backend;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Joseph on 4/4/16.
 */
public class MapSelectListWorker {
    public static final String TAG = MapSelectListWorker.class.getSimpleName();

    /**
     *
     * @param context
     * @param availableMaps
     * @throws JSONException
     */
    public static void updateMapList(Context context, JSONObject availableMaps) throws JSONException {

        JSONArray availableMapsArray = availableMaps.getJSONArray("maps");
        for (int i = 0; i < availableMapsArray.length(); i++) {
            JSONObject mapSelectCardInfo = availableMapsArray.getJSONObject(i);

            /*
             * Check whether the map is on the device already.
             * If it is, download an updated copy from the server
             * and save the changes.
             */
            String mapLink = mapSelectCardInfo.getString("mapLink");
            String mapName = mapSelectCardInfo.getString("mapName");
            String fullPath = context.getFilesDir().getPath() + "/" + mapName;
            File file = new File(fullPath);
            if (file.exists()) {
                Log.i(TAG, mapName + " exists in local storage, and will now be updated.");

                // Download the file from the server
                // and overwrite the existing local file
                Downloader d = new Downloader(context, mapLink);
                d.saveMapData(mapName);

                /*
                 * Since this map is already locally stored,
                 * we want to link the MapSelectCard to the local file.
                 * By doing so, we'll manage to open from local storage
                 * in the event that the user chooses to open *this* map.
                 */
                mapSelectCardInfo.put("mapLink", fullPath);
                mapSelectCardInfo.put("isLocal", true);

                // TODO: We could add some data to mapCard here, indicating that the map exists in local storage.
                // We can display that information to the user in MapSelectActivity. This is optional; not necessary.
            }
            else {
                // The map was not found on local storage,
                // so don't show it in the "local" list.
                mapSelectCardInfo.put("isLocal", false);
            }
        }
    }
}
