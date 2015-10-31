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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import me.diskstation.ammon.gpsrunner.R;
import me.diskstation.ammon.gpsrunner.db.Waypoint;
import me.diskstation.ammon.gpsrunner.misc.LineColors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsMapFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_RUNIDS = "run_ids";

    private long[] runIds;
    private static GoogleMap gMap;
    private DetailsTabActivity detailsActivity;
    private boolean isMapsEnabled = true;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param runIds Array of IDs of runs which should be displayed.
     * @return A new instance of fragment DetailsMapFragment.
     */
    public static DetailsMapFragment newInstance(long[] runIds) {
        DetailsMapFragment fragment = new DetailsMapFragment();
        Bundle args = new Bundle();
        args.putLongArray(ARG_RUNIDS, runIds);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailsMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsActivity = (DetailsTabActivity) this.getActivity();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(detailsActivity.getBaseContext());
        isMapsEnabled = sharedPref.getBoolean("pref_maps", true);
        if (getArguments() != null) {
            runIds = getArguments().getLongArray(ARG_RUNIDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details_map, container, false);
        if (isMapsEnabled) {
            //get SupportMapFragment (wrapping most of the Map logic)
            SupportMapFragment smf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapDetailsFragment);
            //calls onMapReady();
            smf.getMapAsync(this);
        }
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isMapsEnabled) {
            SupportMapFragment map = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapDetailsFragment);
            if (map != null) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(map).commit();
            }
            gMap = null;
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        LatLng first = null;
        for (int i = 0; i < runIds.length; i++){
            ArrayList<LatLng> waypoints = getWaypoints(runIds[i]);
            if(i == 0 && waypoints.size() > 0){
                first = waypoints.get(0);
            }
            PolylineOptions options = new PolylineOptions()
                    .width(5)
                    .color(LineColors.getColor(i))
                    .addAll(waypoints);
            gMap.addPolyline(options);
        }
        if (first != null){
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(first, 14.0f);
            gMap.animateCamera(cameraUpdate);
        }




    }

    protected ArrayList<LatLng> getWaypoints (long id){
        ArrayList<Waypoint> waypoints = detailsActivity.getWaypoints(id);
        return toLatLngs(waypoints);
    }
    protected ArrayList<LatLng> toLatLngs(ArrayList<Waypoint> waypoints){
        ArrayList<LatLng> latLngs = new ArrayList<>(waypoints.size());
        for (int i = 0; i < waypoints.size(); i++){
            latLngs.add(waypoints.get(i).toLatLng());
        }
        if (waypoints.size() != latLngs.size()){
            System.out.println("Fatal error! Lost waypoints during conversion to LatLng");
        }
        return latLngs;
    }

}
