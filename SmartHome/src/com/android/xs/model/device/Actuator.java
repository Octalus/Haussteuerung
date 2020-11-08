package com.android.xs.model.device;

import java.util.LinkedList;
import java.util.List;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.components.AorS_Object;
import com.android.xs.model.device.components.Function;

/**
 * 
 * @author Viktor Mayer
 * 
 */
public class Actuator extends AorS_Object{

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static final long serialVersionUID = 5533878849476669109L;
	//private static final long serialVersionUID = 5532292359565277362L;
	// Die Liste der Funktionen eines Aktuatoren (bis zu vier). Wird mit der
	// Liste aller Aktuatoren gelesen und gesetzt
	private List<Function> myFunction;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Konstrukor mit allen Parametern
	 * 
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
	public Actuator(int nu, String n, String t, double v, long ut, String u,
			List<Function> fl) {
		this.setNumber(nu);
		this.setName(n);
		this.setType(t);
		this.setValue(v, false);
		this.setUtime(ut);
		this.setUnit(u);
		this.setMyFunction(fl);

		// der Name gibt Auskunft ob dimmbar oder nicht
		this.checkDimmable();
	}

	/**
	 * Default Konstruktor. Hier muss checkDimmable() aufgerufen werden, da der
	 * Wert sonst nicht getezt wird!
	 */
	public Actuator() {		
		// Typ ist standardäßig disabled
		this.setType("disabled");
		this.setMyFunction(new LinkedList<Function>());
	};

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * 
	 */
	public void setMyFunction(List<Function> myFunction) {
		this.myFunction = myFunction;
	}

	/**
	 * 
	 * @return
	 */
	public List<Function> getMyFunction() {
		return myFunction;
	}

	/**
	 * Ruft eine Zuvor gespeichert Funktion des Aktuatoren auf
	 * 
	 * @param num
	 *            - Der Wert der Funktion (1-4)
	 * @return - true bei Erfolg, sonst false
	 */
	public boolean doFunction(int num) {
		return RuntimeStorage.getMyHttp().set_state_actuator(this, num);
	}

	/**
	 * Updated die Klasse Aktuator von dem aus dem XS1 gelesenen Daten
	 * 
	 * @return bei erfolg true, sonst false
	 */
	@Override
	public boolean update() {
		Actuator updated = RuntimeStorage.getMyHttp().get_state_actuator(this);

		if (updated != null) {
			this.setNumber(updated.getNumber());
			this.setName(updated.getName());
			this.setType(updated.getType());
			this.setValue(updated.getValue(), false);
			this.setUnit(updated.getUnit());
			this.setUtime(updated.getUtime());
		} else
			return false;
		return true;
	}

	/**
	 * updatet den value des Actuators und sendet zugleich ein Befehl an die XS1
	 * über das Http Objekt.
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
			return RuntimeStorage.getMyHttp().set_state_actuator(this);
		else
			return true;

	}

}