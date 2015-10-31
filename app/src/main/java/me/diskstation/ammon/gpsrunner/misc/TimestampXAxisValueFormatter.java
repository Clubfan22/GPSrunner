package me.diskstation.ammon.gpsrunner.misc;

import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Marco on 17.10.2015.
 */
public class TimestampXAxisValueFormatter implements XAxisValueFormatter {
    @Override
    //Returns String of date for given timestamp
    public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
        long timestamp;
        try {
            timestamp = Long.valueOf(original);
        } catch (NumberFormatException ex){
            System.out.println(ex);
            return null;
        }
        ValueFormatter vf = new ValueFormatter();
        return vf.formatTime(timestamp);
    }
}
