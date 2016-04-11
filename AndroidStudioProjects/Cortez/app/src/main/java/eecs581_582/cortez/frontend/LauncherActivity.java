package eecs581_582.cortez.frontend;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Random;

import eecs581_582.cortez.R;
import eecs581_582.cortez.backend.Constants;
import eecs581_582.cortez.backend.Downloader;
import eecs581_582.cortez.backend.GoogleApiChecker;
import eecs581_582.cortez.backend.MapSelectListWorker;

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
        final String[] tipStrings = {
                getString(R.string.launcher_text),
                getString(R.string.launcher_text1),
                getString(R.string.launcher_text2),
                getString(R.string.launcher_text3),
                getString(R.string.launcher_text4),
                getString(R.string.launcher_text5)
        };
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

                // Randomize the tip string
                TextView currtipString = (TextView) findViewById(R.id.smallertextView);
                Random pickString = new Random();
                int chosenString = pickString.nextInt(6);
                currtipString.setText(tipStrings[chosenString]);

                /*
                 * I think the Intent to transition to MapSelectActivity should be placed
                 * outside the try-catch block, so we don't end up crashing or hanging
                 * in the event that we experience some error in contacting the database
                 * or have trouble handling the downloaded content.
                 */
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

                    /*
                     * Update any maps in the list from local storage.
                     * For each local map, we're going to change its respective path in availableMaps
                     * (e.g., its "mapLink" JSONObject from within availableMaps)
                     * to the local file. That way, Cortez will open the map from local storage.
                     */
                    MapSelectListWorker.updateMapList(getBaseContext(), availableMaps);

                    intent.putExtra("Available Maps", availableMaps.toString());

                    // TODO: Only sleep the thread if the download took less than 3 seconds.
                    // That would keep the user from waiting any longer than necessary, in case there's a ridiculous delay in downloading.
                    sleep(3000);
                    Log.d(TAG, "Finished background work and transitioning to MapSelectActivity.");
                }
                catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                finally {
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
