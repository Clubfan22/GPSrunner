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

package me.diskstation.ammon.gpsrunner.service;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Created by Marco on 06.09.2015.
 * @author Marco Ammon
 * Wrapper class for getting current location
 */
public class LocationHelper {
    //LocationManager used for getting updates from GPS module
    LocationManager locMan;
    LocationListener locLis;
    //Location provider GPS, also uses GLONASS
    String LOCATION_PROVIDER = "gps";
    //minimal interval between location Updates
    private int minUpdateInterval = 1000;
    private int minUpdateDistance = 0;

    public LocationHelper(Context context){
        //requesting LocationManager
        locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //Setting up locLis
        locLis = (LocationListener) context;
    }

    protected boolean addLocationUpdates(){
        return addLocationUpdates(locLis);
    }

    protected boolean addLocationUpdates(LocationListener locLis) {
        boolean successful = false;
        if (locMan.isProviderEnabled(LOCATION_PROVIDER)){
            try {
                locMan.requestLocationUpdates(LOCATION_PROVIDER, minUpdateInterval, minUpdateDistance, locLis);
                successful = true;
            } catch (SecurityException ex){
                System.out.println(ex);
            }
        }
        return successful;
    }

    protected void removeLocationUpdates(){
        removeLocationUpdates(locLis);
    }
    protected void removeLocationUpdates(LocationListener locLis){
         try{
             locMan.removeUpdates(locLis);
         } catch (SecurityException ex){
             System.out.println(ex);
         }
    }



}
