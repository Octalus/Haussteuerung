package com.android.xs.model.device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.android.xs.controller.remote.Http;
import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.controller.usage.MakroController;
import com.android.xs.model.device.components.Function;
import com.android.xs.model.device.components.IpSetting;
import com.android.xs.model.device.components.Makro;
import com.android.xs.model.device.components.XS_Object;
import com.android.xs.model.error.ConnectionException;

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
	private List<Makro> myMakroList = new ArrayList<Makro>();
	private Http myHttp;
	private IpSetting myIpSetting;
	private List<String> proxAlerts = new LinkedList<String>();
	private List<String> alarms = new LinkedList<String>();

	public static final String XS_PROXALERTS_TYPE = "SmartHomeXS.alerts";
	public static final String XS_ALARMS_TYPE = "SmartHomeXS.alarms";

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
	@SuppressWarnings("unchecked")
	public Xsone(String ip, String username, String pass) throws ConnectionException {
		// Username wird gespeichert
		this.setUsername(username);
		// Passwort wird gespeichert;
		this.setPassword(pass);

		// Die IP wird gespeichert
		myIpSetting = new IpSetting();
		myIpSetting.setIp(ip);

		// nun müssen die übrigen Daten geholt werden. Dazu wird die Http Klasse
		// geholt
		myHttp = RuntimeStorage.getMyHttp();

		// Die Konfigurationen der XS1 werden ausgelesen und sollten in diesem
		// Objekt gespeichert werden.
		Xsone retXs = myHttp.get_config_info(this);
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
		// Die Liste mit Actuatoren wird hinzu gefügt
		add_RemObj(act_list);

		// Die Sensoren werden nun ausgelesen und angelegt
		List<XS_Object> sens_list;
		if ((sens_list = myHttp.get_list_sensors()) == null)
			throw new ConnectionException("Keine Internetverbindung!");
		// Die Liste mit Actuatoren wird hinzu gefügt
		add_RemObj(sens_list);

		// Die Timer werden nun ausgelesen und geholt
		List<XS_Object> timer_list;
		if ((timer_list = myHttp.get_list_timers()) == null)
			throw new ConnectionException("Keine Internetverbindung!");
		// Die Liste mit Timern wird hinzu gefügt
		add_RemObj(timer_list);

		// Die Skripte werden nun ausgelesen und geholt
		List<XS_Object> script_list;
		if ((script_list = myHttp.get_list_scripts()) == null)
			throw new ConnectionException("Keine Internetverbindung!");
		// Die Liste mit Timern wird hinzu gefügt
		add_RemObj(script_list);

		// Makros werden geladen
		this.setMyMakroList(MakroController.loadMakros());

		// Proximity Alerts laden
		LinkedList<String> Prox_Alerts = (LinkedList<String>) RuntimeStorage.getMyPers().getData(XS_PROXALERTS_TYPE);
		if (Prox_Alerts == null)
			Prox_Alerts = new LinkedList<String>();
		this.setMyProxAlerts(Prox_Alerts);
		
		//alarme laden
		LinkedList<String> alarms = (LinkedList<String>) RuntimeStorage.getMyPers().getData(XS_ALARMS_TYPE);
		if (alarms == null)
			alarms = new LinkedList<String>();
		this.setMyAlarms(alarms);
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
			add_RemObj(myHttp.get_list_sensors());
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
	public void subscribe(LinkedList<String> data_list) throws IOException {
		myHttp.subscribe(data_list);
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
		int num = this.getFirstFree(new Sensor());
		Sensor new_s = new Sensor();
		new_s.setNumber(num);
		boolean check = myHttp.add_sensor(data, String.valueOf(num));
		if (check) {
			new_s.update();
			this.add_RemObj(new_s);
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
	public boolean add_Timer(List<String> data) {
		int num = this.getFirstFree(new Timer());
		Timer new_t = new Timer();
		new_t.setNumber(num);
		new_t.setName(data.get(0));
		new_t.setType(data.get(1));
		boolean check = myHttp.add_timer(data, num);
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
	public Boolean add_Script(String[] data) {
		int num = this.getFirstFree(new Script());
		Script new_sc = new Script();
		new_sc.setNumber(num);
		new_sc.setName(data[0]);
		new_sc.setType(data[1]);
		boolean check = myHttp.add_script(data, num);
		if (check) {
			this.add_RemObj(new_sc);
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
	public boolean add_Actuator(String[] data) {
		int num = this.getFirstFree(new Actuator());
		Actuator new_a = new Actuator();
		new_a.setNumber(num);
		boolean check = myHttp.add_actuator(data, String.valueOf(num));
		if (check) {
			new_a.update();
			// Für Aktuatoren müssen noch Funktionen angelegt werden, da diese
			// nur beim Start gelesen werden
			new_a.getMyFunction().add(new Function(data[7], data[6]));
			new_a.getMyFunction().add(new Function(data[11], data[10]));
			new_a.getMyFunction().add(new Function(data[15], data[14]));
			new_a.getMyFunction().add(new Function(data[19], data[18]));

			this.add_RemObj(new_a);
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
	 * fügt ein neues Makro in die Liste ein
	 * 
	 * @param m
	 *            - Das neue Makro
	 */
	public void add_Makro(Makro m) {
		getMyMakroList().add(m);
	}

	/**
	 * löscht ein Makro aus der Liste
	 * 
	 * @param num
	 *            - die Nummer des zu löschenden Makros
	 */
	public void del_Makro(int num) {
		getMyMakroList().remove(num);
	}

	/**
	 * Liefert die Erste freie Position einer Bestimmten Klasse in der Liste
	 * alle Objekte der XS1
	 * 
	 * @param obj
	 *            - der zu suchende Klassentyp. Kann eine leere Klasse sein
	 * @return - die Positionen des ersten freien Objektes
	 */
	public int getFirstFree(Object obj) {
		int num = 1;

		if (obj.getClass().equals(Actuator.class)) {
			for (XS_Object rem : myActuatorList) {
				if (rem.getType().equals("disabled")) {
					return num;
				} else
					num++;
			}
		} else if (obj.getClass().equals(Sensor.class)) {
			for (XS_Object rem : mySensorList) {
				if (rem.getType().equals("disabled")) {
					return num;
				} else
					num++;
			}
		} else if (obj.getClass().equals(Timer.class)) {
			for (XS_Object rem : myTimerList) {
				if (rem.getType().equals("disabled")) {
					return num;
				} else
					num++;
			}
		} else if (obj.getClass().equals(Script.class)) {
			for (XS_Object rem : myScriptList) {
				if (rem.getType().equals("disabled")) {
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

	public void setMyActuatorList(List<XS_Object> myActuatorList) {
		this.myActuatorList = myActuatorList;
	}

	public List<XS_Object> getMySensorList() {
		return mySensorList;
	}

	public void setMySensorList(List<XS_Object> mySensorList) {
		this.mySensorList = mySensorList;
	}

	public List<XS_Object> getMyTimerList() {
		return myTimerList;
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

	public void setMyMakroList(List<Makro> myMakroList) {
		this.myMakroList = myMakroList;
	}

	public List<Makro> getMyMakroList() {
		return myMakroList;
	}

	public void setMyProxAlerts(List<String> alerts) {
		this.proxAlerts = alerts;
	}

	public void addProxAlert(String name) {
		proxAlerts.add(name);

		// persistent speichern
		RuntimeStorage.getMyPers().saveData(proxAlerts, XS_PROXALERTS_TYPE);
	}

	public void removeProxAlert(int id) {
		if (proxAlerts == null)
			return;
		proxAlerts.remove(id);

		// speichern
		RuntimeStorage.getMyPers().saveData(proxAlerts, XS_PROXALERTS_TYPE);
	}

	public List<String> getMyProxAlertsList() {
		return proxAlerts;
	}

	public void setMyAlarms(List<String> alarms) {
		this.alarms = alarms;
	}

	public void addAlarm(String name) {
		alarms.add(name);

		// persistent speichern
		RuntimeStorage.getMyPers().saveData(alarms, XS_ALARMS_TYPE);
	}

	public void removeAlarm(int id) {
		if (alarms == null)
			return;
		alarms.remove(id);

		// speichern
		RuntimeStorage.getMyPers().saveData(alarms, XS_ALARMS_TYPE);
	}

	public List<String> getMyAlarmsList() {
		return alarms;
	}

}