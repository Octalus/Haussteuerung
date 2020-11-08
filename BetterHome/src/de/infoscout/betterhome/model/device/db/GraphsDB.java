package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class GraphsDB implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5776763723735612208L;
	private int id;
	private int sensNumber;
	private int actNumber;
	private int sorting;
	
	public GraphsDB() {
	}
	
	public GraphsDB(int sensNumber, int actNumber, int sorting) {
		this.sensNumber=sensNumber;
		this.actNumber=actNumber;
		this.sorting=sorting;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSensNumber() {
		return sensNumber;
	}

	public void setSensNumber(int sensNumber) {
		this.sensNumber = sensNumber;
	}

	public int getActNumber() {
		return actNumber;
	}

	public void setActNumber(int actNumber) {
		this.actNumber = actNumber;
	}

	public int getSorting() {
		return sorting;
	}

	public void setSorting(int sorting) {
		this.sorting = sorting;
	}
}
