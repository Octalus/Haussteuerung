package com.android.xs.view;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.controller.usage.MakroController;
import com.android.xs.model.device.Xsone;
import com.android.xs.view.R;
import com.android.xs.view.tabs.Act_FrameGBD;
import com.android.xs.view.tabs.Options_FrameGBD;
import com.android.xs.view.tabs.Script_FrameGBD;
import com.android.xs.view.tabs.Sens_FrameGBD;
import com.android.xs.view.tabs.Tim_FrameGBD;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * Die Activity MainFrame verwaltet alle Tabs.
 * 
 * @author Viktor Mayer
 * 
 */
@SuppressWarnings("deprecation")
public class MainFrameGBD extends TabActivity {

	private static TabHost tabHost;
	private static int tab_num = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_frame_gbd);

		Resources res = getResources(); // Resource object to get Drawables
		tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab

		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, Act_FrameGBD.class);

		// Ladevorgang anzeigen
		ProgressDialog dialog = ProgressDialog.show(this, "", "Laden...", true);
		dialog.show();

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost
				.newTabSpec("actuators")
				.setIndicator("Aktuatoren",
						res.getDrawable(R.drawable.actuators_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, Sens_FrameGBD.class);
		spec = tabHost
				.newTabSpec("sensors")
				.setIndicator("Sensoren",
						res.getDrawable(R.drawable.sensors_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Tim_FrameGBD.class);
		spec = tabHost.newTabSpec("timers")
				.setIndicator("Timer", res.getDrawable(R.drawable.timers_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Script_FrameGBD.class);
		spec = tabHost
				.newTabSpec("scripts")
				.setIndicator("Skripte", res.getDrawable(R.drawable.script_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Options_FrameGBD.class);
		spec = tabHost
				.newTabSpec("options")
				.setIndicator("Optionen",
						res.getDrawable(R.drawable.options_tab))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(tab_num);

		dialog.dismiss();
	}

	public static Xsone getXsone() {
		return RuntimeStorage.getMyXsone();
	}

	/**
	 * für die Tabs wird der KeyDown für Back sowie Menü überschrieben
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// Beim Back Button wird in für die Tabs ein Hinweis ausgegeben, ob die
		// Anwendung beendet werden soll
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// Abfrage, ob das Programm beendet werden soll
			AlertDialog ad = new AlertDialog.Builder(this).create();
			ad.setCancelable(false); // This blocks the 'BACK' button
			ad.setTitle("Hinweis");
			ad.setMessage("Anwendung wirklich beenden?");
			// Die beiden Auswahlbuttons setzen
			ad.setButton("Beenden", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Makro löschen falls noch aktiv
					if (MakroController.getInstance().MakroEnabled() != null)
						MakroController.getInstance().stopMakro(null);
					// Dialog schließen und Activity beenden
					dialog.dismiss();
					finish();
				}
			});
			ad.setButton2("Zurück", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Dialog schließen
					dialog.dismiss();
				}
			});
			ad.show();
		}

		if ((keyCode == KeyEvent.KEYCODE_MENU && tabHost.getCurrentTab() == 0)) {
			openOptionsMenu();
		}
		return true;
	}

	/**
	 * Das Menü für Makros
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getTitle().equals("aufzeichnen")) {
			Toast.makeText(this, "Makro wird aufgezeichnet!",
					Toast.LENGTH_SHORT).show();
			MakroController.getInstance().newMakro();
		}
		
		if (item.getTitle().equals("stoppen")) {
			if (MakroController.getInstance().MakroEnabled() != null) {
				// Namen holen für Makro

				AlertDialog.Builder alert = new AlertDialog.Builder(this);

				alert.setTitle("Aufzeichnung beenden..");
				alert.setMessage("Makro benennen:");

				// Set an EditText view to get user input 
				final EditText input = new EditText(this);
				alert.setView(input);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				  String value = input.getText().toString();
				  MakroController.getInstance().stopMakro(value);
				  }
				});
				alert.show();				
			}
		}
		if (item.getTitle().equals("Makros")) {
			Intent intent = new Intent(this, Makros_Frame.class);
			startActivity(intent);
		}

		return true;
	}

}