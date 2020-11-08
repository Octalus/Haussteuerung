package com.android.xs.view.setting;

import java.util.Arrays;
import java.util.LinkedList;

import com.android.xs.view.R;
import com.android.xs.view.add.Add_Act;
import com.android.xs.view.add.Add_Script;
import com.android.xs.view.add.Add_Sens;
import com.android.xs.view.add.Add_Tim;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Add extends ListActivity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/
	private String[] options = { "Aktuator", "Sensor", "Timer", "Skript" };
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
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
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
					Intent intent0 = new Intent(Add.this, Add_Act.class);
					startActivity(intent0);
					break;
				case 1:
					Intent intent1 = new Intent(Add.this, Add_Sens.class);
					startActivity(intent1);
					break;
				case 2:
					Intent intent2 = new Intent(Add.this, Add_Tim.class);
					startActivity(intent2);
					break;
				case 3:
					Intent intent3 = new Intent(Add.this, Add_Script.class);
					startActivity(intent3);
					break;
				}
			}
		});
	}
}
