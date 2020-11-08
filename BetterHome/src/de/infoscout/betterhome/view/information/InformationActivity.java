package de.infoscout.betterhome.view.information;


import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class InformationActivity extends Activity {

	// Das Xsone Objekt für diese Aktivity
	private Xsone myXsone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		// Show the Up button in the action bar.
		setupActionBar();
				
		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();
		
		TextView geraetename = (TextView)findViewById(R.id.geraetname);
		geraetename.setText(myXsone.getDeviceName());
		TextView firmware = (TextView)findViewById(R.id.firmware);
		firmware.setText(myXsone.getFirmware());
		TextView hardware = (TextView)findViewById(R.id.hardware);
		hardware.setText(myXsone.getHardware());
		TextView bootloader = (TextView)findViewById(R.id.bootloader);
		bootloader.setText(myXsone.getBootloader());
		TextView systeme = (TextView)findViewById(R.id.systeme);
		systeme.setText(myXsone.getSystem()+"");
		TextView features = (TextView)findViewById(R.id.features);
		features.setText(myXsone.getFeatures().toString() );
		
		TextView laufzeit = (TextView)findViewById(R.id.laufzeit);
		long secs = myXsone.getUptime();
		long tage = (long)(secs/60/60/24);
		long stunden = (long)(secs/60/60)-(tage*24);
		long minuten = (long)(secs/60)-(tage*24*60)-(stunden*60);
		long sekunden = secs - (tage*24*60*60) - (stunden*60*60) - (minuten*60);
		laufzeit.setText(tage+" "+getString(R.string.days)+" "+stunden+" "+getString(R.string.hours)+" "+minuten+" "+getString(R.string.minutes)+" "+sekunden+" "+getString(R.string.seconds));
		
		TextView mac = (TextView)findViewById(R.id.mac);
		mac.setText(myXsone.getMac());
		TextView dhcp = (TextView)findViewById(R.id.dhcp);
		dhcp.setText(myXsone.getAutoip());
		TextView ip = (TextView)findViewById(R.id.ip);
		ip.setText(myXsone.getMyIpSetting().getIp());
		TextView subnet = (TextView)findViewById(R.id.subnet);
		subnet.setText(myXsone.getMyIpSetting().getNetmask());
		TextView gateway = (TextView)findViewById(R.id.gateway);
		gateway.setText(myXsone.getMyIpSetting().getGateway());
		TextView dns = (TextView)findViewById(R.id.dns);
		dns.setText(myXsone.getMyIpSetting().getDns());
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		// Show the Up button in the action bar.
		if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		    getActionBar().setHomeButtonEnabled(true);
		    getActionBar().setDisplayShowHomeEnabled(true);
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
					MenuItemListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
