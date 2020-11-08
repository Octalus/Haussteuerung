package com.android.xs.view.tabs;

import java.util.Arrays;
import java.util.LinkedList;

import com.android.xs.view.R;
import com.android.xs.view.setting.Abo;
import com.android.xs.view.setting.About;
import com.android.xs.view.setting.Add;
import com.android.xs.view.setting.AlarmReceiver;
import com.android.xs.view.setting.Config;
import com.android.xs.view.setting.GInfo;
import com.android.xs.view.setting.Positioning;
import com.android.xs.view.setting.Remove;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Options_FrameGBD extends ListActivity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/
	private String[] options = { "Geräteinfo", "Konfiguration", "Hinzufügen", "Entfernen", "Abonnement", "Positions-Tool", "Alarm-Action", "Über" };
	private LinkedList<String> options_list = new LinkedList<String>();

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Das Grundlayout des tabs ist ein TableLayout, welches Scrollbar ist.
		setContentView(R.layout.tab_list_layout);

		// Die Optionen setzen
		options_list.addAll(Arrays.asList(options));

		// Die Liste ausgeben
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_medium, options_list);
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				// je nach der row ID bzw view muss die Activity gestartet
				// werden
				switch ((int) id) {
				case 0:
					Intent intent0 = new Intent(Options_FrameGBD.this, GInfo.class);
					startActivity(intent0);
					break;
				case 1:
					Intent intent1 = new Intent(Options_FrameGBD.this, Config.class);
					startActivity(intent1);
					break;
				case 2:
					Intent intent2 = new Intent(Options_FrameGBD.this, Add.class);
					startActivity(intent2);
					break;
				case 3:
					Intent intent3 = new Intent(Options_FrameGBD.this, Remove.class);
					startActivity(intent3);
					break;
				case 4:
					Intent intent4 = new Intent(Options_FrameGBD.this, Abo.class);
					startActivity(intent4);
					break;
				case 5:
					Intent intent5 = new Intent(Options_FrameGBD.this, Positioning.class);
					startActivity(intent5);
					break;
				case 6:
					Intent intent6 = new Intent(Options_FrameGBD.this, AlarmReceiver.class);
					startActivity(intent6);
					break;
				case 7:
					Intent intent7 = new Intent(Options_FrameGBD.this, About.class);
					startActivity(intent7);
					break;
				/**
				 * case 7: Intent intent7 = new Intent(Options_FrameGBD.this,
				 * DonationsActivity.class); startActivity(intent7); break;
				 */
				}
			}
		});
	}

	/**
	 * Der Tab übernimmt die Aktionen des Tabhost für Menu und Back Button
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// is activity withing a tabactivity
		if (getParent() != null) {
			return getParent().onKeyDown(keyCode, event);
		}
		return false;
	}
}
