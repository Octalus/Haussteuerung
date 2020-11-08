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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Die Activity aller Sensoren. Jeder Sensor wird aus dem Xsone Objekt
 * ausgelesen und ein String mit dem Wert des Sensors generiert. Dieses wird in
 * eine Liste von Textviews hinzu gef�gt und dieses dann ausgegeben. Bei keinem
 * Sensor wird ein Info Text ausgegeben. Das erste Element ist ein PullToRefresh
 * bzw bei wenigen Elementen TapToRefresh
 * 
 * @author Viktor Mayer
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class Sens_Frame extends ListFragment {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	// Das Xsone Objekt f�r diese Aktivity
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
	 * 
	 */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		// Das Grundlayout des tabs ist ein TableLayout, welches Scrollbar ist.
        return inflater.inflate(R.layout.pull_to_refresh, container, false);
    }
	
	/**
	 * Die OnCreate Funtion stellt den Einstiegspunkt dieser Activity dar. Alle
	 * Aktuatoren werden nochmals einzeln aus der XS1 ausgelesen (Zustand etc)
	 * um die einzelnen ToggleButtons und die Beschriftung entsprechend setzen
	 * zu k�nnen.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

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
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.list_item_medium, sens_list);
		setListAdapter(adapter);
	}

	/**
	 * Ausgelagerte Funktion, da an mehrerern Stellen verwendet. Liest die Liste
	 * der Sensoren aus dem XS1 neu ein und gibt diese als List von Strings
	 * zur�ck
	 * 
	 * @return LinkedList<String> Liste der Strings mit Sensordaten.bei keinen
	 *         Sensoren ein Infotext
	 */
	private LinkedList<String> get_sensList() {
		// Die Liste Der Sensordaten als String
		LinkedList<String> sensor_dataList = new LinkedList<String>();

		// F�r jeden vorhandenen Sensor wird ein String angelegt
		for (XS_Object rem : myXsone.getMySensorList()) {

			// dazu wird gepr�ft, ob es sich bei dem RemoteObjekt um einen
			// Sensor handelt
			if (!rem.getType().equals("disabled")) {
				// Cast zu Sensor
				Sensor remS = (Sensor) rem;
				// Die Daten werden f�r die Darstellung ausgelesen
				String name = remS.getName();
				String typ = remS.getType();
				double val = remS.getValue();
				String unit = remS.getUnit();
				String status = remS.getStatus();
				Calendar time = Calendar.getInstance();
				// da, Java in Millis hier * 1000!!
				time.setTimeInMillis(remS.getUtime() * 1000);
				@SuppressWarnings("deprecation")
				String recv = time.getTime().toLocaleString();
				// Den Text f�r die Ausgabe setzen falls typ nicht disabled
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
	 * Die GetData Klasse stellt eine Aktivit�t dar. sie wird durch das Pull
	 * Update gestartet und aktualisiert die Liste der Sensoren. Die Liste wird
	 * dabei geleert und eine Neue aus der Abfrage des XS1 erstellt
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			// Hier darf keine Aktion erfolgen, f�hrt sonst zu einer
			// IllegalStateException
			// Der Listenionhalt darf nur innerhalb des Kontext ge�ndert werden
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			sens_list.clear();
			// Liste neu holen
			List<XS_Object> tmp = Http.getInstance().get_list_sensors();
			// falls ein Verbinsungsfehler bestand kommt null zur�ck
			if (tmp == null) {
				XsError.printError(getActivity().getBaseContext());
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

	//TODO: funktioniert nicht mehr
	/**
	 * Der Tab �bernimmt die Aktionen des Tabhost f�r Menu und Back Button
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// is activity withing a tabactivity
		if (getActivity().getParent() != null) {
			return getActivity().getParent().onKeyDown(keyCode, event);
		}
		return false;
	}
}
