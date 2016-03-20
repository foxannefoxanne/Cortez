package eecs581_582.cortez.backend;

/**
 * Created by Joseph on 2/2/16.
 */
public class Constants {

    // URLs where Cortez data can be found
    public static final String AVAILABLE_MAPS_LINK = "http://people.eecs.ku.edu/~jchampio/Maps.json";   // Database link containing JSON that lists all available maps for Cortez
    public static final String DATABASE_LINK = "https://thawing-dusk-70157.herokuapp.com/dump";         // Database link containing the JSON for a particular map (eventually non-static)

    /**
     * These make it easy to determine which Activity is a caller.
     */
    public enum Caller {
        MAPSELECT_ACTIVITY,
        MAP_ACTIVITY,
        INFO_ACTIVITY,
        YOUTUBE_ACTIVITY,
        UNKNOWN
    }

    /**
     * These make it easy to determine which Activity is a callee.
     */
    public enum Callee {
        MAPSELECT_ACTIVITY,
        MAP_ACTIVITY,
        INFO_ACTIVITY,
        YOUTUBE_ACTIVITY,
        UNKNOWN
    }
}
