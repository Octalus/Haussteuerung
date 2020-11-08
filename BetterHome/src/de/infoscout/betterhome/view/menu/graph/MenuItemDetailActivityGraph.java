package de.infoscout.betterhome.view.menu.graph;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.view.menu.sens.MenuItemDetailActivitySens;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class MenuItemDetailActivityGraph extends FragmentActivity {
	private int sensorNumber;
	private int actuatorNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuitem_detail_sens_graph);
		
		// Show the Up button in the action bar.
		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		    getActionBar().setHomeButtonEnabled(true);
		    getActionBar().setDisplayShowHomeEnabled(true);
		}
		
		// Keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		sensorNumber = getIntent().getIntExtra("sensorNumber", -1);
		actuatorNumber = getIntent().getIntExtra("actuatorNumber", -1);
		
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			MenuItemDetailFragmentGraph fragment = new MenuItemDetailFragmentGraph();
			
			Bundle args= new Bundle();
	        args.putInt("sensorNumber", sensorNumber);
	        args.putInt("actuatorNumber", actuatorNumber);
	        fragment.setArguments(args);
			
			getSupportFragmentManager().beginTransaction()
					.add(R.id.menuitem_detail_container_handy, fragment).commit();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this,
					MenuItemDetailActivitySens.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
}
