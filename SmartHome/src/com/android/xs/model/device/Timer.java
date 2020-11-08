package com.android.xs.model.device;

import java.sql.Timestamp;

import com.android.xs.model.device.components.XS_Object;

@SuppressWarnings("serial")
public class Timer extends XS_Object {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	// Das nächste Schaltereignis
	private Timestamp next = new Timestamp(0);

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Konstrukor mit allen Parametern. Time kann hier als UTC (long) übergeben
	 * werden
	 * 
	 * @param name
	 *            - name
	 * @param type
	 *            - typ
	 * @param next
	 *            - Nächstes Schaltereignis (in 32 Bit Unix Zeit, UTC)
	 * @param number
	 *            - Die Nummer des Timers
	 * 
	 */
	public Timer(String name, String type, long next, int number) {
		this.setName(name);
		this.setType(type);
		this.setNext(next);
		this.setNumber(number);
	}

	/**
	 * Default Konstruktor
	 */
	public Timer() {
		// Type ist standarmäßig disabled
		this.setType("disabled");
	};

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	public void setNext(long next) {
		// Hier wieder mal 1000, da in Java in Millisec gerechnet wird
		this.next.setTime(next * 1000);
	}

	public Timestamp getNext() {
		return next;
	}

}