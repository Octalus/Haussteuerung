package de.infoscout.betterhome.view;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final String REFRESH_SECONDS_KEY = "prefRefreshSeconds";
	public static final String GRAPH_OUTLIER_ELEMINATION = "prefOutlierElemination";
	
	
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
 
        addPreferencesFromResource(R.xml.settings);
    }
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	        String key) {
	        if (key.equals(REFRESH_SECONDS_KEY)) {
	            int refreshSeconds = Integer.parseInt(sharedPreferences.getString(key, "300"));
	            RuntimeStorage.setRefreshSeconds(refreshSeconds);
	        } else if (key.equals(GRAPH_OUTLIER_ELEMINATION)) {
	            boolean outlierDetection = sharedPreferences.getBoolean(key, false);
	            RuntimeStorage.setGraphOutlierDetection(outlierDetection);
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
