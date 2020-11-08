package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class PositionDB implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1785838994855010095L;
	private int id;
	private String name;
	private double lon;
	private double lat;
	private boolean onEntry = false;
	private int actNumber;
	private double value;
	private boolean lastOnEntry = false;
	private int radius = 150;
	
	public PositionDB(){
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public boolean isOnEntry() {
		return onEntry;
	}
	public void setOnEntry(boolean onEntry) {
		this.onEntry = onEntry;
	}
	public int getActNumber() {
		return actNumber;
	}
	public void setActNumber(int actNumber) {
		this.actNumber = actNumber;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isLastOnEntry() {
		return lastOnEntry;
	}

	public void setLastOnEntry(boolean lastOnEntry) {
		this.lastOnEntry = lastOnEntry;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	
	
}
