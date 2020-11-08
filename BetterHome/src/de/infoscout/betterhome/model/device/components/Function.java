package de.infoscout.betterhome.model.device.components;

import java.io.Serializable;

public class Function implements Serializable{

	/**
	 * Private Variablen
	 ***********************************************************************************************************************************************************/
	private static final long serialVersionUID = 7081894022086938776L;
	private String type;
	private String dsc;
	
	
	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/
	
	/**
	 * Konstruktor mit allen Parametern
	 * @param t - typ
	 * @param d - zustand
	 */
	public Function(String t, String d){
		this.type = t;
		this.dsc = d;
	}
	
	/**
	 * Default Konstruktor
	 */
	public Function (){
		type = "disabled";
		dsc = "";
	};
	
	
	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

public void setType(String type) {
	this.type = type;
}

public String getType() {
	return type;
}

public void setDsc(String dsc) {
	this.dsc = dsc;
}

public String getDsc() {
	return dsc;
}

  
}