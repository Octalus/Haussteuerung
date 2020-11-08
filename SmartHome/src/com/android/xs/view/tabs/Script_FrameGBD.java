package com.android.xs.view.tabs;

import java.util.LinkedList;
import java.util.List;

import com.android.xs.controller.remote.Http;
import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Script;
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
 * Die Activity aller Timer. Jeder Timer wird aus dem Xsone Objekt ausgelesen
 * und ein String mit dem Wert des Timerss generiert. Dieses wird in eine Liste
 * von TextViews hinzu gefügt und dieses dann ausgegeben. Bei keinem Timer wird
 * ein Info Text ausgegeben. Das erste Element ist ein PullToRefresh bzw bei
 * wenigen Elementen TapToRefresh
 * 
 * @author Viktor Mayer
 * 
 */
public class Script_FrameGBD extends ListActivity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	// Das Xsone Objekt für diese Aktivity
	private Xsone myXsone;
	// Liste der auszugebenden Strings
	private LinkedList<String> script_list;

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
		setContentView(R.layout.pull_to_refresh);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();

		// übernommen aus markupartist
		// Set a listener to be invoked when the list should be refreshed.
		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {
					public void onRefresh() {
						// Do work to refresh the list here.
						new GetDataTask().execute();
					}
				});

		// Die Liste der timer holen
		script_list = get_scriptList();

		// Die Liste ausgeben
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item_medium, script_list);
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
	private LinkedList<String> get_scriptList() {
		// Die Liste Der Sensordaten als String
		LinkedList<String> script_dataList = new LinkedList<String>();

		// Für jeden vorhandenen Sensor wird ein String angelegt
		for (XS_Object rem : myXsone.getMyScriptList()) {

			if (!rem.getType().equals("disabled")) {
				// Cast zum Timer
				Script scrpt = (Script) rem;
				// Die daten werden für die Darstellung ausgelesen
				String name = scrpt.getName();
				String typ = scrpt.getType();
				// Den Text für die Ausgabe setzen falls timer nicht disabled
				script_dataList.add(name + " (" + typ + ")");
			}
		}

		// Falls keine Sensoren vorhanden wird ein Info Text gesetzt
		if (script_dataList.size() == 0) {
			script_dataList.add("Keine Skripts definiert!");

		}

		return script_dataList;
	}

	/**
	 * Die GetData Klasse stellt eine Aktivität dar. sie wird durch das Pull
	 * Update gestartet und aktualisiert die Liste der Timer. Die Liste wird
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
			script_list.clear();
			// Liste neu holen
			List<XS_Object> tmp = Http.getInstance().get_list_scripts();
			// falls ein Verbinsungsfehler bestand kommt null zurück
			if (tmp == null) {
				XsError.printError(getBaseContext());
			} else {
				myXsone.add_RemObj(tmp);

				// Die Liste der Sensoren holen
				script_list.addAll(get_scriptList());
				// Call onRefreshComplete when the list has been refreshed.
				((PullToRefreshListView) getListView()).onRefreshComplete();
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
