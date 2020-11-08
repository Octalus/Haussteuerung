package com.android.xs.view.tabs;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import com.android.xs.controller.remote.Http;
import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Sensor;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.XS_Object;
import com.android.xs.model.error.XsError;
import com.android.xs.view.R;
import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;

/**
 * Die Activity aller Sensoren. Jeder Sensor wird aus dem Xsone Objekt
 * ausgelesen und ein String mit dem Wert des Sensors generiert. Dieses wird in
 * eine Liste von Textviews hinzu gefügt und dieses dann ausgegeben. Bei keinem
 * Sensor wird ein Info Text ausgegeben. Das erste Element ist ein PullToRefresh
 * bzw bei wenigen Elementen TapToRefresh
 * 
 * @author Viktor Mayer
 * 
 */
public class Sens_FrameGBD extends ListActivity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	// Das Xsone Objekt für diese Aktivity
	private Xsone myXsone;
	// Liste der auszugebenden Strings
	private LinkedList<String> sens_list;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Die OnCreate Funtion stellt den Einstiegspunkt dieser Activity dar. Alle
	 * Aktuatoren werden nochmals einzeln aus der XS1 ausgelesen (Zustand etc)
	 * um die einzelnen ToggleButtons und die Beschriftung entsprechend setzen
	 * zu können.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Das Grundlayout des tabs ist ein LinearLayout, welches Scrollbar ist.
		setContentView(R.layout.pull_to_refresh);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();

		// Set a listener to be invoked when the list should be refreshed.
		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					public void onRefresh() {
						// Do work to refresh the list here.
						// getListView().setVisibility(View.GONE);
						try {
							new GetDataTask().execute();
						} catch (Exception e) {
							// wtf?
						}
					}
				});

		// Die Liste der Sensoren holen
		sens_list = get_sensList();

		// Die Liste ausgeben
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item_medium, sens_list);
		setListAdapter(adapter);
	}

	/**
	 * Ausgelagerte Funktion, da an mehrerern Stellen verwendet. Liest die Liste
	 * der Sensoren aus dem XS1 neu ein und gibt diese als List von Strings
	 * zurück
	 * 
	 * @return LinkedList<String> Liste der Strings mit Sensordaten.bei keinen
	 *         Sensoren ein Infotext
	 */
	@SuppressWarnings("deprecation")
	private LinkedList<String> get_sensList() {
		// Die Liste Der Sensordaten als String
		LinkedList<String> sensor_dataList = new LinkedList<String>();

		// Für jeden vorhandenen Sensor wird ein String angelegt
		for (XS_Object rem : myXsone.getMySensorList()) {

			// dazu wird geprüft, ob es sich bei dem RemoteObjekt um einen
			// Sensor handelt
			if (!rem.getType().equals("disabled")) {
				// Cast zu Sensor
				Sensor remS = (Sensor) rem;
				// Die Daten werden fï¿½r die Darstellung ausgelesen
				String name = remS.getName();
				String typ = remS.getType();
				double val = remS.getValue();
				String unit = remS.getUnit();
				String status = remS.getStatus();
				Calendar time = Calendar.getInstance();
				// da, Java in Millis hier * 1000!!
				time.setTimeInMillis(remS.getUtime() * 1000);
				String recv = time.getTime().toLocaleString();
				// Den Text fï¿½r die Ausgabe setzen falls typ nicht disabled
				sensor_dataList.add("Empfangszeit: " + recv + "\nSensortyp: " + typ
						+ "\nName: " + name + "\nWert: " + val + unit + "\nStatus: "+status);
			}
		}

		// Falls keine Sensoren vorhanden wird ein Info Text gesetzt
		if (sensor_dataList.size() == 0) {
			sensor_dataList.add("Keine Sensoren definiert!");

		}

		return sensor_dataList;
	}

	/**
	 * Die GetData Klasse stellt eine Aktivität dar. sie wird durch das Pull
	 * Update gestartet und aktualisiert die Liste der Sensoren. Die Liste wird
	 * dabei geleert und eine Neue aus der Abfrage des XS1 erstellt
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Hier darf keine Aktion erfolgen, führt sonst zu einer
			// IllegalStateException
			// Der Listenionhalt darf nur innerhalb des Kontext geändert werden
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			sens_list.clear();
			// Liste neu holen
			List<XS_Object> tmp = Http.getInstance().get_list_sensors();
			// falls ein Verbinsungsfehler bestand kommt null zurück
			if (tmp == null) {
				XsError.printError(getBaseContext());
			} else {
				myXsone.add_RemObj(tmp);

				// Die Liste der Sensoren holen
				sens_list.addAll(get_sensList());

				// Call onRefreshComplete when the list has been refreshed.
				((PullToRefreshListView) getListView()).onRefreshComplete();
				// getListView().setVisibility(View.VISIBLE);
			}
			return;

		}
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
