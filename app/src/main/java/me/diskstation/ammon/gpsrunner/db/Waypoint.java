package me.diskstation.ammon.gpsrunner.db;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Marco on 06.09.2015.
 *
 * Because of performance-reasons, public attributes are used instead of "proper" get()- and set()-methods
 */
public class Waypoint {
    public double longtitude;
    public double latitude;
    public double height;
    public long timestamp;
    public long runId;

    public Waypoint(double longtitude, double latitude, double height, long timestamp, long runId) {
        this.longtitude = longtitude;
        this.latitude = latitude;
        this.height = height;
        this.timestamp = timestamp;
        this.runId = runId;
    }
    public Waypoint(Location location, long runId) {
        longtitude = location.getLongitude();
        latitude = location.getLatitude();
        height = location.getAltitude();
        timestamp = location.getTime();
        this.runId = runId;
    }

    public LatLng toLatLng() {
        return new LatLng(latitude, longtitude);
    }
}
