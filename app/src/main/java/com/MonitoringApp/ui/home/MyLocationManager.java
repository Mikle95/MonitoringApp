package com.MonitoringApp.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class MyLocationManager {
    Context context;
    Location location;
    android.location.LocationManager locationManager;
    String provider = android.location.LocationManager.GPS_PROVIDER;

    MyLocationManager(Context context){
        this.context = context;
        locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return;

        locationManager.requestLocationUpdates(provider, 1000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                MyLocationManager.this.location = location;
            }
        });
    }

    Location getLocation(){
        if (location == null)
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                location = locationManager.getLastKnownLocation(provider);

        return location;
    }
}
