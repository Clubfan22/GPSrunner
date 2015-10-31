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

import android.provider.BaseColumns;

/**
 * Created by Marco on 06.09.2015.
 * @author Marco Ammon
 * Contract class defining database model
 */
public final class GPSrunnerContract {
    public static final String DATABASE_NAME = "GPSrunner.db";
    public static final int DATABASE_VERSION = 2;

    //empty constructor
    public GPSrunnerContract(){ }

    public static abstract class Waypoints implements BaseColumns {
        public static final String TABLE_NAME = "waypoints";
        public static final String COLUMN_NAME_LONGTITUDE = "longtitude";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        public static final String COLUMN_NAME_RUN_ID = "run_id";

        public static  final String[] PROJECTION = {
                _ID,
                COLUMN_NAME_LONGTITUDE,
                COLUMN_NAME_LATITUDE,
                COLUMN_NAME_HEIGHT,
                COLUMN_NAME_TIMESTAMP,
                COLUMN_NAME_RUN_ID
        };
    }


    public static abstract class Sections implements BaseColumns {
        public static final String TABLE_NAME = "sections";
        public static final String COLUMN_NAME_START_ID = "start_id";
        public static final String COLUMN_NAME_END_ID = "end_id";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_TIME_INTERVAL = "time_interval";
        public static final String COLUMN_NAME_VELOCITY = "velocity";
        public static final String COLUMN_NAME_HEIGHT_INTERVAL = "height_interval";
        public static final String COLUMN_NAME_RUN_ID = "run_id";

        public static  final String[] PROJECTION = {
                _ID,
                COLUMN_NAME_START_ID,
                COLUMN_NAME_END_ID,
                COLUMN_NAME_DISTANCE,
                COLUMN_NAME_TIME_INTERVAL,
                COLUMN_NAME_VELOCITY,
                COLUMN_NAME_HEIGHT_INTERVAL,
                COLUMN_NAME_RUN_ID
        };
    }


    public static abstract class Runs implements BaseColumns {

        public static final String TABLE_NAME = "runs";
        //   public static final String COLUMN_NAME_START_ID = "start_id";
        //   public static final String COLUMN_NAME_END_ID = "end_id";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_TIME_INTERVAL = "time_interval";
        public static final String COLUMN_NAME_MAX_VELOCITY = "max_velocity";
        public static final String COLUMN_NAME_MED_VELOCITY = "med_velocity";
        public static final String COLUMN_NAME_ASCEND_INTERVAL = "ascend_interval";
        public static final String COLUMN_NAME_DESCEND_INTERVAL = "descend_interval";
        public static final String COLUMN_NAME_BREAK_TIME = "break_time";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";

        public static  final String[] PROJECTION = {
                _ID,
                COLUMN_NAME_DISTANCE,
                COLUMN_NAME_TIME_INTERVAL,
                COLUMN_NAME_MAX_VELOCITY,
                COLUMN_NAME_MED_VELOCITY,
                COLUMN_NAME_ASCEND_INTERVAL,
                COLUMN_NAME_DESCEND_INTERVAL,
                COLUMN_NAME_BREAK_TIME,
                COLUMN_NAME_TIMESTAMP
        };
    }
}
