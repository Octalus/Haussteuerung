package com.android.xs.view.add;

import java.util.ArrayList;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * 
 * @author Viktor Mayer
 * 
 */
public class Add_Sens extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/
	// Das Xsone Objekt für diese Aktivity
	private Xsone myXsone;
	// Dialog für Ladevorgang
	private Dialog dialog;
	// Liste der Systeme (wird aus dem XS1 ausgelesen)
	private ArrayList<String> system = new ArrayList<String>();
	private Spinner sys_sp;
	private Spinner typ_sp;
	private EditText hc1;
	private EditText hc2;
	private EditText address;
	
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

		// Maske für IP und Passwort anzeigen
		setContentView(R.layout.add_sens_frame);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();
		
		typ_sp = (Spinner) findViewById(R.id.spinner_sens_typ);
		sys_sp = (Spinner) findViewById(R.id.spinner_sens_sys);
		hc1 = (EditText) findViewById(R.id.text_sens_hc1);
		hc2 = (EditText) findViewById(R.id.text_sens_hc2);
		address = (EditText) findViewById(R.id.text_sens_addr);
		
		// Die Spinner befüllen
		// Typ Spinner
		
		ArrayAdapter<String> adapter_typ = new ArrayAdapter<String>(Add_Sens.this, android.R.layout.simple_spinner_item,
				RuntimeStorage.getSens_types());
		typ_sp.setAdapter(adapter_typ);
		// System Spinner
		for (RF_System s : RuntimeStorage.getSystems()) {
			system.add(s.toString());
		}
		ArrayAdapter<String> adapter_sys = new ArrayAdapter<String>(Add_Sens.this, android.R.layout.simple_spinner_item, system);
		sys_sp.setAdapter(adapter_sys);

		// den Button holen
		Button button = (Button) findViewById(R.id.button_sens);
		// dem Button die Click Action hinzu fügen
		button.setOnClickListener(new OnClickListener() {

			// der Button wurde gedrückt
			public void onClick(View v) {
				// Ladevorgang anzeigen
				dialog = ProgressDialog.show(Add_Sens.this, "", "Sensor anlegen...", true, false);
				dialog.show();

				// Daten holen und zu String konvertieren
				String[] sensData = new String[8];
				sensData[0] = Translator.translate(typ_sp.getSelectedItem().toString());
				sensData[1] = sys_sp.getSelectedItem().toString();
				EditText fak = (EditText) findViewById(R.id.text_sens_faktor);
				sensData[2] = fak.getText().toString();
				EditText off = (EditText) findViewById(R.id.text_sens_offset);
				sensData[3] = off.getText().toString();
				EditText name = (EditText) findViewById(R.id.text_sens_name);
				sensData[4] = name.getText().toString();
				sensData[5] = hc1.getText().toString();
				sensData[6] = hc2.getText().toString();
				sensData[7] = address.getText().toString();

				// Starten
				new addSens().execute(sensData);
			}
		});

		// den Button zum Lernen holen
		Button button_learn = (Button) findViewById(R.id.button_sens_learn);
		// dem Button die Click Action hinzufügen
		button_learn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Das system Holen
				String sys = "virtual";
				sys = sys_sp.getSelectedItem().toString();

				if (!sys.equals("virtual")) {

					Intent learner = new Intent(Add_Sens.this, Learner.class);

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
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class addSens extends AsyncTask<String, Boolean, Boolean> {

		/**
		 * 
		 */
		@Override
		protected Boolean doInBackground(String... data) {
			// Der Befehl wird weiter gereicht zur XSone Objekt
			return myXsone.add_Sensor(data);
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
				Toast.makeText(Add_Sens.this, "Sensor erfolgreich angelegt..", Toast.LENGTH_LONG).show();
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
