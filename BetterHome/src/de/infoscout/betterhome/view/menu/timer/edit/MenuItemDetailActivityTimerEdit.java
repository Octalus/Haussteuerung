package de.infoscout.betterhome.view.menu.timer.edit;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailActivityTimer;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;

public class MenuItemDetailActivityTimerEdit extends FragmentActivity {
	private int timerNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuitem_detail_timer_edit);

		// Show the Up button in the action bar.
		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		    getActionBar().setHomeButtonEnabled(true);
		    getActionBar().setDisplayShowHomeEnabled(true);
		}
		
		timerNumber = getIntent().getIntExtra("timerNumber", 0);

		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			MenuItemDetailFragmentTimerEdit fragment = new MenuItemDetailFragmentTimerEdit();
			
			Bundle args= new Bundle();
	        args.putInt("timerNumber", timerNumber);
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
					MenuItemDetailActivityTimer.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}


