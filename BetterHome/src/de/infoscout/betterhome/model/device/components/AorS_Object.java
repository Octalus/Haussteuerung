package de.infoscout.betterhome.model.device.components;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.db.StatisticDB;
import de.infoscout.betterhome.model.device.db.StatisticRangeDB;
import android.content.Context;

public abstract class AorS_Object extends XS_Object {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static final long serialVersionUID = -5313096649370786652L;

	protected double value;
	protected double newvalue;
	protected String unit;

	// die utime wird aus dem XS1 in Sekunden angegeben.
	// in Java in Millisec -> also mal 1000 bei der Ausgabe!!
	protected long utime;

	// gibt an ob es sich um ein dimmbares Objekt handelt (Dimmer)
	protected boolean dimmable;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/*private ArrayList<StatisticRangeDB> getNichtUberlappendeRanges(List<StatisticRangeDB> statisticRanges) {
		List<StatisticRangeDB> stuecke = statisticRanges;
		
		// check auf ueberlappende Stuecke
		ArrayList<StatisticRangeDB> nichtUeberlappendeStuecke = new ArrayList<StatisticRangeDB>();
		ArrayList<Integer> verarbeitet = new ArrayList<Integer>();
		
		for (int i=0; i<stuecke.size(); i++) {
			StatisticRangeDB stueckCurrent = stuecke.get(i);
			
			if (stuecke.size() == 1) {
				// es gibt nur eine Range
				nichtUeberlappendeStuecke.add(stueckCurrent);
				verarbeitet.add(i);
			} else {
				if (!verarbeitet.contains(i)) {
					// aktuelle Range ist noch nicht verarbeitet
					
					if (i == stuecke.size()-1){
						// letzte Range bei mehr als einer Range
						if (!verarbeitet.contains(i)) {
							nichtUeberlappendeStuecke.add(stueckCurrent);
							verarbeitet.add(i);
						}
						
					} else {
						// es gibt eine Nachfolge-Range j
						for (int j=(i+1); j<stuecke.size(); j++) {
							if (!verarbeitet.contains(j)) {
								// noch nicht verarbeitet
								StatisticRangeDB stueckNext = stuecke.get(j);
								
								if (stueckNext.getFrom() <= stueckCurrent.getTo() ) {
									// Nachfolge-Range ragt an die aktuelle Range rein
									if (stueckCurrent.getTo() < stueckNext.getTo()) {
										// Wenn das Ende der Nachfolge Range weiter weg ist als das Ende der aktuellen Range
										// dann verlaengere aktuelle Range auf die Laenge der aktuellen Range (lueckenlos)
										stueckCurrent.setTo(stueckNext.getTo());
										
										// markiere Nachfolge Range als verarbeitet
										verarbeitet.add(j);
									}
								} else {
									// keine reinragende Nachfolge Range
									nichtUeberlappendeStuecke.add(stueckCurrent);
									
									// markiere aktuelle Range als verarbeitet
									verarbeitet.add(i);
									break;
								}
							}
						}
					}	
				}
			}
		}
		
		return nichtUeberlappendeStuecke;
	}*/
	
	private void addLuecke(ArrayList<StatisticRangeDB> luecken, int number, long from, long to) {
		StatisticRangeDB luecke = null;
		
		if (this instanceof Sensor) {
			luecke = new StatisticRangeDB(number, -1, from, to);
		} else if (this instanceof Actuator) {
			luecke = new StatisticRangeDB(-1, number, from, to);
		}
		
		luecken.add(luecke);
	}
	
	private ArrayList<StatisticRangeDB> getLuecken(List<StatisticRangeDB> nichtUberlappendeRanges, long from, long to) {
		// Grenzen der nicht ueberlappenden Stuecke auf Luecken prüfen
		ArrayList<StatisticRangeDB> luecken = new ArrayList<StatisticRangeDB>();
		
		if (nichtUberlappendeRanges.size() > 0) {
		
			boolean fromUeberdeckt = false;
			
			int sizeNichtUeberlappendeRanges = nichtUberlappendeRanges.size();
			
			// erste Range in meine Range hinein ragend?
			if ( nichtUberlappendeRanges.get(0).getFrom() <= from ) {
				fromUeberdeckt = true;
			}
			
			
			for (int i=0; i<nichtUberlappendeRanges.size(); i++) {
				StatisticRangeDB currentRange = nichtUberlappendeRanges.get(i);
				
				if (i==0 && !fromUeberdeckt) {
					// von from bis Beginn erste Luecke
					addLuecke(luecken, this.getNumber(), from, currentRange.getFrom());
				}
				
				// Luecken zwischen den Ranges
				if (i+1 < sizeNichtUeberlappendeRanges) {
					// es gibt eine Nachfolge Range
					StatisticRangeDB nextRange = nichtUberlappendeRanges.get(i+1);
					
					if (currentRange.getTo() < nextRange.getFrom()) {
						addLuecke(luecken, this.getNumber(), currentRange.getTo(), nextRange.getFrom());
					}
				}
				
				if (i==sizeNichtUeberlappendeRanges-1) {
					// von Ende letzte Range bis to
					addLuecke(luecken, this.getNumber(), currentRange.getTo(), to);
				}
				
			}
		} else {
			// keine Ranges existent - gesamter Bereich ist eine Luecke!
			addLuecke(luecken, this.getNumber(), from, to);
		}
		
		return luecken;
		
	}
	
