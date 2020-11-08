package de.infoscout.betterhome.controller.intent;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.service.ProximityAlertService;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.error.ConnectionException;
import de.infoscout.betterhome.view.EntryActivity;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class XSIntent extends BroadcastReceiver {
	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/
	
	private Xsone myXsone;
	private Actuator actuator;
	private double value;
	private boolean actionOnEntry;
	private Context context;
	private String positionName;
	private int act_nrb;
	
	private int nId = 1337;
	
	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	@SuppressLint("DefaultLocale")
	/**
	 * wird beim Ausführen zuerst aufgerufen.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
	
		act_nrb = intent.getIntExtra(ProximityAlertService.ACT_NRB_KEY, -1);
		value = intent.getDoubleExtra(ProximityAlertService.ACT_VALUE_KEY, -1.0);
		actionOnEntry = intent.getBooleanExtra(ProximityAlertService.ACTION_ON_ENTRY_KEY, true);
		positionName = intent.getStringExtra(ProximityAlertService.POSITION_NAME_KEY);

		boolean invalid_data = false;

		// Prüfung für Makro
		if (act_nrb == -1 || value == -1.0)
			invalid_data = true;
		
		
		if (invalid_data) {
			return;
		}

		// XSOne Objekt
		// holen----------------------------------------------------

		// Die Runtime Storage mit Http setzen
		if (RuntimeStorage.getMyHttp() == null)
			RuntimeStorage.setMyHttp();

		(new GetXsOne()).execute(intent); 
	

		return;
	}
	

	/**
	 * Die Klasse führt das makro als eigenen Task aus.
	 *  
	 */
	private class ExecuteMakro extends AsyncTask<Integer, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {
			return actuator.setValue(value, true);
		}

		@Override
		protected void onPostExecute(Boolean result) {			
		}
	}
	
	private class GetXsOne extends AsyncTask<Intent, Boolean, Boolean> {
		Intent intent;
		
		@Override
		protected Boolean doInBackground(Intent... params) {
			intent = params[0];
		
			if (RuntimeStorage.getMyXsone() == null){
				String[] xsdata = RuntimeStorage.getXsdata(context);
				try {
					// beim anlegen entsteht netzlast, da alles ausgelesen wird
					RuntimeStorage.setMyXsone(new Xsone(xsdata[0], xsdata[1],
							xsdata[2], null, context));
				} catch (ConnectionException e) {
					return false;
				}
			}
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {	
			// beim verlassen oder betreten holen
			String k = LocationManager.KEY_PROXIMITY_ENTERING;
			// Key for determining whether user is leaving or entering.. default,
			// also wenn nicht erfolgreich bestimmt werden konnte dann wird es nicht
			// ausgeführt
			boolean state = intent.getBooleanExtra(k, actionOnEntry);
			// DEBUG
			// boolean state = makrosactiononentry;

			if (actionOnEntry == state) {
				Log.i("XSIntent", "################### OnReceive entry="+actionOnEntry);
				// ------------- Notification ----------------
				Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(context)
				        .setSmallIcon(R.drawable.position)
				        .setLargeIcon(bm)
				        .setAutoCancel(true)
				        .setContentTitle(context.getString(R.string.app_name))
				        .setLights(R.color.infoscout_blue , 1000, 1000)
				        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
				        .setContentText((actionOnEntry ? "Betrete " : "Verlasse ") + "Position '"+positionName+"'");
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(context, EntryActivity.class);
				
				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(EntryActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
				    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				mNotificationManager.notify(++nId, mBuilder.build());

				// -------------------
				
				myXsone = RuntimeStorage.getMyXsone();
				actuator = myXsone.getActuator(act_nrb);
				
				new ExecuteMakro().execute();
			}
		}
	}
}
