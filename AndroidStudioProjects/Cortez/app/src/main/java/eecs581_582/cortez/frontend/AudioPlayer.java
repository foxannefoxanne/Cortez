package eecs581_582.cortez.frontend;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.media.MediaPlayer.OnPreparedListener;
import android.view.MotionEvent;
import android.widget.MediaController;
import android.widget.TextView;

import java.io.IOException;

import eecs581_582.cortez.R;
import eecs581_582.cortez.backend.Constants;

/**
 * Activity class for handling audio files.
 * Credit: http://stackoverflow.com/questions/3747139/how-can-i-show-a-mediacontroller-while-playing-audio-in-android
 */
public class AudioPlayer extends Activity
        implements OnPreparedListener, MediaController.MediaPlayerControl {

    public static final String TAG = AudioPlayer.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private String audioFile;

    private Handler handler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audioplayer);

        ((TextView)findViewById(R.id.now_playing_text)).setText(audioFile);

        audioFile = getIntent().getStringExtra(Constants.MediaType.AUDIO.getName());

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);

        mediaController = new MediaController(this);

        try {
            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "Could not open file " + audioFile + " for playback.", e);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaController.hide();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //the MediaController will hide after 3 seconds - tap the screen to make it appear again
        mediaController.show();
        return false;
    }

    //--MediaPlayerControl methods----------------------------------------------------
    public void start() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        return 0;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
    //--------------------------------------------------------------------------------

    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPrepared");
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(findViewById(R.id.main_audio_view));

        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });
    }
}