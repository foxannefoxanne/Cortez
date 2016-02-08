package eecs581_582.cortez.backend;

/**
 * Created by Joseph on 2/2/16.
 */
public class Constants {

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
