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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Marco on 06.09.2015.
 * @author Marco Ammon
 * implemented as "Singleton" because only one writer should be allowed
 */
public class LocationDBHelper extends SQLiteOpenHelper{

    //SQL-statements to create tables, based on GPSRunnerContract
    private final String SQL_CREATE_WAYPOINTS =
            "CREATE TABLE " + GPSrunnerContract.Waypoints.TABLE_NAME + " (" +
                    GPSrunnerContract.Waypoints._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    GPSrunnerContract.Waypoints.COLUMN_NAME_LONGTITUDE + " REAL," +
                    GPSrunnerContract.Waypoints.COLUMN_NAME_LATITUDE + " REAL," +
                    GPSrunnerContract.Waypoints.COLUMN_NAME_HEIGHT + " REAL," +
                    GPSrunnerContract.Waypoints.COLUMN_NAME_TIMESTAMP + " INTEGER," +
                    GPSrunnerContract.Waypoints.COLUMN_NAME_RUN_ID + " INTEGER" + " )";
    private final String SQL_CREATE_SECTIONS =
            "CREATE TABLE " + GPSrunnerContract.Sections.TABLE_NAME + " (" +
                    GPSrunnerContract.Sections._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    GPSrunnerContract.Sections.COLUMN_NAME_START_ID + " INTEGER," +
                    GPSrunnerContract.Sections.COLUMN_NAME_END_ID + " INTEGER," +
                    GPSrunnerContract.Sections.COLUMN_NAME_DISTANCE + " REAL," +
                    GPSrunnerContract.Sections.COLUMN_NAME_TIME_INTERVAL + " INTEGER," +
                    GPSrunnerContract.Sections.COLUMN_NAME_VELOCITY + " REAL," +
                    GPSrunnerContract.Sections.COLUMN_NAME_HEIGHT_INTERVAL + " REAL," +
                    GPSrunnerContract.Sections.COLUMN_NAME_RUN_ID  + " INTEGER )";
    private final String SQL_CREATE_RUNS =
            "CREATE TABLE " + GPSrunnerContract.Runs.TABLE_NAME + " (" +
                    GPSrunnerContract.Runs._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                    GPSrunnerContract.Runs.COLUMN_NAME_START_ID + " INTEGER," +
//                    GPSrunnerContract.Runs.COLUMN_NAME_END_ID + " INTEGER," +
                    GPSrunnerContract.Runs.COLUMN_NAME_DISTANCE + " REAL," +
                    GPSrunnerContract.Runs.COLUMN_NAME_TIME_INTERVAL + " INTEGER," +
                    GPSrunnerContract.Runs.COLUMN_NAME_MAX_VELOCITY + " REAL," +
                    GPSrunnerContract.Runs.COLUMN_NAME_MED_VELOCITY + " REAL," +
                    GPSrunnerContract.Runs.COLUMN_NAME_ASCEND_INTERVAL + " REAL," +
                    GPSrunnerContract.Runs.COLUMN_NAME_DESCEND_INTERVAL + " REAL," +
                    GPSrunnerContract.Runs.COLUMN_NAME_TIMESTAMP + " INTEGER," +
                    GPSrunnerContract.Runs.COLUMN_NAME_BREAK_TIME + " INTEGER" + " )";

    private final String SQL_DROP_WAYPOINTS = "DROP TABLE IF EXISTS " + GPSrunnerContract.Waypoints.TABLE_NAME;
    private final String SQL_DROP_SECTIONS = "DROP TABLE IF EXISTS " + GPSrunnerContract.Sections.TABLE_NAME;
    private final String SQL_DROP_RUNS = "DROP TABLE IF EXISTS " + GPSrunnerContract.Runs.TABLE_NAME;

    public LocationDBHelper(Context context){
        super(context, GPSrunnerContract.DATABASE_NAME, null, GPSrunnerContract.DATABASE_VERSION);
    }


    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_WAYPOINTS);
        db.execSQL(SQL_CREATE_SECTIONS);
        db.execSQL(SQL_CREATE_RUNS);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteTables(db);
        onCreate(db);
    }

    private void deleteTables(SQLiteDatabase db){
        db.execSQL(SQL_DROP_WAYPOINTS);
        db.execSQL(SQL_DROP_SECTIONS);
        db.execSQL(SQL_DROP_RUNS);
    }
}
