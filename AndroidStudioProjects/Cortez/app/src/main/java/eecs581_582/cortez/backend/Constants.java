package eecs581_582.cortez.backend;

/**
 * Created by Joseph on 2/2/16.
 */
public class Constants {

    // URLs where Cortez data can be found
    public static final String URL1 = "http://people.eecs.ku.edu/~jchampio/JsonTemplateFile.json";      // Static webpage (if the database goes down)
    public static final String URL2 = "http://people.eecs.ku.edu/~jchampio/KUMap.json";                 // Different map data for testing (static)
    public static final String URL3 = "https://thawing-dusk-70157.herokuapp.com/dump";                  // Database (eventually non-static)

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
