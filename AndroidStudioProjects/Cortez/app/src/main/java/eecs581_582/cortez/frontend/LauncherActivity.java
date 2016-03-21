package eecs581_582.cortez.frontend;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import eecs581_582.cortez.R;
import eecs581_582.cortez.backend.Constants;
import eecs581_582.cortez.backend.Downloader;
import eecs581_582.cortez.backend.GoogleApiChecker;

/* ********************************************************************
 * LauncherActivity should be the first thing the user sees. It should
 * display the app's logo during initialization, as well as holding for
 * at least a couple of seconds, just to look pretty. Following this,
 * it should direct the user automatically to the MapSelectActivity to
 * begin their experience.
 *
 * - There should be no user-accessible action
 *
 * Credit to http://www.coderefer.com/android-splash-screen-example-tutorial/
 * for initial design
 */

public class LauncherActivity extends Activity {

    public static final String TAG = LauncherActivity.class.getSimpleName();

    private Context context = getBaseContext();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // TODO: If this check fails, we should not go to MapSelectActivity.
        // Require Google Play Services to be installed to enable Cortez.
        GoogleApiChecker.checkPlayServices(this);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        Thread timerThread = new Thread(){
            public void run(){
                Intent intent = new Intent(LauncherActivity.this, MapSelectActivity.class);
                try {
                    Log.d(TAG, "Doing background work...");

                    /*
                     * Get a list of all available maps and their info.
                     * Each JSONObject in this JSONArray will contain
                     * the information that is displayed for each map
                     * in MapSelectCardActivity.
                     *
                     * Specifically, each JSONObject must contain:
                     *
                     * - The map name
                     * - A brief description of the map for users
                     * - The fully-qualified file path to the map
                     */
                    JSONObject availableMaps = new Downloader(context, Constants.AVAILABLE_MAPS_LINK)
                            .getJsonObject();

                    JSONArray localMaps = new JSONArray();


                    JSONArray mapsArray = availableMaps.getJSONArray("maps");

                    for (int i = 0; i < mapsArray.length(); i++) {
                        JSONObject mapSelectCardInfo = mapsArray.getJSONObject(i);

                        /*
                         * Check whether the map is on the device already.
                         * If it is, download an updated copy from the server
                         * and save the changes.
                         */
                        String mapLink = mapSelectCardInfo.getString("mapLink");
                        String mapName = mapSelectCardInfo.getString("mapName");
                        String fileName = mapLink.substring(mapLink.lastIndexOf("/") + 1, mapLink.length());
                        String fullPath = getFilesDir().getPath() + "/" + fileName;
                        File file = new File(fullPath);
                        if (file.exists()) {
                            Log.i(TAG, mapName + " exists in local storage, and will now be updated.");

                            // Download the file from the server
                            // and overwrite the existing local file
                            Downloader d = new Downloader(getBaseContext(), mapLink);
                            d.saveMapData(fileName);

                            /*
                             * Since this map is already locally stored,
                             * we want to link the MapSelectCard to the local file.
                             * By doing so, we'll manage to open from local storage
                             * in the event that the user chooses to open *this* map.
                             */
                            mapSelectCardInfo.put("mapLink", fullPath);

                            localMaps.put(d.getJsonObject());

                            // TODO: We could add some data to mapCard here, indicating that the map exists in local storage.
                            // We can display that information to the user in MapSelectActivity. This is optional; not necessary.
                        }
                    }

                    intent.putExtra("Database Maps", availableMaps.toString());
                    intent.putExtra("Local Maps", localMaps.toString());

                    // TODO: Only sleep the thread if the download took less than 3 seconds.
                    // That would keep the user from waiting any longer than necessary, in case there's a ridiculous delay in downloading.
                    sleep(3000);
                    Log.d(TAG, "Finished background work and transitioning to MapSelectActivity.");
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                } finally {
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        finish();
    }

}
