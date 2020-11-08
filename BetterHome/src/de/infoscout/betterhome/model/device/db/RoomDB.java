package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class RoomDB implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4299022069565999954L;
	private int id;
	private String name;
	private int number;
	
	private Integer point1_x;
	private Integer point1_y;
	private Integer point2_x;
	private Integer point2_y;
	private Integer point3_x;
	private Integer point3_y;
	private Integer point4_x;
	private Integer point4_y;
	
	public RoomDB(){
	}
	
	public RoomDB(String name){
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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getPoint1_x() {
		return point1_x;
	}

	public Integer getPoint1_y() {
		return point1_y;
	}

	public void setPoint1_y(Integer point1_y) {
		this.point1_y = point1_y;
	}

	public Integer getPoint2_x() {
		return point2_x;
	}

	public void setPoint2_x(Integer point2_x) {
		this.point2_x = point2_x;
	}

	public Integer getPoint2_y() {
		return point2_y;
	}

	public void setPoint2_y(Integer point2_y) {
		this.point2_y = point2_y;
	}

	public Integer getPoint3_x() {
		return point3_x;
	}

	public void setPoint3_x(Integer point3_x) {
		this.point3_x = point3_x;
	}

	public Integer getPoint3_y() {
		return point3_y;
	}

	public void setPoint3_y(Integer point3_y) {
		this.point3_y = point3_y;
	}

	public Integer getPoint4_x() {
		return point4_x;
	}

	public void setPoint4_x(Integer point4_x) {
		this.point4_x = point4_x;
	}

	public Integer getPoint4_y() {
		return point4_y;
	}

	public void setPoint4_y(Integer point4_y) {
		this.point4_y = point4_y;
	}

	public void setPoint1_x(Integer point1_x) {
		this.point1_x = point1_x;
	}
}
