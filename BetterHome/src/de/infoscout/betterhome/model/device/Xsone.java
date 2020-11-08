package de.infoscout.betterhome.model.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.content.Context;
import de.infoscout.betterhome.controller.remote.Http;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.components.IpSetting;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.ActuatorDB;
import de.infoscout.betterhome.model.device.db.ScriptDB;
import de.infoscout.betterhome.model.device.db.TimerDB;
import de.infoscout.betterhome.model.error.ConnectionException;
import de.infoscout.betterhome.view.EntryActivity.UpdateXSData;

public class Xsone {

	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/

	private String username;
	private String password;

	private String DeviceName;
	private String Hardware;
	private String Bootloader;
	private String Firmware;
	private int System;
	private int MaxActuators;
	private int MaxSensors;
	private int MaxTimers;
	private int MaxScripts;
	private int MaxRooms;
	private long Uptime;
	private LinkedList<String> Features;
	private String Mac;
	private String Autoip;

	private List<XS_Object> myActuatorList = new ArrayList<XS_Object>();
	private List<XS_Object> mySensorList = new ArrayList<XS_Object>();
	private List<XS_Object> myTimerList = new ArrayList<XS_Object>();
	private List<XS_Object> myScriptList = new ArrayList<XS_Object>();
	private Http myHttp;
	private IpSetting myIpSetting;
	
	private Context context;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Konstruktor für das Xsone Objekt mit IP (für Erstaufruf) legt zugleich
	 * eine neue IpSetting an und speichert die übergebene IP
	 * 
	 * @param ip
	 *            - Die Ip der XS1
	 * @param username
	 *            - Der Benutzername
	 * @param pass
	 *            - Das Passwort
	 * @throws ConnectionException
	 *             -
	 */
	public Xsone(String ip, String username, String pass, UpdateXSData thread, Context context) throws ConnectionException {
		// Username wird gespeichert
		this.setUsername(username);
		// Passwort wird gespeichert;
		this.setPassword(pass);
		
		this.context = context;

		// Die IP wird gespeichert
		myIpSetting = new IpSetting();
		myIpSetting.setIp(ip);

		// nun müssen die übrigen Daten geholt werden. Dazu wird die Http Klasse
		// geholt
		myHttp = RuntimeStorage.getMyHttp();
		
		// Die Konfigurationen der XS1 werden ausgelesen und sollten in diesem
		// Objekt gespeichert werden.
		Xsone retXs = myHttp.get_config_info(this);
		if (thread != null) thread.doPublishProgress();
		// die eigenen Daten werden nun upgedatet wenn erfolgreich Verbindung
		// aufgebaut wurde
		if (!update_device(retXs))
			throw new ConnectionException("Keine Internetverbindung!");

		// Die Listen werden mit der Maximalzahl an Dummys befüllt
		fillDummyLists();

		// Die Aktoren werden nun ausgelesen und angelegt
		List<XS_Object> act_list;
		if ((act_list = myHttp.get_list_actuators()) == null)
			throw new ConnectionException("Keine Internetverbindung!");
		if (thread != null) thread.doPublishProgress();
		// Die Liste mit Actuatoren wird hinzu gefügt
		add_RemObj(act_list);

		// Die Sensoren werden nun ausgelesen und angelegt
		List<XS_Object> sens_list;
		if ((sens_list = myHttp.get_list_sensors(context)) == null)
			throw new ConnectionException("Keine Internetverbindung!");
		if (thread != null) thread.doPublishProgress();
		// Die Liste mit Actuatoren wird hinzu gefügt
		add_RemObj(sens_list);

		if (getFeatures().contains("C")){
			// Die Timer werden nun ausgelesen und geholt
			List<XS_Object> timer_list;
			if ((timer_list = myHttp.get_list_timers()) == null)
				throw new ConnectionException("Keine Internetverbindung!");
			if (thread != null) thread.doPublishProgress();
			// Die Liste mit Timern wird hinzu gefügt
			add_RemObj(timer_list);
	
			// Die Skripte werden nun ausgelesen und geholt
			List<XS_Object> script_list;
			if ((script_list = myHttp.get_list_scripts()) == null)
				throw new ConnectionException("Keine Internetverbindung!");
			if (thread != null) thread.doPublishProgress();
			// Die Liste mit Timern wird hinzu gefügt
			add_RemObj(script_list);
		}		
	}

