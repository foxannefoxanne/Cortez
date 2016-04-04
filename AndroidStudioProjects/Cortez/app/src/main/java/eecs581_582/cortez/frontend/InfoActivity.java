package eecs581_582.cortez.frontend;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import eecs581_582.cortez.R;
import eecs581_582.cortez.backend.Constants;

/* ********************************************************************
 * InfoActivity is the screen that displays data for a particular
 * Marker chosen in MapActivity. It should pull this data from the
 * database, parse it, place text into a webview, and feature links to
 * media content view screens (if enabled by MapActivity call).
 *
 * - Selection of the Help button in the context menu should take the
 *  user to the HelpActivity
 */

public class InfoActivity extends FragmentActivity {
    public static final String TAG = InfoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setUpViewFromIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        Log.d(TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_help: {
                // This means you have selected the Help option
                Log.d(TAG, "Help button selected");
                Intent intent = new Intent(this, HelpActivity.class);
                intent.putExtra(getString(R.string.action_help), Constants.Caller.INFO_ACTIVITY);
                startActivity(intent);
                return true;
            }
            case R.id.action_settings : {
                // This means you have selected the Settings option.
                // Since activity_info.xml has that option removed, you've screwed something up. What did you do?
                Log.d(TAG, "Settings button selected");
//                Intent intent = new Intent(this, SettingsActivity.class);
//                intent.putExtra(getString(R.string.action_settings), Constants.Caller.INFO_ACTIVITY);
//                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     *
     * @param intent
     */
    private void setUpViewFromIntent(Intent intent) {
        Log.d(TAG, "setUpViewFromIntent");
        setTitle(intent.getStringExtra("title"));

        // Set the WebView
        WebView webView = (WebView) findViewById(R.id.infoActivityWebView);
        String message = intent.getStringExtra("infoActivityMessage1");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadData(
                !message.isEmpty()
                        ? message
                        : getString(R.string.infoActivityWebViewDefault),
                "text/html",
                "utf-8");
    }
}