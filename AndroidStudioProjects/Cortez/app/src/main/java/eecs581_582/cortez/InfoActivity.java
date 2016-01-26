package eecs581_582.cortez;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class InfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setUpViewFromIntent(getIntent());
    }

    public void onBackPressed() {
        finish();
    }

    private void setUpViewFromIntent(Intent intent) {
        setTitle(intent.getStringExtra("title"));

        // Set the upper TextView
        TextView textViewToChange = (TextView) findViewById(R.id.infoActivityTextView1);
        String message = intent.getStringExtra("infoActivityMessage1");
//        textViewToChange.setVisibility(message.isEmpty() ? View.INVISIBLE : View.VISIBLE);
        textViewToChange.setText(!message.isEmpty() ? message : getString(R.string.infoActivityTextView1Default));
        textViewToChange.setMovementMethod(new ScrollingMovementMethod()); // Allow scrolling for the TextView

        // Set the lower TextView
        textViewToChange = (TextView) findViewById(R.id.infoActivityTextView2);
        String links = intent.getStringExtra("infoActivityMessage2");
        textViewToChange.setText(!links.isEmpty() ? links : getString(R.string.infoActivityTextView2Default));
    }
}