	public Xsone() {
		myHttp = RuntimeStorage.getMyHttp();
	};

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Updatet die Daten des Xsone Objekts mit denen des Übergebenen
	 * (ausgelesen)
	 * 
	 * @param new_data
	 *            - Das ausgelesne Xsone Objkt mit neuen Daten
	 * @return - true bei Erfolg (wenn das Objekt nicht NULL ist) sonst false
	 */
	public boolean update_device(Xsone new_data) {
		if (new_data != null) {
			this.DeviceName = new_data.getDeviceName();
			this.Hardware = new_data.getHardware();
			this.Bootloader = new_data.getBootloader();
			this.Firmware = new_data.getFirmware();
			this.System = new_data.getSystem();
			this.MaxActuators = new_data.getMaxActuators();
			this.MaxSensors = new_data.getMaxSensors();
			this.MaxTimers = new_data.getMaxTimers();
			this.MaxScripts = new_data.getMaxScripts();
			this.MaxRooms = new_data.getMaxRooms();
			this.Uptime = new_data.getUptime();
			this.Features = new_data.getFeatures();
			this.Mac = new_data.getMac();
			this.Autoip = new_data.getAutoip();
			this.myIpSetting = new_data.getMyIpSetting();
			// this.setMyProxAlerts(new_data.getMyProxAlertsList());
			return true;
		} else
			return false;
	}

	/**
	 * Fügt neue Actuatoren oder Sensoren in die Liste von Remote Objkten hinzu
	 * 
	 * @param rem_list
	 *            - Die Liste mit den neuen Remote Objekten
	 */
	public void add_RemObj(List<XS_Object> rem_list) {
		for (XS_Object act : rem_list) {
			add_RemObj(act);
		}
	}

	/**
	 * Fügt einen neuen Actuator oder Sensor in die Liste von Remote Objkten
	 * hinzu
	 * 
	 * @param rem_obj
	 *            - Das neue Remote Objekt
	 */
	public void add_RemObj(XS_Object rem_obj) {
		// nur anlegen, falls noch nicht vorhanden
		if (rem_obj.getClass().equals(Actuator.class)) {
			myActuatorList.set(rem_obj.getNumber() - 1, rem_obj);
		} else if (rem_obj.getClass().equals(Sensor.class)) {
			mySensorList.set(rem_obj.getNumber() - 1, rem_obj);
		} else if (rem_obj.getClass().equals(Timer.class)) {
			myTimerList.set(rem_obj.getNumber() - 1, rem_obj);
		} else if (rem_obj.getClass().equals(Script.class)) {
			myScriptList.set(rem_obj.getNumber() - 1, rem_obj);
		} else
			return;
	}

	/**
	 * Aktualisert die Liste der Rem Objekte je nach Typ
	 * 
	 * @param type
	 *            - die art der Remote Objekte, die neu ausgelesen werden sollen
	 */
	public void update_RemObj(Object type) {

		if (type.getClass().equals(Actuator.class)) {
			add_RemObj(myHttp.get_list_actuators());
		} else if (type.getClass().equals(Sensor.class)) {
			add_RemObj(myHttp.get_list_sensors(context));
		} else if (type.getClass().equals(Timer.class)) {
			add_RemObj(myHttp.get_list_timers());
		}
	}

	/**
	 * Startet ein Abo aller Sensoren und schreibt ein Ereignis in die Liste
	 * 
	 * @param data_list
	 *            - Die Liste, in welche neue Ereignisse eingetragen werden
	 * @throws IOException
	 */
	public void subscribe(LinkedList<String> data_list, Context context) throws IOException {
		myHttp.subscribe(data_list, context);
	}

