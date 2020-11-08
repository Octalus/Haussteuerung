package com.android.xs.view.setting;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.error.ConnectionException;
import com.android.xs.model.error.XsError;
import com.android.xs.view.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Die Activity FirstStart stellt den Erstaufruf des Programms dar. Es ist noch
 * kein XS1 angelegt und die IP muss erstmalig eingetragen werden
 * 
 * @author Viktor Mayer
 * 
 */
public class Config extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private ProgressDialog dialog;
	private static boolean conn_validated;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Beim Aufruf der Activity wird die Eingabemaske f�r IP und Passwort
	 * aingezeigt. Beim senden wird eine Validit�tspr�fung der IP vorgenommen.
	 * und ein Objekt der Klasse Xsone in der SmartHomeActivity angelegt. Dieses
	 * enth�lt nr die IP, fragt sich jedoch selber ab und erg�nzt die fehlenden
	 * Daten.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// solange Verbindung zum XS1 nicht best�tigt wurde
		setConn_validated(false);

		// Maske f�r IP und Passwort anzeigen
		setContentView(R.layout.first_run);

		final Button button = (Button) findViewById(R.id.button_firstrun);
		button.setOnClickListener(new OnClickListener() {

			// der Button wurde gedr�ckt
			public void onClick(View v) {

				// Ladevorgang anzeigen
				dialog = ProgressDialog.show(Config.this, "",
						"Verbindung aufbauen...", true, false);
				dialog.show();

				String[] userData = new String[3];
				// Die ip wird zum String konvertiert
				EditText ip = (EditText) findViewById(R.id.text_ipurl);
				userData[0] = ip.getText().toString();

				// Das Passwort wird zum String konvertiert
				EditText pw = (EditText) findViewById(R.id.text_pw);
				userData[2] = pw.getText().toString();

				// Das Passwort wird zum String konvertiert
				EditText user = (EditText) findViewById(R.id.text_user);
				userData[1] = user.getText().toString();

				// Die Ladeprozess aufrufen
				new LoadXSone().execute(userData);

			}
		});
	}

	private class LoadXSone extends AsyncTask<String, Xsone, Xsone> {
		/**
		 * F�r Fehler w�hrend In Background Funktion
		 */
		private boolean check = true;

		@Override
		protected Xsone doInBackground(String... params) {

			// Das Xsone Objekt baut beim Anlegen eine Verbindung auf
			// und aktualisiert sich selbstst�ndig
			Xsone tmpXsone = null;
			try {
				tmpXsone = new Xsone(params[0], params[1], params[2]);
			} catch (ConnectionException e) {
				XsError.printError(e.getMessage());
				check = false;
			}
			return tmpXsone;
		}

		@Override
		protected void onPostExecute(Xsone result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war
			if (check && result != null) {
				// Pr�fen ob eine Verbindung hergestellt wurde. In dem Fall
				// wurde die MAC neu beschrieben
				if (result.getMac() != null) {
					// Das HauptXsOne Objekt setzen
					RuntimeStorage.setMyXsone(result);
					// Alle Daten wurden gelesen, Verbindung ist nun gepr�ft
					setConn_validated(true);
					finish();
				} else {
					// XsError.printError("result MAC is NULL");
					XsError.printError(getBaseContext());
					return;
				}
			} else {
				// XsError.printError("check OR result is NULL");
				XsError.printError(getBaseContext());
				return;
			}
		}
	}

	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	public static void setConn_validated(boolean conn_validated) {
		Config.conn_validated = conn_validated;
	}

	public static boolean isConn_validated() {
		return conn_validated;
	}
}
