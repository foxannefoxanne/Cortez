package eecs581_582.cortez.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import eecs581_582.cortez.R;

public class HelpActivity extends Activity {

    public static final String TAG = HelpActivity.class.getSimpleName();
    public int helpFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        // Where did you come from?
        helpFrom = getIntent().getIntExtra("helpfrom", 666);
        Log.d(TAG, ""+helpFrom);
        setUpViewFromIntent(getIntent());
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

        // Set the WebView
        WebView webView = (WebView) findViewById(R.id.helpActivityWebView);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set content depending on where the user has come from:
        String whatitsays = "EVERYTHING IS BROKEN";
        // TODO: Have these pull from strings, instead of hard-coding here
        switch (helpFrom) {
            case 555:
            {
                // This came from the MapsActivity
                whatitsays = "MAPS! BRING ME MAPS!";
                break;
            }
            case 777 :
            {
                // This came from the InfoActivity
                whatitsays = "You came from the InfoActivity";
                break;
            }
            case 666 :
            {
                // We couldn't determine where this came from
                whatitsays = "WTF, m8?";
                break;
            }
            default:{
                // OH GOD, WHAT DID YOU DO?
                whatitsays = "MOST THINGS ARE BROKEN";
                break;
            }
        }

        webView.loadData(
                //getString(R.string.infoActivityWebViewDefault),
                whatitsays,
                "text/html",
                "utf-8");


        // Set the TextView
//        TextView textViewToChange = (TextView) findViewById(R.id.infoActivityTextView);
//        String links = intent.getStringExtra("infoActivityMessage2");
//        textViewToChange.setText(!links.isEmpty() ? links : getString(R.string.infoActivityTextViewDefault));
    }

}
