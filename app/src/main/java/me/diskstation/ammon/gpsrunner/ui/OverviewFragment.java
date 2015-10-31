/*
 * Copyright (c) Marco Ammon 2015.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
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


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.util.concurrent.TimeUnit;

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

    //Callback from smf.getMapAsync(this) in onCreateView()
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        //Enable button to show "my location" (using GMaps API v2)
        gMap.setMyLocationEnabled(true);
        //Create PolylineOptions with a 25px thick, blue line
        plOpt = new PolylineOptions()
                .width(5)
                .color(Color.BLUE);
        //addPolyline through adding the PolylineOptions, waypoints are added in updateMap()
        path = gMap.addPolyline(plOpt);
    }

    public void update(Bundle updatedRun){
        button.setText(R.string.stop);
        buttonBlocked = false;
        updateViews(updatedRun);
        if (isMapsEnabled) {
            updateMap(updatedRun);
        }
    }
    public void updateViews(Bundle updatedRun){
        long timeInterval = updatedRun.getLong("timeInterval", 0);
        double distance = updatedRun.getDouble("distance", 0.0d);
        double velocity = updatedRun.getDouble("velocity", 0.0d);
        durationView.setText(vf.formatTimeInterval(timeInterval));
        distanceView.setText(vf.formatDistance(distance));
        velocityView.setText(vf.formatVelocity(velocity));
    }

    protected void updateMap(Bundle updatedRun){
        double latitude = updatedRun.getDouble("latitude");
        double longtitude = updatedRun.getDouble("longtitude");
        LatLng currentLocation = new LatLng(latitude, longtitude);
        if (waypoints == null){
            waypoints = new ArrayList<>();
        }
        waypoints.add(currentLocation);
        if (path == null){
            path = gMap.addPolyline(plOpt);
        }
        path.setPoints(waypoints);
        float currentZoomLevel = gMap.getCameraPosition().zoom;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, currentZoomLevel);
        gMap.animateCamera(cameraUpdate);
    }

    public void reset(){
        resetViews();
        if (isMapsEnabled) {
            resetMap();
        }
    }
    protected void resetViews(){
        button.setText(R.string.start);
        durationView.setText(getString(R.string.duration_dummy));
        distanceView.setText(getString(R.string.distance_dummy));
        velocityView.setText(getString(R.string.currentVelocity_dummy));

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
