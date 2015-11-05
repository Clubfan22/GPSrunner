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

package me.diskstation.ammon.gpsrunner.misc;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import me.diskstation.ammon.gpsrunner.R;

/**
 * Created by Marco on 28.10.2015.
 */
public class ValueFormatter {
    Context context;
    private String abbrMin;
    private String abbrSec;
    private String separator;

    public ValueFormatter(Context c){
        context = c;
        abbrMin = context.getString(R.string.abbr_min);
        abbrSec = context.getString(R.string.abbr_sec);
        separator = context.getString(R.string.separator);
    }

    public ValueFormatter(){}

    public String formatDistance(double distance){
        return String.valueOf((double) Math.round(distance * 10) / 10.0d + "m");
    }

    public String formatVelocity(double velocity){
        return String.valueOf((double) Math.round(velocity * 10) / 10.0d + "m/s");
    }

    public String formatTimeInterval(long interval){
        String formattedTimeInterval = String.format("%d " + abbrMin + separator + " %d " + abbrSec,
                TimeUnit.MILLISECONDS.toMinutes(interval),
                TimeUnit.MILLISECONDS.toSeconds(interval) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(interval))
        );
        return formattedTimeInterval;
    }

    public String formatDate(long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("dd. MM. HH:mm");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        return sdf.format(c.getTime());
    }

    public String formatDateToFilename(long timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        return sdf.format(c.getTime());
    }

    public String formatTime(long timestamp){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(calendar.getTime());
    }
}
