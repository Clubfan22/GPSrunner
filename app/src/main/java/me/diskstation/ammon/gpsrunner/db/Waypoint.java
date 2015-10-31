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
