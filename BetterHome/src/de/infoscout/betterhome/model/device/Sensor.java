package de.infoscout.betterhome.model.device;

import java.util.Calendar;

import android.content.Context;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.components.AorS_Object;
import de.infoscout.betterhome.model.device.db.SensorDB;

/**
 * 
 * @author Viktor Mayer
 * 
 */
@SuppressWarnings("serial")
public class Sensor extends AorS_Object {
	
	private static final boolean CACHE_HISTORY_TO_DB = true;

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	String status = "unknown";
	private SensorDB sensorDB;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Konstrukor mit allen Parametern
	 * 
	 * @param nu
	 *            - nummer
	 * @param n
	 *            - name
	 * @param t
	 *            - typ
	 * @param v
	 *            - value
	 * @param ut
	 *            - utime
	 * @param u
	 *            - unit
	 * @param fl
	 *            - Function List
	 */
	public Sensor(int nu, String n, String t, double v, long ut, String u, String st) {
		this.setNumber(nu);
		this.setName(n);
		this.setType(t);
		this.setValue(v, false);
		this.setUtime(ut);
		this.setUnit(u);
		this.setStatus(st);
	}

	/**
	 * Default Konstruktor
	 */
	public Sensor() {
		// Type ist standarm��ig disabled
		this.setType("disabled");
	};

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Updated die Klasse Sensor von dem aus dem XS1 gelesenen Daten
	 * 
	 * @return - boolean. bei erfolg true, sonst false
	 */
	@Override
	public boolean update() {
		Sensor updated = RuntimeStorage.getMyHttp().get_state_sensor(this);

		if (updated != null) {
			this.setNumber(updated.getNumber());
			this.setName(updated.getName());
			this.setType(updated.getType());
			this.setValue(updated.getValue(), false);
			this.setUnit(updated.getUnit());
			this.setUtime(updated.getUtime());
			this.setStatus(updated.getStatus());
		} else
			return false;
		return true;
	}
	
	/**
	 * Updated die Klasse Sensor von dem aus dem XS1 gelesenen Daten
	 * 
	 * @return - boolean. bei erfolg true, sonst false
	 */
	@Override
	public boolean updateWithStats(Calendar fromC, Calendar toC, Context context) {
		long from = fromC.getTimeInMillis()/1000;
		long to = toC.getTimeInMillis()/1000;
		
		if (CACHE_HISTORY_TO_DB) {
			updateWithStatsToDB(context, from, to);
		} else {
			
			// immer live abholen
			Sensor updated = RuntimeStorage.getMyHttp().get_states_sensor(this, from, to);

			if (updated != null) {
				this.setNumber(updated.getNumber());
				this.setName(updated.getName());
				this.setType(updated.getType());
				this.setValue(updated.getValue(), false);
				this.setUnit(updated.getUnit());
				this.setUtime(updated.getUtime());
				this.setStatus(updated.getStatus());
				this.setStatistics(updated.getStatistics());
			} else {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * updatet den value des Sensors und sendet zugleich ein Befehl an die XS1
	 * �ber das Http Objekt. Nur bei virtuellen Sensoren m�glich!
	 * 
	 * @param - der neue Wert als double
	 * @return - boolean. TRUE bei Erfolg, sonst FALSE
	 */
	@Override
	public boolean setValue(double value, boolean remote) {
		// Wert neu setzen
		this.value = value;
		// neuen Wert senden
		if (remote)
			return RuntimeStorage.getMyHttp().set_state_sensor(this);
		else
			return true;
	}
	
	public String getAppname() {
		String appName = getSensorDB() != null ? getSensorDB().getName() : getName(); 
		appName = appName.replace("_", " ");
		return appName;
	}

	/**
	 * getter/setter
	 ***********************************************************************************************************************************************************/

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if (status != null && !status.equals(""))
			this.status = status;
	}

	public SensorDB getSensorDB() {
		return sensorDB;
	}

	public void setSensorDB(SensorDB sensorDB) {
		this.sensorDB = sensorDB;
	}
}