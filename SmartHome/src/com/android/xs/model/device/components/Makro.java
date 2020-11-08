package com.android.xs.model.device.components;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Ein Makro ist eine Abfolge von Funktionen, welche vom Benutzer ausgeführt
 * wurden. Diese können dann in einem Schritt durchgeführt werden
 * 
 * @author Viktor Mayer
 * 
 */
public class Makro implements Serializable {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static final long serialVersionUID = 3558824718033840247L;
	private String name = "";
	// Die Liste der Makro Funktionen
	private List<MakroFunction> makros;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	public Makro() {
		makros = new LinkedList<MakroFunction>();
	}

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * fügt dem Makro eine Funktion hinzu. Beschränkt auf 64 Funktionen
	 * (maximale Zahl von Aktuatoren)
	 * 
	 * @param mf
	 *            - die neue Makrofunktion
	 */
	public void add_function(MakroFunction mf) {
		if (makros.size() < 100)
			makros.add(mf);
	}

	/**
	 * gibt alle Funtionen des Makros wieder
	 * 
	 * @return - Gibt true zurück, falls alle Funktionen ausgeführt werden
	 *         konnte, sonst false
	 */
	public boolean replay() {
		boolean check = true;
		for (MakroFunction f : makros) {
			if (!f.execute())
				check = false;
		}
		return check;
	}

	/**
	 * Getter and Setter
	 ***********************************************************************************************************************************************************/

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
