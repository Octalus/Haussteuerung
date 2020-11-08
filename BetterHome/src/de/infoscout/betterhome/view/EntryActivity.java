package de.infoscout.betterhome.view;

import java.util.List;

import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.error.ConnectionException;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.controller.service.ProximityAlertService;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class EntryActivity extends Activity {
	
	public static Activity entryactivity;
	private DatabaseStorage db;
	
	private void startService() {
		
		// Custom Proximity Alert
        Intent intent = new Intent(this, ProximityAlertService.class);
        this.startService(intent);
	}
	
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (ProximityAlertService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);
		
		entryactivity = this;
		db = new DatabaseStorage(this);
		
		// Show the Up button in the action bar.
		if (getActionBar() != null) {
			//getActionBar().setDisplayHomeAsUpEnabled(true);
		    //getActionBar().setHomeButtonEnabled(true);
		    getActionBar().setDisplayShowHomeEnabled(true);
		}
		
		// Die Runtime Stoarge mit Http setzen
		if (RuntimeStorage.getMyHttp() == null)
			RuntimeStorage.setMyHttp();

		// es wird versucht die gespeicherte IP anzulegen.. bei Fehler wird
		// firstrun aufgerufen, welcher die IP abfragt und setmyXsone aufruft um
		// diese das XSOne Objekt im RuntimeStorage zu speichern
		if (RuntimeStorage.getXsdata(entryactivity) == null) {
			// in diesem Fall ist noch keine IP gespeichert
			Intent intent = new Intent(this, InitializeActivity.class);
			startActivity(intent);
		}
		
		// falls die IP erfolgreich geladen wurde wird das Xsone Objekt
		// aktualisiert. Das Objekt wird neu geladen vom
		// XS1 und es kann dann zum Mainframe gewechselt werden nach einem
		// Update des XS1
		else {
			if (RuntimeStorage.getMyXsone() == null){
				new UpdateXSData().execute();
			} else {
				startApp();
			}
		}
	}
	
	
	/**
	 * beim Fortsetzen wurde die IP gesetzt vom Benutzer. Ist nur der Fall nach
	 * Aufruf von Firstrun, da die Activity mit dem Mainframe endet
	 */
	@Override
	public void onRestart() {
		super.onRestart();

		// Das Objekt sollte nun fertig angelegt sein und kann verwendet werden
		// pr�fen (Benutzer k�nnte zur�ck gedr�ckt haben!!)
		if (InitializeActivity.isConn_validated()) {
			// Die XS_Daten m�ssen nun persistent gespeichert werden
			String[] data = {
					RuntimeStorage.getMyXsone().getMyIpSetting().getIp(),
					RuntimeStorage.getMyXsone().getUsername(),
					RuntimeStorage.getMyXsone().getPassword() };
			RuntimeStorage.setXsdata(data, entryactivity);
			Intent intent;
			
			if (!isMyServiceRunning()) {
				startService();
				
			}
			
			// Ist die Verbindung gelungen kann zum Mainframe gewechselt werden
			intent = new Intent(this, MenuItemListActivity.class);
			startActivity(intent);
			finish();
		} else {
			// Andernfalls wird das Programm beendet
			alert(getBaseContext(), getString(R.string.no_connection_defined));
		}
	}
	
	/**
	 * Die Alert Funktion benachrichtigt den User und gibt Option zum beenden
	 * oder neu konfigurieren
	 * 
	 * @param con
	 *            - Der Context der Activity
	 * @param txt
	 *            - Die auszugebende Warnmeldung
	 */
	private void alert(Context con, String txt) {
		AlertDialog ad = new AlertDialog.Builder(this).create();
		ad.setCancelable(false); // This blocks the 'BACK' button
		ad.setTitle(getString(R.string.warning));
		ad.setMessage(txt);
		// Die beiden Auswahlbuttons setzen
		ad.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.exit), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// Dialog schlie�en und Activity beenden
				dialog.dismiss();
				finish();
			}
		});
		ad.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.configure), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// Dialog schlie�en und Konfiguration starten
				dialog.dismiss();
				Intent intent = new Intent(EntryActivity.this,
						InitializeActivity.class);
				startActivity(intent);
			}
		});
		ad.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.retry), new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// Dialog schlie�en und Wiederholung starten
				dialog.dismiss();
				new UpdateXSData().execute();
			}
		});
		if (con != null)
			ad.show();
	}
	
	public void startApp(){
		if (!isMyServiceRunning()){
			startService();
			
		}	
		
		// nun kann zum Hauptprozess gewechselt werden
		Intent intent = new Intent(EntryActivity.this, MenuItemListActivity.class);
		startActivity(intent);
		
		// Die Activity kann beendet werden
		EntryActivity.this.finish();
	}
	
	
	/**
	 * Ausgelagerter Thread, da je nach Verbindung zeitintensiv. UpdateXSData
	 * legt ein neues XSone Objekt an. Stimmen die Daten nicht mehr wird eine
	 * Excpetion geworfen. In diesem Fall wird in PostExecute der Alert
	 * aufgerufen , in dem Das Programm beendet, oder die Verbindung neu
	 * konfiguriert werden kann. Bei Erfolg startet das Mainframe
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	public class UpdateXSData extends AsyncTask<Context, Boolean, Boolean> {
		private final String titles[] = {entryactivity.getString(R.string.laden)+" "+entryactivity.getString(R.string.information)+"",
			entryactivity.getString(R.string.laden)+" "+entryactivity.getString(R.string.actuators)+"",
			entryactivity.getString(R.string.laden)+" "+entryactivity.getString(R.string.sensors)+"",
			entryactivity.getString(R.string.laden)+" "+entryactivity.getString(R.string.timer)+"",
			entryactivity.getString(R.string.laden)+" "+entryactivity.getString(R.string.rules)+""};
		private final int progr[]  = {10, 15, 25, 25, 25};
		private ProgressBar progress;
	    private TextView textview;
	    
	    private int index;
	    
	    @Override
	    protected void onPreExecute() {
	    	progress = (ProgressBar)entryactivity.findViewById(R.id.progressBar1);
	    	textview = (TextView)entryactivity.findViewById(R.id.textViewEntry);
	    	
	        int max = 0;
	        for (final int p : progr) {
	            max += p;
	        }
	        progress.setMax(max);
	        index = 0;
	    }
	   
	    public void doPublishProgress() {
	    	publishProgress();
	    }
	    
		/**
		 * Im Hintergrundprozess wird das XSone angelegt. hier Kann es wegen
		 * Netzverkehr zu verz�gerung kommen. Bei Fehler wird false zur�ck
		 * gegeben
		 * 
		 * @return - False, falls keine Verbindung hergestellt werden konnte
		 * 
		 */
		@Override
		protected Boolean doInBackground(Context... arg0) {
			
			String[] xsdata = RuntimeStorage.getXsdata(entryactivity);
			try {
				// beim anlegen entsteht netzlast, da alles ausgelesen wird
				RuntimeStorage.setMyXsone(new Xsone(xsdata[0], xsdata[1],
						xsdata[2], this, entryactivity));
			} catch (ConnectionException e) {
				return false;
			}
			
			// Die Liste der Actuatorem holen
			List<XS_Object> act_list = RuntimeStorage.getMyXsone().getMyActiveActuatorList(true, null);
			
			// load DB data for actuators
			int number;
			for (int i=0; i< act_list.size(); i++) {
				number = act_list.get(i).getNumber();
				((Actuator)act_list.get(i)).setActuatorDB(db.getActuator(number));
			}
			
			// Die Liste der Sensoren holen
			List<XS_Object> sens_list = RuntimeStorage.getMyXsone().getMyActiveSensorList();
			
			// load DB data for sensors
			for (int i=0; i< sens_list.size(); i++) {
				number = sens_list.get(i).getNumber();
				((Sensor)sens_list.get(i)).setSensorDB(db.getSensor(number));
			}
			
			
			
			startApp();
			
			return true;

		}
		
		@Override
		protected void onProgressUpdate(Boolean... values) {
			textview.setText(titles[index]);
	        progress.incrementProgressBy(progr[index]);
	        ++index;
	        
			super.onProgressUpdate(values);
		}

		/**
		 * Konnte keine Verbindung aufgebaut werden muss der Alert Dialog mit
		 * den Auswahlm�glichkeiten aufgerufen werden
		 */
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			db.closeDB();
						
			if (!result) {
				alert(getBaseContext(),
						getString(R.string.no_connection));
			}
		}
	}

}