	/**
	 * Veranlasst das subscribe eine Exception zu werfen um so den Thread zu
	 * beenden
	 */
	public void unsubscribe() {
		myHttp.unsubscribe();
	}

	/**
	 * Fügt einen Sensor an die erste freie Stelle ein
	 * 
	 * @param data
	 *            - Die Sensordaten
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean add_Sensor(String[] data) {
		int num = this.getFirstFree(new Sensor(), null);
		
		boolean check = false;
		if (num != -1) {
			Sensor new_s = new Sensor();
			new_s.setNumber(num);
			check = myHttp.add_sensor(data, String.valueOf(num));
			if (check) {
				new_s.update();
				this.add_RemObj(new_s);
			}
		}
		
		return check;
	}

	/**
	 * Fügt einen Timer an die erste freie Stelle ein
	 * 
	 * @param data
	 *            - Die Timerdaten
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean add_Timer(List<String> data, Context context) {
		DatabaseStorage db = new DatabaseStorage(context);
		List<TimerDB> dbTimers = db.getAllTimers();
		
		List<Integer> exclusion = new ArrayList<Integer>();
		for (int i=0; i<dbTimers.size(); i++) {
			exclusion.add(dbTimers.get(i).getNumber());
		}
		
		int num = this.getFirstFree(new Timer(), exclusion);
		
		boolean check = false;
		if (num != -1) {
		
			String name = data.get(0);
			
			Timer new_t = new Timer();
			new_t.setNumber(num);
			new_t.setName(name.replace(" ", "_"));
			new_t.setType(data.get(1));
			
			data.set(0, name.replace(" ", "_"));
			
			check = myHttp.add_timer(data, num);
			if (check) {
				this.add_RemObj(new_t);
				
				// add DB entry for real name
				
				TimerDB timerDB = db.getTimer(num);
				if (timerDB != null){
					timerDB.setName(name);
					db.updateTimer(timerDB);
				} else {
					timerDB = new TimerDB();
					timerDB.setName(name);
					timerDB.setNumber(num);
					db.createTimer(timerDB);
				}
			}
		}
		
		db.closeDB();
		
		return check;
	}
	
	/**
	 * Editiert einen Timer
	 * 
	 * @param data
	 *            - Die Timerdaten
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean edit_Timer(int num, List<String> data, Context context, boolean disabled) {
		String name = data.get(0);
		data.set(0, name.replace(" ", "_"));
		boolean check = myHttp.add_timer(data, num);
		if (check) {
			// add DB entry for real name
			DatabaseStorage db = new DatabaseStorage(context);
			TimerDB timerDB = db.getTimer(num);
			if (timerDB != null){
				timerDB.setName(name);
				timerDB.setInactive(disabled);
				db.updateTimer(timerDB);
			} else {
				timerDB = new TimerDB();
				timerDB.setName(name);
				timerDB.setNumber(num);
				timerDB.setInactive(disabled);
				db.createTimer(timerDB);
			}
			
			db.closeDB();
		}
		return check;
	}
	
	/**
	 * Löscht einen Timer
	 * 
	 * @param data
	 *            - Die Timerdaten
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean delete_Timer(int num) {
		Timer new_t = new Timer();
		new_t.setNumber(num);
		new_t.setName("Timer_"+num);
		new_t.setType("disabled");
		
		ArrayList<String> timerData = new ArrayList<String>();
		timerData.add("Timer_"+num);
		timerData.add("disabled");
		timerData.add("12");
		timerData.add("00");
		timerData.add("");
		timerData.add("");
		
		boolean check = myHttp.add_timer(timerData, num);
		if (check) {
			this.add_RemObj(new_t);
		}
		return check;
	}

	/**
	 * Fügt ein Script an die erste freie Stelle ein
	 * 
	 * @param data
	 *            - die Scriptdaten
	 * @return - true bei Erfolg, sonst false
	 */
	public int add_Script(String[] data, Context context) {
		int num = this.getFirstFree(new Script(), null);
		
		boolean check = false;
		if (num != -1) {
			String name = data[0];
			data[0] = name.replace(" ", "_");
			
			Script new_sc = new Script();
			new_sc.setNumber(num);
			new_sc.setName(data[0]);
			new_sc.setType(data[1]);
			check = myHttp.add_script(data, num);
			if (check) {
				this.add_RemObj(new_sc);
				
				DatabaseStorage db = new DatabaseStorage(context);
				ScriptDB scriptDB = new ScriptDB();
				scriptDB.setName(data[0]);
				scriptDB.setNumber(num);
				db.createScript(scriptDB);
				
				db.closeDB();
			}
		}
		return check ? num : -1;
	}
	
