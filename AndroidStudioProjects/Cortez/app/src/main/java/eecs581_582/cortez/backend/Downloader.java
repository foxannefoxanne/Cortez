package eecs581_582.cortez.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by Joseph on 2/29/16.
 */
public class Downloader {

    public static final String TAG = Downloader.class.getSimpleName();

    private JSONObject jsonObject;

    private Context context;

    public Downloader(Context context, String urlString) {
        this.context = context;
        ProcessJSON p = new ProcessJSON();
        p.execute(urlString);
        try {
            this.jsonObject = p.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    /**
     * Credit: http://android--examples.blogspot.com/2015/05/how-to-parse-json-data-in-android.html
     * Created by cfsuman on 31/05/2015.
     */
    class HTTPDataHandler {

        private final String TAG = this.getClass().getSimpleName();

        public HTTPDataHandler(){
        }

        public String getHTTPData(String urlString) {
            String stream = "";

            try{
                Log.d(TAG, "Getting HTTP Data from " + urlString);
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Check the connection status
                if(urlConnection.getResponseCode() == 200) {
                    // if response code = 200 ok
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    // Read the BufferedInputStream
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    // Clean up the stream if necessary
                    while ((line = r.readLine()) != null) {
                        line = line.replace("&quot;", "\"")
                                .replace("&#39;", "\'")
                                .replace("&lt;", "<")
                                .replace("&gt;", ">")
                                .replace("\",", "\",\n");

                        sb.append(line);

                        Log.d(TAG, line);
                    }

                    stream = sb.toString();
                    stream = stream.substring(stream.indexOf("{"), stream.lastIndexOf("}") + 1);
                    // End reading...............

                    // Disconnect the HttpURLConnection
                    urlConnection.disconnect();
                }
                else {
                    // Do something
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            finally {

            }
            // Return the data from specified url
            return stream;
        }
    }

    /**
     * Credit: http://android--examples.blogspot.com/2015/05/how-to-parse-json-data-in-android.html
     */
    class ProcessJSON extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(String... strings){
            Log.d(TAG, "Downloading...");
            String stream = null;
            String urlString = strings[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.getHTTPData(urlString);

            // Return the data from specified url
            try {
                return new JSONObject(stream);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject result){
            Log.d(TAG, "Finished downloading!");
        }
    } // ProcessJSON class end
}
