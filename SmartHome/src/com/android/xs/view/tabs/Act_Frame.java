package com.android.xs.view.tabs;

import java.util.LinkedList;
import java.util.List;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.controller.usage.MakroController;
import com.android.xs.model.device.Actuator;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.Function;
import com.android.xs.model.device.components.Makro;
import com.android.xs.model.device.components.MakroFunction;
import com.android.xs.model.device.components.XS_Object;
import com.android.xs.model.error.XsError;
import com.android.xs.view.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Die Activity fï¿½r den Frame der Actuatoren. eine Liste aller Aktuatoren wird
 * erstellt und fï¿½r jeden ein ToggleButton erzeugt. Diesem wird dann ein
 * Listener angebunden und die Liste aller Buttons angezeigt
 * 
 * @author Viktor Mayer
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class Act_Frame extends Fragment {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	// Das Xsone Objekt fï¿½r diese Aktivity
	private Xsone myXsone;
	// Eine Liste von Views, welche mit der Anzahl ausgelesener Aktuatoren
	// angepasst und erweitert wird. Beinhaltet Schalter etc
	private LinkedList<View> object_list = new LinkedList<View>();
	private LinkedList<Actuator> act_list = new LinkedList<Actuator>();
	private TableLayout table;
	private TableRow row;
	private TextView tv;
	private ProgressDialog dialog;
	private Actuator longClickAct;
	private int old_size = 0;
	private View toBeUpdated = null;
	private List<View> views = new LinkedList<View>();

	private final int ID_BUFFER = 500;

	public static Activity activity;

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		// Das Grundlayout des tabs ist ein TableLayout, welches Scrollbar ist.
		return inflater.inflate(R.layout.tab_table_layout, container, false);
	}

	/**
	 * Die OnCreate Funtion stellt den Einstiegspunkt dieser Activity dar. Alle
	 * Aktuatoren werden nochmals einzeln aus der XS1 ausgelesen (Zustand etc)
	 * um die einzelnen ToggleButtons und die Beschriftung entsprechend setzen
	 * zu kï¿½nnen.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();
	}

	/**
	 * In der OnResume Funktion werden die Views von den XS1 Daten aktualisiert,
	 * falls WIFI aktiviert ist, sonst nicht, um Netzlast zu sparen
	 */
	@SuppressWarnings("unchecked")
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Act_Frame.activity = getActivity();

		// falls sich die Lï¿½nge der Aktuatorliste geï¿½ndert hat muss dennoch
		// upgedatet werden. Zuerst werden die Aktuatoren neu ausgelesen
		act_list.clear();
		for (XS_Object o : myXsone.getMyActuatorList()) {
			if (!o.getType().equals("disabled")) {
				// Der Liste der Aktuatoren fï¿½r den AddObjectsProzess hinzu
				// fï¿½gen
				act_list.add((Actuator) o);
			}
		}
		// und die Lï¿½nge ï¿½berprï¿½ft
		if (act_list.size() != old_size) {
			// Ladevorgang anzeigen
			dialog = ProgressDialog.show(getActivity(), "", "Aktuatoren aktualisieren...", true, false);
			dialog.show();
			// den Add Objects Prozess starten, der die Buttons ausliest und
			// setzt
			new AddObjects().execute(act_list);
			old_size = act_list.size();
		} else {
			// Die stï¿½ndige Prï¿½fung der Buttons erfolgt nur bei eingeschaltetem
			// WLAN und wenn nich alles aktualisiert wurde wegen hocher Netzlast
			WifiManager wifi = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
			if (wifi.isWifiEnabled() || !RuntimeStorage.isStatusValid()) {
//				update_all();
				new AddObjects().execute(act_list);
				// Status ist nun wieder valide
				RuntimeStorage.setStatusValid(true);
			} else
				new AddObjects().onPostExecute(object_list);
		}
	}

