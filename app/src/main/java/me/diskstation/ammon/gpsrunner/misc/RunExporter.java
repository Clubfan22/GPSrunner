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
