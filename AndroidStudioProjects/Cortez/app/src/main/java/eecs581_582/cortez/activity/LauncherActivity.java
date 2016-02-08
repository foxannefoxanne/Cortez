package eecs581_582.cortez.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import eecs581_582.cortez.R;

public class LauncherActivity extends Activity {

    public static final String TAG = InfoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

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
