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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class Options_Frame extends ListFragment {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/
	private String[] options = { "Geräteinfo", "Konfiguration", "Hinzufügen", "Entfernen",
			"Abonnement", "Positions-Tool", "Alarm-Action", "Über" /**, "Spenden"*/};
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// Das Grundlayout des tabs ist ein TableLayout, welches Scrollbar ist.
        return inflater.inflate(R.layout.tab_list_layout, container, false);
    }
	
	/**
	 * 
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Die Optionen setzen
		if (options_list.size() == 0)
			options_list.addAll(Arrays.asList(options));

		// Die Liste ausgeben
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.list_item_medium, options_list);
		setListAdapter(adapter);

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// je nach der row ID bzw view muss die Activity gestartet
				// werden
				switch ((int) id) {
				case 0:
					Intent intent0 = new Intent(getActivity(), GInfo.class);
					startActivity(intent0);
					break;
				case 1:
					Intent intent1 = new Intent(getActivity(),
							Config.class);
					startActivity(intent1);
					break;
				case 2:
					Intent intent2 = new Intent(getActivity(), Add.class);
					startActivity(intent2);
					break;
				case 3:
					Intent intent3 = new Intent(getActivity(), Remove.class);
					startActivity(intent3);
					break;
				case 4:
					Intent intent4 = new Intent(getActivity(), Abo.class);
					startActivity(intent4);
					break;
				case 5:
					Intent intent5 = new Intent(getActivity(), Positioning.class);
					startActivity(intent5);
					break;
				case 6:
					Intent intent6 = new Intent(getActivity(), AlarmReceiver.class);
					startActivity(intent6);
					break;
				case 7:
					Intent intent7 = new Intent(getActivity(), About.class);
					startActivity(intent7);
					break;
				/**case 7:
					Intent intent7 = new Intent(getActivity(), DonationsActivity.class);
					startActivity(intent7);
					break;*/
				}
			}
		});
	}
	
	//TODO: funktioniert nicht mehr
	/**
	 * Der Tab ï¿½bernimmt die Aktionen des Tabhost fï¿½r Menu und Back Button
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// is activity withing a tabactivity
		if (getActivity().getParent() != null) {
			return getActivity().getParent().onKeyDown(keyCode, event);
		}
		return false;
	}
}
