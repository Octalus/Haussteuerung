package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class ScriptDB extends XsDB implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9011738146071024540L;

	public ScriptDB(){
	}
	
	public ScriptDB(int number, String name){
		this.number=number;
		this.name=name;
	}
	
	public ScriptDB(int number, String name, int roomId){
		this.number=number;
		this.name=name;
		this.roomId=roomId;
	}
}
