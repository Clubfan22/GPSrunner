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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

import me.diskstation.ammon.gpsrunner.R;
import me.diskstation.ammon.gpsrunner.db.LocationDBHelper;
import me.diskstation.ammon.gpsrunner.db.LocationDBReader;
import me.diskstation.ammon.gpsrunner.db.Run;
import me.diskstation.ammon.gpsrunner.misc.CSVRunExporter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener, FragmentCompat.OnRequestPermissionsResultCallback{
    private long selectedDate;
    private Spinner yearSpinner;
    private Spinner monthSpinner;
    private RunsListAdapter mAdapter;
    private ListView mListView;
    private Bundle spinnerSelections;
    private View mSpinnerLayout;
    private boolean selectionMode = false;
    private Long[] exportRunIds;
    private final int WRITE_EXTERNAL_STORAGE_REQUEST = 2;

    public static CalendarFragment newInstance(){
        return new CalendarFragment();
    }

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        mSpinnerLayout = inflater.inflate(R.layout.spinner_header, null);
        mListView = (ListView) view.findViewById(R.id.runsListView);
        mListView.addHeaderView(mSpinnerLayout);
        spinnerSelections = new Bundle();
        //initializing spinners with ArrayAdapters getting values from string arrays
        yearSpinner = (Spinner) mSpinnerLayout.findViewById(R.id.yearSpinner);
        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.years_array, R.layout.spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int year = position + 2015;
                        spinnerSelections.putInt("spinner_selection_year", year);
                        refreshList(getRuns(spinnerSelections));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        yearSpinner.setSelection(currentYear - 2015);
        spinnerSelections.putInt("spinner_selection_year", currentYear);
        monthSpinner = (Spinner) mSpinnerLayout.findViewById(R.id.monthSpinner);
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.months_array, R.layout.spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        spinnerSelections.putInt("spinner_selection_month", position);
                        refreshList(getRuns(spinnerSelections));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
        int currentMonth = c.get(Calendar.MONTH); //starts with 0
        monthSpinner.setSelection(currentMonth); //also starts with 0
        spinnerSelections.putInt("spinner_selection_month", currentMonth);
        mAdapter = new RunsListAdapter(getActivity(), getRuns(spinnerSelections));
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        //mListView.setOnItemLongClickListener(this);
        //mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); moved to onLongClickListener
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(this);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedStateInstance){
        super.onActivityCreated(savedStateInstance);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        outState.putInt("spinner_selection_year", spinnerSelections.getInt("spinner_selection_year"));
        outState.putInt("spinner_selection_month", spinnerSelections.getInt("spinner_selection_month"));
        super.onSaveInstanceState(outState);
    }

    protected void refreshList(ArrayList<Run> runs){
        //resets checked items
        //mListView.setItemChecked(-1, true);
        selectionMode = false;
        mAdapter.clear();
        mAdapter.addAll(runs);
    }
    protected ArrayList<Run> getRuns(final Bundle args){
        int year = args.getInt("spinner_selection_year");
        int month = args.getInt("spinner_selection_month");
        Calendar calendar = Calendar.getInstance();
        if (year != 0){
            calendar.set(Calendar.YEAR, year);
        }
        if (month != 0){
            calendar.set(Calendar.MONTH, month);
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        long startTimestamp = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        long endTimestamp = calendar.getTimeInMillis();
        LocationDBHelper dbHelp = new LocationDBHelper(getActivity());
        LocationDBReader dbRead = new LocationDBReader(dbHelp);
        return dbRead.getRuns(startTimestamp, endTimestamp, "ASC");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!selectionMode) {
            //Call DetailsTabActivity with runIds = id;
            Intent intent = new Intent(getActivity(), DetailsTabActivity.class);
            Run selectedRun = (Run) parent.getItemAtPosition(position);
            //Run testRun = (Run) parent.getSelectedItem();
            long[] runIds = {selectedRun.id};
            intent.putExtra("run_ids", runIds);
            startActivity(intent);
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {}

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.menu_calendar_contextual, menu);
        selectionMode = true;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        //Run selectedRun = (Run) parent.getItemAtPosition(position);
        //Run testRun = (Run) parent.getSelectedItem();
        ArrayList<Long> selectedIds = new ArrayList<>();
        SparseBooleanArray selections = mListView.getCheckedItemPositions();
        for (int i = 0; i <= mAdapter.getCount(); i++){
            if (selections.get(i)){
                Run selectedRun = (Run) mListView.getItemAtPosition(i);
                selectedIds.add(selectedRun.id);
            }
        }
        long[] runIds = new long[selectedIds.size()];
        for (int i = 0; i < selectedIds.size(); i++){
            runIds[i] = selectedIds.get(i);
        }

        switch(item.getItemId()){
            case R.id.item_open:
                //Call DetailsTabActivity with runIds = id;
                Intent intent = new Intent(getActivity(), DetailsTabActivity.class);
                intent.putExtra("run_ids", runIds);
                startActivity(intent);
                mode.finish();
                return true;
            case R.id.item_export_csv:

                exportRunIds = new Long[runIds.length];
                for (int i = 0; i < exportRunIds.length; i++){
                    exportRunIds[i] = runIds[i];
                }
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    exportRuns();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                            WRITE_EXTERNAL_STORAGE_REQUEST);

                }
                    mode.finish();
                return true;
            default:
                return false;
        }
    }

    private void exportRuns(){
        //Create CSVRunExporter and export selected runs
        CSVRunExporter csv = new CSVRunExporter(getActivity());
        csv.export(exportRunIds);
    }



    @Override
    public void onDestroyActionMode(ActionMode mode) {
        selectionMode = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportRuns();
                }
                break;
        }

    }


}
