package com.android.xs.controller.remote;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.android.xs.model.device.Actuator;
import com.android.xs.model.device.Script;
import com.android.xs.model.device.Sensor;
import com.android.xs.model.device.Timer;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.Function;
import com.android.xs.model.device.components.RF_System;
import com.android.xs.model.device.components.XS_Object;
import com.android.xs.model.error.XsError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Die Http Klasse stellt die Schnittstell zum Internet dar. Anfragen werden an
 * die XS1 gsndet und Antworten ausgewrtet und in einem Passenden Format an die
 * Anfragende Klasse weiter geleitet. Die Klasse ist als Singleton implementiert
 * 
 * @author Viktor Mayer
 * 
 */
public class Http {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static Http http = null;
	private String user_BASIC = "";
	private String pass_BASIC = "";

	// die Verbidnung für das subscribe und learn
	static HttpURLConnection url_subscribe_c = null;
	static HttpURLConnection url_learn_c = null;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * privater Konstruktor fï¿½r Singleton
	 */
	private Http() {
	};

	/**
	 * Funtkionen
	 ***********************************************************************************************************************************************************/

	/**
	 * getInstance gibt das Objekt der Klasse zurï¿½ck, falls angelegt, sonst
	 * legt es zuvor eins an (Singleton)
	 * 
	 * @return - Das Singlton Objekt der Klasse Http
	 */
	public static Http getInstance() {
		if (http == null)
			http = new Http();
		return http;
	}

	/**
	 * Liest die XS1 Daten aus dem Gerät aus und speichert sie in einem Xsone
	 * Objekt ab, welches dann zurück gegeben wird
	 * 
	 * @param device
	 *            - Das abzufragende Objekt (mindestens IP muss gesetzt sein)
	 * @return - Gibt das abzufragende Objekt mit aktualisierten Daten zurï¿½ck,
	 *         bei Fehler NULL
	 */
	public Xsone get_config_info(Xsone device) {
		// IP für zukünftige Http Anfragen speichern
		CommandBuilder.setIp(device.getMyIpSetting().getIp());
		CommandBuilder.setUser(device.getUsername());
		CommandBuilder.setPass(device.getPassword());
		user_BASIC = device.getUsername();
		pass_BASIC = device.getPassword();

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_config_info");

		// Die JSON Objekte der Anwort werden angelegt
		JSONObject json_main;
		JSONObject json_info;
		JSONObject json_ip;
		try {
			// Das JSON Objekt wird geparst und geholt
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				XsError e = new XsError(json_main.getInt("error"));
				Log.e("XSRequest", e.getError());
				// TODO: extended error report
				// XsError.printError(e.getError());
				return null;
			}

			json_info = json_main.getJSONObject("info");
			json_ip = json_info.getJSONObject("current");
		} catch (Exception e) {
			// TODO: extended error report!
			// XsError.printError(e.getMessage());
			return null;
		}

		// Das Übergebene Xsone Objekt wird aktualisiert
		try {
			device.setDeviceName(json_info.getString("devicename"));
			device.setHardware(json_info.getString("hardware"));
			device.setBootloader(json_info.getString("bootloader"));
			device.setFirmware(json_info.getString("firmware"));
			device.setSystem(json_info.getInt("systems"));
			device.setMaxActuators(json_info.getInt("maxactuators"));
			device.setMaxSensors(json_info.getInt("maxsensors"));
			device.setMaxTimers(json_info.getInt("maxtimers"));
			device.setMaxScripts(json_info.getInt("maxscripts"));
			device.setMaxRooms(json_info.getInt("maxrooms"));
			device.setUptime(json_info.getLong("uptime"));

			org.json.JSONArray array = json_info.getJSONArray("features");
			LinkedList<String> alist = new LinkedList<String>();
			for (int x = 0; x < array.length(); x++) {
				alist.add(array.getString(x));
			}
			device.setFeatures(alist);
			device.setMac(json_info.getString("mac"));
			device.setAutoip(json_info.getString("autoip"));

			// ip darf nicht neu gesetzt werden!! sonst immer lokale ip!
			// device.getMyIpSetting().setIp(json_ip.getString("ip"));
			device.getMyIpSetting().setNetmask(json_ip.getString("netmask"));
			device.getMyIpSetting().setGateway(json_ip.getString("gateway"));
			device.getMyIpSetting().setDns(json_ip.getString("dns"));

		} catch (Exception e) {
			// TODO: extended error report!
			// XsError.printError(e.getMessage());
			return null;
		}