	/**
	 * Editiert ein Script
	 * 
	 * @param data
	 *            - Die Scriptdaten
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean edit_Script(int num, String[] data, Context context) {
		String name = data[0];
		String type = data[1];
		String body = data[2];
		name = name.replace(" ", "_");
		data[0] = name;
		
		boolean check = myHttp.add_script(data, num);
		if (check) {
			Script script = new Script();
			script.setName(name);
			script.setNumber(num);
			script.setType(type);
			script.setBody(body);
			
			// add DB entry for real name
			DatabaseStorage db = new DatabaseStorage(context);
			ScriptDB scriptDB = db.getScript(num);
			if (scriptDB != null){
				scriptDB.setName(name);
				db.updateScript(scriptDB);
			} else {
				scriptDB = new ScriptDB();
				scriptDB.setName(name);
				scriptDB.setNumber(num);
				db.createScript(scriptDB);
			}
			db.closeDB();
			
			script.setScriptDB(scriptDB);
			this.add_RemObj(script);
		}
		return check;
	}
	
	/**
	 * Löscht ein Script
	 * 
	 * @param data
	 *            - Die Scriptdaten
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean delete_Script(int num) {
		Script new_s = new Script();
		new_s.setNumber(num);
		new_s.setName("Script_"+num);
		new_s.setType("disabled");
		new_s.setBody("");
		
		String[] scriptData = new String[3];
		scriptData[0] = "Script_"+num;
		scriptData[1] = "disabled";
		scriptData[2] = "";
		
		
		boolean check = myHttp.add_script(scriptData, num);
		if (check) {
			this.add_RemObj(new_s);
		}
		return check;
	}

	/**
	 * Fügt einen Aktuator an die erste freie Stelle ein
	 * 
	 * @param data
	 *            - Die Aktuatordaten
	 * @return - true bei Erfolg, sonst false
	 */
	public int add_Actuator(String nameDB, String[] data, Context context) {
		int num = this.getFirstFree(new Actuator(), null);
		if (num != -1) {
			Actuator new_a = new Actuator();
			new_a.setNumber(num);
			boolean check = myHttp.add_actuator(data, String.valueOf(num));
			if (check) {
				new_a.update();
				// Für Aktuatoren müssen noch Funktionen angelegt werden, da diese
				// nur beim Start gelesen werden
				//new_a.getMyFunction().add(new Function(data[7], data[6]));
				//new_a.getMyFunction().add(new Function(data[11], data[10]));
				//new_a.getMyFunction().add(new Function(data[15], data[14]));
				//new_a.getMyFunction().add(new Function(data[19], data[18]));
	
				this.add_RemObj(new_a);
				
				DatabaseStorage db = new DatabaseStorage(context);
				ActuatorDB actuatorDB = new ActuatorDB();
				actuatorDB.setName(nameDB);
				actuatorDB.setNumber(num);
				db.createActuator(actuatorDB);
				
				db.closeDB();
			}
		}
		return num;
	}
	
