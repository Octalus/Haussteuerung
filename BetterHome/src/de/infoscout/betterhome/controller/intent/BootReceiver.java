package de.infoscout.betterhome.controller.intent;

import de.infoscout.betterhome.controller.service.ProximityAlertService;
import de.infoscout.betterhome.controller.service.SubscribeService;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.error.ConnectionException;
import de.infoscout.betterhome.view.utils.Utilities;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	private Context context;
		
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
	
		Log.i("XS_BOOTRECEIVER", "################### Bootreceiver gestartet.. ");
		
		try {
			(new GetDataTask()).execute();
			
		} catch (Exception e) {
			Log.e("XS_BOOTRECEIVER", "unbekannter Fehler!!");
		}
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, Boolean> {		
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// Read XsOne
			String[] xsdata = RuntimeStorage.getXsdata(context);
			if (xsdata != null) {
				try {
					// beim anlegen entsteht netzlast, da alles ausgelesen wird
					RuntimeStorage.setMyXsone(new Xsone(xsdata[0], xsdata[1],
							xsdata[2], null, context));
				} catch (ConnectionException e) {
					return false;
				}
			} else {
				return false;
			}
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			// Custom Proximity Alert
            Intent intent = new Intent(context, ProximityAlertService.class);
            context.startService(intent);
            
            if (result){
	            Log.i(Utilities.TAG, "XS Subscribe Service wird gestartet!");
	            context.startService(new Intent(context, SubscribeService.class));
            }
				
			Log.i("XS_BOOTRECEIVER", "################### Proximity Alerts started successfully!");
			
			return;

		}
	}
}
