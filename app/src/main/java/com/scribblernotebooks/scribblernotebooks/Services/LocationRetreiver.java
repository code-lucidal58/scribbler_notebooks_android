package com.scribblernotebooks.scribblernotebooks.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.scribblernotebooks.scribblernotebooks.HelperClasses.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationRetreiver extends IntentService implements LocationListener {

    public LocationRetreiver() {
        super("LocationRetreiver");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Location lastKnown;
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        catch (Exception e){
            try{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            catch (Exception ef){
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
                lastKnown = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }

        try {
            double lattitude = lastKnown.getLatitude();
            double longitude = lastKnown.getLongitude();
            String cityName = "Not Found";
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            try {

                List<Address> addresses = gcd.getFromLocation(lattitude, longitude,
                        10);

                for (Address adrs : addresses) {
                    if (adrs != null) {

                        String city = adrs.getLocality();
                        if (city != null && !city.equals("")) {
                            cityName = city;
                            System.out.println("city ::  " + cityName);
                            Log.e("City", cityName);
                            SharedPreferences userPref = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = userPref.edit();
                            editor.putString(Constants.PREF_DATA_LOCATION, city);
                            editor.apply();
                            break;
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {

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
