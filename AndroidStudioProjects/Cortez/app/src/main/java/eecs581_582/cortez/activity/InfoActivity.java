package eecs581_582.cortez.activity;

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
import android.widget.TextView;

import eecs581_582.cortez.R;
import eecs581_582.cortez.backend.Constants;

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
                // TODO: Implement SettingsActivity before un-commenting these lines.
                // This means you have selected the Settings option
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


        // Set the TextView
//        TextView textViewToChange = (TextView) findViewById(R.id.infoActivityTextView);
//        String links = intent.getStringExtra("infoActivityMessage2");
//        textViewToChange.setText(!links.isEmpty() ? links : getString(R.string.infoActivityTextViewDefault));
    }
}