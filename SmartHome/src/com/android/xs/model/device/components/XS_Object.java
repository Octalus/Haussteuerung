package com.android.xs.model.device.components;

import java.io.Serializable;
import java.util.Locale;

public class XS_Object implements Serializable{

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static final long serialVersionUID = -2458892277554829691L;
	
	protected Integer number;

	protected String name;

	protected String type;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Überschreibt die equals Methode für die Listenprüfungen. Anhand des
	 * Namens wird dies geprüft
	 */
	@Override
	public boolean equals(Object o) {
		if (((XS_Object) o).getName().toLowerCase(Locale.GERMAN).equals(this.name.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return number;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
