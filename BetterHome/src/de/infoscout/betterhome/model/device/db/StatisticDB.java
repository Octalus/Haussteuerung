package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class StatisticDB implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6996570399751176354L;
	private int id;
	private int sensorNumber;
	private int actuatorNumber;
	private long timestamp = -1;
	private double value = -1;
	
	public StatisticDB(){
	}
	
	public StatisticDB(int sensorNumber, int actuatorNumber, long timestamp, double value){
		this.sensorNumber = sensorNumber;
		this.actuatorNumber = actuatorNumber;
		this.timestamp = timestamp;
		this.value = value;
	}
	
	public int getSensorNumber() {
		return sensorNumber;
	}

	public void setSensorNumber(int sensorNumber) {
		this.sensorNumber = sensorNumber;
	}
	
	public int getActuatorNumber() {
		return actuatorNumber;
	}

	public void setActuatorNumber(int actuatorNumber) {
		this.actuatorNumber = actuatorNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	
	
	

}
