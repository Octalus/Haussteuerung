package com.android.xs.view.add;

import java.util.ArrayList;
import java.util.Arrays;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.controller.usage.Learner;
import com.android.xs.controller.usage.Translator;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.RF_System;
import com.android.xs.model.error.XsError;
import com.android.xs.view.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TableRow;
import android.widget.TextView;

public class Add_Act extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private ArrayList<String> system = new ArrayList<String>();
	String[] fn_names = { "", "AN", "AUS", "AUF", "AB", "OEFFNEN", "SCHLIESSEN", "HOCH", "RUNTER", "ZU", "LINKS", "RECHTS", "OBEN", "UNTEN", "VOR",
			"ZURUECK", "MAKRO", "LERNEN", "AN/AUS", "DIMMEN", "HELLER", "DUNKLER" };
	String[] percent = new String[100];
	String[] degree = new String[31];
	// Das Xsone Objekt für diese Aktivity
	private Xsone myXsone;
	// Dialog für Ladevorgang
	private Dialog dialog;
	// Zum anzeigen variabel benï¿½tigter Felder
	private OnItemSelectedListener inputUnhider;
	// Liste mit allen Funktionsnamen, da Temp und Prozent dynamisch erzeugt
	// werden
	ArrayList<String> fn_names_all = new ArrayList<String>();
	// Die Elemente der Gui
	private ArrayAdapter<String> adapter_typ_typ;
	private Spinner typ_sp_typ1;
	private Spinner typ_sp_typ2;
	private Spinner typ_sp_typ3;
	private Spinner typ_sp_typ4;
	private Spinner sys_sp;
	private Spinner typ_sp;
	private EditText address;
	private EditText hc1;
	private EditText hc2;
	// Die Elemente werden je nach Wert des Spinner aus bzw ein geblendet
	private LinearLayout[] layouts_val = new LinearLayout[4];
	private LinearLayout[] layouts_tim = new LinearLayout[4];
	private TableRow[] rows = new TableRow[4];

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

		// Maske für hinzufügen anzeigen
		setContentView(R.layout.add_act_frame);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();

		// Prozentangaben befüllen für den Spinner
		int i;
		for (i = 1; i < 101; i++) {
			percent[i - 1] = i + "%";
		}
		// Grad befüllen für den Spinner
		for (i = 5; i < 36; i++) {
			degree[i - 5] = i + "°C";
		}
		// Die Gesamtliste für Funktionsnamen erstellen
		fn_names_all.addAll(Arrays.asList(fn_names));
		fn_names_all.addAll(Arrays.asList(percent));
		fn_names_all.addAll(Arrays.asList(degree));

		// Die Eingabefelder für Wert, die dynamisch ein bzw ausgeblendet werden
		layouts_val[0] = (LinearLayout) findViewById(R.id.linearLayout_val1);
		layouts_val[1] = (LinearLayout) findViewById(R.id.linearLayout_val2);
		layouts_val[2] = (LinearLayout) findViewById(R.id.linearLayout_val3);
		layouts_val[3] = (LinearLayout) findViewById(R.id.linearLayout_val4);

		layouts_tim[0] = (LinearLayout) findViewById(R.id.linearLayout_tim1);
		layouts_tim[1] = (LinearLayout) findViewById(R.id.linearLayout_tim2);
		layouts_tim[2] = (LinearLayout) findViewById(R.id.linearLayout_tim3);
		layouts_tim[3] = (LinearLayout) findViewById(R.id.linearLayout_tim4);

		hc1 = (EditText) findViewById(R.id.text_act_hc1);
		hc2 = (EditText) findViewById(R.id.text_act_hc2);
		address = (EditText) findViewById(R.id.text_act_address);

		// Die Rows die dynamisch ein bzw ausgeblendet werden
		rows[0] = (TableRow) findViewById(R.id.row_fnct1);
		rows[1] = (TableRow) findViewById(R.id.row_fnct2);
		rows[2] = (TableRow) findViewById(R.id.row_fnct3);
		rows[3] = (TableRow) findViewById(R.id.row_fnct4);

		// Der Event Listener für den Spinner typ blendet bei Auswahl von
		// "an,warten,aus" sowie "absolut" die nötigen Felder ein
		inputUnhider = new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int num = (Integer) ((Spinner) arg0).getTag();
				String chosen = "";
				if (arg1 != null)
					chosen = ((TextView) arg1).getText().toString();
				if (num >= 0 && num < 10) {
					// Hier wird Wert und Zeit eingeblendet
					if (chosen.equals("absolut")) {
						layouts_val[num].setVisibility(LinearLayout.VISIBLE);
						layouts_tim[num].setVisibility(LinearLayout.VISIBLE);
						rows[num].setVisibility(TableRow.VISIBLE);
					}
					// Hier wird Zeit eingeblendet
					else if (chosen.equals("on_wait_off")) {
						rows[num].setVisibility(TableRow.VISIBLE);
						layouts_tim[num].setVisibility(LinearLayout.VISIBLE);
						layouts_val[num].setVisibility(LinearLayout.GONE);
					}
					// Hier wird Wert eingeblendet
					else if (chosen.equals("auto") || chosen.equals("manual")) {
						rows[num].setVisibility(TableRow.VISIBLE);
						layouts_tim[num].setVisibility(LinearLayout.GONE);
						layouts_val[num].setVisibility(LinearLayout.VISIBLE);
					}
					// Sonst wird alles aus geblendet
					else {
						rows[num].setVisibility(TableRow.GONE);
					}
				} else {
					// Für das FHT sytem werden gesonderte Typen benötigt
					// (Temperatur)
					for (RF_System s : RuntimeStorage.getSystems()) {
						if (s.getName().equals(chosen)) {
							adapter_typ_typ = new ArrayAdapter<String>(Add_Act.this, android.R.layout.simple_spinner_item, s.getFunction());
							typ_sp_typ1.setAdapter(adapter_typ_typ);
							typ_sp_typ2.setAdapter(adapter_typ_typ);
							typ_sp_typ3.setAdapter(adapter_typ_typ);
							typ_sp_typ4.setAdapter(adapter_typ_typ);
						}
					}
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}

		};

		// Die Spinner befüllen für Typ und System
		typ_sp = (Spinner) findViewById(R.id.spinner_act_typ);
		ArrayAdapter<String> adapter_typ = new ArrayAdapter<String>(Add_Act.this, android.R.layout.simple_spinner_item, RuntimeStorage.getAct_types());
		typ_sp.setAdapter(adapter_typ);

		// System Spinner
		for (RF_System s : RuntimeStorage.getSystems()) {
			system.add(s.toString());
		}
		sys_sp = (Spinner) findViewById(R.id.spinner_act_sys);
		ArrayAdapter<String> adapter_sys = new ArrayAdapter<String>(Add_Act.this, android.R.layout.simple_spinner_item, system);
		sys_sp.setAdapter(adapter_sys);
		sys_sp.setOnItemSelectedListener(inputUnhider);
		sys_sp.setTag(10);

		// Funktionsnamen Spinner befüllen
		final Spinner typ_sp_fn1 = (Spinner) findViewById(R.id.spinner_act_fn_one);
		ArrayAdapter<String> adapter_typ_fn = new ArrayAdapter<String>(Add_Act.this, android.R.layout.simple_spinner_item, fn_names_all);
		typ_sp_fn1.setAdapter(adapter_typ_fn);
		// Funktionsspinner zwei
		final Spinner typ_sp_fn2 = (Spinner) findViewById(R.id.spinner_act_fn_two);
		typ_sp_fn2.setAdapter(adapter_typ_fn);
		// Funktionsspinner drei
		final Spinner typ_sp_fn3 = (Spinner) findViewById(R.id.spinner_act_fn_three);
		typ_sp_fn3.setAdapter(adapter_typ_fn);
		// Funktionsspinner vier
		final Spinner typ_sp_fn4 = (Spinner) findViewById(R.id.spinner_act_fn_four);
		typ_sp_fn4.setAdapter(adapter_typ_fn);

		// Funktionstyp Spinner befüllen und den Listener hinzufügen
		typ_sp_typ1 = (Spinner) findViewById(R.id.spinner_act_typ_one);
		typ_sp_typ1.setTag(0);
		typ_sp_typ1.setOnItemSelectedListener(inputUnhider);
		// Typ Spinner zwei
		typ_sp_typ2 = (Spinner) findViewById(R.id.spinner_act_typ_two);
		typ_sp_typ2.setTag(1);
		typ_sp_typ2.setOnItemSelectedListener(inputUnhider);
		// Typ Spinner drei
		typ_sp_typ3 = (Spinner) findViewById(R.id.spinner_act_typ_three);
		typ_sp_typ3.setTag(2);
		typ_sp_typ3.setOnItemSelectedListener(inputUnhider);
		// Typ Spinner vier
		typ_sp_typ4 = (Spinner) findViewById(R.id.spinner_act_typ_four);
		typ_sp_typ4.setTag(3);
		typ_sp_typ4.setOnItemSelectedListener(inputUnhider);

		// den Button zum Senden holen
		Button button = (Button) findViewById(R.id.button_act_ok);
		// dem Button die Click Action hinzufügen
		button.setOnClickListener(new OnClickListener() {

			// der Button wurde gedrückt
			public void onClick(View v) {
				// Ladevorgang anzeigen
				dialog = ProgressDialog.show(Add_Act.this, "", "Aktuator anlegen...", true, false);
				dialog.show();

				// Das Daten array für die Eingabewerte
				String[] data = new String[22];
				// Das Array wird mit den Werten der Felder befüllt
				data[0] = Translator.translate(typ_sp.getSelectedItem().toString());
				data[1] = sys_sp.getSelectedItem().toString();
				EditText name = (EditText) findViewById(R.id.text_act_name);
				data[2] = name.getText().toString();
				data[3] = hc1.getText().toString();
				data[4] = hc2.getText().toString();
				data[5] = address.getText().toString();
				// ab hier Funktionen
				data[6] = typ_sp_fn1.getSelectedItem().toString();
				data[7] = Translator.translate(typ_sp_typ1.getSelectedItem().toString());
				EditText val1 = (EditText) findViewById(R.id.text_act_val1);
				data[8] = val1.getText().toString();
				EditText tim1 = (EditText) findViewById(R.id.text_act_tim1);
				data[9] = tim1.getText().toString();
				// Funktion zwei
				data[10] = typ_sp_fn2.getSelectedItem().toString();
				data[11] = Translator.translate(typ_sp_typ2.getSelectedItem().toString());
				EditText val2 = (EditText) findViewById(R.id.text_act_val2);
				data[12] = val2.getText().toString();
				EditText tim2 = (EditText) findViewById(R.id.text_act_tim2);
				data[13] = tim2.getText().toString();
				// Funktion drei
				data[14] = typ_sp_fn3.getSelectedItem().toString();
				data[15] = Translator.translate(typ_sp_typ3.getSelectedItem().toString());
				EditText val3 = (EditText) findViewById(R.id.text_act_val3);
				data[16] = val3.getText().toString();
				EditText tim3 = (EditText) findViewById(R.id.text_act_tim3);
				data[17] = tim3.getText().toString();
				// Funktion vier
				data[18] = typ_sp_fn4.getSelectedItem().toString();
				data[19] = Translator.translate(typ_sp_typ4.getSelectedItem().toString());
				EditText val4 = (EditText) findViewById(R.id.text_act_val4);
				data[20] = val4.getText().toString();
				EditText tim4 = (EditText) findViewById(R.id.text_act_tim4);
				data[21] = tim4.getText().toString();

				// Starten
				new addAct().execute(data);
			}
		});

		// den Button zum Lernen holen
		Button button_learn = (Button) findViewById(R.id.button_act_learn);
		// dem Button die Click Action hinzufügen
		button_learn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Das system Holen
				String sys = "virtual";
				sys = sys_sp.getSelectedItem().toString();

				if (!sys.equals("virtual")) {

					Intent learner = new Intent(Add_Act.this, Learner.class);

					learner.putExtra("data", sys);
					startActivityForResult(learner, 10);
				} else
					Toast.makeText(getBaseContext(), "System virtual nicht möglich!", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	/**
	 * Reads data scanned by user and returned by smarthome-xs1
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (null != data && data.getExtras() != null) {
			String result = data.getExtras().getString("status");

			if (result.contains("ok")) {
				ArrayList<String> values = data.getExtras().getStringArrayList("values");

				// the values contain 1. type 2. system 3. adress 4. hc1 5. hc2
				// typ setzen
				String type = "";
				type = values.get(0);
				int num = RuntimeStorage.getAct_types().indexOf(type);
				typ_sp.setSelection(num);

				// system setzen
				String sys = "";
				sys = values.get(1);
				num = system.indexOf(sys);
				sys_sp.setSelection(num);

				// adresse setzen
				address.setText(values.get(2));
				// hc1
				hc1.setText(values.get(3));
				// hc2
				hc2.setText(values.get(4));
			}
		} else
			Toast.makeText(this, "keine Daten empfangen!", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Ausgelagerter Task um Dialog anzeigen zu können. Der neue Aktuator wird
	 * an das XS1 gesendet
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class addAct extends AsyncTask<String, Boolean, Boolean> {

		/**
		 * 
		 */
		@Override
		protected Boolean doInBackground(String... data) {
			// myXsone.add_RemObj(rem_obj);
			return myXsone.add_Actuator(data);
		}

		/**
		 * 
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result) {
				Toast.makeText(Add_Act.this, "Aktuator erfolgreich angelegt..", Toast.LENGTH_LONG).show();
				finish();
			}
			// Sonst erfolgt ein Hinweistext
			else {
				XsError.printError(getBaseContext());
				return;
			}
		}
	}
}