	public void updateWithStatsToDB(Context context, long from, long to) {
		DatabaseStorage db = new DatabaseStorage(context);
		
		List<StatisticRangeDB> statisticRanges = null;
		if (this instanceof Sensor) {
			statisticRanges = db.getStatisticRangesForSensorFromTo(this.getNumber(), from, to);
		} else if (this instanceof Actuator) {
			statisticRanges = db.getStatisticRangesForActuatorFromTo(this.getNumber(), from, to);
		}
	
		// checke, ob alles in DB liegt oder lücken in den Ranges da sind
		// wenn ja, müssen die Lücken gelesen und in DB
		
		// kombiniere überlappende Ranges
		//ArrayList<StatisticRangeDB> nichtUberlappendeRanges = getNichtUberlappendeRanges(statisticRanges);
		
		// Finde Luecken
		ArrayList<StatisticRangeDB> luecken = getLuecken(statisticRanges, from, to);
		
		// Lese Luecken und schreibe in DB
		for (int i=0; i<luecken.size(); i++) {
			StatisticRangeDB luecke = luecken.get(i);
			
			AorS_Object updated = null;
			if (this instanceof Sensor) {
				updated = RuntimeStorage.getMyHttp().get_states_sensor((Sensor)this, luecke.getFrom(), luecke.getTo());	
			} else if (this instanceof Actuator) {
				updated = RuntimeStorage.getMyHttp().get_states_actuator((Actuator)this, luecke.getFrom(), luecke.getTo());	
			}

			if (updated != null) {
				ArrayList<StatisticItem> statistics = updated.getStatistics();
				
				// add all read statistics to DB
				for (int j=0; j<statistics.size(); j++) {
					StatisticDB statistic = null;
					if (this instanceof Sensor) {
						statistic = new StatisticDB(getNumber(), -1, statistics.get(j).getUtime(), statistics.get(j).getValue());
					} else if (this instanceof Actuator) {
						statistic = new StatisticDB(-1, getNumber(), statistics.get(j).getUtime(), statistics.get(j).getValue());
					}
					db.createStatistic(statistic);
				}
				
				// add new statistic range to DB
				StatisticRangeDB statisticRange = null;
				if (this instanceof Sensor) {
					statisticRange = new StatisticRangeDB(getNumber(), -1, luecke.getFrom(), luecke.getTo());
				} else if (this instanceof Actuator) {
					statisticRange = new StatisticRangeDB(-1, getNumber(), luecke.getFrom(), luecke.getTo());
				}
				db.createStatisticRange(statisticRange);
			}
		}
		
		// get statistics from DB for current range
		ArrayList<StatisticDB> statisticDBs = null;
		if (this instanceof Sensor) {
			statisticDBs = db.getStatisticsForSensorFromRange(this.getNumber(), from, to);
		} else if (this instanceof Actuator) {
			statisticDBs = db.getStatisticsForActuatorFromRange(this.getNumber(), from, to);
		}
		
		// set statistic items based on DB entries
		statistics = new ArrayList<StatisticItem>();
		for (int i=0; i< statisticDBs.size(); i++) {
			statistics.add(new StatisticItem(statisticDBs.get(i).getTimestamp(), statisticDBs.get(i).getValue()));
		}
								
		db.closeDB();
	}
	
	/**
	 * gibt zurück, ob es sich um ein dimmbares Objekt handelt
	 * 
	 * @return - true, falls dimmbar, sonst false
	 */
	public boolean isDimmable() {
		return dimmable;
	}

	/**
	 * Prüft anhand des Namens, ob es sich um ein dimmbares Objekt handelt und
	 * setzt den Wert
	 */
	public void checkDimmable() {
		if (type.equals("dimmer"))
			dimmable = true;
		else
			dimmable = false;
	}

	public boolean update() {
		return false;
	}
	
	public boolean updateWithStats(Calendar from, Calendar to, Context context){
		return false;
	}

	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	public boolean setValue(double value, boolean remote) {
		this.value = value;
		return false;
	}

	public double getValue() {
		return value;
	}
	
	public double getNewvalue() {
		return newvalue;
	}

	public void setNewvalue(double newvalue) {
		this.newvalue = newvalue;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getUnit() {
		return unit;
	}

	public void setUtime(long l) {
		this.utime = l;
	}

	public long getUtime() {
		return utime;
	}

}