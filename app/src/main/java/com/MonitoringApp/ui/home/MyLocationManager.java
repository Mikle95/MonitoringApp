package com.MonitoringApp.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.List;

public class MyLocationManager {
    Context context;
    Location location;
    android.location.LocationManager locationManager;
    String provider = android.location.LocationManager.GPS_PROVIDER;

    MyLocationManager(Activity activity){
        this.context = activity;
        locationManager = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return;

        location = getLastKnownLocation(activity);

//        locationManager.requestLocationUpdates(provider, 1000, 10, new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull Location location) {
//                MyLocationManager.this.location = location;
//            }
//        });
    }

    Location getLocation(Activity activity){
//        if (location == null)
        location = getLastKnownLocation(activity);

        return location;
    }

    private Location getLastKnownLocation(Activity activity) {
        String bestProvider = provider;
        List<String> providers = locationManager.getProviders(true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                    }, 1);

        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
                bestProvider = provider;
            }
        }
        provider = bestProvider;
        return bestLocation;
    }
}