//	/**
//	 * update all ist eine ausgelagerte Funktion, welche alle gelisteten
//	 * Schalter Dimmer etc aktualisiert
//	 */
//	private void update_all() {
//		// jedes View wird geholt und der Status entsprechende dem Actuator
//		// Objekt gesetzt
//		for (View v : object_list) {
//			update_view(v);
//
//		}
//		// new AddObjects().onPostExecute(object_list);
//	}
//
	/**
	 * update view aktualisiert einen ï¿½bergebenen view indem der Zustand des
	 * zugehï¿½reigen Aktuators neu geladen wird
	 * 
	 * @param v
	 *            - der zu aktualisierende View
	 */
	private void update_view(View v) {
		Actuator actual;
		Actuator comparator = new Actuator();
		comparator.setName((String) v.getTag(R.string.obj_name));
		int id = act_list.indexOf(comparator);
		actual = act_list.get(id);
		// und aktualisert falls nicht disabled, da sonst stets die
		// Maximalzahl abgefragt wird
		if (!actual.getType().equals("disabled")) {
			actual.update();
			// nun kann der View entsprechend gesetzt werden
			if (actual.isDimmable()) {
				((SeekBar) v).setProgress((int) actual.getValue());
				 //update Dimmers TextView
				 for (View view : views){
				 if (view.getId() == v.getId()+ID_BUFFER)
				 ((TextView)view).setText(actual.getName() + " (" +
				 Double.valueOf(actual.getValue()).intValue() + "%)");
				 }
				 TextView tv = (TextView) getActivity().findViewById(v.getId()
				 + ID_BUFFER);
				 tv.setText(actual.getName() + " (" +
				 Double.valueOf(actual.getValue()).intValue() + "%)");
			} else if (actual.getType().equals("temperature")) {
				// Mit der Funktion (x -10)/2 werden die Temperaturwerte
				// zwischen 10 und 34 Grad auf die Listenwerte des
				// Spinners 0-10 gemappt
				int selection = (Double.valueOf(actual.getValue()).intValue() - 10) / 2;
				if (selection > 12)
					selection = 12;
				if (selection < 0)
					selection = 0;
				((Spinner) v).setSelection(selection);
			} else {
				if (actual.getValue() > 99)
					((ToggleButton) v).setChecked(true);
				else
					((ToggleButton) v).setChecked(false);
			}
		}
	}

	/**
	 * Der Prozess baut Die Aktor Objekt efï¿½r den View auf, je nach Typ. Hier
	 * kï¿½nnen zukï¿½nftige neue Typen erweitert werden. Der Typ wird anhand des
	 * Namens abgefragt, da dieser vom XS1 definiert ist. Im PostExecute wird
	 * dann die generierte Liste ausgegeben.
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class AddObjects extends AsyncTask<LinkedList<Actuator>, Boolean, LinkedList<View>> {
		/**
		 * Der Parameter zur Prï¿½fung der Verbindung
		 */
		private boolean check = true;

		/**
		 * Hier wird anhand des Namens eine Liste mit zu den Aktoren passenden
		 * Views erstellt und zu dem Steuerelement gleich die entsprechende
		 * Funktion hinterlegt. Diese werden an in einer Liste gespeichert und
		 * an PostExecute weiter gereicht zur Ausgabe.
		 */
		@Override
		protected LinkedList<View> doInBackground(LinkedList<Actuator>... acts) {
			Looper.prepare();

			LinkedList<View> views = new LinkedList<View>();
			// Alle Objekte der Liste werden betrachet
			for (final Actuator rem : acts[0]) {

				/**
				 * Fï¿½r den Fall "Dimmbar" muss ein Schieberegler zurï¿½ck gegeben
				 * werden......................................................
				 */
				if (rem.isDimmable()) {
					final SeekBar seek = new SeekBar(getActivity());
					seek.setId(rem.getNumber());
					seek.setMinimumWidth(250);

					// der Seekbar wird ein Listener hinzugefï¿½gt
					seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
							// lï¿½dt der Dialog gerade stimmt der Kontext nicht!
							// der Text darf dann nicht neu gesetzt werden!
							if (!dialog.isShowing()) {
								// den Textview aktualisieren direkt bei der
								// Bewegung
								TextView tv = (TextView) getActivity().findViewById(seekBar.getId() + ID_BUFFER);
								// nï¿½tig, da bei erstem Aufruf noch nicht
								// vorhanden!!
								if (tv != null)
									tv.setText(rem.getName() + " (" + seekBar.getProgress() + "%)");
							}
						}

						public void onStartTrackingTouch(SeekBar seekBar) {

						}

						/**
						 * beim ï¿½ndern des Wertes wird dieser gesendet
						 */
						public void onStopTrackingTouch(SeekBar seekBar) {
							if (rem.setValue(seek.getProgress(), true)) {
								// Der Neue alte Wert fï¿½r die Seekbar
								seek.setTag(R.string.tag_seekbar, seek.getProgress());
								// den Textview aktualisieren
								TextView tv = (TextView) getActivity().findViewById(seekBar.getId() + ID_BUFFER);
								tv.setText(rem.getName() + " (" + Double.valueOf(rem.getValue()).intValue() + "%)");
								// Makro aufzeichnen falls eingestellt
								makro_call(rem, seek.getProgress());
							} else {
								seek.setProgress((Integer) seek.getTag(R.string.tag_seekbar));
								XsError.printError(getActivity().getBaseContext());
							}
						}
					});

					// Zustand auslesen und Schieberegler setzen
					if (!rem.update())
						check = false;
					seek.setProgress((int) rem.getValue());
					seek.setTag(rem.getName() + " (" + Double.valueOf(rem.getValue()).intValue() + "%)");
					// der alte Stand falls zurï¿½ck gesetzt werden muss
					// (Verbindungfehler)
					seek.setTag(R.string.tag_seekbar, seek.getProgress());
					// Wichtig fï¿½r die spï¿½tere suche beim Longclick auf den Text
					seek.setTag(R.string.obj_name, rem.getName());
					views.add(seek);
				}

				/**
				 * Fï¿½r den Fall Temperatursteuerung wird ein Spinner
				 * ausgegeben.......................................
				 */
				else if (rem.getType().equals("temperature")) {
					// Die Werte fï¿½r den Temperatur Spinner setzen
					String[] vals = { "10", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34" };
					// Den Spinner anlegen
					final Spinner temp_spin = new Spinner(getActivity());
					// Name und ID setzen
					temp_spin.setTag(rem.getName());
					temp_spin.setId(rem.getNumber());
					// Den Adapter für den Spinner anlegen
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, vals);
					// und setzen
					temp_spin.setAdapter(adapter);
					// Den Listener für Änderungen setzen
					temp_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
						public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
							// Der gesetzte Wert wird geholt
							String selected_val = (String) parent.getItemAtPosition(pos);
							int val = Integer.parseInt(selected_val);
							// Den Wert setzen
							if (rem.setValue(val, true)) {
								// Den alten Wert neu setzen
								temp_spin.setTag(R.string.tag_spin, pos);
								// Makro speichern falls eingestellt
								makro_call(rem, val);
							} else {
								// den alten Wert wieder herstellen
								temp_spin.setSelection((Integer) temp_spin.getTag(R.string.tag_spin));
								XsError.printError(getActivity().getBaseContext());
							}
						}

						public void onNothingSelected(AdapterView<?> parent) {
						}
					});
					if (!rem.update())
						check = false;
					// Mit der Funktion (x -10)/2 werden die Temperaturwerte
					// zwischen 10 und 34 Grad auf die Listenwerte des Spinners
					// 0-10 gemappt
					int selection = (Double.valueOf(rem.getValue()).intValue() - 10) / 2;
					if (selection > 12)
						selection = 12;
					if (selection < 0)
						selection = 0;
					temp_spin.setSelection(selection);
					// Der alte Wert, der gesetzt wird bei Verbindzngsfehlern
					temp_spin.setTag(R.string.tag_spin, selection);
					// Wichtig für die spätere suche beim Longclick auf den Text
					temp_spin.setTag(R.string.obj_name, rem.getName());
					views.add(temp_spin);

				}

				/**
				 * Fï¿½r alle ï¿½brigen Fï¿½lle muss ein Button angelegt
				 * werden...............................................
				 */
				else {
					final ToggleButton tbutton = new ToggleButton(getActivity());
					tbutton.setWidth(150);
					tbutton.setGravity(Gravity.CENTER_VERTICAL);
					tbutton.setId(rem.getNumber());
					tbutton.setTag(rem.getName());
					// Wichtig für die spätere suche beim Longclick auf den Text
					tbutton.setTag(R.string.obj_name, rem.getName());
					tbutton.setTextOn("Schalter an");
					tbutton.setTextOff("Schalter aus");
					// dem Button wird ein Listener aller AktuatorButtons hinzu
					// gefügt
					tbutton.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if (tbutton.isChecked()) {
								if (rem.setValue(100.0, true))
									makro_call(rem, 100.0);
								else {
									tbutton.setChecked(false);
									XsError.printError(getActivity().getBaseContext());
								}
							} else {
								if (rem.setValue(0.0, true))
									makro_call(rem, 0.0);
								else {
									tbutton.setChecked(true);
									XsError.printError(getActivity().getBaseContext());
								}
							}
						}
					});

					// Zustand auslesen und den Button entsprechend setzen. Bei
					// Fehler wird ein entsprechender Hinweistext ausgegeben,
					// das Programm aber fortgesetzt
					if (!rem.update())
						check = false;
					boolean b_active = false;
					if (rem.getValue() > 99)
						b_active = true;
					tbutton.setChecked(b_active);

					views.add(tbutton);
				}
			}
			return views;
		}

		/**
		 * 
		 */
		protected void onPostExecute(LinkedList<View> result) {
			super.onPostExecute(result);
			// Die Globale Objektliste setzen für zukünftige Änderungen
			object_list = result;

			// das bestehende Layout für den Tab holen
			table = (TableLayout) getActivity().findViewById(R.id.tab_table);
			table.removeAllViews();

			int count = 0;
			// alle angelegten Views ausgeben
			for (View obj : object_list) {

				// eine neue Zeile anlegen
				row = new TableRow(getActivity());
				row.setVerticalGravity(Gravity.CENTER);
				row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				row.setId(count + 999);

				// einen neuen Textview mit dem Namen des Buttons anlegen
				tv = new TextView(getActivity());
				tv.setWidth(350);
				tv.setHeight(70);
				// id der TexteObjekte darf sich nicht ï¿½berschneiden mit der der
				// übrigen Objekte!
				tv.setId(obj.getId() + ID_BUFFER);
				tv.setPadding(20, 0, 0, 0);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				tv.setTextSize(20);
				// Der Object Tag enthält den Namen zum Remote Objekt
				tv.setText((String) obj.getTag());
				tv.setTag((String) obj.getTag(R.string.obj_name));
				// Beim Click auf Textview werden die für die Aktuatoren
				// festgelegten functions gezeigt
				tv.setClickable(true);
				// Für die LongClickOption registrieren
				registerForContextMenu(tv);

				// den Button und Text zur Zeile hinzu fügen
				row.addView(obj);
				row.addView(tv);

				// die Zeile zum TabelLayout hinzu fügen
				views.add(tv);
				table.addView(row);
				count++;
			}
			if (dialog.isShowing())
				dialog.dismiss();
			if (!check)
				XsError.printError(getActivity().getBaseContext());
		}
	}

	/**
	 * In der Funktion wird das Verhalten beim langen click auf den Text eines
	 * Elementes definiert. Hierbei werden die vorher gesetzten Funktionen zur
	 * Auswahl ausgegeben
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		boolean no_functions = true;
		// den View für den folgenden update setzen
		toBeUpdated = v;
		Actuator comparator = new Actuator();
		comparator.setName((String) v.getTag());
		int id = act_list.indexOf(comparator);
		longClickAct = act_list.get(id);
		// Die Liste mit den Funktionen für das Objekt holen
		List<Function> fn_list = longClickAct.getMyFunction();
		menu.setHeaderTitle("Funktionen");

		int i = 1;
		for (Function f : fn_list) {
			// Für jede Funktion, die nicht disabled ist wird ein Menï¿½punkt
			// gesetzt
			if (!f.getType().equals("disabled")) {
				menu.add(Menu.NONE, i, i, f.getDsc());
				no_functions = false;
				i++;
			}
		}
		if (no_functions)
			menu.add(Menu.NONE, 1, 1, "keine Funktion definiert!");

	}

	/**
	 * In der Funktion wird beschrieben, was passiert, wenn nach dem Langen
	 * Click auf einen Text eine Auswahl (Funktion) gewählt wurde.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean check = true;
		if (!item.getTitle().equals("keine Funktion definiert!")) {
			check = longClickAct.doFunction(item.getItemId());
			// Makrofunktion ggf speichern
			makro_call(longClickAct, item.getItemId());
			// muss ausgefï¿½hrt werden, da sonst die Views nicht mehr ï¿½berein
			// stimmen, jedoch nur bei Erfolg und falls View gesetzt
			if (check && toBeUpdated != null)
				update_view(getActivity().findViewById(toBeUpdated.getId() - ID_BUFFER));
			// sonst wird ein Fehlertext ausgegeben
			else
				XsError.printError(getActivity().getBaseContext());
			toBeUpdated = null;
			return check;
		}
		return check;
	}

	/**
	 * Aufzeichnung der Buttons. Diese werden mit Double Wert aufgrufen
	 * 
	 * @param a
	 *            - Der Aktuator
	 * @param val
	 *            - der zu sendende Wert
	 */
	private void makro_call(final Actuator a, final double val) {
		// Makro aufzeichnung, falls Aufzeichnung
		// gestartet. in dem Fall ist m != null
		Makro m = MakroController.getInstance().MakroEnabled();
		if (m != null) {
			m.add_function(new MakroFunction(a, val));
		}
	}

	/**
	 * Aufzeichnung der Funktionen. Es wird nach Datentyp unterschieden.
	 * Funktionen werden mit einem Integer aufgerufen
	 * 
	 * @param a
	 *            - Der Aktuator
	 * @param func
	 *            - Die Funktionsnummer
	 */
	private void makro_call(final Actuator a, final int func) {
		// Makro aufzeichnung, falls Aufzeichnung
		// gestartet. in dem Fall ist m != null
		Makro m = MakroController.getInstance().MakroEnabled();
		if (m != null) {
			m.add_function(new MakroFunction(a, func));
		}
	}

	/**
	 * speichern der Views um sie beim neu-laden des Tabs wiederherstellen zu
	 * können
	 */
	public void onDestroyView() {
		super.onDestroyView();
		// myContainer.removeAllViews();
		for (View v : object_list) {
			((ViewGroup) v.getParent()).removeAllViews();
		}

	}

	/**
	 * Der Tab übernimmt die Aktionen des Tabhost für Menu und Back Button
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// is activity withing a tabactivity
		if (getActivity().getParent() != null) {
			return getActivity().getParent().onKeyDown(keyCode, event);
		}
		return false;
	}
}
