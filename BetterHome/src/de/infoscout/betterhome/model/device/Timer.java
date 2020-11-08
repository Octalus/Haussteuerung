package de.infoscout.betterhome.model.device;

import java.sql.Timestamp;

import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.TimerDB;

@SuppressWarnings("serial")
public class Timer extends XS_Object {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	// Das nächste Schaltereignis
	private Timestamp next = new Timestamp(0);
	
	private boolean monday = false;
	private boolean tuesday = false;
	private boolean wednesday = false;
	private boolean thursday = false;
	private boolean friday = false;
	private boolean saturday = false;
	private boolean sunday = false;
	
	private int hour = 0;
	private int minute = 0;
	private int second = 0;
	
	private int random = 0;
	private int offset = 0;
	private int earliest = 0;
	private int latest = 0;
	
	private String actuator = "";
	private int function = 0;
	
	private TimerDB timerDB;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Konstrukor mit allen Parametern. Time kann hier als UTC (long) übergeben
	 * werden
	 * 
	 * @param name
	 *            - name
	 * @param type
	 *            - typ
	 * @param next
	 *            - Nächstes Schaltereignis (in 32 Bit Unix Zeit, UTC)
	 * @param number
	 *            - Die Nummer des Timers
	 * 
	 */
	public Timer(String name, String type, long next, int number) {
		this.setName(name);
		this.setType(type);
		this.setNext(next);
		this.setNumber(number);
	}

	/**
	 * Default Konstruktor
	 */
	public Timer() {
		// Type ist standarmäßig disabled
		this.setType("disabled");
	};

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	public void setNext(long next) {
		// Hier wieder mal 1000, da in Java in Millisec gerechnet wird
		this.next.setTime(next * 1000);
	}

	public Timestamp getNext() {
		return next;
	}
	
	public String getAppname() {
		String appName = getTimerDB() != null ? getTimerDB().getName() : getName();
		appName = appName.replace("_", " ");
		return appName;
	}
	
	public boolean isMonday() {
		return monday;
	}

	public void setMonday(boolean monday) {
		this.monday = monday;
	}

	public boolean isTuesday() {
		return tuesday;
	}

	public void setTuesday(boolean tuesday) {
		this.tuesday = tuesday;
	}

	public boolean isWednesday() {
		return wednesday;
	}

	public void setWednesday(boolean wednesday) {
		this.wednesday = wednesday;
	}

	public boolean isThursday() {
		return thursday;
	}

	public void setThursday(boolean thursday) {
		this.thursday = thursday;
	}

	public boolean isFriday() {
		return friday;
	}

	public void setFriday(boolean friday) {
		this.friday = friday;
	}

	public boolean isSaturday() {
		return saturday;
	}

	public void setSaturday(boolean saturday) {
		this.saturday = saturday;
	}

	public boolean isSunday() {
		return sunday;
	}

	public void setSunday(boolean sunday) {
		this.sunday = sunday;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getRandom() {
		return random;
	}

	public void setRandom(int random) {
		this.random = random;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getEarliest() {
		return earliest;
	}

	public void setEarliest(int earliest) {
		this.earliest = earliest;
	}

	public int getLatest() {
		return latest;
	}

	public void setLatest(int latest) {
		this.latest = latest;
	}

	public String getActuator() {
		return actuator;
	}

	public void setActuator(String actuator) {
		this.actuator = actuator;
	}

	public int getFunction() {
		return function;
	}

	public void setFunction(int function) {
		this.function = function;
	}

	public void setNext(Timestamp next) {
		this.next = next;
	}

	public TimerDB getTimerDB() {
		return timerDB;
	}

	public void setTimerDB(TimerDB timerDB) {
		this.timerDB = timerDB;
	}
	
	

}