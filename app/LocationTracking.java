/**
 * LocationTracking - Finds Where You Are
 * 
 * This class helps the app know where you are in the world.
 * It's like having a map that tells us your location, which helps us:
 * - Find restaurants near you
 * - Suggest recipes from your area
 * - Show you where to buy ingredients
 */
package com.example.recipe_app;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.widget.Toast;

/**
 * Code taken / adapted from: https://www.digitalocean.com/community/tutorials/android-location-api-tracking-gps
 */

public class LocationTracking extends Service implements LocationListener {

    // The app we're running in
    private final Context mContext;
    
    // Whether we can use GPS or the internet to find location
    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;
    
    // Where you are right now
    Location loc;
    double latitude;   // How far north or south you are
    double longitude;  // How far east or west you are

    // How often to check your location
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;  // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;  // 1 minute
    protected LocationManager locationManager;  // The tool we use to find location

    /**
     * Creates a new location tracker
     * 
     * This sets up everything we need to know where you are.
     */
    public LocationTracking(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    /**
     * Gets your current location
     * 
     * This is like looking at a map to find where you are.
     * It tries to use GPS first, then the internet if GPS isn't working.
     */
    private Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // Check if GPS is turned on
            checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Check if we can use the internet for location
            checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!checkGPS && !checkNetwork) {
                // If neither GPS nor internet is working, we can't get location
                Toast.makeText(mContext, "No location service available", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
                
                // Try to get location from the internet first (it's faster)
                if (checkNetwork) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((android.app.Activity) mContext,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                1);
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null) {
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                        }
                    }
                }
                
                // If internet location didn't work, try GPS
                if (checkGPS) {
                    if (loc == null) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((android.app.Activity) mContext,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    1);
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (loc != null) {
                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loc;
    }

    /**
     * Gets how far east or west you are
     */
    public double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }

    /**
     * Gets how far north or south you are
     */
    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }

    /**
     * Checks if we can find your location
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Shows a message asking you to turn on location services
     * 
     * This is like asking you to turn on your map so we can find you.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Location Settings");
        alertDialog.setMessage("Location is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    /**
     * Stops checking your location
     */
    public void stopListener() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((android.app.Activity) mContext,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            locationManager.removeUpdates(LocationTracking.this);
        }
    }

    /**
     * Required for the Service class
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Called when your location changes
     */
    @Override
    public void onLocationChanged(Location location) {
        // We don't need to do anything here
    }

    /**
     * Called when the way we get your location changes
     */
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // We don't need to do anything here
    }

    /**
     * Called when GPS or internet location is turned on
     */
    @Override
    public void onProviderEnabled(String s) {
        // We don't need to do anything here
    }

    /**
     * Called when GPS or internet location is turned off
     */
    @Override
    public void onProviderDisabled(String s) {
        // We don't need to do anything here
    }
}