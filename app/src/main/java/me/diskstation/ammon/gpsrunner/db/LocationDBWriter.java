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

package me.diskstation.ammon.gpsrunner.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Marco on 06.09.2015.
 */
public class LocationDBWriter {
    LocationDBHelper dbHelp;

    public LocationDBWriter(LocationDBHelper dbHelp){
        this.dbHelp = dbHelp;
    }

    public long storeWaypoint(Waypoint wp) {
        SQLiteDatabase db = dbHelp.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GPSrunnerContract.Waypoints.COLUMN_NAME_LONGTITUDE, wp.longtitude);
        values.put(GPSrunnerContract.Waypoints.COLUMN_NAME_LATITUDE, wp.latitude);
        values.put(GPSrunnerContract.Waypoints.COLUMN_NAME_HEIGHT, wp.height);
        values.put(GPSrunnerContract.Waypoints.COLUMN_NAME_TIMESTAMP, wp.timestamp);
        values.put(GPSrunnerContract.Waypoints.COLUMN_NAME_RUN_ID, wp.runId);
        long newRowId = db.insert(GPSrunnerContract.Waypoints.TABLE_NAME, null, values);
        if (newRowId == -1) {
            System.out.println("Inserting waypoint " + wp.longtitude + " " + wp.latitude + " failed!");
        }
        db.close();
        return newRowId;
    }
    public long storeSegment(Segment sg){
        SQLiteDatabase db = dbHelp.getWritableDatabase();
        ContentValues values = sg.toContentValues();
        long newRowId = db.insert(GPSrunnerContract.Sections.TABLE_NAME, null, values);
        if (newRowId == -1) {
            System.out.println("Inserting segment " + sg.startId + " " + sg.endId + " failed!");
        }
        db.close();
        //System.out.println(values.toString());
        return newRowId;
    }
    public long storeRun(Run run){
        SQLiteDatabase db = dbHelp.getWritableDatabase();
        ContentValues values = run.toContentValues(false);
        long newRowID = db.insert(GPSrunnerContract.Runs.TABLE_NAME, null, values);
        if (newRowID == -1){
            System.out.println("Inserting run failed!");
        }
        db.close();
        //System.out.println(values.toString());

        return newRowID;

    }

    public Run updateRun(long id, LocationDBReader dbRead){
        Run updatedRun = dbRead.calculateRun(id);
        updateRun(updatedRun);
        return updatedRun;
    }

    private long updateRun(Run run){
        SQLiteDatabase db = dbHelp.getWritableDatabase();
        ContentValues values = run.toContentValues(false);
        String selection = GPSrunnerContract.Runs._ID  + " = ?";
        String[] selectionArgs = {String.valueOf(run.id)};
        int affectedRows = db.update(GPSrunnerContract.Runs.TABLE_NAME,values, selection, selectionArgs);
        db.close();
        return affectedRows;
    }

}
