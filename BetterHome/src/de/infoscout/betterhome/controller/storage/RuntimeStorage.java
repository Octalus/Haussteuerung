package de.infoscout.betterhome.controller.storage;

import java.util.LinkedList;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import de.infoscout.betterhome.controller.remote.Http;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.db.SettingDB;
import de.infoscout.betterhome.view.AlertSettingsActivity;
import de.infoscout.betterhome.view.SettingsActivity;
import de.infoscout.betterhome.view.utils.Utilities;

/**
 * 
 * @author Viktor Mayer
 * 
 */
public class RuntimeStorage {

	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/

	// Stellt die Http Verbindungen bereit
	private static Http myHttp;
	// gibt nach Makros an, dass alle Daten aktualisiert werden müssen (für GPRS
	// Verbindung)
	private static boolean statusValid = true;
	// Das Hauptobjekt, welches vom Speicher oder vom XS1 geladen wird und im
	// Betrieb aktualisiert wird und an andere Activities weiter gereicht
	private static volatile Xsone myXsone;

	// Beinhaltet nur die wichtigsten Daten um weitere aus dem XS1 abrufen zu
	// können (Passwort, user, ip)
	private static String[] xsdata;

	private static final String IP_KEY = "IP_KEY";
	private static final String USERNAME_KEY = "USERNAME_KEY";
	private static final String PASSWORD_KEY = "PASSWORD_KEY";
	
	// Die Datenliste für die Subscribe Methode (als Service ausgeführt)
	private static LinkedList<String> subscribe_data_list = new LinkedList<String>();
	
	// Alarm Settings
	private static boolean alarm = false;
	private static boolean alarmVibration = false;
	private static boolean alarmRingtone = false;
	private static String ringtone = null;
	private static boolean smokeDetectorAlarm = false;
	private static boolean heatDetectorAlarm = false;
	private static boolean waterDetectorAlarm = false;
	private static boolean windowBreakAlarm = false;
	private static boolean fenceDetectorAlarm = false;
	private static boolean motionDetectorAlarm = false;
	
	// Konfigurationen
	private static int refreshSeconds = 300;
	private static boolean graphOutlierDetection = false;

	private static void readAlarmSettings(Context context){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		setAlarm(prefs.getBoolean(AlertSettingsActivity.ALARM_ACTIVE_KEY, false));
		setAlarmVibration(prefs.getBoolean(AlertSettingsActivity.ALARM_ACTIVE_VIBRATION_KEY, false));
		setAlarmRingtone(prefs.getBoolean(AlertSettingsActivity.ALARM_ACTIVE_RINGTONE_KEY, false));
		setRingtone(prefs.getString(AlertSettingsActivity.ALARM_RINGTONE_KEY, android.provider.Settings.System.DEFAULT_RINGTONE_URI.getPath()));
		setSmokeDetectorAlarm(prefs.getBoolean(AlertSettingsActivity.ALARM_SMOKEDETECTOR_KEY, false));
		setHeatDetectorAlarm(prefs.getBoolean(AlertSettingsActivity.ALARM_HEATDETECTOR_KEY, false));
		setWaterDetectorAlarm(prefs.getBoolean(AlertSettingsActivity.ALARM_WATERDETECTOR_KEY, false));
		setWindowBreakAlarm(prefs.getBoolean(AlertSettingsActivity.ALARM_WINDOWBREAK_KEY, false));
		setFenceDetectorAlarm(prefs.getBoolean(AlertSettingsActivity.ALARM_FENCEDETECTOR_KEY, false));
		setMotionDetectorAlarm(prefs.getBoolean(AlertSettingsActivity.ALARM_MOTIONDETECTOR_KEY, false));
	}
	
	private static void readConfigurations(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		setRefreshSeconds(Integer.parseInt(prefs.getString(SettingsActivity.REFRESH_SECONDS_KEY, "300")));
		setGraphOutlierDetection(prefs.getBoolean(SettingsActivity.GRAPH_OUTLIER_ELEMINATION,  false));
	}

	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	public static void setXsdata(String[] xsdata, Context context) {
		RuntimeStorage.xsdata = xsdata;
		
		String ip = xsdata[0];
		String username = xsdata[1];
		String passwort = xsdata[2];
		DatabaseStorage db;
		db = new DatabaseStorage(context);
		db.createSetting(new SettingDB(IP_KEY, ip));
		db.createSetting(new SettingDB(USERNAME_KEY, username));
		db.createSetting(new SettingDB(PASSWORD_KEY, passwort));
		
		db.close();
		
		readAlarmSettings(context);
		readConfigurations(context);
	}

