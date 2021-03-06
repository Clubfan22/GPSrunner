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

package me.diskstation.ammon.gpsrunner.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import me.diskstation.ammon.gpsrunner.db.LocationDBHelper;
import me.diskstation.ammon.gpsrunner.db.LocationDBReader;
import me.diskstation.ammon.gpsrunner.db.LocationDBWriter;
import me.diskstation.ammon.gpsrunner.db.Run;
import me.diskstation.ammon.gpsrunner.db.Segment;
import me.diskstation.ammon.gpsrunner.db.Waypoint;

public class LocationService extends Service implements LocationListener {
    private LocationHelper locHelp;
    private LocationDBHelper dbHelp;
    private LocationDBWriter dbWrite;
    private LocationDBReader dbRead;
    private long runId;
    private Location latestLocation;
    private Location secondLatestLocation;
    private Long latestWaypointId;
    private Long secondLatestWaypointId;
    private Long firstSegmentId;
    private Run currentRun;
    private boolean isFirstSegment = true;
    private final IBinder mBinder = new LocationBinder();
    private ArrayList<OnDataChangedListener> listeners = new ArrayList<OnDataChangedListener>();
    private Bundle runBundle;



    public LocationService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        locHelp.removeLocationUpdates();
        return true;
    }

    public boolean initialize(){
        locHelp = new LocationHelper(this);
        dbHelp = new LocationDBHelper(this);
        dbWrite = new LocationDBWriter(dbHelp);
        dbRead = new LocationDBReader(dbHelp);
        runId = dbRead.getLatestRunId() + 1;
        runBundle = new Bundle();
        boolean successful = locHelp.addLocationUpdates();

        return successful;
    }



    /**
     * Called when the location has changed.
     * <p/>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        secondLatestWaypointId = latestWaypointId;
        secondLatestLocation = latestLocation;
        latestLocation = location;
        latestWaypointId = storeWaypoint(location);
        //in the very first update, there is only one Location, so we can't create a segment
        if (secondLatestWaypointId != null && secondLatestLocation != null ){
            Segment sg = new Segment(secondLatestWaypointId, latestWaypointId, secondLatestLocation, latestLocation, runId);
            long insertedSegmentId = storeSegment(sg);
            if (isFirstSegment){
                firstSegmentId = insertedSegmentId;
                isFirstSegment = false;
                storeRun();
            } else {
                updateRun(runId);
            }
        }
        triggerOnDataChangedListener();
    }


    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     * @param status   {@link LocationProvider#OUT_OF_SERVICE} if the
     *                 provider is out of service, and this is not expected to change in the
     *                 near future; {@link LocationProvider#TEMPORARILY_UNAVAILABLE} if
     *                 the provider is temporarily unavailable but is expected to be available
     *                 shortly; and {@link LocationProvider#AVAILABLE} if the
     *                 provider is currently available.
     * @param extras   an optional Bundle which will contain provider specific
     *                 status variables.
     *                 <p/>
     *                 <p> A number of common key/value pairs for the extras Bundle are listed
     *                 below. Providers that use any of the keys on this list must
     *                 provide the corresponding value as described below.
     *                 <p/>
     *                 <ul>
     *                 <li> satellites - the number of satellites used to derive the fix
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //TODO: add code in case GPS is not available
    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {
        //TODO: add handling in case GPS is disabled

    }

    private long storeWaypoint(Location location) {
        Waypoint wp = new Waypoint(location, runId);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        runBundle.putParcelable("position", latLng);
        return dbWrite.storeWaypoint(wp);
    }

    private long storeSegment(Segment sg){
        runBundle.putDouble("velocity", sg.velocity);
        return dbWrite.storeSegment(sg);
    }

    private void updateRun(long runId){
        currentRun = dbWrite.updateRun(runId, dbRead);
        runBundle.putDouble("distance", currentRun.distance);
        runBundle.putLong("timeInterval", currentRun.timeInterval);
    }
    private long storeRun(Run run){
        return dbWrite.storeRun(run);
    }

    private long storeRun(){
        Run run = new Run(runId);
        return storeRun(run);
    }

    //To be called from displaying activity
    public void addOnDataChangedListener(OnDataChangedListener listener){
        listeners.add(listener);
    }

    public void triggerOnDataChangedListener(){
        for (int i = 0; i < listeners.size(); i++){
            listeners.get(i).onDataChanged(runBundle);
        }

    }

    public class LocationBinder extends Binder {
        public LocationService getService(){
            return LocationService.this;
        }
    }
    public interface OnDataChangedListener{
        //interface methods are always public
        void onDataChanged(Bundle run);
    }



}
