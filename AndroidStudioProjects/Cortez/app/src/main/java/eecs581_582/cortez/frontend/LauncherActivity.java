package eecs581_582.cortez.frontend;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import eecs581_582.cortez.R;

/* ********************************************************************
 * LauncherActivity should be the first thing the user sees. It should
 * display the app's logo during initialization, as well as holding for
 * at least a couple of seconds, just to look pretty. Following this,
 * it should direct the user automatically to the MapSelectActivity to
 * begin their experience.
 *
 * - There should be no user-accessible action
 *
 * Credit to http://www.coderefer.com/android-splash-screen-example-tutorial/
 * for initial design
 */

public class LauncherActivity extends Activity {

    public static final String TAG = InfoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        Thread timerThread = new Thread(){
            public void run(){
                try {
                    Log.d(TAG, "timerThread");
                    sleep(3000);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(LauncherActivity.this,MapSelectActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onPause");
        super.onPause();
        finish();
    }

}
