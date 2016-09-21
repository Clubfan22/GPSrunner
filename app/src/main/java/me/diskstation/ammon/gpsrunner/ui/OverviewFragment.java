/*
 * Copyright (c) Marco Ammon 2016.
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


import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import me.diskstation.ammon.gpsrunner.R;
import me.diskstation.ammon.gpsrunner.misc.ValueFormatter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment implements OnMapReadyCallback {

    private static GoogleMap gMap;
    private DrawerActivity drawAct;
    private TextView distanceView;
    private TextView durationView;
    private TextView velocityView;
    private Polyline path;
    private ArrayList<LatLng> waypoints;
    private PolylineOptions plOpt;
    private boolean buttonBlocked = false;
    private Button button;
    private boolean isMapsEnabled = true;
    private ValueFormatter vf;
    private Bundle run;
    private String separator;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     */

    public static OverviewFragment newInstance() {
        OverviewFragment fragment = new OverviewFragment();
        return fragment;
    }

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawAct = (DrawerActivity) this.getActivity();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(drawAct.getBaseContext());
        isMapsEnabled = sharedPref.getBoolean("pref_maps", true);
        vf = new ValueFormatter(drawAct.getApplicationContext());
        waypoints = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        button = (Button) view.findViewById(R.id.controlButton);
        button.setText(getString(R.string.start));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buttonBlocked) {
                    if (!OverviewFragment.this.drawAct.isLocationServiceBound()) {
                        buttonBlocked = true;
                        OverviewFragment.this.drawAct.startLocationService(OverviewFragment.this);
                        button.setText(getString(R.string.searching_signal));
                    } else {
                        OverviewFragment.this.drawAct.stopLocationService();
                        button.setText(getString(R.string.start));
                    }
                }
            }
        });
        distanceView = (TextView) view.findViewById(R.id.distanceView);
        durationView = (TextView) view.findViewById(R.id.durationView);
        velocityView = (TextView) view.findViewById(R.id.currentVelocityView);
        velocityView.setText(vf.formatVelocity(0.0d));

        if (isMapsEnabled) {
            //get SupportMapFragment (wrapping most of the Map logic)
            SupportMapFragment smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
            //calls onMapReady();
            smf.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if (isMapsEnabled) {
            //check if gMap had been instantiated successfully
            if (gMap != null) {
                //remove gMap from FragmentManager, otherwise next time the fragment is loaded, the application crashes
                //getChildFragmentManager().beginTransaction().remove(getChildFragmentManager().findFragmentById(R.id.mapFragment)).commit();
                gMap = null;
            }
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<Parcelable> parcelable = savedInstanceState.getParcelableArrayList("waypoints");
            if (waypoints.size() == 0 && parcelable != null) {

                if (parcelable.size() > 0) {
                    for (int i = 0; i < parcelable.size(); i++) {
                        waypoints.add((LatLng) parcelable.get(i));
                    }
                    updatePolyline(waypoints, waypoints.get(waypoints.size() - 1));
                }
            }
            if (savedInstanceState.containsKey("run")) {
                Bundle run = savedInstanceState.getBundle("run");
                if (run != null) {
                    update(savedInstanceState.getBundle("run"));
                }
            }

        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if (drawAct.mBound){
            if (run != null){
                outState.putBundle("run", run);
            }
            if (waypoints != null) {
                outState.putParcelableArrayList("waypoints", waypoints);
            }
        }
    }
    //Callback from smf.getMapAsync(this) in onCreateView()
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Enable button to show "my location" (using GMaps API v2)
            gMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        //Create PolylineOptions with a 25px thick, blue line
        plOpt = new PolylineOptions()
                .width(5)
                .color(Color.BLUE);
        //addPolyline through adding the PolylineOptions, waypoints are added in updateMap()
        path = gMap.addPolyline(plOpt);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, proceed to the normal flow.
                    gMap.setMyLocationEnabled(true);
                }
                break;
        }
    }

    protected void unblockButton(){
        buttonBlocked = false;
    }

    public void update(Bundle updatedRun, LatLng latestPosition){
        update(updatedRun);
        if (isMapsEnabled) {
            updateMap(latestPosition);
        }
    }

    protected void update(Bundle updatedRun){
        button.setText(R.string.stop);
        buttonBlocked = false;
        updateViews(updatedRun);
        run = updatedRun;
    }

    public void updateViews(Bundle updatedRun){
        long timeInterval = updatedRun.getLong("timeInterval", 0);
        double distance = updatedRun.getDouble("distance", 0.0d);
        double velocity = updatedRun.getDouble("velocity", 0.0d);
        durationView.setText(vf.formatTimeInterval(timeInterval));
        distanceView.setText(vf.formatDistance(distance));
        velocityView.setText(vf.formatVelocity(velocity));
    }

    protected void updateMap(LatLng latestPosition){
        if (waypoints == null){
            waypoints = new ArrayList<>();
        }
        waypoints.add(latestPosition);
        updatePolyline(waypoints, latestPosition);
    }

    private void updatePolyline (final ArrayList<LatLng> points, final LatLng latestPosition){
        if (gMap != null) {
            if (path == null) {
                path = gMap.addPolyline(plOpt);
            }
            path.setPoints(points);
            float currentZoomLevel = gMap.getCameraPosition().zoom;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latestPosition, currentZoomLevel);
            gMap.animateCamera(cameraUpdate);
        } else {
            SupportMapFragment smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
            smf.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    OverviewFragment.this.onMapReady(googleMap);
                    updatePolyline(points, latestPosition);
                }
            });
        }
    }

    public void reset(){
        resetViews();
        run = null;
        if (isMapsEnabled) {
            resetMap();
        }
    }

    protected void resetViews(){
        button.setText(R.string.start);
        durationView.setText(getString(R.string.duration_dummy));
        distanceView.setText(getString(R.string.distance_dummy));
        velocityView.setText(vf.formatVelocity(0.0d));
    }

    protected void resetMap(){
        if (waypoints != null){
            waypoints.clear();
        }
        if (path != null) {
            path.remove();
            path = null;
        }
    }

}
