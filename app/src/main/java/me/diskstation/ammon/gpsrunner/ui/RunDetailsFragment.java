package me.diskstation.ammon.gpsrunner.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

import me.diskstation.ammon.gpsrunner.R;
import me.diskstation.ammon.gpsrunner.db.Run;
import me.diskstation.ammon.gpsrunner.misc.LineColors;
import me.diskstation.ammon.gpsrunner.misc.ValueFormatter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunDetailsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RUNIDS = "run_ids";

    private long[] runIds;
    private ArrayList<Run> runs;
    private DetailsTabActivity detailsActivity;
    private Context context;
    private ValueFormatter vf;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param runIds long array of runIds
     * @return A new instance of fragment RunDetailsFragment.
     */

    public static RunDetailsFragment newInstance(long[] runIds) {
        RunDetailsFragment fragment = new RunDetailsFragment();
        Bundle args = new Bundle();
        args.putLongArray(ARG_RUNIDS, runIds);
        fragment.setArguments(args);
        return fragment;
    }

    public RunDetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            runIds = getArguments().getLongArray(ARG_RUNIDS);
        }
        vf = new ValueFormatter(getActivity().getApplicationContext());
        detailsActivity = (DetailsTabActivity) getActivity();
        runs = getRuns();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_run_details, container, false);
        context = getActivity();

        //Get all Layouts
        LinearLayout distanceLayout = (LinearLayout) view.findViewById(R.id.distanceLayout);
        LinearLayout timeIntervalLayout = (LinearLayout) view.findViewById(R.id.intervalLayout);
        LinearLayout maxVelocityLayout = (LinearLayout) view.findViewById(R.id.maxVelocityLayout);
        LinearLayout avgVelocityLayout = (LinearLayout) view.findViewById(R.id.avgVelocityLayout);
        LinearLayout ascentLayout = (LinearLayout) view.findViewById(R.id.ascIntervalLayout);
        LinearLayout descentLayout = (LinearLayout) view.findViewById(R.id.descIntervalLayout);
        LinearLayout breakTimeLayout = (LinearLayout) view.findViewById(R.id.totalBreakLayout);

        //Generate all the value layouts
        LinearLayout distanceValueLayout = new LinearLayout(context);
        distanceValueLayout.setOrientation(LinearLayout.VERTICAL);
        distanceLayout.addView(distanceValueLayout);

        LinearLayout timeIntervalValueLayout = new LinearLayout(context);
        timeIntervalValueLayout.setOrientation(LinearLayout.VERTICAL);
        timeIntervalLayout.addView(timeIntervalValueLayout);

        LinearLayout maxVelocityValueLayout = new LinearLayout(context);
        maxVelocityValueLayout.setOrientation(LinearLayout.VERTICAL);
        maxVelocityLayout.addView(maxVelocityValueLayout);

        LinearLayout avgVelocityValueLayout = new LinearLayout(context);
        avgVelocityValueLayout.setOrientation(LinearLayout.VERTICAL);
        avgVelocityLayout.addView(avgVelocityValueLayout);

        LinearLayout ascentValueLayout = new LinearLayout(context);
        ascentValueLayout.setOrientation(LinearLayout.VERTICAL);
        ascentLayout.addView(ascentValueLayout);

        LinearLayout descentValueLayout = new LinearLayout(context);
        descentValueLayout.setOrientation(LinearLayout.VERTICAL);
        descentLayout.addView(descentValueLayout);

        LinearLayout breakTimeValueLayout = new LinearLayout(context);
        breakTimeValueLayout.setOrientation(LinearLayout.VERTICAL);
        breakTimeLayout.addView(breakTimeValueLayout);

        //populate Layouts with TextViews and values
        for (int i = 0; i < runs.size(); i++){
            Run run = runs.get(i);

            //set up all TextViews
            TextView distanceView = new TextView(context);
            distanceView.setText(vf.formatDistance(run.distance));
            setUpTextView(distanceView, i);

            TextView timeIntervalView = new TextView(context);
            timeIntervalView.setText(vf.formatTimeInterval(run.timeInterval));
            setUpTextView(timeIntervalView, i);

            TextView maxVelocityView = new TextView(context);
            maxVelocityView.setText(vf.formatVelocity(run.maxVelocity));
            setUpTextView(maxVelocityView, i);

            TextView avgVelocityView = new TextView(context);
            avgVelocityView.setText(vf.formatVelocity(run.medVelocity));
            setUpTextView(avgVelocityView, i);

            TextView ascentView = new TextView(context);
            ascentView.setText(vf.formatDistance(run.ascendInterval));
            setUpTextView(ascentView, i);

            TextView descentView = new TextView(context);
            descentView.setText(vf.formatDistance(run.descendInterval));
            setUpTextView(descentView, i);

            TextView breakView = new TextView(context);
            breakView.setText(vf.formatTimeInterval(run.breakTime));
            setUpTextView(breakView, i);

            //add TextViews to value layouts
            distanceValueLayout.addView(distanceView);
            timeIntervalValueLayout.addView(timeIntervalView);
            maxVelocityValueLayout.addView(maxVelocityView);
            avgVelocityValueLayout.addView(avgVelocityView);
            ascentValueLayout.addView(ascentView);
            descentValueLayout.addView(descentView);
            breakTimeValueLayout.addView(breakView);
        }
        return view;
    }

    private void setUpTextView(TextView tv, int index){
        tv.setTextAppearance(context, R.style.TextAppearance_AppCompat_Medium);
        //tv.setBackgroundColor(LineColors.getLighterColor(index));
        tv.setTextColor(LineColors.getColor(index));
        tv.setGravity(Gravity.RIGHT);
    }

    private ArrayList<Run> getRuns(){
        return detailsActivity.getRuns(runIds);
    }
}
