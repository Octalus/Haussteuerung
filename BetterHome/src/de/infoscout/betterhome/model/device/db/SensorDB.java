package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class SensorDB extends XsDB implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3864789860518253324L;

	public SensorDB(){
	}
	
	public SensorDB(int number, String name){
		this.number=number;
		this.name=name;
	}
	
	public SensorDB(int number, String name, int roomId){
		this.number=number;
		this.name=name;
		this.roomId=roomId;
	}
}
