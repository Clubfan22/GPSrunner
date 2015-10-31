package me.diskstation.ammon.gpsrunner.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Marco on 06.09.2015.
 * @author Marco Ammon
 * Wrapper class for getting current location
 */
public class LocationHelper {
    //LocationManager used for getting updates from GPS module
    LocationManager locMan;
    //Criteria specifying properties of location updates
    Criteria criteria;
    LocationListener locLis;
    //Location provider GPS, also uses GLONASS
    String LOCATION_PROVIDER = "gps";
    //minimal interval between location Updates
    private int minUpdateInterval = 1000;
    private int minUpdateDistance = 0;

    public LocationHelper(Context context){
        //requesting LocationManager
        locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //setting up criteria with fine accuracy
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //Setting up locLis
        locLis = (LocationListener) context;
    }

    protected boolean addLocationUpdates(){
        return addLocationUpdates(locLis);
    }

    protected boolean addLocationUpdates(LocationListener locLis) {
        boolean successful = false;
        if (locMan.isProviderEnabled(LOCATION_PROVIDER)){
            locMan.requestLocationUpdates(LOCATION_PROVIDER, minUpdateInterval, minUpdateDistance, locLis);
            successful = true;
        }
        return successful;
    }

    protected void removeLocationUpdates(){
        removeLocationUpdates(locLis);
    }
    protected void removeLocationUpdates(LocationListener locLis){
         locMan.removeUpdates(locLis);
    }



}
