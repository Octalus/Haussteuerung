package de.infoscout.betterhome.view.menu.room.edit;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.room.MenuItemDetailActivityRoom;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailFragmentTimer;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;

/**
 * An activity representing a single MenuItem detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a {@link MenuItemListActivity}
 * .
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link MenuItemDetailFragmentTimer}.
 */
public class MenuItemDetailActivityRoomEdit extends FragmentActivity {
	
	private int roomId;
	private String roomName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuitem_detail_room_edit);
		
		roomId = getIntent().getIntExtra("roomId", 0);
		roomName = getIntent().getStringExtra("roomName");
		
		setTitle(roomName);
		
		// Show the Up button in the action bar.
		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		    getActionBar().setHomeButtonEnabled(true);
		    getActionBar().setDisplayShowHomeEnabled(true);
		}

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
			MenuItemDetailFragmentRoomEdit fragment = new MenuItemDetailFragmentRoomEdit();
			
			Bundle args= new Bundle();
	        args.putInt("roomId", roomId);
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
					MenuItemDetailActivityRoom.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
