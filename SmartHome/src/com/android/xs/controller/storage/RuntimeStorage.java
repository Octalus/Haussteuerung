package com.android.xs.controller.storage;

import java.util.ArrayList;
import java.util.LinkedList;
import com.android.xs.controller.remote.Http;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.RF_System;

/**
 * 
 * @author Viktor Mayer
 * 
 */
public class RuntimeStorage {

	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/

	// stellt die Verbindung zum Persistenz Element dar
	private static PersistantStorage myPers;
	// Stellt die Http Verbindungen bereit
	private static Http myHttp;
	// gibt nach Makros an, dass alle Daten aktualisiert werden müssen (für GPRS
	// Verbindung)
	private static boolean statusValid = true;
	// Das Hauptobjekt, welches vom Speicher oder vom XS1 geladen wird und im
	// Betrieb aktualisiert wird und an andere Activities weiter gereicht
	private static volatile Xsone myXsone;
	private static ArrayList<RF_System> Systems;
	private static ArrayList<String> Act_types;
	private static ArrayList<String> Sens_types;
	private static ArrayList<String> Tim_types;
	// private static ArrayList<String> Functions;

	// Beinhaltet nur die wichtigsten Daten um weitere aus dem XS1 abrufen zu
	// können (Passwort, user, ip)
	private static String[] xsdata;

	private static final String XS_DATA = "SmartHomeXS.data";
	private static final String XS_SYS = "SmartHomeXS.systems";
	private static final String XS_ACT_TYPE = "SmartHomeXS.actuators";
	private static final String XS_SENS_TYPE = "SmartHomeXS.sensors";
	private static final String XS_TIM_TYPE = "SmartHomeXS.timers";

	// private static final String XS_FNCT = "SmarthomeXS.functions";

	// Die Datenliste für die Subscribe Methode (als Service ausgeführt)
	private static LinkedList<String> abo_data_list = new LinkedList<String>();

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Hier werden die Daten für die Spinner ausgelesen. Dabei handelt es sich
	 * um die Strings für Systeme, Typen u.A. Zuerst wird versucht diese aus dem
	 * Speicher zu lesen. Sollten dort keine Werte hinterlegt sein werden diese
	 * vom XS1 abgefragt. Schließlich werden die Werte noch im RuntimeStorage
	 * gesetzt
	 */
	@SuppressWarnings("unchecked")
	public static void get_content() {

		// Systeme werden abgefragt
		ArrayList<RF_System> Systems = (ArrayList<RF_System>) RuntimeStorage.getMyPers().getData(XS_SYS);
		if (Systems == null) {
			Systems = Http.getInstance().get_list_systems();
			RuntimeStorage.getMyPers().saveData(Systems, XS_SYS);
		}
		RuntimeStorage.setSystems(Systems);

		// Aktuatortypen werden abgefragt
		ArrayList<String> Act_types = (ArrayList<String>) RuntimeStorage.getMyPers().getData(XS_ACT_TYPE);
		if (Act_types == null) {
			Act_types = Http.getInstance().get_types_actuators();
			RuntimeStorage.getMyPers().saveData(Act_types, XS_ACT_TYPE);
		}
		RuntimeStorage.setAct_types(Act_types);

		// Timer Typen werden abgefragt
		ArrayList<String> Tim_types = (ArrayList<String>) RuntimeStorage.getMyPers().getData(XS_TIM_TYPE);
		if (Tim_types == null) {
			Tim_types = Http.getInstance().get_types_timers();
			RuntimeStorage.getMyPers().saveData(Tim_types, XS_TIM_TYPE);
		}
		RuntimeStorage.setTim_types(Tim_types);

		// Sensor Typen werden abgefragt
		ArrayList<String> Sens_types = (ArrayList<String>) RuntimeStorage.getMyPers().getData(XS_SENS_TYPE);
		if (Sens_types == null) {
			Sens_types = Http.getInstance().get_types_sensors();
			RuntimeStorage.getMyPers().saveData(Sens_types, XS_SENS_TYPE);
		}
		RuntimeStorage.setSens_types(Sens_types);

		// Functions = (ArrayList<String>) myPers.getData(XS_FNCT);
		// if (Functions == null) {
		// Functions = Http.getInstance().get_list_functions(myXsone);
		// myPers.saveData(Functions, XS_FNCT);
		// }
	}

	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	public static void setMyPers(PersistantStorage myPers) {
		RuntimeStorage.myPers = myPers;
	}

	public static PersistantStorage getMyPers() {
		return myPers;
	}

	public static void setXsdata(String[] xsdata) {
		RuntimeStorage.xsdata = xsdata;
		myPers.saveData(xsdata, XS_DATA);
	}

	// ist das Objekt zur Laufzeit null (idR beim ersten Start) wird versucht es
	// aus dem Speicher zu lesen
	public static String[] getXsdata() {
		if (xsdata == null)
			xsdata = (String[]) myPers.getData(XS_DATA);
		return xsdata;
	}

	public static void setMyXsone(Xsone myXsone) {
		RuntimeStorage.myXsone = myXsone;
	}

	public static Xsone getMyXsone() {
		return myXsone;
	}

	public static void setSystems(ArrayList<RF_System> systems) {
		Systems = systems;
	}

	public static ArrayList<RF_System> getSystems() {
		return Systems;
	}

	public static void setAct_types(ArrayList<String> act_types) {
		Act_types = act_types;
	}

	public static ArrayList<String> getAct_types() {
		return Act_types;
	}

	public static void setSens_types(ArrayList<String> sens_types) {
		Sens_types = sens_types;
	}

	public static ArrayList<String> getSens_types() {
		return Sens_types;
	}
	
	public static void setProx_Alerts(LinkedList<String> prox_alerts) {
		myXsone.setMyProxAlerts(prox_alerts);
	}

	public static void setTim_types(ArrayList<String> tim_types) {
		Tim_types = tim_types;
	}

	public static ArrayList<String> getTim_types() {
		return Tim_types;
	}

	public static void setAbo_data_list(LinkedList<String> abo_data_list) {
		RuntimeStorage.abo_data_list = abo_data_list;
	}

	public static LinkedList<String> getAbo_data_list() {
		return abo_data_list;
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

}
