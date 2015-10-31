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
