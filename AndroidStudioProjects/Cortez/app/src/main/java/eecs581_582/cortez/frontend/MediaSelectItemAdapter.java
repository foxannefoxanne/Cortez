package eecs581_582.cortez.frontend;

import java.util.ArrayList;

import eecs581_582.cortez.R;
import eecs581_582.cortez.backend.Constants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Credit: https://github.com/hmkcode/Android/blob/master/android-pro-listview/src/com/hmkcode/android/MyAdapter.java
 */
public class MediaSelectItemAdapter extends ArrayAdapter<MediaSelectItem> {

    private final Context context;
    private final ArrayList<MediaSelectItem> modelsArrayList;

    public MediaSelectItemAdapter(Context context, ArrayList<MediaSelectItem> modelsArrayList) {

        super(context, R.layout.target_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = inflater.inflate(R.layout.target_item, parent, false);

        // 3. Get icon,title & counter views from the rowView
        ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaSelectItem selected = modelsArrayList.get(position);
                Log.d("MediaSelectItemAdapter", "Clicked " + selected.getTitle());

                Constants.MediaType mediaType = selected.getMediaType();

                Intent intent = new Intent(context, mediaType.getHandlerClass());
                intent.putExtra(mediaType.getName(), selected.getLink());

                context.startActivity(intent);
            }
        });
        TextView titleView = (TextView) rowView.findViewById(R.id.item_title);
        TextView counterView = (TextView) rowView.findViewById(R.id.item_counter);

        // 4. Set the text for textView
        imgView.setImageResource(modelsArrayList.get(position).getIcon());
        titleView.setText(modelsArrayList.get(position).getTitle());
        counterView.setText(modelsArrayList.get(position).getCounter());

        // 5. return rowView
        return rowView;
    }
}