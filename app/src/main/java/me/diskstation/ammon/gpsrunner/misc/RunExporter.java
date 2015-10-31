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

package me.diskstation.ammon.gpsrunner.misc;

import android.content.Context;

import me.diskstation.ammon.gpsrunner.db.LocationDBHelper;
import me.diskstation.ammon.gpsrunner.db.LocationDBReader;

/**
 * Created by Marco on 28.10.2015.
 */
public abstract class RunExporter {
    private long[] runIds;
    private Context context;
    protected LocationDBHelper dbHelp;
    protected LocationDBReader dbRead;

    public RunExporter(Context context){
        this.context = context;
        dbHelp = new LocationDBHelper(context);
        dbRead = new LocationDBReader(dbHelp);
    }

    public void export(Long[] runIds){}

    protected void exportRun(long runId){}

}
