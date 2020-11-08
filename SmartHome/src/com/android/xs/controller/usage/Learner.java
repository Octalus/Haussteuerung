package com.android.xs.controller.usage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.error.XsError;
import com.android.xs.view.R;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Learner extends ListActivity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/
	Intent result = new Intent();
	private Dialog dialog;
	private Xsone myXsone;
	private ArrayList<String> vals;
	private LinkedList<String> all_result;
	private ArrayAdapter<String> adapter;
	String data = "";

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
		setContentView(R.layout.tab_list_layout);

		myXsone = RuntimeStorage.getMyXsone();

		all_result = new LinkedList<String>();
		adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item_medium, all_result);

		// das system holen
		String system = getIntent().getExtras().getString("data");
		// Ladevorgang anzeigen.. hier werden 30 Sekuden lang Daten empfangen
		dialog = ProgressDialog.show(this, "", "Datenempfang (30s)...", true, false);
		dialog.show();
		new Receive().execute(system);

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				vals = new ArrayList<String>();
				String temp_result = "";
				if (all_result.size() < 1)
					return;
				//das Gewählte Element der Liste lesen
				temp_result = all_result.get((int) id);
				temp_result.trim();

				String system = null;
				String type = null;
				Integer hc1 = null;
				Integer hc2 = null;
				Integer address = null;
				// das gewählte Objet iste in JSON Objekt und kann entsprechend
				// geparst werden
				try {
					JSONObject json = (JSONObject) new JSONTokener(temp_result).nextValue();
					system = (String) json.get("system");
					type = (String) json.get("type");
					hc1 = (Integer) json.get("hc1");
					hc2 = (Integer) json.get("hc2");
					address = (Integer) json.get("address");
				} catch (JSONException e) {
					XsError.printError(getBaseContext(), "Fehler beim Parsen!");
					// e.printStackTrace();
					return;
				}

				// Die Liste mit den gewählten Daten befüllen
				vals.add(type);
				vals.add(system);
				vals.add("" + address);
				vals.add("" + hc1);
				vals.add("" + hc2);
				data = "ok";

				// Daten setzen und zurücksenden
				result.putExtra("status", data);
				result.putStringArrayListExtra("values", vals);
				finish();
			}
		});

	}

	/**
	 * die Funtion beendet die Activity und sendet die Daten per Intent zurück
	 * an den Aufrufer
	 */
	@Override
	public void finish() {
		setResult(RESULT_OK, result);
		super.finish();
	}

	/**
	 * 
	 * Hintergrundtask der die Verbindung zum XS1 aufbaut, da hier 30 Sekunden
	 * eine Verbindung gehalten wird. Danach werden alle Ergebnisse in die Liste
	 * zurück gegeben
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class Receive extends AsyncTask<String, Boolean, LinkedList<String>> {

		/**
		 * 
		 */
		@Override
		protected LinkedList<String> doInBackground(String... data) {
			try {
				// hier wird der Prozess gestartet. Zurück kommt die Liste mit
				// den empfangenen Geräten
				return myXsone.learn(data[0]);
			} catch (IOException e) {
				// e.printStackTrace();
			}
			return null;
		}

		/**
		 * 
		 */
		@Override
		protected void onPostExecute(LinkedList<String> result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result == null) {
				XsError.printError(getBaseContext());
				return;
			}
			all_result = result;
			// ersten und letzen wert entfernen da das nur Füllwerte sind
			all_result.removeFirst();
			all_result.removeLast();
			// die Liste mit den Werten befüllen
			for (String s : result)
				adapter.add(s);
			// den Adapter der Listenansicht setzen
			setListAdapter(adapter);
		}
	}
}
