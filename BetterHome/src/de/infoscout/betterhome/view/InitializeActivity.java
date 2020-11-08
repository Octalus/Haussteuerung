package de.infoscout.betterhome.view;

import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.error.ConnectionException;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InitializeActivity extends Activity {
	private ProgressDialog dialog;
	private static boolean conn_validated;
	private Activity context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		
		// Show the Up button in the action bar.
		/*if (getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		    getActionBar().setHomeButtonEnabled(true);
		    getActionBar().setDisplayShowHomeEnabled(true);
		}*/
		
		// solange Verbindung zum XS1 nicht bestätigt wurde
		setConn_validated(false);
		
		setContentView(R.layout.activity_initialize);
		
		if (RuntimeStorage.getMyXsone() != null) {
			String ipportS = RuntimeStorage.getMyXsone().getMyIpSetting().getIp();
			String ipS = ipportS.substring(0, ipportS.indexOf(":"));
			String portS = ipportS.substring(ipportS.indexOf(":")+1, ipportS.length());
			String benutzerS = RuntimeStorage.getMyXsone().getUsername();
			String passS = RuntimeStorage.getMyXsone().getPassword();
			
			EditText ip = (EditText) findViewById(R.id.ip);
			EditText port = (EditText) findViewById(R.id.port);
			EditText benutzername = (EditText) findViewById(R.id.benutzername);
			EditText passwort = (EditText) findViewById(R.id.passwort);
			
			ip.setText(ipS);
			port.setText(portS);
			benutzername.setText(benutzerS);
			passwort.setText(passS);
		}
		
		Button buttonLogin = (Button) findViewById(R.id.loginButton);
		buttonLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Connection prüfen
				dialog = ProgressDialog.show(InitializeActivity.this, "",
						getString(R.string.build_connection), true, false);
				dialog.show();
				
				EditText ip = (EditText) findViewById(R.id.ip);
				EditText port = (EditText) findViewById(R.id.port);
				EditText benutzername = (EditText) findViewById(R.id.benutzername);
				EditText passwort = (EditText) findViewById(R.id.passwort);
				
				String portS = "80";
				if (!port.getText().toString().equals("")) portS=port.getText().toString();
				
				String[] userData = new String[3];
				userData[0] = ip.getText().toString()+":"+portS;
				userData[1] = benutzername.getText().toString();
				userData[2] = passwort.getText().toString();
				
				// Die Ladeprozess aufrufen
				new LoadXSone().execute(userData);
			}
		});
	}
	
	
	private class LoadXSone extends AsyncTask<String, Xsone, Xsone> {
		/**
		 * Fï¿½r Fehler wï¿½hrend In Background Funktion
		 */
		private boolean check = true;

		@Override
		protected Xsone doInBackground(String... params) {

			// Das Xsone Objekt baut beim Anlegen eine Verbindung auf
			// und aktualisiert sich selbstständig
			Xsone tmpXsone = null;
			try {
				tmpXsone = new Xsone(params[0], params[1], params[2], null, context);
			} catch (ConnectionException e) {
				XsError.printError(context, e.getMessage());
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
				// Prüfen ob eine Verbindung hergestellt wurde. In dem Fall
				// wurde die MAC neu beschrieben
				if (result.getMac() != null) {
					// Das HauptXsOne Objekt setzen
					RuntimeStorage.setMyXsone(result);
					// Alle Daten wurden gelesen, Verbindung ist nun geprï¿½ft
					setConn_validated(true);
					
					finish();
				} else {
					// XsError.printError("result MAC is NULL");
					XsError.printError(context);
					return;
				}
			} else {
				// XsError.printError("check OR result is NULL");
				XsError.printError(context);
				return;
			}
		}
	}
	
	public static void setConn_validated(boolean conn_validated) {
		InitializeActivity.conn_validated = conn_validated;
	}

	public static boolean isConn_validated() {
		return conn_validated;
	}

}
