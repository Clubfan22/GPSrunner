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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.diskstation.ammon.gpsrunner.R;
import me.diskstation.ammon.gpsrunner.service.LocationService;

public class DrawerActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, LocationService.OnDataChangedListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static FragmentManager fragmentManager;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    //Constants for different elements of Drawer
    @IntDef({ITEM_OVERVIEW, ITEM_CALENDAR, ITEM_MISCELLANEOUS})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Items {}

    private static final int ITEM_OVERVIEW = 0;
    private static final int ITEM_CALENDAR = 1;
    private static final int ITEM_MISCELLANEOUS = 2;
    private final int ACCESS_LOCATION_FINE_REQUEST = 1;
    LocationService locServ;
    boolean mBound = false;
    public static final String ACTION_STOP = "me.diskstation.ammon.gpsrunner.stop_location_service";
    private BroadcastReceiver receiver;
    private Bundle currentRun;
    private OverviewFragment ovFragment;
    private CalendarFragment calFragment;
    private MiscFragment miscFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        //Load FragmentManager
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null){
            ovFragment = (OverviewFragment) fragmentManager.getFragment(savedInstanceState,"ovFragment");
        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fragmentManager.findFragmentById(R.id.navigation_drawer);
        mTitle = getString(R.string.title_section1);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        setUpBroadcastReceiver();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mBound){
            issueNotification();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (mBound){
            locServ.stopForeground(true);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        removeReceiver();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBundle("run", currentRun);
        //getSupportFragmentManager().putFragment(outState, "ovFragment", ovFragment);
    }


    public boolean startLocationService(OverviewFragment overviewFragment){
        if (!mBound) {
            //TODO: add permission management for Android M
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, me.diskstation.ammon.gpsrunner.service.LocationService.class);
                boolean successful = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
                System.out.println(successful);
                return successful;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        ACCESS_LOCATION_FINE_REQUEST);
                return false;
            }
        } else {
            return false;
        }
    }
    public void stopLocationService(){
        try {
            unbindService(mConnection);
        } catch (RuntimeException ex){
            System.out.println(ex);
        }
        ovFragment.reset();
        locServ = null;
        mBound = false;
        currentRun = null;
    }

    @Override
    public void onDataChanged(Bundle run) {
        currentRun = run;
        LatLng position = run.getParcelable("position");
        ovFragment.update(run, position);
    }

    public boolean isLocationServiceBound(){
        return mBound;
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.LocationBinder binder = (LocationService.LocationBinder) service;
            DrawerActivity.this.locServ = binder.getService();
            boolean successful = DrawerActivity.this.locServ.initialize();
            if (successful){
                DrawerActivity.this.locServ.addOnDataChangedListener(DrawerActivity.this);
                DrawerActivity.this.ovFragment.unblockButton();
                DrawerActivity.this.mBound = true;
                System.out.println("onServiceConnected was called");
            } else {
                Toast toast = Toast.makeText(DrawerActivity.this, getString(R.string.activate_gps), Toast.LENGTH_LONG);
                toast.show();
                stopLocationService();
            }


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            DrawerActivity.this.mBound = false;
            System.out.println("Service successfully disconnected!");
        }
    };

    private void setUpBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_STOP);
        receiver = new StopBroadcastReceiver();
        registerReceiver(receiver, intentFilter);
    }

    private void removeReceiver() {
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ex){
            System.out.println(ex);
        }
    }

    private void issueNotification(){
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
        //TODO: add proper icons
        nBuilder.setSmallIcon(R.drawable.ic_stat_name2);
        nBuilder.setContentTitle(getString(R.string.app_name));
        nBuilder.setContentText(getString(R.string.running));
        Intent overviewIntent = this.getIntent();
        overviewIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingOverviewIntent = PendingIntent.getActivity(getApplicationContext(), 0, overviewIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingOverviewIntent);

        int icon = R.drawable.ic_stop_icon;
        String actionTitle = getString(R.string.stop_recording);
        Intent stopIntent = new Intent();
        stopIntent.setAction(ACTION_STOP);
        PendingIntent pendingStopIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, stopIntent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action(icon, actionTitle, pendingStopIntent);
        nBuilder.addAction(action);
        nBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        //Disable dismissability
        nBuilder.setOngoing(true);
        locServ.startForeground(1, nBuilder.build());
    }

    @Override
    public void onNavigationDrawerItemSelected(@Items int position) {
        // create fragment depending on selection
        Fragment fragment;
        switch (position){
            case ITEM_OVERVIEW:
                if (ovFragment == null){
                    ovFragment = OverviewFragment.newInstance();
                }
                fragment =  ovFragment;
                mTitle = getString(R.string.title_section1);
                break;
            case ITEM_CALENDAR:
                if (calFragment == null){
                    calFragment = CalendarFragment.newInstance();
                }
                fragment = calFragment;
                mTitle = getString(R.string.title_section2);
                break;
            case ITEM_MISCELLANEOUS:
                if (miscFragment == null){
                    miscFragment = MiscFragment.newInstance();
                }
                fragment =  miscFragment;
                mTitle = getString(R.string.title_section3);
                break;
            default:
                fragment = null;
                break;
        }
        // update the main content by replacing fragments

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,fragment)
                .commit();
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.drawer, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    protected class StopBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ACTION_STOP:
                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                            .cancelAll();
                    DrawerActivity.this.stopLocationService();
                    break;
            }
        }

    }

}
