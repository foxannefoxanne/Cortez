package eecs581_582.cortez.frontend;

import android.app.Activity;
import android.content.pm.ActivityInfo;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import eecs581_582.cortez.R;

/**
 * Created by Joseph on 4/6/16.
 */
public class VideoViewer extends Activity {
    public static final String TAG = VideoViewer.class.getSimpleName();
    private static final String path ="https://prod-cortez-asset-storage.s3.amazonaws.com/app/public/video/4/Weaver.mp4?1459184875";
    private VideoView videoView;
    private MediaController mediaController;
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_videoviewer);

        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setVideoPath(path);

        mediaController = new MediaController(this);
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);
        videoView.requestFocus();
        videoView.start();
    }
}
