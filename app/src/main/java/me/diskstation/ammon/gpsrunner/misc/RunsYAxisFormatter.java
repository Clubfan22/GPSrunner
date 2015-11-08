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

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Marco on 08.11.2015.
 */
public class RunsYAxisFormatter implements YAxisValueFormatter {
    private String unit;
    private DecimalFormat mFormat;
    private String separator;

    public RunsYAxisFormatter(int mode, String separator){
        switch (mode){
            case 0:
                unit = " m/s";
                break;
            case 1:
                unit = " km/h";
                break;
            case 2:
                unit = " m";
                break;
            default:
                unit ="";
        }
        mFormat = new DecimalFormat("###0.0");
        this.separator = separator;
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        String formattedValue = mFormat.format(value) + unit;
        formattedValue = formattedValue.replace(".", separator).trim();
        return formattedValue;
    }
}