	/**
	 * Löscht einen Actuator
	 * 
	 * @param num
	 *            - Nummer
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean delete_Actuator(int num) {
		Actuator new_t = new Actuator();
		new_t.setNumber(num);
		new_t.setName("Actuator_"+num);
		new_t.setType("disabled");
		
		String[] actuatorData = new String[22];
		actuatorData[0] = "disabled";
		actuatorData[1] = "virtual";
		actuatorData[2] = "Actuator_"+num;
		actuatorData[3] = "";
		actuatorData[4] = "";
		actuatorData[5] = "";
		actuatorData[6] = "";
		actuatorData[7] = "";
		actuatorData[8] = "";
		actuatorData[9] = "";
		actuatorData[10] = "";
		actuatorData[11] = "";
		actuatorData[12] = "";
		actuatorData[13] = "";
		actuatorData[14] = "";
		actuatorData[15] = "";
		actuatorData[16] = "";
		actuatorData[17] = "";
		actuatorData[18] = "";
		actuatorData[19] = "";
		actuatorData[20] = "";
		actuatorData[21] = "";
		
		boolean check = myHttp.add_actuator(actuatorData, num+"");
		if (check) {
			this.add_RemObj(new_t);
		}
		return check;
	}

	/**
	 * Entfernt das Object aus der XS1
	 * 
	 * @param obj
	 *            - das zu löschende Objekt
	 * @param remote
	 *            - Gibt an, ob der Befehl an die XS1 gesendet werden soll
	 * @return - true bei Erfold, sonst false
	 */
	public boolean remove(XS_Object obj, boolean remote) {
		boolean check = true;
		if (remote)
			check = myHttp.remove_Object(obj);
		if (check) {
			if (obj.getClass().equals(Actuator.class)) {
				myActuatorList.get(obj.getNumber() - 1).setType("disabled");
			} else if (obj.getClass().equals(Sensor.class)) {
				mySensorList.get(obj.getNumber() - 1).setType("disabled");
			} else if (obj.getClass().equals(Timer.class)) {
				myTimerList.get(obj.getNumber() - 1).setType("disabled");
			} else if (obj.getClass().equals(Script.class)) {
				myScriptList.get(obj.getNumber() - 1).setType("disabled");
			}
		}
		return check;
	}

	
	/**
	 * Gibt anhand eines Namens einen Actuator zurück aus der Liste aller
	 * Objekte
	 * 
	 * @param name
	 *            - Der Name des Objekts, nach dem gesucht wird
	 * @return - Das Objekt mit dem Namen aus der Liste, null , falls nicht
	 *         gefunden
	 */
	public Actuator getActiveActuator(String name) {
		Actuator act;
		for (XS_Object o : getMyActiveActuatorList(true, null)) {
			act = (Actuator)o;
			if (act.getAppname().equals(name)) {
				return act;
			}
		}
		
		return null;
	}
	
	/**
	 * Gibt anhand eines Namens einen Sensor zurück aus der Liste aller
	 * Objekte
	 * 
	 * @param name
	 *            - Der Name des Objekts, nach dem gesucht wird
	 * @return - Das Objekt mit dem Namen aus der Liste, null , falls nicht
	 *         gefunden
	 */
	public Sensor getActiveSensor(String name) {
		Sensor sens;
		for (XS_Object o : getMyActiveSensorList()) {
			sens = (Sensor)o;
			if (sens.getAppname().equals(name)) {
				return sens;
			}
		}
		
		return null;
	}
	
	/**
	 * Gibt anhand eines Namens ein Skript zurück aus der Liste aller
	 * Objekte
	 * 
	 * @param name
	 *            - Der Name des Objekts, nach dem gesucht wird
	 * @return - Das Objekt mit dem Namen aus der Liste, null , falls nicht
	 *         gefunden
	 */
	public Script getActiveScript(String name) {
		Script script;
		for (XS_Object o : getMyActiveScriptList()) {
			script = (Script)o;
			if (script.getAppname().equals(name)) {
				return script;
			}
		}
		
		return null;
	}
	
