/*
 * Copyright (c) Marco Ammon 2015.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.diskstation.ammon.gpsrunner.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.diskstation.ammon.gpsrunner.R;
import me.diskstation.ammon.gpsrunner.db.Run;
import me.diskstation.ammon.gpsrunner.misc.ValueFormatter;

/**
 * Created by Marco on 30.09.2015.
 */
public class RunsListAdapter extends ArrayAdapter<Run> {
    ValueFormatter vf;

    public RunsListAdapter(Context context, ArrayList<Run> runs) {
        super(context, 0, runs);
        vf = new ValueFormatter(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Run run = getItem(position);
        //if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.runs_item, parent, false);
        //}
        TextView timestampView = (TextView) convertView.findViewById(R.id.runItemDateView);
        TextView distanceView = (TextView) convertView.findViewById(R.id.runItemDistanceView);
        TextView durationView = (TextView) convertView.findViewById(R.id.runItemDurationView);

        timestampView.setText(vf.formatDate(run.timestamp));
        distanceView.setText(vf.formatDistance(run.distance));
        durationView.setText(vf.formatTimeInterval(run.timeInterval));
        return convertView;
    }

}
