/*
 * Copyright (C) 2014 Francesco Azzola
 *  Surviving with Android (http://www.survivingwithandroid.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eecs581_582.cortez.frontend;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import eecs581_582.cortez.R;
import eecs581_582.cortez.backend.Downloader;

public class MapSelectCardAdapter extends RecyclerView.Adapter<MapSelectCardAdapter.MapSelectViewHolder> {

    private List<MapSelectCard> mapSelectList;

    public MapSelectCardAdapter(List<MapSelectCard> mapSelectList) {
        this.mapSelectList = mapSelectList;
    }

    @Override
    public int getItemCount() {
        return mapSelectList.size();
    }

    @Override
    public void onBindViewHolder(MapSelectViewHolder mapSelectViewHolder, int i) {
        MapSelectCard ci = mapSelectList.get(i);
        mapSelectViewHolder.vDescriptionMessage.setText(ci.descriptionMessage);
        mapSelectViewHolder.vTitle.setText(ci.name);
        mapSelectViewHolder.path = ci.path;
    }

    @Override
    public MapSelectViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.map_select_card, viewGroup, false);

        return new MapSelectViewHolder(itemView);
    }

    public static class MapSelectViewHolder extends RecyclerView.ViewHolder {

        protected TextView vTitle;              // Title for the Cortez map in this card
        protected TextView vDescriptionMessage; // Description for the Cortez map in this card
        protected ImageView vIcon;              // Icon for the Cortez map in this card
        protected String path;                  // Fully-qualified file path (local or external) for the Cortez map in this card

        public MapSelectViewHolder(View v) {
            super(v);
            vTitle = (TextView) v.findViewById(R.id.map_select_card_title);
            vDescriptionMessage = (TextView)  v.findViewById(R.id.map_select_card_txtDescriptionMessage);
            vIcon = (ImageView) v.findViewById(R.id.map_select_card_mapicon);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent;

                    if (path.contains("http")) {

                        // Download and store the map contained in this card.
                        Downloader d = new Downloader(view.getContext(), path);
                        d.saveMapData((String) vTitle.getText());

                        // Let the user know the map was downloaded
                        Toast.makeText(view.getContext(),
                                ("\"" + vTitle.getText() + "\" downloaded."),
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        intent = new Intent(view.getContext(), MapActivity.class);

                        intent.putExtra("CortezMapData", path);

                        view.getContext().startActivity(intent);
                    }
                }
            });
        }
    }
}