	/**
	 * Gibt anhand eines Namens einen Timer zurück aus der Liste aller
	 * Objekte
	 * 
	 * @param name
	 *            - Der Name des Objekts, nach dem gesucht wird
	 * @return - Das Objekt mit dem Namen aus der Liste, null , falls nicht
	 *         gefunden
	 */
	public Timer getActiveTimer(String name) {
		Timer timer;
		for (XS_Object o : getMyActiveTimerList()) {
			timer = (Timer)o;
			if (timer.getAppname().equals(name)) {
				return timer;
			}
		}
		
		return null;
	}
	
	/**
	 * Gibt anhand eines Namens ein Remote Objekt zurück aus der Liste aller
	 * Objekte
	 * 
	 * @param name
	 *            - Der Name des Objekts, nach dem gesucht wird
	 * @return - Das Objekt mit dem Namen aus der Liste, null , falls nicht
	 *         gefunden
	 */
	public XS_Object getActiveObject(String name) {
		XS_Object comp = new XS_Object();
		comp.setName(name.replace(" ", "_"));

		for (XS_Object o : getMyActiveActuatorList(true, null)) {
			if (o.equalsName(comp)) {
				return o;
			}
		}
		for (XS_Object o : getMyActiveSensorList()) {
			if (o.equalsName(comp)) {
				return o;
			}
		}
		for (XS_Object o : getMyActiveTimerList()) {
			if (o.equalsName(comp)) {
				return o;
			}
		}
		for (XS_Object o : getMyActiveScriptList()) {
			if (o.equalsName(comp)) {
				return o;
			}
		}
		return null;
	}
	
	/**
	 * Gibt anhand eines Namens ein Remote Objekt zurück aus der Liste aller
	 * Objekte
	 * 
	 * @param name
	 *            - Der Name des Objekts, nach dem gesucht wird
	 * @return - Das Objekt mit dem Namen aus der Liste, null , falls nicht
	 *         gefunden
	 */
	public XS_Object getObject(String name) {
		XS_Object comp = new XS_Object();
		comp.setName(name);

		for (XS_Object o : myActuatorList) {
			if (o.equals(comp)) {
				return o;
			}
		}
		for (XS_Object o : mySensorList) {
			if (o.equals(comp)) {
				return o;
			}
		}
		for (XS_Object o : myTimerList) {
			if (o.equals(comp)) {
				return o;
			}
		}
		for (XS_Object o : myScriptList) {
			if (o.equals(comp)) {
				return o;
			}
		}
		return null;
	}

	/**
	 * Liefert die Erste freie Position einer Bestimmten Klasse in der Liste
	 * alle Objekte der XS1
	 * 
	 * @param obj
	 *            - der zu suchende Klassentyp. Kann eine leere Klasse sein
	 * @return - die Positionen des ersten freien Objektes
	 */
	public int getFirstFree(Object obj, List<Integer> excludes) {
		int num = 1;

		if (obj.getClass().equals(Actuator.class)) {
			for (XS_Object rem : myActuatorList) {
				if (rem.getType().equals("disabled") && (excludes==null || !excludes.contains(rem.getNumber()))) {
					return num;
				} else
					num++;
			}
		} else if (obj.getClass().equals(Sensor.class)) {
			for (XS_Object rem : mySensorList) {
				if (rem.getType().equals("disabled") && (excludes==null || !excludes.contains(rem.getNumber()))) {
					return num;
				} else
					num++;
			}
		} else if (obj.getClass().equals(Timer.class)) {
			for (XS_Object rem : myTimerList) {
				if (rem.getType().equals("disabled") && (excludes==null || !excludes.contains(rem.getNumber()))) {
					return num;
				} else
					num++;
			}
		} else if (obj.getClass().equals(Script.class)) {
			for (XS_Object rem : myScriptList) {
				if (rem.getType().equals("disabled") && (excludes==null || !excludes.contains(rem.getNumber()))) {
					return num;
				} else
					num++;
			}
		}
		return -1;
	}

