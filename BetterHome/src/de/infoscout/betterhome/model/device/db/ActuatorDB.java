package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class ActuatorDB extends XsDB implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6871478474753281572L;
	private boolean useFunction = false;

	public ActuatorDB(){
	}
	
	public ActuatorDB(int number, String name){
		this.number=number;
		this.name=name;
	}
	
	public ActuatorDB(int number, String name, int roomId){
		this.number=number;
		this.name=name;
		this.roomId=roomId;
	}

	public boolean isUseFunction() {
		return useFunction;
	}

	public void setUseFunction(boolean useFunction) {
		this.useFunction = useFunction;
	}
	
	
}
