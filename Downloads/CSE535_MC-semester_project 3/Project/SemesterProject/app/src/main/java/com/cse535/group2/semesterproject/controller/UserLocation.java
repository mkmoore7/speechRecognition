package com.cse535.group2.semesterproject.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 10/23/2016.
 */

public class UserLocation {
    private static UserLocation instance = null;

    private static LocationManager mLocationManager;
    private static String mProvider;
    private static Location mLastLocation = new Location("");
    private static UserLocationListener userLocationListener = null;

    public static final int PERMISSION_LOCATION = 1212;
    private static final int GPS_UPDATE_MILLISECONDS = 1000;

    public static final String BROADCAST = "com.cse535.group2.semesterproject.controller.UserLocation.BROADCAST";

    List<UserLocationReceiver> userLocationReceiverList;

    private UserLocation(){
        userLocationReceiverList = new ArrayList<>();
    }

    public static UserLocation getInstance(){
        if(instance == null){
            instance = new UserLocation();
        }

        return instance;
    }

    private void initLocationService(Activity activity) {
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mProvider = mLocationManager.getBestProvider(criteria, false);
        userLocationListener = new UserLocationListener();

        if(checkLocationPermission(activity)){
            mLocationManager.requestLocationUpdates(mProvider, GPS_UPDATE_MILLISECONDS, 1, userLocationListener);
        }
    }

    private boolean checkLocationPermission(final Activity activity){
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission. ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission. ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(activity.getApplicationContext())
                        .setTitle("Location Permission")
                        .setMessage("Need location permission to find nearby buildings and businesses to better predict favorite vocabulary,")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission. ACCESS_FINE_LOCATION},
                        PERMISSION_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    public void requestLocationUpdates(Activity activity){
        initLocationService(activity);
    }

    public Location getLocation(){
        return mLastLocation;
    }

    private class UserLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            Log.d("onLocationChanged", "lat: " + lat + ", lon: " + lng);

            broadcastLocationChanged();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public void registerReceiver(UserLocationReceiver receiver){
        userLocationReceiverList.add(receiver);
    }

    public void unregisterReceiver(UserLocationReceiver receiver){
        userLocationReceiverList.remove(receiver);
    }

    public void broadcastLocationChanged() {
        for(UserLocationReceiver receiver : userLocationReceiverList){
            receiver.receiveBroadcast();
        }
    }

    public interface UserLocationReceiver{
        public void receiveBroadcast();
    }
}
