package me.diskstation.ammon.gpsrunner.misc;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import me.diskstation.ammon.gpsrunner.db.Run;
import me.diskstation.ammon.gpsrunner.db.Waypoint;

/**
 * Created by Marco on 28.10.2015.
 */
public class CSVRunExporter extends RunExporter {
    private String separator;
    private ValueFormatter vf;


    public CSVRunExporter(Context context){
        super(context);
        separator = System.getProperty("line.separator");
        vf = new ValueFormatter(context);
    }

    @Override
    public void export(Long[] runIds){
        for (int i = 0; i < runIds.length; i++){
            exportRun(runIds[i]);
        }
    }

    public void export(){
        export(dbRead.getRunIds());

    }

    protected void exportRun (long runId){
        if (isExternalStorageWritable()) {
            Run run = dbRead.getRun(runId);
            String runTitle = vf.formatDateToFilename(run.timestamp) + ".csv";
            ArrayList<Waypoint> waypoints = dbRead.getWaypoints(runId);
            try {
                File file = new File(getStorageDir(), runTitle);
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                writeHeader(osw);
                for (int i = 0; i < waypoints.size(); i++) {
                    writeRow(osw, getRow(waypoints.get(i)));
                }
                osw.flush();
                osw.close();
                fos.close();
            } catch (FileNotFoundException ex){
                System.out.println(ex);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    private String getRow(Waypoint wp){
        return wp.longtitude + ";" + wp.latitude + ";" + wp.height;
    }

    private void writeHeader(OutputStreamWriter osw){
        try{
            osw.append("longtitude;latitude;altitude");
            osw.append(separator);
        } catch (IOException ex){
            System.out.println(ex);
        }
    }

    private void writeRow(OutputStreamWriter osw, String row){
        try{
            osw.append(row);
            osw.append(separator);
        } catch (IOException ex){
            System.out.println(ex);
        }
    }


    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private File getStorageDir(){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "GPSrunner");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                System.out.println("Creation of export directory failed");
            }
        }
        return file;
    }

}
