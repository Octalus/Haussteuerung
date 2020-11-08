package com.android.xs.model.device.components;

import java.io.Serializable;

import com.android.xs.model.device.Actuator;


/**
 * Ersetzt die Funktionszeiger in Java. Die Set Value kann für den jeweiligen
 * Aktor beim aufzeichen gesetzt werden mit dem Zugehöigen Wert
 * 
 * @author Viktor Mayer
 * 
 */
public class MakroFunction implements Serializable {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static final long serialVersionUID = 1686588888213171712L;
	private Actuator a;
	double val = -1;
	int func = -1;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	public MakroFunction(Actuator a, double val) {
		this.a = a;
		this.val = val;
	}
	
	public MakroFunction(Actuator a, int fn) {
		this.a = a;
		this.func = fn;
	}

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	private boolean setValue() {
		return a.setValue(val, true);
	}
	
	private boolean doFunction(){
		return a.doFunction(func);
	}
	
	public boolean execute(){
		if (val < 0 )
			return doFunction();
		else
			return setValue();
	}

}
