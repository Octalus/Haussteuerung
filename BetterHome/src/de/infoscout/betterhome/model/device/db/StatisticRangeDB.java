package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class StatisticRangeDB implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8104437899972545954L;
	private int id;
	private int sensorNumber;
	private int actuatorNumber;
	private long from = -1;
	private long to = -1;
	
	public StatisticRangeDB(){
	}
	
	public StatisticRangeDB(int sensorNumber, int actuatorNumber, long from, long to){
		this.sensorNumber = sensorNumber;
		this.actuatorNumber = actuatorNumber;
		this.from = from;
		this.to = to;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public long getFrom() {
		return from;
	}

	public void setFrom(long from) {
		this.from = from;
	}

	public long getTo() {
		return to;
	}

	public void setTo(long to) {
		this.to = to;
	}
}
