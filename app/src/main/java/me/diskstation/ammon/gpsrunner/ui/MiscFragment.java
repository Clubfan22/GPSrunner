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


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import me.diskstation.ammon.gpsrunner.R;
import me.diskstation.ammon.gpsrunner.misc.CSVRunExporter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MiscFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MiscFragment extends Fragment implements FragmentCompat.OnRequestPermissionsResultCallback{

    private Context context;
    private final int WRITE_EXTERNAL_STORAGE_REQUEST = 2;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MiscFragment.
     */
    public static MiscFragment newInstance() {
        MiscFragment fragment = new MiscFragment();
        return fragment;
    }

    public MiscFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        // Display the fragment as the main content.
        getChildFragmentManager().beginTransaction()
                .replace(R.id.preferencesContainer, SettingsFragment.newInstance())
                .commit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_misc, container, false);
        Button exportCSVButton  = (Button) view.findViewById(R.id.exportCSVButton);
        exportCSVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    CSVRunExporter csv = new CSVRunExporter(context);
                    csv.export();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                            WRITE_EXTERNAL_STORAGE_REQUEST);
                }
            }
        });
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CSVRunExporter csv = new CSVRunExporter(context);
                    csv.export();
                }
                break;
        }

    }


}
