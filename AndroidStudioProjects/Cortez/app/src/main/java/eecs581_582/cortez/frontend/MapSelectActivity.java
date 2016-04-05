package eecs581_582.cortez.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eecs581_582.cortez.R;
import eecs581_582.cortez.backend.Constants;
import eecs581_582.cortez.backend.MapSelectListWorker;

import static eecs581_582.cortez.backend.JSONHandler.getStringFromJsonObject;

/* ********************************************************************
 * MapSelectActivity is a screen that allows the user to decide which
 * set of pins to display and interact with in MapActivity. It will
 * default to showing the maps already stored on the device, and allows
 * for the user to download more maps from the database.
 *
 * - Selection of a map from the local list should take the user to
 *   MapActivity
 * - Selection of the Help button in the context menu should take the
 *   user to the HelpActivity
 * - Selection of the Add New Map button in the context menu should
 *   swap the MapSelectCardAdapter to show the user available maps on
 *   the database
 */

public class MapSelectActivity extends Activity {

    public static final String TAG = MapSelectActivity.class.getSimpleName();
    RecyclerView recList;
    MapSelectCardAdapter local, external;

    /*
     * Since we have to manipulate the JSONObject representing available maps
     * at various points in the code
     * in order to display updates to the local map file list,
     * this local variable is necessary.
     */
    JSONObject availableMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select);

        // Set up and display all available Cortez maps in a card layout menu.
        recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // Populate the list with the number of Cortez maps
        populateList();
    }

    @Override
    public void onBackPressed() {
        if (!isViewingLocalMaps()) {
            Log.d(TAG, "Back button pressed while viewing external maps. Resetting view to Local maps.");
            populateList(); // Automatically switches back to view the local map list.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_select, menu);
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
                intent.putExtra(getString(R.string.action_help), Constants.Caller.MAPSELECT_ACTIVITY);
                startActivity(intent);
                return true;
            }
            case R.id.action_settings : {
                // TODO: Implement SettingsActivity before un-commenting these lines.
                // This means you have selected the Settings option
                Log.d(TAG, "Settings button selected");
//                Intent intent = new Intent(this, SettingsActivity.class);
//                intent.putExtra(getString(R.string.action_settings), Constants.Caller.MAPSELECT_ACTIVITY);
//                startActivity(intent);
                return true;
            }
            case R.id.action_add_map: {
                // This means you want to add a map from the database.
                Log.d(TAG, "Adding a map");
                // Switch to the Database Map adapter
                recList.setAdapter(external);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Populates the list of available Cortez maps, which will be displayed in the RecyclerView.
     * @param list all available Cortez maps
     * @return the list of available Cortez maps
     */
    private List<MapSelectCard> createList(String list) {
        try {

            JSONObject mapsArray = new JSONObject(list);
            JSONArray jsonArray = mapsArray.getJSONArray("maps");

            List<MapSelectCard> result = new ArrayList<MapSelectCard>();
            for (int i = 0; i < jsonArray.length(); i++) {
                MapSelectCard ci = new MapSelectCard();

                JSONObject map = jsonArray.getJSONObject(i);

                ci.name = getStringFromJsonObject(map, "mapName", (MapSelectCard.NAME_PREFIX + (i + 1)));
                ci.descriptionMessage = getStringFromJsonObject(map, "mapDescription", "None.");
                ci.path = getStringFromJsonObject(map, "mapLink", Constants.AVAILABLE_MAPS_LINK);

                result.add(ci);
            }

            return result;
        }
        catch (JSONException e) {
            // TODO: Handle the bad JSON in some way
            Log.e(TAG, e.getLocalizedMessage());
            return new ArrayList<MapSelectCard>();
        }
    }

    /**
     *
     */
    private void populateList() {
        try {
            /*
             * The only time availableMaps will == null is during onCreate().
             * At that time, we can grab the available maps from the Intent extra that was passed in
             * from LauncherActivity, and store it into a local JSONObject called 'availableMaps'.
             *
             * When populateList() is used after onCreate(), we want to work with the local JSONObject.
             * This will allow us to display updates to the list of maps that are on the device.
             */
            if (availableMaps == null) {
                availableMaps = new JSONObject(getIntent().getStringExtra("Available Maps"));
            }
            else {
                MapSelectListWorker.updateMapList(getBaseContext(), availableMaps);
            }
            JSONObject localMaps = new JSONObject();
            JSONArray localMapsArray = new JSONArray();
            JSONObject externalMaps = new JSONObject();
            JSONArray externalMapsArray = new JSONArray();

            JSONArray availableMapsArray = availableMaps.getJSONArray("maps");
            for (int i = 0; i < availableMapsArray.length(); i++) {
                JSONObject mapSelectCardInfo = availableMapsArray.getJSONObject(i);
                if (mapSelectCardInfo.getBoolean("isLocal")) {
                    localMapsArray.put(mapSelectCardInfo);
                }
                else {
                    externalMapsArray.put(mapSelectCardInfo);
                }
            }

            localMaps.put("maps", localMapsArray);
            externalMaps.put("maps", externalMapsArray);

            local = new MapSelectCardAdapter(createList(localMaps.toString()));
            external = new MapSelectCardAdapter(createList(externalMaps.toString()));

            // Display the maps on local storage.
            // User can check remaining maps available on the database from the "Add Maps" MenuOption.
            recList.setAdapter(local);
        } catch (JSONException e) {}
    }

    /**
     *
     * @return whether the user is currently viewing maps from local storage.
     */
    private boolean isViewingLocalMaps() {
        return recList.getAdapter().equals(local);
    }
}
