package com.android.xs.controller.intent;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

public class Dictate extends Activity {

	static public final int SPEECH_RECOGNIZED = 1;
	static public final int XS_REMOTE_COMMAND = 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		recognize(this);

	}

	public static void recognize(Activity activity) {
		Log.i("xs", "dictate called!!!");
		Intent intent = new Intent();
		intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		if (!intent.hasExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE)) {
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.dummy");
		}
		activity.startActivityForResult(intent, SPEECH_RECOGNIZED);
	}

	@Override
	/**
	 * Reads data scanned by user and returned by smarthome-xs1
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SPEECH_RECOGNIZED) {
			if (data != null) {
				final Intent intent = new Intent("com.android.xs.controller.SEND_XS");

				ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				String audio = result.get(0);
				audio = audio.toLowerCase(Locale.GERMAN);

				// Makro ausführen-----------------------------------------
				if (audio.contains("makro") || audio.contains("marko") || audio.contains("marco")) {
					ArrayList<String> makro = new ArrayList<String>();
					// makro rausschneiden
					audio = audio.replaceAll("makro", "");
					audio = audio.replaceAll("marko", "");
					audio = audio.replaceAll("marco", "");
					audio = audio.replaceAll(" ", "");

					makro.add(audio);
					intent.putStringArrayListExtra("makros", makro);

					Toast.makeText(this.getApplicationContext(), "Makro wird ausgeführt: " + audio, Toast.LENGTH_LONG).show();
				}
				// einzelnen Aktuator/Sensor ansprechen-----------------------
				else {
					ArrayList<String> names = new ArrayList<String>();
					ArrayList<Integer> vals = new ArrayList<Integer>();
					String value = "";
					String name = "";
					int val;

					String[] splitted = audio.split(" ");

					int l = splitted.length;
					boolean withunit = false;
					boolean request = false;

					// anzahl der Werte prüfen.. der letzte ist der schaltwert..
					// prüfen ob Abfrage erfolgen soll
					if (l > 1) {
						if (splitted[l - 1].equals("prozent") || splitted[l - 1].equals("grad")) {
							value = splitted[l - 2];
							withunit = true;
						} else if (splitted[l - 1].equals("abfragen"))
							request = true;
						else
							value = splitted[l - 1];
					}

					if (!request) {
						try {
							// schaltwert konvertieren
							if (value.equals("an") || value.equals("anschalten"))
								val = 100;
							else if (value.equals("aus") || value.equals("ausschalten"))
								val = 0;
							else {
								val = Integer.parseInt(value);
							}
						} catch (NumberFormatException e) {
							Toast.makeText(this.getApplicationContext(), "Wert nicht erkannt!", Toast.LENGTH_LONG).show();
							this.finish();
							return;
						}
						// Wert hinzufügen
						vals.add(val);
					}else
						vals.add(0);

					// mit prozent am ende
					if (withunit) {
						// wir haben name .. x .. wert .. prozent/grad -> x wird
						// verschmolzen mit Name
						if (l > 3) {
							for (int x = 0; x < l - 2; x++)
								name += splitted[x];
						} else
							name = splitted[0];
					} else {
						// wir haben name .. x .. wert -> x wird
						// verschmolzen mit Name
						if (l > 2) {
							for (int x = 0; x < l - 1; x++)
								name += splitted[x];
						} else
							name = splitted[0];
					}

					// name hinzufügen
					names.add(name);

					intent.putStringArrayListExtra("names", names);
					intent.putIntegerArrayListExtra("vals", vals);

					Toast.makeText(this.getApplicationContext(), "Kommando wird ausgeführt: " + audio, Toast.LENGTH_LONG).show();
				}

				startActivityForResult(intent, XS_REMOTE_COMMAND);

				// DEBUG
				Log.i("XS1", "speechcommand: " + audio);
			}
		} else if (requestCode == XS_REMOTE_COMMAND) {
			if (null != data && data.getExtras() != null) {
				String result = data.getExtras().getString("Status");
				Toast.makeText(this.getApplicationContext(), result, Toast.LENGTH_LONG).show();

				if (data.getStringArrayListExtra("Values").size() > 0)
					Toast.makeText(this, "Sensorwert: " + data.getStringArrayListExtra("Values").get(0), Toast.LENGTH_LONG).show();
			}
			this.finish();
		}
	}
}
