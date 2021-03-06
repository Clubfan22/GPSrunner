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

/**
 * Created by Marco on 07.09.2015.
 */
public class Run {
    public long id;
    // public long startId;
    // public long endId;
    public double distance;
    public long timeInterval;
    public double maxVelocity;
    public double medVelocity;
    public double ascendInterval;
    public double descendInterval;
    public long breakTime;
    public long timestamp;

    public Run(){}

    public Run(long id, double distance, long timeInterval, long timestamp){
        this.id = id;
        this.distance = distance;
        this.timeInterval = timeInterval;
        this.timestamp = timestamp;
    }
    public Run(long id){
        this.id = id;
    }
    public Run(long id, double distance, long timeInterval, double maxVelocity, double medVelocity, double ascendInterval, double descendInterval, long breakTime, long timestamp){
        this. id = id;
        this.distance = distance;
        this.timeInterval = timeInterval;
        this.maxVelocity = maxVelocity;
        this.medVelocity = medVelocity;
        this.ascendInterval = ascendInterval;
        this.descendInterval = descendInterval;
        this.breakTime = breakTime;
        this.timestamp = timestamp;
    }

    public ContentValues toContentValues(boolean storeId){
        ContentValues values = new ContentValues();
        if (storeId){
            values.put(GPSrunnerContract.Runs._ID, id);
        }
        //values.put(GPSrunnerContract.Runs.COLUMN_NAME_START_ID, startId);
        //values.put(GPSrunnerContract.Runs.COLUMN_NAME_END_ID, endId);
        values.put(GPSrunnerContract.Runs.COLUMN_NAME_DISTANCE, distance);
        values.put(GPSrunnerContract.Runs.COLUMN_NAME_TIME_INTERVAL, timeInterval);
        values.put(GPSrunnerContract.Runs.COLUMN_NAME_MAX_VELOCITY, maxVelocity);
        values.put(GPSrunnerContract.Runs.COLUMN_NAME_MED_VELOCITY, medVelocity);
        values.put(GPSrunnerContract.Runs.COLUMN_NAME_ASCEND_INTERVAL, ascendInterval);
        values.put(GPSrunnerContract.Runs.COLUMN_NAME_DESCEND_INTERVAL, descendInterval);
        values.put(GPSrunnerContract.Runs.COLUMN_NAME_BREAK_TIME, breakTime);
        values.put(GPSrunnerContract.Runs.COLUMN_NAME_TIMESTAMP, timestamp);
        return values;
    }
}
