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
