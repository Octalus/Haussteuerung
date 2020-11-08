package de.infoscout.betterhome.view.menu.act;

import de.infoscout.betterhome.model.device.Actuator;

public class ActValueHolder{
	private Actuator actuator;
	private double value = -1;
	private int functionNummer = -1;
	private int makroNummer;
	
	public ActValueHolder(){
	}
	
	public ActValueHolder(Actuator act, double val, int makroNummer, int functionNummer){
		this.actuator = act;
		this.value = val;
		this.makroNummer = makroNummer;
		this.functionNummer=functionNummer;
	}

	public Actuator getActuator() {
		return actuator;
	}

	public void setActuator(Actuator actuator) {
		this.actuator = actuator;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getMakroNummer() {
		return makroNummer;
	}

	public void setMakroNummer(int makroNummer) {
		this.makroNummer = makroNummer;
	}

	public int getFunctionNummer() {
		return functionNummer;
	}

	public void setFunctionNummer(int functionNummer) {
		this.functionNummer = functionNummer;
	}
	
	
	
}
