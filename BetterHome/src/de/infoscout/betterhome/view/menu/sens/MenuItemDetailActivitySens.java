package de.infoscout.betterhome.view.menu.sens;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MenuItemDetailActivitySens extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuitem_detail_sens);

		// Show the Up button in the action bar.
		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		    getActionBar().setHomeButtonEnabled(true);
		    getActionBar().setDisplayShowHomeEnabled(true);
		}
		
		// Keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			MenuItemDetailFragmentSens fragment = new MenuItemDetailFragmentSens();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.menuitem_detail_container_handy, fragment).commit();
		}
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
