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

import java.util.List;

import eecs581_582.cortez.R;

public class MapSelectCardAdapter extends RecyclerView.Adapter<MapSelectCardAdapter.MapFileChoiceHolder> {

    private List<MapSelectCard> mapSelectList;

    public MapSelectCardAdapter(List<MapSelectCard> mapSelectList) {
        this.mapSelectList = mapSelectList;
    }

    @Override
    public int getItemCount() {
        return mapSelectList.size();
    }

    @Override
    public void onBindViewHolder(MapFileChoiceHolder mapFileChoiceHolder, int i) {
        MapSelectCard ci = mapSelectList.get(i);
        mapFileChoiceHolder.vDescription.setText(ci.description);
        mapFileChoiceHolder.vTitle.setText(ci.name);
    }

    @Override
    public MapFileChoiceHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.map_select_card, viewGroup, false);

        return new MapFileChoiceHolder(itemView);
    }

    public static class MapFileChoiceHolder extends RecyclerView.ViewHolder {

        protected TextView vTitle;              // Title for the Cortez map in this card
        protected TextView vDescription;        // Description for the Cortez map in this card
        protected ImageView vIcon;              // Icon for the Cortez map in this card

        public MapFileChoiceHolder(View v) {
            super(v);
            vTitle = (TextView) v.findViewById(R.id.map_select_card_title);
            vDescription = (TextView)  v.findViewById(R.id.map_select_card_txtDescription);
            vIcon = (ImageView) v.findViewById(R.id.map_select_card_mapicon);

            // TODO: Clicking the icon should take the user to the desired map
            vIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.getContext().startActivity(new Intent(view.getContext(), MapActivity.class));
                }
            });
        }
    }
}