	// ist das Objekt zur Laufzeit null (idR beim ersten Start) wird versucht es
	// aus dem Speicher zu lesen
	public static String[] getXsdata(Context context) {
		if (xsdata == null){
			DatabaseStorage db;
			db = new DatabaseStorage(context);
			
			SettingDB ip = db.getSetting(IP_KEY);
			SettingDB username = db.getSetting(USERNAME_KEY);
			SettingDB password = db.getSetting(PASSWORD_KEY);
			
			if (ip != null && username != null && password != null){
				xsdata = new String[3];
				xsdata[0] = ip.getValue();
				xsdata[1] = username.getValue();
				xsdata[2] = password.getValue();
			}
			
			db.closeDB();
			
			readAlarmSettings(context);
			readConfigurations(context);
		}
			
		return xsdata;
	}

	public static void setMyXsone(Xsone myXsone) {
		RuntimeStorage.myXsone = myXsone;
	}

	public static Xsone getMyXsone() {		
		return myXsone;
	}

	public static void setMyHttp(Http myHttp) {
		RuntimeStorage.myHttp = myHttp;
	}

	public static void setMyHttp() {
		RuntimeStorage.myHttp = Http.getInstance();
	}

	public static Http getMyHttp() {
		if (myHttp == null)
			setMyHttp();
		return myHttp;
	}

	public static void setStatusValid(boolean status) {
		statusValid = status;
	}

	public static boolean isStatusValid() {
		return statusValid;
	}
	
	public static void setSubscribe_data_list(LinkedList<String> subscribe_data_list) {
		RuntimeStorage.subscribe_data_list = subscribe_data_list;
	}

	public static LinkedList<String> getSubscribe_data_list() {
		return subscribe_data_list;
	}
	
	public static boolean isAlarm() {
		return alarm;
	}

	public static void setAlarm(boolean alarm) {
		RuntimeStorage.alarm = alarm;
	}

	public static boolean isAlarmVibration() {
		return alarmVibration;
	}

	public static void setAlarmVibration(boolean alarmVibration) {
		RuntimeStorage.alarmVibration = alarmVibration;
	}

	public static boolean isAlarmRingtone() {
		return alarmRingtone;
	}

	public static void setAlarmRingtone(boolean alarmRingtone) {
		RuntimeStorage.alarmRingtone = alarmRingtone;
	}

	public static String getRingtone() {
		Log.v(Utilities.TAG, "get Ringtone="+ringtone);
		return ringtone;
	}

	public static void setRingtone(String ringtone) {
		RuntimeStorage.ringtone = ringtone;
		Log.v(Utilities.TAG, "set Ringtone="+ringtone);
	}

	public static boolean isSmokeDetectorAlarm() {
		return smokeDetectorAlarm;
	}

	public static void setSmokeDetectorAlarm(boolean smokeDetectorAlarm) {
		RuntimeStorage.smokeDetectorAlarm = smokeDetectorAlarm;
	}

	public static boolean isHeatDetectorAlarm() {
		return heatDetectorAlarm;
	}

	public static void setHeatDetectorAlarm(boolean heatDetectorAlarm) {
		RuntimeStorage.heatDetectorAlarm = heatDetectorAlarm;
	}

	public static boolean isWaterDetectorAlarm() {
		return waterDetectorAlarm;
	}

	public static void setWaterDetectorAlarm(boolean waterDetectorAlarm) {
		RuntimeStorage.waterDetectorAlarm = waterDetectorAlarm;
	}

	public static boolean isWindowBreakAlarm() {
		return windowBreakAlarm;
	}

	public static void setWindowBreakAlarm(boolean windowBreakAlarm) {
		RuntimeStorage.windowBreakAlarm = windowBreakAlarm;
	}

	public static boolean isFenceDetectorAlarm() {
		return fenceDetectorAlarm;
	}

	public static void setFenceDetectorAlarm(boolean fenceDetectorAlarm) {
		RuntimeStorage.fenceDetectorAlarm = fenceDetectorAlarm;
	}

	public static boolean isMotionDetectorAlarm() {
		return motionDetectorAlarm;
	}

	public static void setMotionDetectorAlarm(boolean motionDetectorAlarm) {
		RuntimeStorage.motionDetectorAlarm = motionDetectorAlarm;
	}

	public static int getRefreshSeconds() {
		//System.out.println("RuntimeStorage.getRefreshSeconds="+refreshSeconds);
		return refreshSeconds;
	}

	public static void setRefreshSeconds(int refreshSeconds) {
		RuntimeStorage.refreshSeconds = refreshSeconds;
	}

	public static boolean isGraphOutlierDetection() {
		//System.out.println("RuntimeStorage.isGraphOutlierDetection="+graphOutlierDetection);
		return graphOutlierDetection;
	}

	public static void setGraphOutlierDetection(boolean graphOutlierDetection) {
		RuntimeStorage.graphOutlierDetection = graphOutlierDetection;
	}
	
}
