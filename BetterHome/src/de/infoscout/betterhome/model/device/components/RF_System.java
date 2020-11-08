package de.infoscout.betterhome.model.device.components;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Die Klasse stellt ein RF System dar. Bestimme Systeme bieten nur dazu
 * gehörige Funktionen in der Liste der definierbaren Funktionen. Alle
 * unterstützden RF Systeme werden bei erster Installation aus der XS1
 * ausgelesen und im Gerät gespeichert. Um die Liste zu ändern muss zurücksetzen
 * gewählt werden
 * 
 * @author Viktor Mayer
 * 
 */
public class RF_System implements Serializable{

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static final long serialVersionUID = 3103880212168164472L;
	private String name;
	private ArrayList<String> function;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Konstruktor für das RF_System mit allen nötigen Parametern
	 * 
	 * @param name
	 *            - Der name des RF_Systems
	 * @param fnct
	 *            - Die Liste der dazu gehörigen Funktionen vom Typ
	 *            ArrayList<String>
	 */
	public RF_System(String name, ArrayList<String> fnct) {
		this.setName(name);
		this.setFunction(fnct);
	}
	
	public RF_System() {
	}

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Überschreibt die equals Funktion. Geprüft werden RF_Systeme nur anhand
	 * des Namens
	 * 
	 * @return - true, wenn gleich, sonst false
	 */
	@Override
	public boolean equals(Object o) {
		if (((RF_System) o).getName().equals(this.name))
			return true;
		else
			return false;

	}

	/**
	 * Überschreibt die toString Methode. Der Name wird als String zurück
	 * gegeben.
	 * 
	 * @return - Der name des RF_Systems als String
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setFunction(ArrayList<String> function) {
		this.function = function;
	}

	public ArrayList<String> getFunction() {
		return function;
	}

}