	/**
	 * Befüllt alle Listen mit der Maximalen Zahl an Elementen (ohne Inhalt)
	 */
	public void fillDummyLists() {
		// Die Listen werden mit der Maximalzahl an Dummys befüllt
		// Aktuatorenliste
		for (int x = 0; x < this.getMaxActuators(); x++)
			myActuatorList.add(new Actuator());
		// Sensorenliste
		for (int x = 0; x < this.getMaxSensors(); x++)
			mySensorList.add(new Sensor());
		// Timerliste
		for (int x = 0; x < this.getMaxTimers(); x++)
			myTimerList.add(new Timer());
		// Skriptliste
		for (int x = 0; x < this.getMaxScripts(); x++)
			myScriptList.add(new Script());
	}

	public LinkedList<String> learn(String system) throws IOException {
		return myHttp.learn(system);
	}

	/**
	 * Getter and Setter
	 ***********************************************************************************************************************************************************/

	public void setMyIpSetting(IpSetting myIpSetting) {
		this.myIpSetting = myIpSetting;
	}

	public IpSetting getMyIpSetting() {
		return myIpSetting;
	}

	public void setDeviceName(String deviceName) {
		DeviceName = deviceName;
	}

	public String getDeviceName() {
		return DeviceName;
	}

	public void setHardware(String hardware) {
		Hardware = hardware;
	}

	public String getHardware() {
		return Hardware;
	}

	public void setBootloader(String bootloader) {
		Bootloader = bootloader;
	}

	public String getBootloader() {
		return Bootloader;
	}

	public void setFirmware(String firmware) {
		Firmware = firmware;
	}

	public String getFirmware() {
		return Firmware;
	}

	public void setSystem(int system) {
		System = system;
	}

	public int getSystem() {
		return System;
	}

	public void setMaxActuators(int maxActuators) {
		MaxActuators = maxActuators;
	}

	public int getMaxActuators() {
		return MaxActuators;
	}

	public void setMaxSensors(int maxSensors) {
		MaxSensors = maxSensors;
	}

	public int getMaxSensors() {
		return MaxSensors;
	}

	public void setMaxTimers(int maxTimers) {
		MaxTimers = maxTimers;
	}

	public int getMaxTimers() {
		return MaxTimers;
	}

	public void setMaxScripts(int maxScripts) {
		MaxScripts = maxScripts;
	}

	public int getMaxScripts() {
		return MaxScripts;
	}

	public void setMaxRooms(int maxRooms) {
		MaxRooms = maxRooms;
	}

	public int getMaxRooms() {
		return MaxRooms;
	}

	public void setUptime(long uptime) {
		Uptime = uptime;
	}

	public long getUptime() {
		return Uptime;
	}

	public void setFeatures(LinkedList<String> alist) {
		Features = alist;
	}

	public LinkedList<String> getFeatures() {
		return Features;
	}

	public void setMac(String mac) {
		Mac = mac;
	}

	public String getMac() {
		return Mac;
	}

	public void setAutoip(String autoip) {
		Autoip = autoip;
	}

