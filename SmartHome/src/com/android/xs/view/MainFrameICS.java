package com.android.xs.view;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.controller.usage.MakroController;
import com.android.xs.model.device.Xsone;
import com.android.xs.view.R;
import com.android.xs.view.tabs.Act_Frame;
import com.android.xs.view.tabs.Options_Frame;
import com.android.xs.view.tabs.Script_Frame;
import com.android.xs.view.tabs.Sens_Frame;
import com.android.xs.view.tabs.Tim_Frame;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Die Activity MainFrame verwaltet alle Tabs.
 * 
 * @author Viktor Mayer
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class MainFrameICS extends Activity {

	private static ActionBar actionbar;
	public static Context appcontext;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actionbar);

		// Ladevorgang anzeigen
		ProgressDialog dialog = ProgressDialog.show(this, "", "Laden...", true);
		dialog.show();

		appcontext = this.getBaseContext();

		// ActionBar gets initiated
		actionbar = getActionBar();
		// Tell the ActionBar we want to use Tabs.
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// initiating five tabs and set text to it.
		ActionBar.Tab actuatorsTab = actionbar.newTab().setText("Aktuatoren")
				.setIcon(R.drawable.actuators_tab);
		ActionBar.Tab sensorsTab = actionbar.newTab().setText("Sensoren")
				.setIcon(R.drawable.sensors_tab);
		ActionBar.Tab timersTab = actionbar.newTab().setText("Timer")
				.setIcon(R.drawable.timers_tab);
		ActionBar.Tab scriptsTab = actionbar.newTab().setText("Skripte")
				.setIcon(R.drawable.script_tab);
		ActionBar.Tab optionsTab = actionbar.newTab().setText("Optionen")
				.setIcon(R.drawable.options_tab);

		// create the five fragments we want to use for display content
		Fragment actuatorsFragment = new Act_Frame();
		Fragment sensorsFragment = new Sens_Frame();
		Fragment timersFragment = new Tim_Frame();
		Fragment scriptsFragment = new Script_Frame();
		Fragment optionsFragment = new Options_Frame();

		// set the Tab listener. Now we can listen for clicks.
		actuatorsTab.setTabListener(new MyTabsListener(actuatorsFragment));
		sensorsTab.setTabListener(new MyTabsListener(sensorsFragment));
		timersTab.setTabListener(new MyTabsListener(timersFragment));
		scriptsTab.setTabListener(new MyTabsListener(scriptsFragment));
		optionsTab.setTabListener(new MyTabsListener(optionsFragment));

		actionbar.addTab(actuatorsTab);
		actionbar.addTab(sensorsTab);
		actionbar.addTab(timersTab);
		actionbar.addTab(scriptsTab);
		actionbar.addTab(optionsTab);

		dialog.dismiss();
	}

	public static Xsone getXsone() {
		return RuntimeStorage.getMyXsone();
	}

	/**
	 * fï¿½r die Tabs wird der KeyDown fï¿½r Back sowie Menï¿½ ï¿½berschrieben
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// Beim Back Button wird in fï¿½r die Tabs ein Hinweis ausgegeben, ob die
		// Anwendung beendet werden soll
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// Abfrage, ob das Programm beendet werden soll
			AlertDialog ad = new AlertDialog.Builder(this).create();
			ad.setCancelable(false); // This blocks the 'BACK' button
			ad.setTitle("Hinweis");
			ad.setMessage("Anwendung wirklich beenden?");
			// Die beiden Auswahlbuttons setzen
			ad.setButton(AlertDialog.BUTTON_POSITIVE, "Beenden",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// Makro lï¿½schen falls noch aktiv
							if (MakroController.getInstance().MakroEnabled() != null)
								MakroController.getInstance().stopMakro(null);
							// Dialog schlieï¿½en und Activity beenden
							dialog.dismiss();
							finish();
						}
					});
			ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Zurück",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// Dialog schlieï¿½en
							dialog.dismiss();
						}
					});
			ad.show();
		}

		if ((keyCode == KeyEvent.KEYCODE_MENU && actionbar.getSelectedNavigationIndex() == 0)) {
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
				// Namen holen fï¿½r Makro

				AlertDialog.Builder alert = new AlertDialog.Builder(this);

				alert.setTitle("Aufzeichnung beenden..");
				alert.setMessage("Makro benennen:");

				// Set an EditText view to get user input
				final EditText input = new EditText(this);
				alert.setView(input);

				alert.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
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

	class MyTabsListener implements ActionBar.TabListener {
		public android.app.Fragment fragment;

		public MyTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			
		}

		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			ft.replace(R.id.fragment_container, fragment);
		}

		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			ft.remove(fragment);
		}

	}

}
