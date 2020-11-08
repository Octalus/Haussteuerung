package com.android.xs.model.device.components;

public abstract class AorS_Object extends XS_Object {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static final long serialVersionUID = -5313096649370786652L;

	protected double value;

	protected String unit;

	// die utime wird aus dem XS1 in Sekunden angegeben.
	// in Java in Millisec -> also mal 1000 bei der Ausgabe!!
	protected long utime;

	// gibt an ob es sich um ein dimmbares Objekt handelt (Dimmer)
	protected boolean dimmable;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * gibt zurück, ob es sich um ein dimmbares Objekt handelt
	 * 
	 * @return - true, falls dimmbar, sonst false
	 */
	public boolean isDimmable() {
		return dimmable;
	}

	/**
	 * Prüft anhand des Namens, ob es sich um ein dimmbares Objekt handelt und
	 * setzt den Wert
	 */
	public void checkDimmable() {
		if (type.equals("dimmer"))
			dimmable = true;
		else
			dimmable = false;
	}

	public boolean update() {
		return false;
	}

	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	public boolean setValue(double value, boolean remote) {
		this.value = value;
		return false;
	}

	public double getValue() {
		return value;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}

	public void setUtime(long l) {
		this.utime = l;
	}

	public long getUtime() {
		return utime;
	}

}