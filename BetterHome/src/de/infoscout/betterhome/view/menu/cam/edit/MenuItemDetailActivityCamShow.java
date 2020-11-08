package de.infoscout.betterhome.view.menu.cam.edit;

import de.infoscout.betterhome.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class MenuItemDetailActivityCamShow extends FragmentActivity {
	private String camName;
	private String camUrl;
	private String camUser;
	private String camPass;
	private int stream;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		
		if (stream == 1){
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(
			    WindowManager.LayoutParams.FLAG_FULLSCREEN,  
			     WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
		}
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuitem_detail_cam_show);

		camName = getIntent().getStringExtra("camName");
		camUrl = getIntent().getStringExtra("camUrl");
		camUser = getIntent().getStringExtra("camUser");
		camPass = getIntent().getStringExtra("camPass");
		stream = getIntent().getIntExtra("stream", 0);
		
		
		
		// Show the Up button in the action bar.
		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		    getActionBar().setHomeButtonEnabled(true);
		    getActionBar().setDisplayShowHomeEnabled(true);
		}
		
		setTitle(camName);

		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			MenuItemDetailFragmentCamShow fragment = new MenuItemDetailFragmentCamShow();
			
			Bundle args= new Bundle();
	        args.putString("camUrl", camUrl);
	        args.putInt("stream", stream);
	        args.putString("camName", camName);
	        args.putString("camUser", camUser);
	        args.putString("camPass", camPass);
	        
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
					MenuItemDetailActivityCamShow.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}


