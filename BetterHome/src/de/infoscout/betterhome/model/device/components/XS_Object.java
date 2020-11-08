package de.infoscout.betterhome.model.device.components;

import java.io.Serializable;
import java.util.ArrayList;

public class XS_Object implements Serializable{

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static final long serialVersionUID = -2458892277554829691L;
	
	protected Integer number;

	protected String name;

	protected String type;
	
	protected ArrayList<StatisticItem> statistics;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	
	/**
	 * Überschreibt die equals Methode für die Listenprüfungen. Anhand der
	 * Nummer wird dies geprüft
	 */
	@Override
	public boolean equals(Object o) {
		if (o != null && getNumber() != null && ((XS_Object) o).getNumber() == ( getNumber().intValue())) {
			return true;
		}
		return false;
	}
	
	public boolean equalsName(Object o) {
		if (o != null && getName() != null && ((XS_Object) o).getName().equals(getName())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getNumber() {
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
	
	public ArrayList<StatisticItem> getStatistics() {
		return statistics;
	}

	public void setStatistics(ArrayList<StatisticItem> statistics) {
		this.statistics = statistics;
	}
	
	

}
