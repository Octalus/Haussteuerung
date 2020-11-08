package de.infoscout.betterhome.view;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.service.SubscribeService;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.utils.Utilities;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

public class AlertSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final String ALARM_ACTIVE_KEY = "prefEnableAlert";
	public static final String ALARM_ACTIVE_VIBRATION_KEY = "prefAlarmEnableVibration";
	public static final String ALARM_ACTIVE_RINGTONE_KEY = "prefAlarmEnableRingtone";
	public static final String ALARM_RINGTONE_KEY = "prefAlarmRingtone";
	
	public static final String ALARM_SMOKEDETECTOR_KEY = "prefSmokeAlert";
	public static final String ALARM_HEATDETECTOR_KEY = "prefHeatAlert";
	public static final String ALARM_WATERDETECTOR_KEY = "prefWaterAlert";
	public static final String ALARM_WINDOWBREAK_KEY = "prefWindowBreakAlert";
	public static final String ALARM_FENCEDETECTOR_KEY = "prefFenceAlert";
	public static final String ALARM_MOTIONDETECTOR_KEY = "prefMotionAlert";
	
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        
        // Show the Up button in the action bar.
 		if (getActionBar() != null) {
 			getActionBar().setDisplayHomeAsUpEnabled(true);
 		    getActionBar().setHomeButtonEnabled(true);
 		    getActionBar().setDisplayShowHomeEnabled(true);
 		}
 		
 		super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.alertsettings);
        	
    }
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	        String key) {
	        if (key.equals(ALARM_ACTIVE_KEY)) {
	            boolean alarmActivated = sharedPreferences.getBoolean(key, false);
	            RuntimeStorage.setAlarm(alarmActivated);
	            
	            if (alarmActivated) {
	            	// start live console
	            	Log.i(Utilities.TAG, "XS Subscribe Service wird gestartet!");
	    			startService(new Intent(this, SubscribeService.class));
	            } else {
	            	// stop live console
	            	stopService(new Intent(this, SubscribeService.class));
	            }
	        } else
	        if (key.equals(ALARM_ACTIVE_VIBRATION_KEY)){
	        	boolean alarmVibration = sharedPreferences.getBoolean(key, false);
	        	RuntimeStorage.setAlarmVibration(alarmVibration);
	        } else
	        if (key.equals(ALARM_ACTIVE_RINGTONE_KEY)){
	        	boolean alarmRingtone = sharedPreferences.getBoolean(key, false);
	        	RuntimeStorage.setAlarmRingtone(alarmRingtone);
	        } else
	        if (key.equals(ALARM_RINGTONE_KEY)){
	        	String ringtone = sharedPreferences.getString(key, android.provider.Settings.System.DEFAULT_RINGTONE_URI.getPath());
	        	RuntimeStorage.setRingtone(ringtone);
	        } else
	        if (key.equals(ALARM_SMOKEDETECTOR_KEY)){
	        	boolean smokeAlarm = sharedPreferences.getBoolean(key, false);
	        	RuntimeStorage.setSmokeDetectorAlarm(smokeAlarm);
	        } else
	        if (key.equals(ALARM_HEATDETECTOR_KEY)){
	        	boolean heatAlarm = sharedPreferences.getBoolean(key, false);
	        	RuntimeStorage.setHeatDetectorAlarm(heatAlarm);
	        } else
	        if (key.equals(ALARM_WATERDETECTOR_KEY)){
	        	boolean waterAlarm = sharedPreferences.getBoolean(key, false);
	        	RuntimeStorage.setWaterDetectorAlarm(waterAlarm);
	        } else
	        if (key.equals(ALARM_WINDOWBREAK_KEY)){
	        	boolean windowBreakAlarm = sharedPreferences.getBoolean(key, false);
	        	RuntimeStorage.setWindowBreakAlarm(windowBreakAlarm);
	        } else
	        if (key.equals(ALARM_FENCEDETECTOR_KEY)){
	        	boolean fenceDetectorAlarm = sharedPreferences.getBoolean(key, false);
	        	RuntimeStorage.setFenceDetectorAlarm(fenceDetectorAlarm);
	        } else
	        if (key.equals(ALARM_MOTIONDETECTOR_KEY)){
	        	boolean motionDetectorAlarm = sharedPreferences.getBoolean(key, false);
	        	RuntimeStorage.setMotionDetectorAlarm(motionDetectorAlarm);
	        }
	        
	        
	    }
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this,
					MenuItemListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
