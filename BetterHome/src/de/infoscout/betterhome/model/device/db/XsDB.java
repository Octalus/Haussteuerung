package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public abstract class XsDB implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6587388862371926127L;
	protected int id;
	protected int number;
	protected String name;
	protected int roomId;
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

}
