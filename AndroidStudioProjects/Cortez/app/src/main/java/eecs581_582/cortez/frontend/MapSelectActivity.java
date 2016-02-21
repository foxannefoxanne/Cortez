package eecs581_582.cortez.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import eecs581_582.cortez.R;
import eecs581_582.cortez.backend.Constants;

/* ********************************************************************
 * PICK YO MAPS
 * TODO: Actually fill in comment shit to look professional like an adult
 * TODO: MAKE YO MAPS PICKABLE
 */

public class MapSelectActivity extends Activity {

    public static final String TAG = MapSelectActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select);

        // Set up and display all available Cortez maps in a card layout menu.
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        // TODO: Populate the list with the number of Cortez maps
        MapSelectCardAdapter ca = new MapSelectCardAdapter(createList(10));
        recList.setAdapter(ca);
    }

    @Override
    public void onBackPressed() {
        // Placeholder to disable the back button (and thus prevents the LauncherActivity from reappearing)
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
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Populates the list of available Cortez maps, which will be displayed in the RecyclerView.
     * @param size the number of available Cortez maps
     * @return the list of available Cortez maps
     */
    private List<MapSelectCard> createList(int size) {

        //TODO: Dynamically set the name and description for each Cortez map

        List<MapSelectCard> result = new ArrayList<MapSelectCard>();
        for (int i = 1; i <= size; i++) {
            MapSelectCard ci = new MapSelectCard();
            ci.name = MapSelectCard.NAME_PREFIX + i;
            ci.description = MapSelectCard.DESCRIPTION_PREFIX;

            result.add(ci);

        }

        return result;
    }
}
