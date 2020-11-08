package com.android.xs.view.add;

import java.util.LinkedList;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.AorS_Object;
import com.android.xs.model.device.components.XS_Object;
import com.android.xs.model.error.XsError;
import com.android.xs.view.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 
 * @author Viktor Mayer
 * 
 */
public class Add_Script extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	// Das Xsone Objekt f�r diese Aktivity
	private Xsone myXsone;
	// Dialog f�r Ladevorgang
	private Dialog dialog;
	// Spinner Array
	private String[] script_type = { "disabled", "onchange" };
	// Aktuatorliste f�r den Spinner
	private LinkedList<AorS_Object> obj_list = new LinkedList<AorS_Object>();

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

		// Maske f�r IP und Passwort anzeigen
		setContentView(R.layout.add_script_frame);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();
		
		LinkedList<XS_Object> a_or_s_List = new LinkedList<XS_Object>();
		a_or_s_List.addAll(myXsone.getMyActuatorList());
		a_or_s_List.addAll(myXsone.getMySensorList());
		// Alle Aktuatoren und Sensoren f�r den Spinner holen
		for (XS_Object obj : a_or_s_List) {
			if (!obj.getType().equals("disabled"))
				obj_list.add((AorS_Object) obj);
		}

		// Array mit Namen anlegen f�r Spinner Adapter
		String[] obj_names = new String[obj_list.size()+1];
		obj_names[0] = "";
		int i = 1;
		for (AorS_Object o : obj_list) {
			obj_names[i] = o.getName();
			i++;
		}

		// Die Spinner bef�llen
		// Typ Spinner
		Spinner typ_sp = (Spinner) findViewById(R.id.spinner_script_typ);
		ArrayAdapter<String> script_typ = new ArrayAdapter<String>(
				Add_Script.this, android.R.layout.simple_spinner_item,
				script_type);
		typ_sp.setAdapter(script_typ);

		// Objekt Spinner
		Spinner obj_sp = (Spinner) findViewById(R.id.spinner_script_element);
		ArrayAdapter<String> adapter_sys = new ArrayAdapter<String>(
				Add_Script.this, android.R.layout.simple_spinner_item,
				obj_names);
		obj_sp.setAdapter(adapter_sys);
		// Itemseletcted Listener hinzuf�gen (bei gew�hltem Item wird der Text
		// in das Textfeld kopiert
		obj_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				String obj_name = ((TextView)arg1).getText().toString();
				EditText body = (EditText) findViewById(R.id.text_script_code);
				body.append(obj_name);
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});

		// den Button holen
		Button button = (Button) findViewById(R.id.button_script);
		// dem Button die Click Action hinzu f�gen
		button.setOnClickListener(new OnClickListener() {

			// der Button wurde gedr�ckt
			public void onClick(View v) {
				// Ladevorgang anzeigen
				dialog = ProgressDialog.show(Add_Script.this, "",
						"Script anlegen...", true, false);
				dialog.show();

				// Daten holen und zu String konvertieren
				String[] sensData = new String[3];
				EditText name = (EditText) findViewById(R.id.text_skript_name);
				sensData[0] = name.getText().toString();
				Spinner typ = (Spinner) findViewById(R.id.spinner_script_typ);
				sensData[1] = typ.getSelectedItem().toString();
				EditText body = (EditText) findViewById(R.id.text_script_code);
				sensData[2] = body.getText().toString();

				// Starten
				new addSens().execute(sensData);
			}
		});
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
			return myXsone.add_Script(data);
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
				Toast.makeText(Add_Script.this,
						"Skript erfolgreich angelegt..", Toast.LENGTH_LONG)
						.show();
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