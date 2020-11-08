package de.infoscout.betterhome.controller.service;

import java.io.IOException;
import java.lang.Thread.State;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.error.ConnectionException;
import de.infoscout.betterhome.view.EntryActivity;
import de.infoscout.betterhome.view.menu.subscription.SubscriptionActivity;
import de.infoscout.betterhome.view.utils.Utilities;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SubscribeService extends Service{
	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/
		
	private static Xsone myXsone = null;
	private Context context = null;
	// Abo Thread
	private Thread t = null;
	// Wird zur Prï¿½fung genutzt, ob der Service lï¿½uft
	private static SubscribeService instance = null;
	// Timer zur Verbindungsprï¿½fung
	private Timer check_timer;
	private Timer internet_check_timer;
	private boolean foreground = true;
	private final static int myID = 5678;
	
	private int connectionCheckThreadCounter = 1;
	private int subscriptionThreadCounter = 1;
	
	// Connection check Periode 10s
	public static final int CHECK_PERIOD = 10000;
	// Internet Check Periode ist 2 min
	public static final int INTERNET_CHECK_PERIOD = 120000;
	// Zeit ohne neue Zeile in Console, bis neu aufgebaut wird (min)
	public static final int INTERNET_CHECK_LIMIT = 6;
	
	// Connection post Delay 5s
	public static final int POST_DELAY = 5000;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Gibt zurï¿½ck, ob der Service lï¿½uft
	 * 
	 * @return - true, wenn eine Instanz vorliegt, also der Service lï¿½uft, sonst
	 *         false
	 */
	public static boolean isInstanceCreated() {
		return instance != null;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * Hier wird die Instanz angelegt sowie das Xsone Objekt geholt
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		myXsone = RuntimeStorage.getMyXsone();
		context = this;
	}

	/**
	 * Beim beenden wird die Instanz gelï¿½scht. Zudem der thread gestoppt und die
	 * Liste geleert
	 */
	@Override
	public void onDestroy() {
		Log.i(Utilities.TAG,  "onDestroy()");
		if (foreground) this.stopForeground(true);
		
		// Den Verbindungschecker beenden
		check_timer.cancel();
		internet_check_timer.cancel();
		
		// Die Liste leeren
		RuntimeStorage.getSubscribe_data_list().clear();

		myXsone.unsubscribe();

		instance = null;
		t = null;
		
		super.onDestroy();
		
		Toast.makeText(this, "XS Live Service beendet...", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (RuntimeStorage.isAlarm()){
			Log.i(Utilities.TAG,  "onStartCommand()");
			
			ConnectionCheck checker = new ConnectionCheck(connectionCheckThreadCounter++);
			check_timer = new Timer(true);
			check_timer.schedule(checker, POST_DELAY, CHECK_PERIOD);
			
			InternetChecker internetChecker = new InternetChecker();
			internet_check_timer = new Timer(true);
			internet_check_timer.schedule(internetChecker, POST_DELAY, INTERNET_CHECK_PERIOD);
	
			Toast.makeText(this, "XS Subscribe Service gestartet...", Toast.LENGTH_SHORT).show();
			Log.i(Utilities.TAG, "XS Subscribe Service gestartet...");
			
			if (foreground) startForeground();
			
			return START_STICKY;
		} else {
			return -1;
		}
	}	
	
	@SuppressWarnings("deprecation")
	void startForeground() {
		Log.i(Utilities.TAG,  "eigene startForeground()");
		// Notification
		//The intent to launch when the user clicks the expanded notification
	    Intent entry_intent = new Intent(this, EntryActivity.class);
	    Intent subscr_intent = new Intent(this, SubscriptionActivity.class);
	    entry_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    //PendingIntent pendIntent = PendingIntent.getActivity(this, 0, entry_intent, 0);
	    PendingIntent pendIntent = PendingIntent.getActivity(this, 0, subscr_intent, 0);
	
	    Notification notification = null;
	    
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
	    	Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
	    	NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
    	    builder.setTicker(getString(R.string.subscription_service_started))
    	    	    .setContentTitle(getString(R.string.app_name))
    	    	    .setContentText(getString(R.string.subscription_service))
    	            .setWhen(System.currentTimeMillis())
    	            .setAutoCancel(false)
    	            .setOngoing(true)
    	            .setPriority(Notification.PRIORITY_HIGH)
    	            .setContentIntent(pendIntent)
    	            .setSmallIcon(R.drawable.ic_launcher)
    	            .setColor( getResources().getColor(R.color.infoscout_blue) )
    	    		.setLargeIcon(bm);
    	    			            
    	    notification = builder.build();
    	}
	    
    	notification.flags |= Notification.FLAG_NO_CLEAR;
    	startForeground(myID, notification);
	}
	
	private class SubscriptionThread extends Thread implements Runnable{
		private int number;
		
		public SubscriptionThread(int number){
			this.number = number;
		}
		
		public void run() {
			try {
				if (myXsone != null) {
					//RuntimeStorage.getSubscribe_data_list().add(" done! SubscriptionThread-"+number+" \n");
					RuntimeStorage.getSubscribe_data_list().add(" done! \n");
					myXsone.subscribe(RuntimeStorage.getSubscribe_data_list(), context);
					
				} else {
					Log.i(Utilities.TAG, "myXsone in SubscriptionThread-"+number+" ist null!");
					RuntimeStorage.getSubscribe_data_list().add("myXsone in Service Thread ist null! \n");
					
					// Reload Xs Data
					String[] xsdata = RuntimeStorage.getXsdata(instance);
					try {
						// beim anlegen entsteht netzlast, da alles ausgelesen wird
						RuntimeStorage.setMyXsone(new Xsone(xsdata[0], xsdata[1],
								xsdata[2], null, context));
						
						myXsone = RuntimeStorage.getMyXsone();

						RuntimeStorage.getSubscribe_data_list().add("myXsone neu aufgebaut und subscribed! \n");
						Log.i(Utilities.TAG, "myXsone neu aufgebaut und subscribed!");
						
						myXsone.subscribe(RuntimeStorage.getSubscribe_data_list(), context);
						
						
					} catch (ConnectionException e) {
						e.printStackTrace();
						RuntimeStorage.getSubscribe_data_list().add("Verbindung zu eZcontrol fehlgeschlagen für Neuaufbau myXsone! \n");
						Log.i(Utilities.TAG, "Verbindung zu eZcontrol fehlgeschlagen für Neuaufbau myXsone!");
					}
					
				}
				
			} catch (IOException e) {
				// Thread gestoppt. Die Exception kommt vom subscribe
				Log.i(Utilities.TAG, "Verbindung abgebrochen!");
				RuntimeStorage.getSubscribe_data_list().add("Verbindung abgebrochen! \n");
				return;
			} catch (Exception e) {
				Log.i(Utilities.TAG, "Unbekannter Fehler - Verbindung abgebrochen!");
				RuntimeStorage.getSubscribe_data_list().add("Unbekannter Fehler - Verbindung abgebrochen! \n");
				return;
			}
			Log.i(Utilities.TAG, "Subscription Thread - fertig!");
		}
	}

	private class ConnectionCheck extends TimerTask {
		private int number;
		
		
		public ConnectionCheck (int number) {
			this.number = number;
		}
		
		@Override
		public void run() {
			Log.d(Utilities.TAG, "Enter ConnectionChecker:");
			if (t == null || !t.isAlive()) {
				// alle n Sek Verbindung versuchen neu aufzubauen
				
				Log.i(Utilities.TAG, "Verbindung wird neu aufgebaut...  ConnectionCheck-"+number);
				//RuntimeStorage.getSubscribe_data_list().add("Verbindung wird neu aufgebaut... ConnectionCheck-"+number+" \n");
				RuntimeStorage.getSubscribe_data_list().add("Verbindung wird neu aufgebaut...");
				
				if (t == null || t.getState() == State.TERMINATED) {
					//rebuild thread + start
					t = new SubscriptionThread(subscriptionThreadCounter++);
					t.start();
					
					Log.i(Utilities.TAG, "Neuer Thread gestartet... ConnectionCheck-"+number);
					//RuntimeStorage.getSubscribe_data_list().add("Neuer Thread gestartet... ConnectionCheck-"+number+" \n");
				} else {
					t.start();
				}
			}
		}
	}
	
	private class InternetChecker extends TimerTask {
		private Calendar lastExecutionTime = null;
		private String lastOutputLine = null;
		
		
		public InternetChecker () {
		}
		
		private String getLastOutputLine(){
			int size = RuntimeStorage.getSubscribe_data_list().size();
			String out = null;
			
			if (size > 0) {
				out = RuntimeStorage.getSubscribe_data_list().get(size-1);
			}
			
			return out;
		}
		
		@Override
		public void run() {
			Log.i(Utilities.TAG, "Internet Checker entry");
			
			if (lastExecutionTime == null || lastOutputLine == null) {
				lastExecutionTime = Calendar.getInstance();
				lastOutputLine = getLastOutputLine();
			} else {
				String currentOutputLine = getLastOutputLine();
				Calendar currentExecuteTime = Calendar.getInstance();
				
				Log.i(Utilities.TAG, "currentLine="+currentOutputLine+"lastLine="+lastOutputLine);
				
				// letzte Zeile ist die selbe
				if (currentOutputLine != null && lastOutputLine != null && currentOutputLine.equals(lastOutputLine)){
					
					int current_stunde = currentExecuteTime.get(Calendar.HOUR_OF_DAY);
					int current_minute = currentExecuteTime.get(Calendar.MINUTE);
					
					int last_stunde = lastExecutionTime.get(Calendar.HOUR_OF_DAY);
					int last_minute = lastExecutionTime.get(Calendar.MINUTE);
					
					Log.i(Utilities.TAG, "alte Zeit:"+last_stunde+":"+last_minute+" / neue Zeit:"+current_stunde+":"+current_minute);
					
					int diff_minuten = 0;
					if (current_stunde > last_stunde) {
						diff_minuten = 60 - last_minute + current_minute;
					} else {
						diff_minuten = current_minute - last_minute;
					}
					
					Log.i(Utilities.TAG, "diffMinuten = "+diff_minuten);
					
					// gleiche Ausgabe länger als n minuten
					if (diff_minuten > INTERNET_CHECK_LIMIT) {
						Log.e(Utilities.TAG, "Internet für "+INTERNET_CHECK_LIMIT+" Minuten weg - Verbindung neu aufbauen...");
						RuntimeStorage.getSubscribe_data_list().add("Internet für "+INTERNET_CHECK_LIMIT+" Minuten weg - Verbindung neu aufbauen...\n");
						
						myXsone.unsubscribe();
						//t.stop();
						lastExecutionTime = currentExecuteTime;
						lastOutputLine = currentOutputLine;
					}
				// letzt Zeile hat sich verändert
				} else {
					lastExecutionTime = currentExecuteTime;
					lastOutputLine = currentOutputLine;
				}
			}
		}
	}
}
