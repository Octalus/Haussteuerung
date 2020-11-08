package com.android.xs.controller.intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import com.android.xs.controller.storage.PersistantStorage;
import com.android.xs.controller.storage.RuntimeStorage;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Wird genutzt um nach einem Neustart des Gerätes die Alarme neu zu setzen und
 * die Näherungs-Alarme zu löschen
 * 
 * @author Viktor Mayer
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/

	private final int ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
	private AlarmManager am;

	public static final String XS_PROXALERTS_TYPE = "SmartHomeXS.alerts";
	public static final String XS_ALARMS_TYPE = "SmartHomeXS.alarms";

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	@SuppressWarnings("unchecked")
	@Override
	public void onReceive(Context context, Intent intent)

	{
		try {
			RuntimeStorage.setMyPers(PersistantStorage.getInstance(context));

			Log.i("XS_BOOTRECEIVER", "################### Bootreceiver gestartet.. ");

			List<String> alarms = (List<String>) RuntimeStorage.getMyPers().getData(XS_ALARMS_TYPE);

			// namen und zeit separieren
			if (alarms != null) {
				int id = 1;
				am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
				for (String a : alarms) {
					int div = a.indexOf('_');

					if (div < 0) {
						Log.e("XS_BOOTRECEIVER", "Fehler beim Konvertieren des Alarms!");
						return;
					}
					String name = a.substring(0, div);
					String hour = a.substring(div + 1, div + 3);
					String minute = a.substring(div + 4);

					int h = Integer.parseInt(hour);
					int m = Integer.parseInt(minute);

					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.HOUR_OF_DAY, h);
					cal.set(Calendar.MINUTE, m);

					// Create a new PendingIntent and add it to the AlarmManager
					final Intent i = new Intent("com.android.xs.controller.SEND_XS");

					Log.i("XS_BOOTRECEIVER", "################### Intent angelegt");

					ArrayList<String> mnames = new ArrayList<String>();
					mnames.add(name);
					i.putStringArrayListExtra("makros", mnames);

					Log.i("XS_BOOTRECEIVER", "################### Makros für Alarme geladen");

					PendingIntent pendingIntent = PendingIntent
							.getActivity(context.getApplicationContext(), id, i, PendingIntent.FLAG_CANCEL_CURRENT);

					am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), ONE_DAY_IN_MILLIS, pendingIntent);

					Log.i("XS_BOOTRECEIVER", "################### Alarme erfolgreich gestartet");

					id++;
				}
			}

			// delete prox alerts list

			RuntimeStorage.getMyPers().saveData(new LinkedList<String>(), XS_PROXALERTS_TYPE);

			Log.i("XS_BOOTRECEIVER", "################### Proximity-Alerts gelöscht");

		} catch (Exception e) {
			Log.e("XS_BOOTRECEIVER", "unbekannter Felher!!");
		}
	}
}
