package de.infoscout.betterhome.controller.intent;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationHelper {
	private LocationManager locationManager;
	private Activity activity;
	
	public LocationHelper(Activity act){
		this.activity = act;
		this.locationManager=(LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public boolean isInProximity(double latitude, double longitude, int radius) {
    	if (getDistance(getBestLocation(), latitude, longitude) <= radius) {
            return true; 
        } else {
        	return false;
        }
    }
    
    private Location getBestLocation() {
    	Location bestLocation = null;
    	        
        for (String provider : locationManager.getProviders(true)) {
            Location location = locationManager.getLastKnownLocation(provider);
            
            if (location != null) {
	            if (bestLocation == null) {
	                bestLocation = location;
	            } else {
	                if (location.getAccuracy() < bestLocation.getAccuracy()) {
	                    bestLocation = location;
	                }
	            }
            }
        }
        return bestLocation;
    }
    
    public float getDistance(Location location, double lat, double lon) {
        float[] results = new float[1];
        
        Location.distanceBetween(lat,
                lon,
                location.getLatitude(),
                location.getLongitude(),
                results);
        
        return results[0];
    }

}
