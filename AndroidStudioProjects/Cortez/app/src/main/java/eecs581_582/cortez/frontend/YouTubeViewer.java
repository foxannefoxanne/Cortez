package eecs581_582.cortez.frontend;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import eecs581_582.cortez.backend.Developer;
import eecs581_582.cortez.backend.GoogleApiChecker;
import eecs581_582.cortez.R;

/**
 * Created by Joseph on 1/30/16.
 */
public class YouTubeViewer extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener {

    public static final String TAG = YouTubeViewer.class.getSimpleName();

    // TODO: use one of these test strings by setting YOUTUBE_URL equal to it.
    private final String TEST_VIDEO_URL = "https://www.youtube.com/watch?v=C0DPdy98e4c";
    private final String TEST_PLAYLIST_URL = "https://www.youtube.com/playlist?list=PL59FEE129ADFF2B12";

    // TODO: getIntent().getStringExtra("youtube") will be the official item coming from an Intent, whose value was received from CortezMapData.
    // All of that is going to be accomplished in the MapActivity class,
    private String YOUTUBE_URL = TEST_VIDEO_URL; //getIntent().getStringExtra("youtube");
    private YouTubePlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtubeviewer);
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);

        // Make sure YouTube is on the device
        GoogleApiChecker.checkYouTubeAPI(this);

        youTubePlayerView.initialize(Developer.API_KEY, this);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        setUpYouTubePlayer();
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult error) {
        Log.d(TAG, "onInitializationFailure");
        error.getErrorDialog(this, 2).show();
    }

    @Override
    public void onInitializationSuccess(final Provider provider, YouTubePlayer player, boolean wasRestored) {
        Log.d(TAG, "onInitializationSuccess");
        this.player = player;

        setUpYouTubePlayer();

        playVideos();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        super.onBackPressed();
    }

    /**
     * Prepares the YouTubePlayer to display as specified.
     */
    private void setUpYouTubePlayer() {
        // Force lock the screen into landscape mode while the video is playing
        // (but allow user to flip the device to the opposite landscape orientation)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        if (player != null) {
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);   // Don't give access to YouTube controls
            player.setFullscreen(true);

            // These listeners are currently only being used for debugging
            player.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean b) {
                    Log.d(TAG, "YouTubePlayer is full screen.");
                }
            });
            player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                @Override
                public void onPlaying() {
                    Log.d(TAG, "YouTubePlayer is playing.");
                }

                @Override
                public void onPaused() {
                    Log.d(TAG, "YouTubePlayer is paused.");
                }

                @Override
                public void onStopped() {
                    Log.d(TAG, "YouTubePlayer has stopped.");
                }

                @Override
                public void onBuffering(boolean b) {
                    Log.d(TAG, "YouTubePlayer " + (b ? "is buffering..." : "has stopped buffering."));
                }

                @Override
                public void onSeekTo(int i) {
                    Log.d(TAG, "YouTubePlayer seekTo " + formatTime(i));
                }
            });
        }
    }

    /**
     * Gives back an easy-to-read time format.
     * @param millis milliseconds
     * @return the equivalent length of time, in a more readable format.
     */
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return (hours == 0 ? "" : hours + ":")
                + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }

    /**
     * A somewhat hacky (no API / no JSON) method of opening YouTube videos.
     * It works for now, but this will need to be replaced with the more appropriate API call.
     */
    private void playVideos() {
        if (YOUTUBE_URL != null) {
            String videoType = YOUTUBE_URL.substring(YOUTUBE_URL.indexOf('?')+1, YOUTUBE_URL.indexOf('='));
            String videoId = YOUTUBE_URL.substring(YOUTUBE_URL.indexOf('=')+1, YOUTUBE_URL.length());

            // Currently, we're only supporting video and playlist.
            if (videoType.equals("v")) {            // If it's a video ID
                player.loadVideo(videoId);
            }
            else if (videoType.equals("list")) {    // If it's a playlist ID
                player.loadPlaylist(videoId);
            }
            else {
                Log.d(TAG, "uncaught case for playVideos()");
            }

        }
    }
}