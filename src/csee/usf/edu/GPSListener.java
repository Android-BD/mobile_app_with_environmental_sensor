package csee.usf.edu;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class GPSListener implements LocationListener {  

public static double latitude;  
public static double longitude;  

@Override  
public void onLocationChanged(Location loc)  
{  
    loc.getLatitude();  
    loc.getLongitude();  
    latitude = loc.getLatitude();  
    longitude = loc.getLongitude(); 
    Log.d("GPSLISTENER ", "lat: "+latitude+"  long:"+longitude);
}

@Override
public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
	
}

@Override
public void onProviderEnabled(String provider) {
	// TODO Auto-generated method stub
	
}

@Override
public void onStatusChanged(String provider, int status, Bundle extras) {
	// TODO Auto-generated method stub
	
}
}
