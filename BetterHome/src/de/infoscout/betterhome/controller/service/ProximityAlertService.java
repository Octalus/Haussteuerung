package de.infoscout.betterhome.controller.service;

import java.util.List;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.intent.ProximityPendingIntentFactory;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.model.device.db.PositionDB;
import de.infoscout.betterhome.view.EntryActivity;
import de.infoscout.betterhome.view.utils.Utilities;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Service that provides an alternative implementation for a proximity alert.
 * 
 * @author Marcus Hammer
 */
public class ProximityAlertService extends Service implements LocationListener {
    public static final String LATITUDE_INTENT_KEY = "LATITUDE_INTENT_KEY";
    public static final String LONGITUDE_INTENT_KEY = "LONGITUDE_INTENT_KEY";
    public static final String RADIUS_INTENT_KEY = "RADIUS_INTENT_KEY";
    public static final String ACT_NRB_KEY = "ACT_NRB_INTENT_KEY";
    public static final String ACT_VALUE_KEY = "ACT_VALUE_INTENT_KEY";
    public static final String ACTION_ON_ENTRY_KEY = "ACTION_ON_ENTRY_INTENT_KEY";
    public static final String POSITION_NAME_KEY = "POSITION_NAME_INTENT_KEY";
    
    public static final long UPDATE_TIME_MIN = 25 * 1000;
    public static final long UPDATE_DISTANCE_MIN = 50;
    public boolean GPS_USAGE = false;
    
    private final static int myID = 1234;
    
    private DatabaseStorage db;
    private List<PositionDB> position_list;
        
    public LocationManager locationManager;
    
    private LocationListener context;
    private Service myService;
    
    private float[] updateDistance;
    private float[] distanceFromRadius;
    private Location prevLocation = null;
    
    private float minUpdateDistance;
    
    private static boolean DEBUG = false;
    private static boolean forground = true;

    @Override
    public IBinder onBind(Intent intent) {
        // no-op
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();    
             
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        
        db = new DatabaseStorage(this);
        context = this; 
        myService = this;
    }

	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	    	
        (new GetPositions()).execute();
        
        if (DEBUG) {
        	Toast.makeText(this, getString(R.string.pos_service_started),
					Toast.LENGTH_LONG).show();
        }
        
