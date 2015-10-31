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

import android.graphics.Color;

/**
 * Created by Marco on 19.10.2015.
 */
public class LineColors {
    private static int[] colors = {Color.BLUE,
            Color.GREEN,
            Color.RED,
            Color.YELLOW,
            Color.BLACK,
            Color.CYAN,
            Color.GRAY,
            Color.MAGENTA,
            Color.LTGRAY,
            Color.DKGRAY,
    };

    public static int getColor(int index){
        return colors[index % colors.length];
    }

    /*public static int getLighterColor(int index){
        return getLighterColor(index, 0.4f);
    }
    public static int getLighterColor(int index, float value){
        float[] color = new float[3];
        Color.colorToHSV(getColor(index), color);
        color[2] = value;
        return Color.HSVToColor(color);

    }*/
}