		return device;
	}

	/**
	 * Fragt bim XS1 alle gespeicherten Aktuatoren ab und gibt diese zurï¿½ck
	 * 
	 * @return - eine Liste von RemObjects, welche alle Aktuatoren enthï¿½lt,
	 *         bei Fehler NULL, bei keinen Aktuatoren eine leere Liste
	 */
	public List<XS_Object> get_list_actuators() {

		LinkedList<XS_Object> act = new LinkedList<XS_Object>();

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_list_actuators");

		// Die JSON Objekte der Anwort werden angelegt
		JSONObject json_main;
		JSONArray actuators;

		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				XsError e = new XsError(json_main.getInt("error"));
				Log.e("XSRequest", e.getError());
				XsError.printError(e.getError());
				return null;
			}

			actuators = json_main.getJSONArray("actuator");

		} catch (Exception e) {
			XsError.printError(e.getMessage());
			return null;
		}

		JSONObject json_actual;
		JSONObject json_function;
		for (int num = 0; num < actuators.length(); num++) {

			try {
				json_actual = actuators.getJSONObject(num);

				// Die Funktionen des aktuellen Aktuators auslesen
				LinkedList<Function> fn_list = new LinkedList<Function>();
				JSONArray json_functions;

				json_functions = json_actual.getJSONArray("function");

				for (int y = 0; y < json_functions.length(); y++) {
					json_function = json_functions.getJSONObject(y);
					fn_list.add(new Function(json_function.getString("type"), json_function.getString("dsc")));
				}

				// Actuator wird angelegt und der Liste der Aktuatoren hinzu
				// gefï¿½gt
				act.add(new Actuator(num + 1, json_actual.getString("name"), json_actual.getString("type"), json_actual.getDouble("value"),
						json_actual.getLong("utime"), json_actual.getString("unit"), fn_list));

			} catch (JSONException e) {
				XsError.printError(e.getMessage());
				return null;
			}
		}

		return act;
	}

	/**
	 * sendet einen neuen Wert an die XS1. Dieser wird aus dem RemoteObject
	 * ausgelesen
	 * 
	 * @param act
	 *            - Das RemoteObject (Actuator)
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean set_state_actuator(Actuator act) {

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri(String.valueOf(act.getValue()), String.valueOf(act.getNumber()), "set_value_actuator");

		JSONObject json_main;
		try {
			json_main = request(uri);
			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	/**
	 * sendet einen neuen Wert an die XS1. Dieser wird als Funktionswert
	 * ï¿½bergeben. Die Funktionen mï¿½ssen zuvor definiert worden sein
	 * 
	 * @param act
	 *            - Das RemoteObject (Actuator)
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean set_state_actuator(Actuator act, int num) {

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri(String.valueOf(num), String.valueOf(act.getNumber()), "set_function_actuator");

		JSONObject json_main;
		try {
			json_main = request(uri);
			// Prï¿½fen ob Befehl ausgefï¿½hrt wurde und true zurï¿½ck geben
			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	/**
	 * Fragt den Zustand eines Aktuators ab. Sendet dazu ein Http Request an die
	 * XS1 und holt den Zustand eines Aktuatoren. als JSONObjekt. die Daten
	 * wrden in einem neuen Aktuator Objekt gespeichert und bei Erfolg dieses
	 * dann zurï¿½ck gegeben.
	 * 
	 * @param act
	 *            - der zu Prï¿½fende Actuator
	 * @return - gibt den Aktuator mit neuen Werten zurï¿½ck, NULL bei Fehler
	 */
	public Actuator get_state_actuator(Actuator act) {

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri(String.valueOf(act.getNumber()), "get_state_actuator");

		// Das JSON Hauptobjekt wird geholt
		JSONObject json_main;
		JSONObject json_actuator;
		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return null;
			}

			json_actuator = json_main.getJSONObject("actuator");
		} catch (JSONException e) {
			return null;
		}

		// Die Daten aus dem Actuator auslesen
		Actuator updated = new Actuator();
		try {
			updated.setNumber(json_actuator.getInt("number"));
			updated.setName(json_actuator.getString("name"));
			updated.setType(json_actuator.getString("type"));
			updated.setValue(json_actuator.getDouble("value"), false);
			updated.setUnit(json_actuator.getString("unit"));
			updated.setUtime(json_actuator.getLong("utime"));
			// prï¿½fen ob dimmbar, da default Konstruktor verwendet wurde
			updated.checkDimmable();
		} catch (JSONException e) {
			return null;
		}

		return updated;
	}

	/**
	 * Holt die Liste aller Sensoren aus dem XS1 und gibt Diese als eine Liste
	 * von Objekten der Klasse Remote Object zurï¿½ck
	 * 
	 * @param device
	 *            - Xsone. Das Xsone Objekt, welches die RemObjekte enthï¿½lt
	 * @return - Gibt eine Liste von Remote Objekten mit allen Sensoren
	 *         zurï¿½ck, null bei Fehler
	 */
	public List<XS_Object> get_list_sensors() {
		LinkedList<XS_Object> sens = new LinkedList<XS_Object>();

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_list_sensors");

		// Die JSON Objekte der Anwort werden angelegt
		JSONObject json_main;
		JSONArray senors;

		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				XsError e = new XsError(json_main.getInt("error"));
				Log.e("XSRequest", e.getError());
				XsError.printError(e.getError());
				return null;
			}

			senors = json_main.getJSONArray("sensor");

		} catch (Exception e) {
			XsError.printError(e.getMessage());
			return null;
		}

		JSONObject json_actual;
		for (int num = 0; num < senors.length(); num++) {

			try {
				json_actual = senors.getJSONObject(num);
			} catch (JSONException e) {
				XsError.printError(e.getMessage());
				return null;
			}
			// wird gesondert behandelt.. nicht immer vorhanden!
			String state = "unknown";
			try {
				// status ist neu in firmware 3
				JSONArray status = json_actual.getJSONArray("state");
				if (status.length() > 0)
					state = status.getString(0);
			} catch (JSONException e) {
				// wenn nicht vorhanden, dann eben nicht (alte firmware??)
			}

			// Sensor wird angelegt und der Liste der Remote Objects hinzu
			// gefügt
			// if (!json_actual.getString("type").equals("disabled"))
			try {
				sens.add(new Sensor(num + 1, json_actual.getString("name"), json_actual.getString("type"), json_actual.getDouble("value"), json_actual
						.getLong("utime"), json_actual.getString("unit"), state));
			} catch (JSONException e) {
				return null;
			}
		}
		return sens;
	}

	/**
	 * Liest für einen übergebenen Sensor aktuelle Sensorwerte aus und gibt
	 * diese als ein Sensorobjekt zurück
	 * 
	 * @param sens
	 *            - Sensor. Das auszulesende Sensor Objekt
	 * @return - Sensor. Das Sensor Objekt mit neuen Werten aus der XS1
	 */
	public Sensor get_state_sensor(Sensor sens) {
		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri(String.valueOf(sens.getNumber()), "get_state_sensor");

		// Das JSON Hauptobjekt wird geholt
		JSONObject json_main;
		JSONObject json_sensor;
		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return null;
			}

			json_sensor = json_main.getJSONObject("sensor");
		} catch (JSONException e) {
			return null;
		}

		// Die Daten aus dem Sesnor auslesen
		Sensor updated = new Sensor();
		try {
			updated.setNumber(json_sensor.getInt("number"));
			updated.setName(json_sensor.getString("name"));
			updated.setType(json_sensor.getString("type"));
			updated.setValue(json_sensor.getDouble("value"), false);
			updated.setUnit(json_sensor.getString("unit"));
			// UTC Zeit in Sekunden!!
			updated.setUtime(json_sensor.getLong("utime"));
		} catch (JSONException e) {
			return null;
		}
		// wird gesondert behandelt.. nicht immer vorhanden je nach Firmware!
		try {
			String state = "unknown";
			JSONArray status = json_sensor.getJSONArray("state");
			if (status.length() > 0)
				state = status.getString(0);
			updated.setStatus(state);
		} catch (JSONException e) {
			return updated;
		}

		return updated;
	}

	/**
	 * Nur in Ausnahmefällen bei "virtuellen" Sensoren sinnvoll. Es
	 * können so z.B. auch externe Datenquellen auf der Speicherkarte
	 * mitgeloggt werden oder in Skriptentscheidungen miteinbezogen werden. Das
	 * Sensorsystem muss vom Type virtual sein, damit die Werte gesetzt werden
	 * dürfen.
	 * 
	 * @param sens
	 *            - Das zu ändernde Sensorobjekt
	 * @return - boolean. TRUE bei erfolg, sonst FALSE
	 */
	public boolean set_state_sensor(Sensor sens) {

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri(String.valueOf(sens.getValue()), String.valueOf(sens.getNumber()), "set_state_sensor");

		JSONObject json_main;
		try {
			json_main = request(uri);
			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
		return true;

	}

	/**
	 * Liest alle Timer aus der XS1 und gibt diese als eine Liste aller Timer
	 * Objekte zurück
	 * 
	 * @param - Xsone. Stellt das auszulesende XS1 dar
	 * @return - Eine Liste aller Timer, die nicht deaktiviert sind
	 */
	public List<XS_Object> get_list_timers() {
		LinkedList<XS_Object> tim = new LinkedList<XS_Object>();

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_list_timers");

		// Die JSON Objekte der Anwort werden angelegt
		JSONObject json_main;
		JSONArray timers;

		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				XsError e = new XsError(json_main.getInt("error"));
				XsError.printError(e.getError());
				Log.e("XSRequest", e.getError());
				return null;
			}

			timers = json_main.getJSONArray("timer");

		} catch (Exception e) {
			XsError.printError(e.getMessage());
			return null;
		}

		JSONObject json_actual;
		for (int num = 0; num < timers.length(); num++) {

			try {
				json_actual = timers.getJSONObject(num);

				// Timer wird angelegt und der Liste der Timer hinzu
				// gefï¿½gt
				// if (!json_actual.getString("type").equals("disabled"))
				tim.add(new Timer(json_actual.getString("name"), json_actual.getString("type"), json_actual.getLong("next"), num + 1));

			} catch (JSONException e) {
				XsError.printError(e.getMessage());
				return null;
			}
		}

		return tim;
	}

	/**
	 * Liest alle Scripts aus der XS1 und gibt diese als eine Liste aller Script
	 * Objekte zurï¿½ck
	 * 
	 * @param - Xsone. Stellt das auszulesende XS1 dar
	 * @return - Eine Liste aller Scripts
	 */
	public List<XS_Object> get_list_scripts() {
		LinkedList<XS_Object> script_list = new LinkedList<XS_Object>();

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_list_scripts");

		// Die JSON Objekte der Anwort werden angelegt
		JSONObject json_main;
		JSONArray scripts;

		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return null;
			}

			scripts = json_main.getJSONArray("script");
		} catch (Exception e) {
			return null;
		}

		JSONObject json_actual;
		for (int num = 0; num < scripts.length(); num++) {

			try {
				json_actual = scripts.getJSONObject(num);

				// Script wird angelegt und der Liste der Scripts hinzu
				// gefï¿½gt
				// if (!json_actual.getString("type").equals("disabled"))
				script_list.add(new Script(json_actual.getString("name"), num + 1, json_actual.getString("type")));

			} catch (JSONException e) {
				return null;
			}
		}

		return script_list;
	}

	/**
	 * 
	 * @param device
	 * @return
	 */
	public ArrayList<RF_System> get_list_systems() {
		ArrayList<RF_System> sys = new ArrayList<RF_System>();
		JSONArray fnct;

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_list_systems");

		// Die JSON Objekte der Anwort werden angelegt
		JSONObject json_main;
		JSONArray system;

		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return null;
			}

			system = json_main.getJSONArray("system");

		} catch (Exception e) {
			return null;
		}

		JSONObject json_actual;
		for (int num = 0; num < system.length(); num++) {

			try {
				ArrayList<String> fncts = new ArrayList<String>();
				json_actual = system.getJSONObject(num);
				fnct = json_actual.getJSONArray("functions");
				for (int i = 0; i < fnct.length(); i++) {
					fncts.add(fnct.getString(i));
				}

				sys.add(new RF_System(json_actual.getString("name"), fncts));

			} catch (JSONException e) {
				return null;
			}
		}

		return sys;
	}

	public ArrayList<String> get_types_actuators() {
		ArrayList<String> act_types = new ArrayList<String>();

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_types_actuators");

		// Die JSON Objekte der Anwort werden angelegt
		JSONObject json_main;
		JSONArray act_type;

		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return null;
			}

			act_type = json_main.getJSONArray("actuatortype");

		} catch (Exception e) {
			return null;
		}

		JSONObject json_actual;
		for (int num = 0; num < act_type.length(); num++) {
			try {
				json_actual = act_type.getJSONObject(num);
				act_types.add(json_actual.getString("name"));
			} catch (JSONException e) {
				return null;
			}
		}

		return act_types;
	}

	public ArrayList<String> get_types_timers() {
		ArrayList<String> tim_types = new ArrayList<String>();

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_types_timers");

		// Die JSON Objekte der Anwort werden angelegt
		JSONObject json_main;
		JSONArray tim_type;

		try {
			json_main = request(uri);
			tim_type = json_main.getJSONArray("timertype");

		} catch (Exception e) {
			return null;
		}

		JSONObject json_actual;
		for (int num = 0; num < tim_type.length(); num++) {
			try {
				json_actual = tim_type.getJSONObject(num);
				tim_types.add(json_actual.getString("name"));
			} catch (JSONException e) {
				return null;
			}
		}

		return tim_types;
	}

	public ArrayList<String> get_types_sensors() {
		ArrayList<String> sens_types = new ArrayList<String>();

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_types_sensors");

		// Die JSON Objekte der Anwort werden angelegt
		JSONObject json_main;
		JSONArray tim_type;

		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return null;
			}

			tim_type = json_main.getJSONArray("sensortype");

		} catch (Exception e) {
			return null;
		}

		JSONObject json_actual;
		for (int num = 0; num < tim_type.length(); num++) {
			try {
				json_actual = tim_type.getJSONObject(num);
				sens_types.add(json_actual.getString("name"));
			} catch (JSONException e) {
				return null;
			}
		}

		return sens_types;
	}

	public ArrayList<String> get_list_functions() {
		ArrayList<String> fncts = new ArrayList<String>();

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_list_functions");

		// Die JSON Objekte der Anwort werden angelegt
		JSONObject json_main;
		JSONArray function;

		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return null;
			}

			function = json_main.getJSONArray("function");

		} catch (Exception e) {
			return null;
		}

		JSONObject json_actual;
		for (int num = 0; num < function.length(); num++) {

			try {
				json_actual = function.getJSONObject(num);

				fncts.add(json_actual.getString("name"));

			} catch (JSONException e) {
				return null;
			}
		}

		return fncts;
	}

	/**
	 * Startet ein Abonnement von allen Aktor / Sensor Ereignissen. Das XS1
	 * behï¿½lt die aktuelle Verbindung offen und liefert bei einen
	 * Schaltereignis, bzw. empfangenen Sensorwert eine Zeile mit Zeitstempel
	 * und Wert aus.
	 * 
	 * @param data_list
	 * 
	 * @param tv
	 * @throws IOException
	 */
	public void subscribe(LinkedList<String> data_list) throws IOException {

		InputStreamReader isr = null;
		BufferedReader subscribe_reader = null;
		URL url;

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("subscribe");

		String userPassword = user_BASIC + ":" + pass_BASIC;
		String encoding = Base64.encodeToString(userPassword.getBytes(), Base64.DEFAULT);

		// die HttpURLConnection wird angelegt und konfiguriert
		url = new URL(uri.toString());
		url_subscribe_c = (HttpURLConnection) url.openConnection();
		url_subscribe_c.setDoOutput(false);
		url_subscribe_c.setRequestMethod("GET");
		url_subscribe_c.setReadTimeout(0);
		url_subscribe_c.setDoInput(true);
		url_subscribe_c.setUseCaches(false);
		url_subscribe_c.setRequestProperty("Connection", "close");
		url_subscribe_c.setChunkedStreamingMode(0);
		if (!pass_BASIC.equals(""))
			url_subscribe_c.setRequestProperty("Authorization", "Basic " + encoding);

		// Verbindung aufbauen
		url_subscribe_c.connect();

		data_list.add(" abgeschlossen!\n");

		isr = new InputStreamReader(url_subscribe_c.getInputStream());

		// Der Buffered Reader empfï¿½ngt die Daten der Verbindung zur XS1
		subscribe_reader = new BufferedReader(isr);

		// Charbuffer ist eine alternative Lösung, da readline blockierend
		// aufgerufen wird, und dadurch immer eins verzögert, weil schon
		// getInputStream blockiert
		CharBuffer c = CharBuffer.allocate(100);
		while (subscribe_reader.read(c) > 0) {
			data_list.add(c.flip() + "\n");
			c.clear();
		}
	}

	public void unsubscribe() {
		// Verursachte eine Exception beim subscribe
		url_subscribe_c.disconnect();
	}

	public LinkedList<String> learn(String system) throws IOException {

		LinkedList<String> result = new LinkedList<String>();

		InputStreamReader isr = null;
		BufferedReader subscribe_reader = null;
		URL url;

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri(system, "learn");

		String userPassword = user_BASIC + ":" + pass_BASIC;
		String encoding = Base64.encodeToString(userPassword.getBytes(), Base64.DEFAULT);

		// die HttpURLConnection wird angelegt und konfiguriert
		url = new URL(uri.toString());
		url_learn_c = (HttpURLConnection) url.openConnection();
		url_learn_c.setDoOutput(false);
		url_learn_c.setRequestMethod("GET");
		url_learn_c.setReadTimeout(35000);
		url_learn_c.setDoInput(true);
		url_learn_c.setUseCaches(false);
		url_learn_c.setRequestProperty("Connection", "close");
		url_learn_c.setChunkedStreamingMode(0);
		if (!pass_BASIC.equals(""))
			url_learn_c.setRequestProperty("Authorization", "Basic " + encoding);

		// Verbindung aufbauen
		url_learn_c.connect();

		isr = new InputStreamReader(url_learn_c.getInputStream());

		// Der Buffered Reader empfï¿½ngt die Daten der Verbindung zur XS1
		subscribe_reader = new BufferedReader(isr);

		int count = 0;
		// Charbuffer ist eine alternative Lösung, da readline blockierend
		// aufgerufen wird, und dadurch immer eins verzögert, weil schon
		// getInputStream blockiert
		CharBuffer c = CharBuffer.allocate(200);
		while (subscribe_reader.read(c) > 0) {
			// ab dem 3. muss das Komma am anfang entfernt werden
			c.flip();
			if (count > 1)
				c.position(2);
			result.add(c + "\n");
			c.clear();
			count++;
		}
		return result;
	}

	/**
	 * Liest die Protokollinformationen aus dem XS1 und gibt diese zurï¿½ck
	 * 
	 * @return - gibt eine Array von Strings mit zwei Werten (Version und
	 *         Typ)zurï¿½ck. NULL bei Fehler
	 */
	public String[] get_protocol_info() {
		// das zurï¿½ck zu gebende String Array mit den Protokoll Informationen
		String[] prot_info = new String[2];

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_protocol_info");

		// Das JSON Hauptobjekt wird geholt
		JSONObject json_main;
		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return null;
			}

		} catch (JSONException e) {
			return null;
		}

		// Die Daten auslesen und zurï¿½ck geben
		try {
			prot_info[0] = json_main.getString("version");
			prot_info[1] = json_main.getString("type");
		} catch (JSONException e) {
			return null;
		}
		return prot_info;

	}

	/**
	 * Liest die XS1 interne Batterie gestï¿½tzte Echtzeit Uhr (RTC) arbeitet
	 * mit UTC / GMT Zeit.
	 * 
	 * @return - Die Zeit des XS1 als Calendar Objekt. NULL bei Fehler
	 */
	public Calendar get_date_time() {
		// das zurï¿½ck zu gebende String Array mit den Protokoll Informationen
		Calendar dat_tim = Calendar.getInstance();

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri("get_date_time");

		// Das JSON Hauptobjekt wird geholt
		JSONObject json_main;
		JSONObject date;
		JSONObject time;
		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return null;
			}

			date = json_main.getJSONObject("date");
			time = json_main.getJSONObject("time");
		} catch (JSONException e) {
			return null;
		}

		// die Ausgelesenen Werte setzen und zurï¿½ck geben
		try {
			dat_tim.set(date.getInt("year"), date.getInt("month"), date.getInt("day"), time.getInt("hour"), time.getInt("min"), time.getInt("sec"));
		} catch (JSONException e) {
			return null;
		}
		return dat_tim;
	}

	/**
	 * Funktion zu Anlegen eines neuen Sensors
	 * 
	 * @param data
	 * @param num
	 * @return
	 */
	public boolean add_sensor(String[] data, String num) {
		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri(data, num, "add_sensor");

		JSONObject json_main;
		try {
			json_main = request(uri);
			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	/**
	 * Funktion zu Anlegen eines neuen Sensors
	 * 
	 * @param data
	 * @param num
	 * @return
	 */
	public boolean add_actuator(String[] data, String num) {
		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri(data, num, "add_actuator");

		JSONObject json_main;
		try {
			json_main = request(uri);

			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return false;
			}

		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	/**
	 * Funktion zum anlegen eines neuen Timers
	 * 
	 * @param data
	 * @param number
	 * @return
	 */
	public boolean add_timer(List<String> data, int number) {

		Uri uri = CommandBuilder.buildUri(data, String.valueOf(number), "add_timer");

		JSONObject json_main;
		try {
			json_main = request(uri);
			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
		return true;

	}

	/**
	 * Funktion zum anlegen eines neuen Skripts
	 * 
	 * @param data
	 * @param number
	 * @return
	 */
	public boolean add_script(String[] data, int number) {

		Uri uri = CommandBuilder.buildUri(data, String.valueOf(number), "add_script");

		JSONObject json_main;
		try {
			json_main = request(uri);
			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
		return true;

	}

	/**
	 * Lï¿½scht ein Objekt aus dem XS1. castet nach dem Typ das Objekt und setzt
	 * es anhand der Nummer auf disabled
	 * 
	 * @param Obj
	 *            - das zu lï¿½schende Objekt
	 * @return - true bei erfolg, sonst false
	 */
	public boolean remove_Object(Object Obj) {

		// Die Abfrage wird angelegt
		Uri uri = CommandBuilder.buildUri(Obj, "remove_object");

		JSONObject json_main;
		try {
			json_main = request(uri);
			// Fehlerbehandlung
			if (json_main.getString("type").equals("void")) {
				Log.e("XSRequest", new XsError(json_main.getInt("error")).getError());
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
		return true;

	}

	/**
	 * Setter fï¿½r die IP, mit der Das Http Objekt zukï¿½nftig Verbindungen zum
	 * XS1 aufbaut. wird an den CommandBuilder durch gereicht
	 * 
	 * @param ip
	 *            - String. Die IP des XS1 als String
	 */
	public void setIp(String ip) {
		CommandBuilder.setIp(ip);
	}

	// /**
	// * Getter fï¿½r die im Http Objekt gespeicherte IP des XS1
	// *
	// * @return - gibt die gespeicherte IP als String zurï¿½ck
	// */
	// public String getIp() {
	//
	// }

	/**
	 * sendt die Anfrage und parst zuglich die Antwort in ein JSON Objekt,
	 * welches dann zurï¿½ck gegeben wird
	 * 
	 * @param uri
	 *            - Das Uri Objekt, welches die Adresse enthï¿½lt
	 * @return - Ein geparstes JSON Objekt mit allen Daten , NULL bei Fehler
	 * @throws JSONException
	 */
	private JSONObject request(Uri uri) throws JSONException {
		Object json_obj = new Object();

		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 7000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		// Client Request und Response wird angelegt
		DefaultHttpClient client = new DefaultHttpClient(httpParameters);

		// BASIC authent
		if (pass_BASIC.trim().length() > 0) {
			client.getCredentialsProvider().setCredentials(new AuthScope(uri.getHost(), uri.getPort(), AuthScope.ANY_SCHEME),
					new UsernamePasswordCredentials(user_BASIC, pass_BASIC));
		}

		HttpGet request = new HttpGet(uri.toString());

		HttpResponse response;

		// Der request wird gesendet
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			// TODO: extended error report!
			XsError.printError(e.getMessage());
			return new JSONObject();
		} catch (IOException e) {
			// TODO: extended error report!
			XsError.printError(e.getMessage());
			return new JSONObject();
		}

		// aus dem Response muss ein JSon Object gemappt werden. Hierzu wird der
		// cname mit Klammern entfernt
		String str = null;
		try {
			str = new Scanner(response.getEntity().getContent()).useDelimiter("\\A").next();
		} catch (IllegalStateException e1) {
			// TODO: extended error report!
			XsError.printError(e1.getMessage());
		} catch (IOException e1) {
			// TODO: extended error report!
			XsError.printError(e1.getMessage());
		}

		// der Index der Klammern wird gesucht um diese zu entfernen
		int begin = str.indexOf("(");
		int end = str.indexOf(")");
		char[] buf = new char[str.length() - (begin + 1) - (str.length() - (end))];
		str.getChars(begin + 1, end, buf, 0);

		// Der JSONTOkener legt das JSONObjekt an
		json_obj = new JSONTokener(new String(buf)).nextValue();

		return (JSONObject) json_obj;
	}

}