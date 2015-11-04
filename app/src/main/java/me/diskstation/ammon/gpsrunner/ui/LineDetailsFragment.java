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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import me.diskstation.ammon.gpsrunner.R;
import me.diskstation.ammon.gpsrunner.db.Segment;
import me.diskstation.ammon.gpsrunner.db.Waypoint;
import me.diskstation.ammon.gpsrunner.misc.LineColors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LineDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LineDetailsFragment extends Fragment {

    private static final String ARG_RUNIDS = "run_ids";
    private static final String ARG_MODE = "mode";
    protected static final int MODE_ALTITUDE = 0;
    protected static final int MODE_VELOCITY = 1;
    private long[] runIds;
    private int mode;
    private ArrayList<ArrayList<Waypoint>> runsWaypoints;
    private ArrayList<ArrayList<Segment>> runsSegments;
    private LineChart lineChart;
    private ArrayList<Long> xValues;

    private DetailsTabActivity detailsActivity;

    private String abbrMin;
    private String abbrSec;
    private String separator;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param runIds Array of IDs of runs to be displayed
     * @param mode mode of chart (velocity, altitude, ...)
     * @return A new instance of fragment LineDetailsFragment.
     */

    public static LineDetailsFragment newInstance(long[] runIds, int mode) {
        LineDetailsFragment fragment = new LineDetailsFragment();
        Bundle args = new Bundle();
        args.putLongArray(ARG_RUNIDS, runIds);
        args.putInt(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }
    public static LineDetailsFragment newInstance(long[] runId){
        return newInstance(runId, MODE_ALTITUDE);
    }
    public LineDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            runIds = getArguments().getLongArray(ARG_RUNIDS);
            mode = getArguments().getInt(ARG_MODE, MODE_ALTITUDE);
        }
        detailsActivity = (DetailsTabActivity) this.getActivity();
        abbrMin = detailsActivity.getString(R.string.abbr_min);
        abbrSec = detailsActivity.getString(R.string.abbr_sec);
        separator = detailsActivity.getString(R.string.separator);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_line_details, container, false);
        lineChart = (LineChart) view.findViewById(R.id.runLineChart);
        setUpChart(lineChart);
        Spinner modeSpinner = (Spinner) view.findViewById(R.id.modeSpinner);
        //Adding modes via adapter
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.detail_modes_array, R.layout.spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);
        modeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mode = position;
                        setUpChart(lineChart);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        return view;
    }

    private void setUpChart(LineChart lc){
        switch (mode){
            case MODE_ALTITUDE:
                lc.setDescription(getString(R.string.altitude));
                break;
            case MODE_VELOCITY:
                lc.setDescription(getString(R.string.velocity));
            //Todo: add more cases
        }
        lc.setMaxVisibleValueCount(30);
        lc.setHighlightEnabled(false);
        lc.setDrawGridBackground(false);
        XAxis xAxis = lc.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        //xAxis.setValueFormatter(new TimestampXAxisValueFormatter());
        xAxis.setAvoidFirstLastClipping(true);

        //remove right yAxis (ain't nobody got time for that)
        lc.getAxisRight().setEnabled(false);
        YAxis yAxis = lc.getAxisLeft();
        yAxis.setStartAtZero(false);
        yAxis.setSpaceTop(0.2f);
        yAxis.setSpaceBottom(0.2f);
        //Waypoints is needed in every case for generating xAxis labels
        runsWaypoints = new ArrayList<>();
        for (int i = 0; i < runIds.length; i++){
            runsWaypoints.add(detailsActivity.getWaypoints(runIds[i]));
        }
        switch (mode){
            case MODE_VELOCITY:
                runsSegments = new ArrayList<>();
                for (int i = 0; i < runIds.length; i++){
                    runsSegments.add(detailsActivity.getSegments(runIds[i]));
                }
                break;
            //TODO: add more cases
        }
        LineData cd = generateLineData(mode);
        lc.setData(cd);
        lc.invalidate();

    }

    protected LineData generateLineData(int mode){
        ArrayList<LineDataSet> sets = new ArrayList<>();
        xValues = generateXAxisValues();
        for (int i = 0; i < runIds.length; i++){
            sets.add(generateLineDataSet(i, mode));
        }
        ArrayList<String> xLabels = formatValues(xValues);
        return new LineData(xLabels, sets);
    }

    protected LineDataSet generateLineDataSet(int index, int mode){
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Waypoint> waypoints = runsWaypoints.get(index);
        long initial;
        if (runsWaypoints.size() > 1){
            initial = waypoints.get(0).timestamp;
        } else {
            initial = 0;
        }
        ArrayList<Integer> indizes = new ArrayList<>();
        switch (mode){
            case MODE_ALTITUDE:
                for (int i = 0; i < waypoints.size(); i++) {
                    Entry entry = getEntryFromWaypoint(waypoints.get(i), mode, initial);
                    if (entry != null){
                        int xIndex = entry.getXIndex();
                        if (!indizes.contains(xIndex)){
                            indizes.add(xIndex);
                            entries.add(entry);
                        }
                    }
                }

                break;
            case MODE_VELOCITY:
                ArrayList<Segment> segments = runsSegments.get(index);
                for (int i = 0; i < segments.size(); i++) {
                    long timestamp = waypoints.get(i).timestamp;
                    Entry entry = getEntryFromSegment(segments.get(i), mode, timestamp, initial);
                    if (entry != null){
                        int xIndex = entry.getXIndex();
                        if (!indizes.contains(xIndex)){
                            indizes.add(xIndex);
                            entries.add(entry);
                        }
                    }
                }
        }
        //Run 0 should show up as Run 1
        LineDataSet lds = new LineDataSet(entries, getString(R.string.run) + " " + (index + 1));
        lds.setAxisDependency(YAxis.AxisDependency.LEFT);
        lds.setDrawCircles(false);
        lds.setDrawValues(false);
        lds.setColor(LineColors.getColor(index));
        lds.setLineWidth(2f);
        return lds;
    }

    protected Entry getEntryFromWaypoint(Waypoint wp, int mode, long initial){
        float value = 0.0f;
        switch (mode){
            case MODE_ALTITUDE:
                value = (float) wp.height;
                break;
            //TODO: add more modes
        }
        long timestamp = wp.timestamp - initial;
        double temp = timestamp / 1000;
        long xValue = Math.round(temp)*1000;
        int index = xValues.indexOf(xValue);
        System.out.println("value: " + value + " xValue: " + xValue + " index: " + index);
        //Experimental
        if (index == -1){
//            xValue = xValue + 1000;
//            index = xValues.indexOf(xValue);
//            System.out.println("value: " + value + " xValue: " + xValue + " index: " + index);
            return null;
        }
        return new Entry(value, index);
    }

    protected Entry getEntryFromSegment(Segment sg, int mode, long timestamp, long initial){
        float value = 0.0f;
        switch (mode){
            case MODE_VELOCITY:
                value = (float) sg.velocity;
                break;
            //Todo: add more modes
        }
        timestamp = timestamp - initial;
        double temp = timestamp / 1000;
        long xValue = Math.round(temp) * 1000;
        int index = xValues.indexOf(xValue);
        if (index == -1){
            //xValue = xValue + 1000;
            //index = xValues.indexOf(xValue);
            return null;
        }
        return new Entry(value, index);
    }

    protected ArrayList<Long> generateXAxisValues(){
        //if only one run is shown at a time
        if(runsWaypoints.size() > 1){
            ArrayList<Long> labels = new ArrayList<>();
            int maxRunId = -1;
            int maxRunWaypoints = 0;
            for (int i = 0; i < runsWaypoints.size(); i++){
                int size = runsWaypoints.get(i).size();
                if (size > maxRunWaypoints){
                    maxRunWaypoints = size;
                    maxRunId = i;
                }
            }
            ArrayList<Waypoint> waypoints = runsWaypoints.get(maxRunId);
            long initial = waypoints.get(0).timestamp;
            for (int i = 0; i < waypoints.size(); i++){
                long value = Math.round((waypoints.get(i).timestamp - initial) / 1000)*1000;
                //while (labels.contains(value)){
                //    value = value + 1000;
                //}
                if (!labels.contains(value)){
                    labels.add(value);
                }
                //labels.add(value);
            }
            return labels;
        } else {
            ArrayList<Waypoint> waypoints = new ArrayList<>(runsWaypoints.get(0));
            ArrayList<Long> labels = new ArrayList<>();
            for (int i = 0; i < waypoints.size(); i++){
                Waypoint wp = waypoints.get(i);
                double timestamp = wp.timestamp / 1000;
                long test = Math.round(timestamp);
                Long value = test * 1000;
                if (!labels.contains(value)) {
                    labels.add(value);
                }
            }
            //There's one segment less than waypoint, otherwise a segments initial timestamp is exactly the same as a run's
            if (mode == MODE_VELOCITY){
                labels.remove(labels.size()-1);
            }
            return labels;
        }

    }

    private ArrayList<String> formatValues(ArrayList<Long> values){
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            if (runsWaypoints.size() > 1) {
                labels.add(formatInterval(values.get(i)));
            } else {
                labels.add(formatTime(values.get(i)));
            }
        }
        return labels;
    }

    private String formatTime(long original){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(original);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(calendar.getTime());
    }

    private String formatInterval(long interval){
        String formattedTimeInterval = String.format("%d " + abbrMin +", %d " + abbrSec,
                TimeUnit.MILLISECONDS.toMinutes(interval),
                TimeUnit.MILLISECONDS.toSeconds(interval) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(interval))
        );
        return formattedTimeInterval;
    }

}
