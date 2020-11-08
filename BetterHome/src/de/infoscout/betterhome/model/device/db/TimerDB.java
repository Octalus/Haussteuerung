package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class TimerDB extends XsDB implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1573749018309882870L;
	private boolean inactive = false;
	
	public TimerDB(){
	}
	
	public TimerDB(int number, String name){
		this.number=number;
		this.name=name;
	}
	
	public TimerDB(int number, String name, int roomId){
		this.number=number;
		this.name=name;
		this.roomId=roomId;
	}

	public boolean isInactive() {
		return inactive;
	}

	public void setInactive(boolean inactive) {
		this.inactive = inactive;
	}
	
	
}
