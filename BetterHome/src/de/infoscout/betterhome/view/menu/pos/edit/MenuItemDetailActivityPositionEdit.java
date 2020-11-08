package de.infoscout.betterhome.view.menu.pos.edit;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.view.menu.rule.MenuItemDetailActivityRule;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;

public class MenuItemDetailActivityPositionEdit extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuitem_detail_position_edit);

		// Show the Up button in the action bar.
		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		    getActionBar().setHomeButtonEnabled(true);
		    getActionBar().setDisplayShowHomeEnabled(true);
		}
				
		int positionId = getIntent().getIntExtra("positionId", -1);
		String positionName = getIntent().getStringExtra("positionName");
		int positionActNrb = getIntent().getIntExtra("positionActNrb", -1);
		double positionValue = getIntent().getDoubleExtra("positionValue", -1.0);
		double positionLon = getIntent().getDoubleExtra("positionLon", -1.0);
		double positionLat = getIntent().getDoubleExtra("positionLat", -1.0);
		boolean positionOnEntry = getIntent().getBooleanExtra("positionOnEntry", true);
		int positionRadius = getIntent().getIntExtra("positionRadius", 150);
		
		setTitle(positionName);
		
		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			MenuItemDetailFragmentPositionEdit fragment = new MenuItemDetailFragmentPositionEdit();
			
			Bundle args= new Bundle();
	        args.putInt("positionId", positionId);
	        args.putString("positionName", positionName);
	        args.putInt("positionActNrb", positionActNrb);
	        args.putDouble("positionValue", positionValue);
	        args.putDouble("positionLon", positionLon);
	        args.putDouble("positionLat", positionLat);
	        args.putBoolean("positionOnEntry", positionOnEntry);
	        args.putInt("positionRadius", positionRadius);
	        fragment.setArguments(args);
			
			getSupportFragmentManager().beginTransaction()
					.add(R.id.menuitem_detail_container_handy, fragment).commit();
		}

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this, new Intent(this,
					MenuItemDetailActivityRule.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}