	public String getAutoip() {
		return Autoip;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public List<XS_Object> getMyActuatorList() {
		return myActuatorList;
	}
		
	public List<XS_Object> getMyActiveActuatorList(boolean makro, List<Actuator> exclude) {
		ArrayList<XS_Object> myActiveActuatorList = new ArrayList<XS_Object>();
		
		Actuator act = null;
		for (int i=0; i<myActuatorList.size(); i++){
			act = (Actuator)myActuatorList.get(i);
			if (!act.getType().equals("disabled")) {
				if (makro) {
					if (exclude != null && !exclude.contains(act)){
						myActiveActuatorList.add(act);
					} else if (exclude == null) {
						myActiveActuatorList.add(act);
					}
				} else {
					if (!act.isMakro()) {
						if (exclude != null && !exclude.contains(act)){
							myActiveActuatorList.add(act);
						} else if (exclude == null) {
							myActiveActuatorList.add(act);
						}
					}
				}
			}
		}
		
		return myActiveActuatorList;
	}
	
	public Actuator getActuator(int number){
		Actuator a;
		List<XS_Object> actuators = getMyActiveActuatorList(true, null);
		for (int i=0; i<actuators.size(); i++){
			a = (Actuator)actuators.get(i);
			if (a.getNumber()==number){
				return a;
			}
		}
		return null;
	}

	public void setMyActuatorList(List<XS_Object> myActuatorList) {
		this.myActuatorList = myActuatorList;
	}

	public List<XS_Object> getMySensorList() {
		return mySensorList;
	}
	
	public List<XS_Object> getMyActiveSensorList(){
		ArrayList<XS_Object> myActiveSensorList = new ArrayList<XS_Object>();
		
		XS_Object sens = null;
		for (int i=0; i<mySensorList.size(); i++){
			sens = (XS_Object)mySensorList.get(i);
			if (!sens.getType().equals("disabled")) {
				myActiveSensorList.add(sens);
			}
		}
		
		return myActiveSensorList;
	}
	
	public Sensor getSensor(int number){
		Sensor s;
		List<XS_Object> sensors = getMyActiveSensorList();
		for (int i=0; i< sensors.size(); i++){
			s = (Sensor)sensors.get(i);
			if (s.getNumber()==number){
				return s;
			}
		}
		return null;
	}

	public void setMySensorList(List<XS_Object> mySensorList) {
		this.mySensorList = mySensorList;
	}

	public List<XS_Object> getMyTimerList() {
		return myTimerList;
	}
	
	public List<XS_Object> getMyActiveTimerList() {
		ArrayList<XS_Object> myActiveTimerList = new ArrayList<XS_Object>();
		
		XS_Object act = null;
		for (int i=0; i<myTimerList.size(); i++){
			act = (XS_Object)myTimerList.get(i);
			if (!act.getType().equals("disabled")) {
				myActiveTimerList.add(act);
			}
		}
		
		return myActiveTimerList;
	}
	
	public Timer getTimer(int number){
		Timer t;
		for (int i=0; i<myTimerList.size(); i++){
			t = (Timer)myTimerList.get(i);
			if (t.getNumber()==number){
				return t;
			}
		}
		return null;
	}

	public void setMyTimerList(List<XS_Object> myTimerList) {
		this.myTimerList = myTimerList;
	}

	public List<XS_Object> getMyScriptList() {
		return myScriptList;
	}

	public void setMyScriptList(List<XS_Object> myScriptList) {
		this.myScriptList = myScriptList;
	}
	
	public List<XS_Object> getMyActiveScriptList(boolean makro) {
		ArrayList<XS_Object> myActiveScriptList = new ArrayList<XS_Object>();
		
		XS_Object script = null;
		for (int i=0; i<myScriptList.size(); i++){
			script = (XS_Object)myScriptList.get(i);
			if (!script.getType().equals("disabled")) {
				if (makro && script.getName().startsWith("S_")){
					myActiveScriptList.add(script);
				} else if (!makro && !script.getName().startsWith("S_")) {
					myActiveScriptList.add(script);
				}
			}
		}
		
		return myActiveScriptList;
	}
	
	public List<XS_Object> getMyActiveScriptList() {
		ArrayList<XS_Object> myActiveScriptList = new ArrayList<XS_Object>();
		
		XS_Object script = null;
		for (int i=0; i<myScriptList.size(); i++){
			script = (XS_Object)myScriptList.get(i);
			if (!script.getType().equals("disabled")) {
				myActiveScriptList.add(script);
			}
		}
		
		return myActiveScriptList;
	}
	
	public Script getScript(int number){
		Script sc;
		for (int i=0; i<myScriptList.size(); i++){
			sc = (Script)myScriptList.get(i);
			if (sc.getNumber()==number){
				return sc;
			}
		}
		return null;
	}

}