        return START_STICKY;
    }
    
    @Override
    public void onLocationChanged(Location location) {
    	Log.i(Utilities.TAG, "Location changed");
    	
    	if (location != null) {
    	
	    	if (DEBUG) {
	    		Toast.makeText(this, "Location changed; pos_size="+position_list.size(),
						Toast.LENGTH_LONG).show();
	    	}
	    	
	    	if (position_list != null && position_list.size() > 0){
	    		PositionDB pos;
	    		    	
		        for (int i=0; i<position_list.size(); i++){
		        	pos = position_list.get(i);
			    	float distance = getDistance(location, pos.getLat(), pos.getLon());
			    	
			    	distanceFromRadius[i] = Math.abs(distance - pos.getRadius());
		            float locationEvaluationDistance = (distanceFromRadius[i] - location.getAccuracy()) / 2;
		            //updateDistance[i] = Math.max(1, locationEvaluationDistance);
		            updateDistance[i] = Math.max(UPDATE_DISTANCE_MIN, locationEvaluationDistance);
			    	
			    	Log.i(Utilities.TAG, "distance("+i+") = "+distance);
			    	Log.i(Utilities.TAG, "updateDistance ("+i+") = "+(int)updateDistance[i]);
			        
			        if (distance <= pos.getRadius() && !pos.isLastOnEntry()) {
			            Log.i(Utilities.TAG, "Entering Proximity");
			            if (DEBUG) {
			        		Toast.makeText(this, "Entering Proximity",
			    					Toast.LENGTH_LONG).show();
			        	}
			            
			            pos.setLastOnEntry(true);
			            (new SetLastOnEntry()).execute(pos);
			            		            
			        } else if (distance > pos.getRadius() && pos.isLastOnEntry()) {
			            Log.i(Utilities.TAG, "Exiting Proximity");
			            if (DEBUG) {
			        		Toast.makeText(this, "Exiting Proximity",
			    					Toast.LENGTH_LONG).show();
			        	}
			            
			            pos.setLastOnEntry(false);
			            (new SetLastOnEntry()).execute(pos);
			        }         
		        }
		        
		        if (prevLocation == null || getDistance(location, prevLocation.getLatitude(), prevLocation.getLongitude()) > UPDATE_DISTANCE_MIN){
		        	locationManager.removeUpdates(this);
		        	
		            minUpdateDistance = 999999999;
		            int indexWithMinDistance = 0;
		            for (int i=0; i<updateDistance.length; i++) {
		            	if (updateDistance[i] < minUpdateDistance) { 
		            		minUpdateDistance = updateDistance[i];
		            		indexWithMinDistance = i;
		            	}
		            }
		            
		            String provider;
		            if (distanceFromRadius[indexWithMinDistance] <= location.getAccuracy() && GPS_USAGE) {
		                provider = LocationManager.GPS_PROVIDER;
		            } else {
		                provider = LocationManager.NETWORK_PROVIDER;
		            }
		            
		            if (locationManager.isProviderEnabled(provider)) {
		            	locationManager.requestLocationUpdates(provider, UPDATE_TIME_MIN, minUpdateDistance, this);
		            } else {
		            	// im schlimmsten Fall auf passiv ausweichen!
		            	locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, UPDATE_TIME_MIN, minUpdateDistance, this);
		            	//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_TIME_MIN, minUpdateDistance, this);
		            }
		        }
		        prevLocation = location;
		        
	    	} else {
	    		this.stopForeground(true);
	    		this.stopSelf();
	    	}
    	} else {
    		Log.e(Utilities.TAG, "location == null");
    	}
    }

    @Override
    public void onDestroy() {
        this.stopForeground(true);
        locationManager.removeUpdates(this);
        super.onDestroy();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // no-op
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider.equals(LocationManager.NETWORK_PROVIDER) && position_list != null && position_list.size() > 0) {
        	// Network wieder erreichbar
        	minUpdateDistance = 999999999;
            for (int i=0; i<updateDistance.length; i++) {
            	if (updateDistance[i] < minUpdateDistance) { 
            		minUpdateDistance = updateDistance[i];
            	}
            }
        	
        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_TIME_MIN, minUpdateDistance, this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // no-op
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
    
    // --------------- DB interactions -----------------------------------------
    
    private class GetPositions extends AsyncTask<Void, Void, String[]> {		
		
		@Override
		protected String[] doInBackground(Void... params) {
			position_list = db.getAllPositions();
			
			db.closeDB();
			
			return null;
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
	        
			if (position_list != null && position_list.size() > 0){
		        
				if (forground) {
					// Notification
					//The intent to launch when the user clicks the expanded notification
				    Intent entry_intent = new Intent(myService, EntryActivity.class);
				    entry_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				    PendingIntent pendIntent = PendingIntent.getActivity(myService, 0, entry_intent, 0);
				
				    Notification notification = null;
				    
				    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
				    	Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				    	NotificationCompat.Builder builder = new NotificationCompat.Builder(myService);
			    	    builder.setTicker(getString(R.string.pos_service_started))
			    	    	    .setContentTitle(getString(R.string.app_name))
			    	    	    .setContentText(getString(R.string.pos_service))
			    	            .setWhen(System.currentTimeMillis())
			    	            .setAutoCancel(false)
			    	            .setOngoing(true)
			    	            .setPriority(Notification.PRIORITY_HIGH)
			    	            .setContentIntent(pendIntent)
			    	            .setSmallIcon(R.drawable.ic_launcher)
			    	            .setColor( getResources().getColor(R.color.infoscout_blue) )
			    	    		.setLargeIcon(bm);
			    	    			            
			    	    notification = builder.build();
			    	}
				    
			    	notification.flags |= Notification.FLAG_NO_CLEAR;
			    	startForeground(myID, notification);
				}
				
		    	
				// current Position
				String currentBestProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER;
		        
		        updateDistance = new float[position_list.size()];
		        distanceFromRadius = new float[position_list.size()];
		        for (int i=0; i< position_list.size(); i++){
		        	updateDistance[i] = 0;
		        	distanceFromRadius[i] = 0;
		        }
		        
		        locationManager.requestLocationUpdates(currentBestProvider, UPDATE_TIME_MIN, UPDATE_DISTANCE_MIN, context);
			} else {
				myService.stopForeground(true);
				myService.stopSelf();
			}
		        
			return;

		}
	}

	private class SetLastOnEntry extends AsyncTask<PositionDB, Void, String[]> {		
		PositionDB position;
		
		@Override
		protected String[] doInBackground(PositionDB... params) {
			position = params[0];
			db.updatePosition(position);
						
			db.closeDB();
			
			return null;
		}
	
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			Intent intent =
                    new Intent(ProximityPendingIntentFactory.PROXIMITY_ACTION);
            intent.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, position.isLastOnEntry());
            intent.putExtra(ProximityAlertService.ACT_NRB_KEY, position.getActNumber());
			intent.putExtra(ProximityAlertService.ACT_VALUE_KEY, position.getValue());
			intent.putExtra(ProximityAlertService.ACTION_ON_ENTRY_KEY, position.isOnEntry());
			intent.putExtra(ProximityAlertService.POSITION_NAME_KEY, position.getName());
            
            sendBroadcast(intent);
			
			return;
	
		}
	}
}
