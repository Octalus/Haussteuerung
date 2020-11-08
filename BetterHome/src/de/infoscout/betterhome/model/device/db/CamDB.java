package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class CamDB implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7438365613207560922L;
	private int id;
	private String name;
	private String url;
	private String username;
	private String password;
	private int roomId;
	private int stream; 
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getStream() {
		return stream;
	}

	public void setStream(int stream) {
		this.stream = stream;
	}

	public CamDB(){
	}
	
	public CamDB(String name){
		this.name=name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
