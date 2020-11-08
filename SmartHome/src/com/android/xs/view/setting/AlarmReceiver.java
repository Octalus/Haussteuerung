package com.android.xs.view.setting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.Makro;
import com.android.xs.view.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmReceiver extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private Xsone myXsone;
	private Button save;
	private Button del;
	private TimePicker tp;
	private Spinner makros_spin;
	private Spinner alarms_spin;
	private int h = 0;
	private int m = 0;
	private final int ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
	private AlarmManager am;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.alarm_action_layout);

		myXsone = RuntimeStorage.getMyXsone();

		save = (Button) findViewById(R.id.alarm_register);
		del = (Button) findViewById(R.id.alarm_del);
		tp = (TimePicker) findViewById(R.id.alarm_timePicker);
		tp.setIs24HourView(true);
		makros_spin = (Spinner) findViewById(R.id.alarm_makros);
		alarms_spin = (Spinner) findViewById(R.id.alarms_existing);

		h = tp.getCurrentHour();
		m = tp.getCurrentMinute();

		am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);

		// Liste mit Makros befüllen--------------------
		ArrayList<String> mvals = new ArrayList<String>();
		final List<Makro> makros = myXsone.getMyMakroList();

		for (Makro m : makros) {
			mvals.add(m.getName());
		}
		// Den Adapter für den Spinner anlegen
		ArrayAdapter<String> madapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, mvals);
		makros_spin.setAdapter(madapter);

		// bestehende Alarme füllen--------------------------
		ArrayList<String> avals = new ArrayList<String>();
		final List<String> alarms = myXsone.getMyAlarmsList();

		for (String a : alarms) {
			avals.add(a);
		}
		// Den Adapter für den Spinner anlegen
		final ArrayAdapter<String> aadapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, avals);
		alarms_spin.setAdapter(aadapter);

		tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				h = hourOfDay;
				m = minute;
			}
		});

		save.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, h);
				cal.set(Calendar.MINUTE, m);

				// makro name and time used for save
				String name = makros.get(makros_spin.getSelectedItemPosition()).getName();
				String extended_name = name;
				if (h < 10)
					extended_name = name + "_0" + h;
				else
					extended_name = name + "_" + h;
				if (m < 10)
					extended_name += ":0" + m;
				else
					extended_name += ":" + m;

				// Create a new PendingIntent and add it to the AlarmManager
				final Intent intent = new Intent("com.android.xs.controller.SEND_XS");

				ArrayList<String> mnames = new ArrayList<String>();
				mnames.add(name);
				intent.putStringArrayListExtra("makros", mnames);

				int id = alarms.size() + 1;

				PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

				// DEBUG
				// Log.i("ALARM_MANAGER", "Time: " + h + ":" + m);

				// am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
				// pendingIntent);
				am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), ONE_DAY_IN_MILLIS, pendingIntent);

				// speichern
				RuntimeStorage.getMyXsone().addAlarm(extended_name);
				aadapter.add(extended_name);

				Toast.makeText(getBaseContext(), "gespeichert..", Toast.LENGTH_SHORT).show();

			}

		});

		del.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				int id = alarms_spin.getSelectedItemPosition();

				final Intent intent = new Intent("com.android.xs.controller.SEND_XS");
				PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), id+1, intent, 0);

				am.cancel(pendingIntent);

				aadapter.remove(RuntimeStorage.getMyXsone().getMyAlarmsList().get(id));
				RuntimeStorage.getMyXsone().removeAlarm(id);
				Toast.makeText(getBaseContext(), "gelöscht..", Toast.LENGTH_SHORT).show();
			}
		});
	}

}
