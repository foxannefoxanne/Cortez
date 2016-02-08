package eecs581_582.cortez.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;

import eecs581_582.cortez.R;

/* ********************************************************************
 * TODO: MAKE THIS COMMENT LOOK PRETTY
 * TODO: MAKE CORTEZ LOGO SHOW UP, LOOK PRETTY, IMPRESS LADIES/INVE$TOR$
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

        // Begin status bar hiding snippet
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        // End status bar hiding snippet

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
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
