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

import static eecs581_582.cortez.backend.JSONHandler.getStringFromJsonObject;

/* ********************************************************************
 * PICK YO MAPS
 * TODO: Actually fill in comment shit to look professional like an adult
 */

public class MapSelectActivity extends Activity {

    public static final String TAG = MapSelectActivity.class.getSimpleName();
    RecyclerView recList;
    MapSelectCardAdapter local, external;

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
        try {
            JSONObject availableMaps = new JSONObject(getIntent().getStringExtra("Available Maps"));
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

    @Override
    public void onBackPressed() {
        if (!isViewingLocalMaps()) {
            Log.d(TAG, "Back button pressed. Resetting view to Local maps.");
            recList.setAdapter(local);
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
                Log.d(TAG,"Adding a map");
                // Switch to the Database Map adapter
                // TODO: Once a map is added, make sure it is added to local. Presently, local doesn't update when new maps are added.
                recList.setAdapter(external);
                // TODO: Once downloaded, recList.setAdapter(local)
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
                ci.description = MapSelectCard.DESCRIPTION_PREFIX + getStringFromJsonObject(map, "mapDescription", "None.");
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
     * @return whether the user is currently viewing maps from local storage.
     */
    private boolean isViewingLocalMaps() {
        return recList.getAdapter().equals(local);
    }
}
