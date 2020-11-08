package com.android.xs.view.add;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.controller.usage.Translator;
import com.android.xs.model.device.Actuator;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.Function;
import com.android.xs.model.device.components.XS_Object;
import com.android.xs.model.error.XsError;
import com.android.xs.view.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Add_Tim extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/
	Boolean[] days = new Boolean[7];
	// Das Xsone Objekt f�r diese Aktivity
	private Xsone myXsone;
	// Dialog f�r Ladevorgang
	private Dialog dialog;
	//Aktuatorliste f�r den Spinner 
	private LinkedList<Actuator> act_list = new LinkedList<Actuator>();
	private int mHour;
	private int mMinute;
	// aus Folgenter Quelle �bernommen:
	// http://developer.android.com/resources/tutorials/views/hello-timepicker.html
	static final int TIME_DIALOG_ID = 0;

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
		setContentView(R.layout.add_tim_frame);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();

		// Alle Aktuatoren f�r den Spinner holen
		for (XS_Object obj : myXsone.getMyActuatorList()) {
			if (!obj.getType().equals("disabled"))
				act_list.add((Actuator) obj);
		}

		// Array mit Namen anlegen f�r Spinner Adapter
		String[] act_names = new String[act_list.size()];
		int i = 0;
		for (Actuator a : act_list) {
			act_names[i] = a.getName();
			i++;
		}

		// Dem Time Text ein oncklickaction geben
		EditText time = (EditText) findViewById(R.id.text_time_tim);
		time.setInputType(InputType.TYPE_NULL);
		time.setText("12:00:00");
		time.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//TODO: shall be changed in later versions to fragments!!
				showDialog(TIME_DIALOG_ID);
			}
		});

		// Die Spinner bef�llen
		Spinner typ_sp = (Spinner) findViewById(R.id.spinner_tim_typ);
		ArrayAdapter<String> adapter_typ = new ArrayAdapter<String>(
				Add_Tim.this, android.R.layout.simple_spinner_item, RuntimeStorage.getTim_types());
		typ_sp.setAdapter(adapter_typ);
		// Beim Act Spinner muss auf �nderungen reagiert werden. Der Funktion
		// Spinner h�ngt vom Wert hier ab
		Spinner act_sp = (Spinner) findViewById(R.id.spinner_tim_act);
		ArrayAdapter<String> adapter_act = new ArrayAdapter<String>(
				Add_Tim.this, android.R.layout.simple_spinner_item, act_names);
		act_sp.setAdapter(adapter_act);
		act_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// ist ein Element gew�hlt muss der Funktionsspinner gesetzt
				// werden
				// zuerst muss das gew�hlte Objekt geholt werden
				String name = ((TextView) arg1).getText().toString();
				Actuator comp = new Actuator();
				comp.setName(name);
				for (Actuator a : act_list) {
					if (a.equals(comp)) {
						comp = a;
						break;
					}
				}
				// Dann m�ssen alle Funktionen ausgelesen werden falls nicht
				// null und als String[] gesetzt
				ArrayList<String> fn_list = new ArrayList<String>();
				if (comp.getMyFunction() != null) {
					for (Function f : comp.getMyFunction()) {
						if (!f.getType().equals("disabled"))
							fn_list.add(f.getDsc());
					}
				} else
					fn_list.add("leer");

				// Nun kann der Spinner geholt und die Werte gesetzt werden
				Spinner fnk_sp = (Spinner) findViewById(R.id.spinner_tim_func);
				ArrayAdapter<String> adapter_act = new ArrayAdapter<String>(
						Add_Tim.this, android.R.layout.simple_spinner_item,
						fn_list);
				fnk_sp.setAdapter(adapter_act);

			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// den Button holen
		Button button = (Button) findViewById(R.id.button_tim);
		// dem Button die Click Action hinzu f�gen
		button.setOnClickListener(new OnClickListener() {

			// der Button wurde gedr�ckt
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				// Ladevorgang anzeigen
				dialog = ProgressDialog.show(Add_Tim.this, "",
						"Timer anlegen...", true, false);
				dialog.show();

				// Alle Werte auslesen
				ArrayList<String> timerData = new ArrayList<String>();
				EditText name = (EditText) findViewById(R.id.text_tim_name);
				timerData.add(name.getText().toString());
				Spinner typ = (Spinner) findViewById(R.id.spinner_tim_typ);
				timerData.add(Translator.translate(typ.getSelectedItem().toString()));
				EditText zeit = (EditText) findViewById(R.id.text_time_tim);
				timerData.add(zeit.getText().toString().substring(0, 2));
				timerData.add(zeit.getText().toString().substring(3, 5));
				Spinner act = (Spinner) findViewById(R.id.spinner_tim_act);
				timerData.add(act.getSelectedItem().toString());
				Spinner fnkt = (Spinner) findViewById(R.id.spinner_tim_func);
				timerData.add(String.valueOf(fnkt.getSelectedItemPosition()));
				// Die Checkboxen der Wochentage pr�fen
				CheckBox cb;
				cb = (CheckBox) findViewById(R.id.cb_tin_mo);
				if (cb.isChecked())
					timerData.add("Mo");
				cb = (CheckBox) findViewById(R.id.cb_tim_di);
				if (cb.isChecked())
					timerData.add("Tu");
				cb = (CheckBox) findViewById(R.id.cb_tim_mi);
				if (cb.isChecked())
					timerData.add("We");
				cb = (CheckBox) findViewById(R.id.cb_tim_do);
				if (cb.isChecked())
					timerData.add("Th");
				cb = (CheckBox) findViewById(R.id.cb_tim_fr);
				if (cb.isChecked())
					timerData.add("Fr");
				cb = (CheckBox) findViewById(R.id.cb_tim_sa);
				if (cb.isChecked())
					timerData.add("Sa");
				cb = (CheckBox) findViewById(R.id.cb_tim_so);
				if (cb.isChecked())
					timerData.add("Su");

				// Starten
				new addTim().execute(timerData);
			}
		});

	}

	/**
	 * the callback received when the user "sets" the time in the dialog
	 */
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			EditText zeit = (EditText) findViewById(R.id.text_time_tim);
			zeit.setText(pad(mHour) + ":" + pad(mMinute) + ":00");
		}
	};

	/**
	 * 
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
					false);
		}
		return null;
	}

	/**
	 * bei einstelliger Stunde eine zweistelligen Wert f�r die Ausgabe zur�ck
	 * 
	 * @param c
	 *            - Der Stunden/Minuten Wert
	 * @return - Der Wert mit zwei Stellen als String
	 */
	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	/**
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class addTim extends AsyncTask<List<String>, Boolean, Boolean> {

		/**
		 * 
		 */
		@Override
		protected Boolean doInBackground(List<String>... data) {
			return myXsone.add_Timer(data[0]);
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
				Toast.makeText(Add_Tim.this, "Timer erfolgreich angelegt..",
						Toast.LENGTH_LONG).show();
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