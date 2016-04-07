package eecs581_582.cortez.frontend;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;


import com.koushikdutta.ion.Ion;

import eecs581_582.cortez.R;

/**
 * Created by Joseph on 4/6/16.
 */
public class ImageViewer extends Activity {
    public static final String TAG = ImageViewer.class.getSimpleName();

    private ImageView imageView;
    private String imageFile;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_imageviewer);

        imageFile = "https://prod-cortez-asset-storage.s3.amazonaws.com/app/public/images/20/8576533889_fa7c57fc34_o.jpg?1459184869";

        imageView = (ImageView) findViewById(R.id.image_view);
        Ion.with(imageView)
                .placeholder(R.mipmap.logo)
                .error(R.mipmap.ic_launcher)
                .load(imageFile);

    }
}
