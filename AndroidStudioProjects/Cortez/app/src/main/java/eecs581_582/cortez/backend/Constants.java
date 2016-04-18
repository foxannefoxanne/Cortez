package eecs581_582.cortez.backend;

import eecs581_582.cortez.R;
import eecs581_582.cortez.frontend.AudioPlayer;
import eecs581_582.cortez.frontend.ImageViewer;
import eecs581_582.cortez.frontend.VideoViewer;

/**
 * Created by Joseph on 2/2/16.
 */
public class Constants {

    // URLs where Cortez data can be found
    public static final String AVAILABLE_MAPS_LINK = "https://thawing-dusk-70157.herokuapp.com/maps_dump";   // Database link containing JSON that lists all available maps for Cortez
    public static final String DATABASE_LINK = "https://thawing-dusk-70157.herokuapp.com/dump";         // Database link containing the JSON for a particular map (eventually non-static)

    /**
     * These make it easy to determine which Activity is a caller.
     */
    public enum Caller {
        MAPSELECT_ACTIVITY,
        MAP_ACTIVITY,
        INFO_ACTIVITY,
        YOUTUBE_ACTIVITY,
        MEDIASELECT_ACTIVITY,
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
        MEDIASELECT_ACTIVITY,
        UNKNOWN
    }

    public enum MediaType {
        IMAGE(R.mipmap.ic_action_picture, "Image", ImageViewer.class),
        AUDIO(R.mipmap.ic_action_play, "Audio", AudioPlayer.class),
        VIDEO(R.mipmap.ic_action_video, "Video", VideoViewer.class);

        int icon;
        String name;
        Class handlerClass;
        MediaType(int icon, String name, Class handlerClass) {
            this.icon = icon;
            this.name = name;
            this.handlerClass = handlerClass;
        }

        public int getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }

        public Class getHandlerClass() {
            return handlerClass;
        }
    }
}
