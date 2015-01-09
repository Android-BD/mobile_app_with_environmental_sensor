package csee.usf.edu;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
 
public class GPSTracker extends Service implements LocationListener {
 
    private final Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean getLocationEnabled = false;
    double latitude;
    double longitude;
    Location location;
    private static final long MIN_DISTANCE = 0;
    private static final long MIN_TIME = 0;
    protected LocationManager locationManager;
 
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }
 
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (!isGPSEnabled && !isNetworkEnabled) {
                // TODO: No GPS or Network available alert
            } else {
                this.getLocationEnabled = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME,
                            MIN_DISTANCE, this);
                    Log.d("NETWORK", "NETWORK ENABLED");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME,
                                MIN_DISTANCE, this);
                        Log.d("GPS", "GPS ENABLED");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                            	
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }
     
    public void stopGPSUpdates(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }       
    }
    public ArrayList<Double> getGPSCoordinates() {
    	getLocation();
    	ArrayList<Double> gpsCoords = new ArrayList<Double>();
    	if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    	gpsCoords.add(latitude);
    	gpsCoords.add(longitude);
    	
    	return gpsCoords;
    }
     
    public boolean canGetLocation() {
        return this.getLocationEnabled;
    }

    @Override
    public void onLocationChanged(Location location) {
    	try {
    		latitude = location.getLatitude();
    		longitude = location.getLongitude();
    	 }
	    catch (NullPointerException e){
	    	latitude = -1.0;
	    	longitude = -1.0;
	      }
    }
 
    @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
}