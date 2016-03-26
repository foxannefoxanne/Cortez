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

/* ********************************************************************
 * PICK YO MAPS
 * TODO: Actually fill in comment shit to look professional like an adult
 */

public class MapSelectActivity extends Activity {

    public static final String TAG = MapSelectActivity.class.getSimpleName();
    RecyclerView recList;
    MapSelectCardAdapter ca, cab;
    boolean viewingLocalMaps;

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

        // TODO: Populate the list with the number of Cortez maps
        ca = new MapSelectCardAdapter(createList(getIntent().getStringExtra("Database Maps")));
        cab = new MapSelectCardAdapter(createList(getIntent().getStringExtra("Local Maps")));
        recList.setAdapter(cab);
        viewingLocalMaps = true;
    }

    @Override
    public void onBackPressed() {
        // Placeholder to disable the back button (and thus prevents the LauncherActivity from reappearing)
        if (!viewingLocalMaps) {
            Log.d(TAG, "Back button pressed. Resetting view to Local maps.");
            recList.setAdapter(cab);
            viewingLocalMaps = true;
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
                // TODO: Once downloaded, recList.setAdapter back to the Local Map adapter
                recList.setAdapter(ca);
                viewingLocalMaps = false;
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
}
