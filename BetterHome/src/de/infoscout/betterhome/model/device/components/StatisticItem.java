package de.infoscout.betterhome.model.device.components;

import java.util.Calendar;
import java.util.Date;

public class StatisticItem {
	private long utime;
	private double value;
	
	public StatisticItem(long ut, double v) {
		this.utime = ut;
		this.value = v;
	}
	
	public long getUtime() {
		return utime;
	}
	
	public Calendar getCalendar(){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(utime*1000);
		return cal;
	}
	
	public Date getDate(){
		Date date = new Date(utime*1000);
		return date;
	}

	public void setUtime(long utime) {
		this.utime = utime;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
}
