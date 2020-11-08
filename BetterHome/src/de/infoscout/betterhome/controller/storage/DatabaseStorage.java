package de.infoscout.betterhome.controller.storage;

import java.util.ArrayList;
import java.util.List;

import de.infoscout.betterhome.model.device.db.ActuatorDB;
import de.infoscout.betterhome.model.device.db.CamDB;
import de.infoscout.betterhome.model.device.db.GraphsDB;
import de.infoscout.betterhome.model.device.db.PositionDB;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.model.device.db.ScriptDB;
import de.infoscout.betterhome.model.device.db.SensorDB;
import de.infoscout.betterhome.model.device.db.SettingDB;
import de.infoscout.betterhome.model.device.db.StatisticDB;
import de.infoscout.betterhome.model.device.db.StatisticRangeDB;
import de.infoscout.betterhome.model.device.db.TimerDB;
import de.infoscout.betterhome.view.utils.Utilities;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseStorage extends SQLiteOpenHelper {
	
	// Name und Version der Datenbank
	private static final String DATABASE_NAME = "betterhome.db";
	private static final int DATABASE_VERSION = 19;
	
	// Name und Attribute der Tabelle "settings"
	public static final String TABLE_SETTINGS = "SETTINGS";
	public static final String KEY = "KEY";
	public static final String VALUE = "VALUE";
	
	// Generelle Namen
	public static final String ID = "ID";
	public static final String NUMBER = "NUMBER";
	public static final String NAME = "NAME";
	
	// Name und Attribute der Tabelle "actuator"
	public static final String TABLE_ACTUATOR = "ACTUATOR";
	public static final String ACTUATOR_ROOM = "ROOM_ID";
	public static final String ACTUATOR_USE_FUNCTION = "USE_FUNCTION";
	
	// Name und Attribute der Tabelle "sensor"
	public static final String TABLE_SENSOR = "SENSOR";
	public static final String SENSOR_ROOM = "ROOM_ID";
	
	// Name und Attribute der Tabelle "timer"
	public static final String TABLE_TIMER = "TIMER";
	public static final String TIMER_ROOM = "ROOM_ID";
	public static final String TIMER_INACTIVE = "INACTIVE";
	
	// Name und Attribute der Tabelle "script"
	public static final String TABLE_SCRIPT = "SCRIPT";
	public static final String SCRIPT_ROOM = "ROOM_ID";
	
	// Name und Attribute der Tabelle "room"
	public static final String TABLE_ROOM = "ROOM";
	public static final String ROOM_POINT1_X = "POINT1_X";
	public static final String ROOM_POINT1_Y = "POINT1_Y";
	public static final String ROOM_POINT2_X = "POINT2_X";
	public static final String ROOM_POINT2_Y = "POINT2_Y";
	public static final String ROOM_POINT3_X = "POINT3_X";
	public static final String ROOM_POINT3_Y = "POINT3_Y";
	public static final String ROOM_POINT4_X = "POINT4_X";
	public static final String ROOM_POINT4_Y = "POINT4_Y";
	
	// Name und Attribute der Tabelle "camera"
	public static final String TABLE_CAM = "CAM";
	public static final String CAM_URL = "URL";
	public static final String CAM_USERNAME = "USERNAME";
	public static final String CAM_PASSWORD = "PASSWORD";
	public static final String CAM_ROOM = "ROOM_ID";
	public static final String CAM_STREAM = "STREAM";
	
	// Name und Attribute der Tabelle "position"
	public static final String TABLE_POSITION = "POSITION";
	public static final String POS_LAT = "LAT";
	public static final String POS_LON = "LON";
	public static final String POS_ON_ENTRY = "ON_ENTRY";
	public static final String POS_ACT_NUMBER = "ACT_NUMBER";
	public static final String POS_ACT_VALUE = "ACT_VALUE";
	public static final String POS_LAST_ON_ENTRY = "LAST_ON_ENTRY";
	public static final String POS_RADIUS = "RADIUS";
	
	// Name und Attribute der Tabelle "statistic"
	public static final String TABLE_STATISTIC = "STATISTIC";
	public static final String STAT_SENS_NUMBER = "SENS_NUMBER";
	public static final String STAT_ACT_NUMBER = "ACT_NUMBER";
	public static final String STAT_TIMESTAMP = "TIMESTAMP";
	public static final String STAT_VALUE = "VALUE";
	
	// Name und Attribute der Tablle "statistic_range"
	public static final String TABLE_STATISTIC_RANGE = "STATISTIC_RANGE";
	public static final String STAT_RANGE_SENS_NUMBER = "SENS_NUMBER";
	public static final String STAT_RANGE_ACT_NUMBER = "ACT_NUMBER";
	public static final String STAT_RANGE_FROM = "RANGE_FROM";
	public static final String STAT_RANGE_TO = "RANGE_TO";
	
	// Name und Attribute der Tablle "graphs"
	public static final String TABLE_GRAPHS = "GRAPHS";
	public static final String GRAPHS_SENS_NUMBER = "SENS_NUMBER";
	public static final String GRAPHS_ACT_NUMBER = "ACT_NUMBER";
	public static final String GRAPHS_SORTING = "SORTING";
	
	
	// Tabelle settings anlegen
	private static final String TABLE_SETTINGS_CREATE = "CREATE TABLE " + TABLE_SETTINGS + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY + " TEXT, " + VALUE + " TEXT);";
	
	// Tabelle actuator anlegen
	private static final String TABLE_ACTUATOR_CREATE = "CREATE TABLE " + TABLE_ACTUATOR + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER + " INTEGER, " + NAME + " TEXT, " + ACTUATOR_ROOM + " INTEGER, " + ACTUATOR_USE_FUNCTION + " INTEGER);";
	
	// Tabelle sensor anlegen
	private static final String TABLE_SENSOR_CREATE = "CREATE TABLE " + TABLE_SENSOR + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER + " INTEGER, " + NAME + " TEXT, " + SENSOR_ROOM +" INTEGER);";
	
	// Tabelle timer anlegen
	private static final String TABLE_TIMER_CREATE = "CREATE TABLE " + TABLE_TIMER + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER + " INTEGER, " + NAME + " TEXT, " + TIMER_ROOM + " INTEGER, " + TIMER_INACTIVE + " INTEGER);";
	
	// Tabelle timer anlegen
	private static final String TABLE_SCRIPT_CREATE = "CREATE TABLE " + TABLE_SCRIPT + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER + " INTEGER, " + NAME + " TEXT, " + SCRIPT_ROOM + " INTEGER);";
	
	// Tabelle room anlegen
	private static final String TABLE_ROOM_CREATE = "CREATE TABLE " + TABLE_ROOM + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NUMBER + " INTEGER, " + NAME + " TEXT"+
			", " + ROOM_POINT1_X + " INTEGER, " + ROOM_POINT1_Y + " INTEGER, " + ROOM_POINT2_X + " INTEGER, " + ROOM_POINT2_Y + " INTEGER, " + ROOM_POINT3_X + " INTEGER, " + ROOM_POINT3_Y + " INTEGER, " + ROOM_POINT4_X + " INTEGER, " + ROOM_POINT4_Y + " INTEGER);";
	
	// Tabelle cam anlegen
	private static final String TABLE_CAM_CREATE = "CREATE TABLE " + TABLE_CAM + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + CAM_URL + " TEXT, " + CAM_USERNAME + " TEXT, " + CAM_PASSWORD + " TEXT, " + CAM_ROOM + " INTEGER, " + CAM_STREAM + " INTEGER);";
	
	// Tabelle position anlegen
	private static final String TABLE_POSITION_CREATE = "CREATE TABLE " + TABLE_POSITION + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, "+POS_LON+" REAL, "+POS_LAT+" REAL, "+POS_ON_ENTRY+" INTEGER, "+POS_ACT_NUMBER+" INTEGER, "+POS_ACT_VALUE+" REAL, "+POS_LAST_ON_ENTRY+" INTEGER, "+POS_RADIUS+" INTEGER);";
	
	// Tabelle statistic anlegen
	private static final String TABLE_STATISTIC_CREATE = "CREATE TABLE " + TABLE_STATISTIC + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+STAT_SENS_NUMBER+" INTEGER, "+STAT_ACT_NUMBER+" INTEGER, "+STAT_TIMESTAMP+" INTEGER, "+STAT_VALUE+" REAL);";
	
	// Tabelle statistic_range anlegen
	private static final String TABLE_STATISTIC_RANGE_CREATE = "CREATE TABLE " + TABLE_STATISTIC_RANGE + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+STAT_RANGE_SENS_NUMBER+" INTEGER, "+STAT_RANGE_ACT_NUMBER+" INTEGER, "+STAT_RANGE_FROM+" INTEGER, "+STAT_RANGE_TO+" INTEGER);";
	
	// Tabelle statistic_range anlegen
	private static final String TABLE_GRAPHS_CREATE = "CREATE TABLE " + TABLE_GRAPHS + 
	      " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+GRAPHS_SENS_NUMBER+" INTEGER, "+GRAPHS_ACT_NUMBER+" INTEGER, "+GRAPHS_SORTING+" INTEGER);";

	// Tabellen löschen
	@SuppressWarnings("unused")
	private static final String TABLE_SETTINGS_DROP = "DROP TABLE IF EXISTS " + TABLE_SETTINGS;
	@SuppressWarnings("unused")
	private static final String TABLE_ACTUATOR_DROP = "DROP TABLE IF EXISTS " + TABLE_ACTUATOR;
	@SuppressWarnings("unused")
	private static final String TABLE_SENSOR_DROP = "DROP TABLE IF EXISTS " + TABLE_SENSOR;
	@SuppressWarnings("unused")
	private static final String TABLE_TIMER_DROP = "DROP TABLE IF EXISTS " + TABLE_TIMER;
	@SuppressWarnings("unused")
	private static final String TABLE_SCRIPT_DROP = "DROP TABLE IF EXISTS " + TABLE_SCRIPT;
	@SuppressWarnings("unused")
	private static final String TABLE_ROOM_DROP = "DROP TABLE IF EXISTS " + TABLE_ROOM;
	@SuppressWarnings("unused")
	private static final String TABLE_CAM_DROP = "DROP TABLE IF EXISTS " + TABLE_CAM;
	@SuppressWarnings("unused")
	private static final String TABLE_POSITION_DROP = "DROP TABLE IF EXISTS " + TABLE_POSITION;
	@SuppressWarnings("unused")
	private static final String TABLE_STATISTIC_DROP = "DROP TABLE IF EXISTS " + TABLE_STATISTIC;
	@SuppressWarnings("unused")
	private static final String TABLE_STATISTIC_RANGE_DROP = "DROP TABLE IF EXISTS " + TABLE_STATISTIC_RANGE;
	@SuppressWarnings("unused")
	private static final String TABLE_GRAPHS_DROP = "DROP TABLE IF EXISTS " + TABLE_GRAPHS;
	
	public DatabaseStorage(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_SETTINGS_CREATE);
		db.execSQL(TABLE_ACTUATOR_CREATE);
		db.execSQL(TABLE_SENSOR_CREATE);
		db.execSQL(TABLE_TIMER_CREATE);
		db.execSQL(TABLE_SCRIPT_CREATE);
		db.execSQL(TABLE_ROOM_CREATE);
		db.execSQL(TABLE_CAM_CREATE);
		db.execSQL(TABLE_POSITION_CREATE);
		db.execSQL(TABLE_STATISTIC_CREATE);
		db.execSQL(TABLE_STATISTIC_RANGE_CREATE);
		db.execSQL(TABLE_GRAPHS_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db,
		int oldVersion, int newVersion) {
		
		if (oldVersion < 19) {
			db.execSQL("ALTER TABLE "+TABLE_ACTUATOR+" ADD "+ACTUATOR_USE_FUNCTION+" INTEGER;");
			db.execSQL("UPDATE "+TABLE_ACTUATOR+" SET "+ACTUATOR_USE_FUNCTION+" = 0;");
		}
		
		if (oldVersion < 18) {
			db.execSQL("ALTER TABLE "+TABLE_ROOM+" ADD "+ROOM_POINT1_X+" INTEGER;");
			db.execSQL("ALTER TABLE "+TABLE_ROOM+" ADD "+ROOM_POINT1_Y+" INTEGER;");
			db.execSQL("ALTER TABLE "+TABLE_ROOM+" ADD "+ROOM_POINT2_X+" INTEGER;");
			db.execSQL("ALTER TABLE "+TABLE_ROOM+" ADD "+ROOM_POINT2_Y+" INTEGER;");
			db.execSQL("ALTER TABLE "+TABLE_ROOM+" ADD "+ROOM_POINT3_X+" INTEGER;");
			db.execSQL("ALTER TABLE "+TABLE_ROOM+" ADD "+ROOM_POINT3_Y+" INTEGER;");
			db.execSQL("ALTER TABLE "+TABLE_ROOM+" ADD "+ROOM_POINT4_X+" INTEGER;");
			db.execSQL("ALTER TABLE "+TABLE_ROOM+" ADD "+ROOM_POINT4_Y+" INTEGER;");
		}
		
		if (oldVersion == 15) {
			db.execSQL("ALTER TABLE "+TABLE_GRAPHS+" ADD "+GRAPHS_SORTING+" INTEGER;");
			db.execSQL("UPDATE "+TABLE_GRAPHS+" SET "+GRAPHS_SORTING+" = "+ID+";");
		}
		
		if (oldVersion < 15) {
			db.execSQL(TABLE_GRAPHS_CREATE);
			db.execSQL("UPDATE "+TABLE_GRAPHS+" SET "+GRAPHS_SORTING+" = "+ID+";");
		}
		
		if (oldVersion == 13) {
			db.execSQL("ALTER TABLE "+TABLE_STATISTIC+" ADD "+STAT_ACT_NUMBER+" INTEGER;");
			db.execSQL("ALTER TABLE "+TABLE_STATISTIC_RANGE+" ADD "+STAT_RANGE_ACT_NUMBER+" INTEGER;");
			
			db.execSQL("UPDATE "+TABLE_STATISTIC+" SET "+STAT_ACT_NUMBER+" = -1;");
			db.execSQL("UPDATE "+TABLE_STATISTIC_RANGE+" SET "+STAT_RANGE_ACT_NUMBER+" = -1;");
		} else if (oldVersion == 12) {
			db.execSQL(TABLE_STATISTIC_RANGE_CREATE);
			db.execSQL("ALTER TABLE "+TABLE_STATISTIC+" ADD "+STAT_SENS_NUMBER+" INTEGER;");
			
			db.execSQL("ALTER TABLE "+TABLE_STATISTIC+" ADD "+STAT_ACT_NUMBER+" INTEGER;");
			db.execSQL("ALTER TABLE "+TABLE_STATISTIC_RANGE+" ADD "+STAT_RANGE_ACT_NUMBER+" INTEGER;");
			
			db.execSQL("UPDATE "+TABLE_STATISTIC+" SET "+STAT_ACT_NUMBER+" = -1;");
			db.execSQL("UPDATE "+TABLE_STATISTIC_RANGE+" SET "+STAT_RANGE_ACT_NUMBER+" = -1;");
		} else if (oldVersion == 11) {
			db.execSQL(TABLE_STATISTIC_RANGE_CREATE);
			db.execSQL(TABLE_STATISTIC_CREATE);
		} else
		
		if (oldVersion == 10) {
			db.execSQL(TABLE_STATISTIC_RANGE_CREATE);
			db.execSQL(TABLE_STATISTIC_CREATE);
			db.execSQL(TABLE_SETTINGS_CREATE);
		} 
	}
	
	// closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
    
  //-------------------------------------
  	// SETTINGS Methods
  	//-------------------------------------
  	
  	public long createSetting(SettingDB setting) {
  	    SQLiteDatabase db = this.getWritableDatabase();
  	 
  	    ContentValues values = new ContentValues();
  	    values.put(KEY, setting.getKey());
  	    values.put(VALUE, setting.getValue());
  	 
  	    // insert row
  	    long setting_id = db.insert(TABLE_SETTINGS, null, values);
  	 
  	    return setting_id;
  	}
  	
  	public SettingDB getSetting(String key) {
  	    SQLiteDatabase db = this.getReadableDatabase();
  	 
  	    String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS + " WHERE "
  	            + KEY + " = '" + key+"'";
  	 
  	    Log.i(Utilities.TAG, selectQuery);
  	 
  	    Cursor c = db.rawQuery(selectQuery, null);
  	 
  	    if (c != null && c.getCount() > 0) {
  	        c.moveToFirst();
  	 
  		    SettingDB setting = new SettingDB();
  		    setting.setKey((c.getString(c.getColumnIndex(KEY))));
  		    setting.setValue(c.getString(c.getColumnIndex(VALUE)));
  		 
  		    return setting;
  	    } else {
  	    	return null;
  	    }
  	}
  	
  	public List<SettingDB> getAllSettings() {
  	    List<SettingDB> settings = new ArrayList<SettingDB>();
  	    String selectQuery = "SELECT  * FROM " + TABLE_SETTINGS;
  	 
  	    Log.i(Utilities.TAG, selectQuery);
  	 
  	    SQLiteDatabase db = this.getReadableDatabase();
  	    Cursor c = db.rawQuery(selectQuery, null);
  	 
  	    // looping through all rows and adding to list
  	    if (c.moveToFirst()) {
  	        do {
  	        	SettingDB setting = new SettingDB();
  	  		    setting.setKey((c.getString(c.getColumnIndex(KEY))));
  	  		    setting.setValue(c.getString(c.getColumnIndex(VALUE)));
  	 
  	            // adding to settings list
  	  		    settings.add(setting);
  	        } while (c.moveToNext());
  	    }
  	 
  	    return settings;
  	}
  	
  	public int updateSetting(SettingDB setting) {
  	    SQLiteDatabase db = this.getWritableDatabase();
  	 
  	    ContentValues values = new ContentValues();
  	    values.put(VALUE, setting.getValue());
  	 
  	    // updating row
  	    return db.update(TABLE_SETTINGS, values, KEY + " = '?'",
  	            new String[] { String.valueOf(setting.getKey()) });
  	}
  	
  	public void deleteSetting(String key) {
  	    SQLiteDatabase db = this.getWritableDatabase();
  	    db.delete(TABLE_SETTINGS, KEY + " = ?",
  	            new String[] { key });
  	}
	
	//-------------------------------------
	// ACTUATOR Methods
	//-------------------------------------
	
	public long createActuator(ActuatorDB act) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NUMBER, act.getNumber());
	    values.put(NAME, act.getName());
	    values.put(ACTUATOR_USE_FUNCTION, act.isUseFunction() ? 1 : 0);
	    
	    if (act.getRoomId() > 0){
	    	values.put(ACTUATOR_ROOM, act.getRoomId());
	    }
	 
	    // insert row
	    long act_id = db.insert(TABLE_ACTUATOR, null, values);
	 
	    return act_id;
	}
	
	public ActuatorDB getActuator(int number) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_ACTUATOR + " WHERE "
	            + NUMBER + " = " + number;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null && c.getCount() > 0) {
	        c.moveToFirst();
	 
		    ActuatorDB act = new ActuatorDB();
		    act.setId(c.getInt(c.getColumnIndex(ID)));
		    act.setNumber((c.getInt(c.getColumnIndex(NUMBER))));
		    act.setName(c.getString(c.getColumnIndex(NAME)));
		    act.setRoomId(c.getInt(c.getColumnIndex(ACTUATOR_ROOM)));
		    
		    try {
		    	act.setUseFunction((c.getInt(c.getColumnIndex(ACTUATOR_USE_FUNCTION)) == 0 ? false : true));
		    } catch (Exception e) {
		    	act.setUseFunction(false);
		    }
		 
		    return act;
	    } else {
	    	return null;
	    }
	}
	
	public List<ActuatorDB> getAllActuators() {
	    List<ActuatorDB> actuators = new ArrayList<ActuatorDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_ACTUATOR;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	            ActuatorDB act = new ActuatorDB();
	            act.setId(c.getInt((c.getColumnIndex(ID))));
	            act.setNumber((c.getInt(c.getColumnIndex(NUMBER))));
	            act.setName(c.getString(c.getColumnIndex(NAME)));
	            act.setRoomId(c.getInt(c.getColumnIndex(ACTUATOR_ROOM)));
	            try {
			    	act.setUseFunction((c.getInt(c.getColumnIndex(ACTUATOR_USE_FUNCTION)) == 0 ? false : true));
			    } catch (Exception e) {
			    	act.setUseFunction(false);
			    }
	 
	            // adding to actuator list
	            actuators.add(act);
	        } while (c.moveToNext());
	    }
	 
	    return actuators;
	}
	
	public int updateActuator(ActuatorDB act) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NAME, act.getName());
	    if (act.getRoomId() >= 0){
	    	values.put(ACTUATOR_ROOM, act.getRoomId());
	    }
	    values.put(ACTUATOR_USE_FUNCTION, act.isUseFunction() ? 1 : 0);
	 
	    // updating row
	    return db.update(TABLE_ACTUATOR, values, NUMBER + " = ?",
	            new String[] { String.valueOf(act.getNumber()) });
	}
	
	public void deleteActuator(int number) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_ACTUATOR, NUMBER + " = ?",
	            new String[] { String.valueOf(number) });
	}
	
	public void deleteAllActuator() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_ACTUATOR, null, null);
	}
	
	//-------------------------------------
	// SENSOR Methods
	//-------------------------------------
	
	public long createSensor(SensorDB sens) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NUMBER, sens.getNumber());
	    values.put(NAME, sens.getName());
	    if (sens.getRoomId() > 0){
	    	values.put(SENSOR_ROOM, sens.getRoomId());
	    }
	 
	    // insert row
	    long sens_id = db.insert(TABLE_SENSOR, null, values);
	 
	    return sens_id;
	}
	
	public SensorDB getSensor(int number) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_SENSOR + " WHERE "
	            + NUMBER + " = " + number;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null && c.getCount() > 0) {
	        c.moveToFirst();
	 
		    SensorDB sens = new SensorDB();
		    sens.setId(c.getInt(c.getColumnIndex(ID)));
		    sens.setNumber((c.getInt(c.getColumnIndex(NUMBER))));
		    sens.setName(c.getString(c.getColumnIndex(NAME)));
		    sens.setRoomId(c.getInt(c.getColumnIndex(SENSOR_ROOM)));
		 
		    return sens;
	    } else {
	    	return null;
	    }
	}
	
	public List<SensorDB> getAllSensors() {
	    List<SensorDB> sensors = new ArrayList<SensorDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_SENSOR;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	            SensorDB sens = new SensorDB();
	            sens.setId(c.getInt((c.getColumnIndex(ID))));
	            sens.setNumber((c.getInt(c.getColumnIndex(NUMBER))));
	            sens.setName(c.getString(c.getColumnIndex(NAME)));
	            sens.setRoomId(c.getInt(c.getColumnIndex(SENSOR_ROOM)));
	 
	            // adding to sensor list
	            sensors.add(sens);
	        } while (c.moveToNext());
	    }
	 
	    return sensors;
	}
	
	public int updateSensor(SensorDB sens) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NAME, sens.getName());
	    if (sens.getRoomId() >= 0) {
	    	values.put(SENSOR_ROOM, sens.getRoomId());
	    }
	 
	    // updating row
	    return db.update(TABLE_SENSOR, values, NUMBER + " = ?",
	            new String[] { String.valueOf(sens.getNumber()) });
	}
	
	public void deleteSensor(int number) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_SENSOR, NUMBER + " = ?",
	            new String[] { String.valueOf(number) });
	}
	
	public void deleteAllSensor() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_SENSOR, null, null);
	}
	
	//-------------------------------------
	// TIMER Methods
	//-------------------------------------
	
	public long createTimer(TimerDB timer) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NUMBER, timer.getNumber());
	    values.put(NAME, timer.getName());
	    values.put(TIMER_INACTIVE, timer.isInactive());
	 
	    // insert row
	    long timer_id = db.insert(TABLE_TIMER, null, values);
	 
	    return timer_id;
	}
	
	public TimerDB getTimer(int number) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_TIMER + " WHERE "
	            + NUMBER + " = " + number;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null && c.getCount() > 0) {
	        c.moveToFirst();
	 
		    TimerDB timer = new TimerDB();
		    timer.setId(c.getInt(c.getColumnIndex(ID)));
		    timer.setNumber((c.getInt(c.getColumnIndex(NUMBER))));
		    timer.setName(c.getString(c.getColumnIndex(NAME)));
		    timer.setRoomId(c.getInt(c.getColumnIndex(TIMER_ROOM)));
		    timer.setInactive(c.getInt(c.getColumnIndex(TIMER_INACTIVE)) == 1 ? true : false );
		 
		    return timer;
	    } else {
	    	return null;
	    }
		   
	}
	
	public List<TimerDB> getAllTimers() {
	    List<TimerDB> timers = new ArrayList<TimerDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_TIMER;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	            TimerDB timer = new TimerDB();
	            timer.setId(c.getInt((c.getColumnIndex(ID))));
	            timer.setNumber((c.getInt(c.getColumnIndex(NUMBER))));
	            timer.setName(c.getString(c.getColumnIndex(NAME)));
	            timer.setRoomId(c.getInt(c.getColumnIndex(TIMER_ROOM)));
	            timer.setInactive(c.getInt(c.getColumnIndex(TIMER_INACTIVE)) == 1 ? true : false );
	 
	            // adding to timer list
	            timers.add(timer);
	        } while (c.moveToNext());
	    }
	 
	    return timers;
	}
	
	public int updateTimer(TimerDB timer) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NAME, timer.getName());
	    values.put(TIMER_ROOM, timer.getRoomId());
	    values.put(TIMER_INACTIVE, timer.isInactive() ? 1 : 0);
	 
	    // updating row
	    return db.update(TABLE_TIMER, values, NUMBER + " = ?",
	            new String[] { String.valueOf(timer.getNumber()) });
	}
	
	public void deleteTimer(int number) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_TIMER, NUMBER + " = ?",
	            new String[] { String.valueOf(number) });
	}
	
	public void deleteAllTimer() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_TIMER, null, null);
	}
	
	//-------------------------------------
	// ROOM Methods
	//-------------------------------------
	
	public long createRoom(RoomDB room) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NUMBER, room.getNumber());
	    values.put(NAME, room.getName());
	    values.put(ROOM_POINT1_X, room.getPoint1_x());
	    values.put(ROOM_POINT1_Y, room.getPoint1_y());
	    values.put(ROOM_POINT2_X, room.getPoint2_x());
	    values.put(ROOM_POINT2_Y, room.getPoint2_y());
	    values.put(ROOM_POINT3_X, room.getPoint3_x());
	    values.put(ROOM_POINT3_Y, room.getPoint3_y());
	    values.put(ROOM_POINT4_X, room.getPoint4_x());
	    values.put(ROOM_POINT4_Y, room.getPoint4_y());
	 
	    // insert row
	    long room_id = db.insert(TABLE_ROOM, null, values);
	 
	    return room_id;
	}
	
	public long createRoomWithId(RoomDB room) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(ID, room.getId());
	    values.put(NUMBER, room.getNumber());
	    values.put(NAME, room.getName());
	    values.put(ROOM_POINT1_X, room.getPoint1_x());
	    values.put(ROOM_POINT1_Y, room.getPoint1_y());
	    values.put(ROOM_POINT2_X, room.getPoint2_x());
	    values.put(ROOM_POINT2_Y, room.getPoint2_y());
	    values.put(ROOM_POINT3_X, room.getPoint3_x());
	    values.put(ROOM_POINT3_Y, room.getPoint3_y());
	    values.put(ROOM_POINT4_X, room.getPoint4_x());
	    values.put(ROOM_POINT4_Y, room.getPoint4_y());
	 
	    // insert row
	    long room_id = db.insert(TABLE_ROOM, null, values);
	 
	    return room_id;
	}
	
	public RoomDB getRoom(int number) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_ROOM + " WHERE "
	            + NUMBER + " = " + number;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null && c.getCount() > 0) {
	        c.moveToFirst();
	 
		    RoomDB room = new RoomDB();
		    room.setId(c.getInt(c.getColumnIndex(ID)));
		    room.setNumber((c.getInt(c.getColumnIndex(NUMBER))));
		    room.setName(c.getString(c.getColumnIndex(NAME)));
		    room.setPoint1_x(c.getInt(c.getColumnIndex(ROOM_POINT1_X)));
		    room.setPoint1_y(c.getInt(c.getColumnIndex(ROOM_POINT1_Y)));
		    room.setPoint2_x(c.getInt(c.getColumnIndex(ROOM_POINT2_X)));
		    room.setPoint2_y(c.getInt(c.getColumnIndex(ROOM_POINT2_Y)));
		    room.setPoint3_x(c.getInt(c.getColumnIndex(ROOM_POINT3_X)));
		    room.setPoint3_y(c.getInt(c.getColumnIndex(ROOM_POINT3_Y)));
		    room.setPoint4_x(c.getInt(c.getColumnIndex(ROOM_POINT4_X)));
		    room.setPoint4_y(c.getInt(c.getColumnIndex(ROOM_POINT4_Y)));
		 
		    return room;
	    } else {
	    	return null;
	    }
	}
	
	public List<RoomDB> getAllRooms() {
	    List<RoomDB> rooms = new ArrayList<RoomDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_ROOM;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	            RoomDB room = new RoomDB();
	            room.setId(c.getInt((c.getColumnIndex(ID))));
	            room.setNumber((c.getInt(c.getColumnIndex(NUMBER))));
	            room.setName(c.getString(c.getColumnIndex(NAME)));
	            room.setPoint1_x(c.getInt(c.getColumnIndex(ROOM_POINT1_X)));
			    room.setPoint1_y(c.getInt(c.getColumnIndex(ROOM_POINT1_Y)));
			    room.setPoint2_x(c.getInt(c.getColumnIndex(ROOM_POINT2_X)));
			    room.setPoint2_y(c.getInt(c.getColumnIndex(ROOM_POINT2_Y)));
			    room.setPoint3_x(c.getInt(c.getColumnIndex(ROOM_POINT3_X)));
			    room.setPoint3_y(c.getInt(c.getColumnIndex(ROOM_POINT3_Y)));
			    room.setPoint4_x(c.getInt(c.getColumnIndex(ROOM_POINT4_X)));
			    room.setPoint4_y(c.getInt(c.getColumnIndex(ROOM_POINT4_Y)));
	 
	            // adding to room list
	            rooms.add(room);
	        } while (c.moveToNext());
	    }
	 
	    return rooms;
	}
	
	public int updateRoom(RoomDB room) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NAME, room.getName());
	    values.put(ROOM_POINT1_X, room.getPoint1_x());
	    values.put(ROOM_POINT1_Y, room.getPoint1_y());
	    values.put(ROOM_POINT2_X, room.getPoint2_x());
	    values.put(ROOM_POINT2_Y, room.getPoint2_y());
	    values.put(ROOM_POINT3_X, room.getPoint3_x());
	    values.put(ROOM_POINT3_Y, room.getPoint3_y());
	    values.put(ROOM_POINT4_X, room.getPoint4_x());
	    values.put(ROOM_POINT4_Y, room.getPoint4_y());
	 
	    // updating row
	    return db.update(TABLE_ROOM, values, ID + " = ?",
	            new String[] { String.valueOf(room.getId()) });
	}
	
	public void deleteRoom(int id) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    
	    ContentValues values = new ContentValues();
	    values.put(ACTUATOR_ROOM, 0);
	    db.update(TABLE_ACTUATOR, values, ACTUATOR_ROOM + " = ?",
	            new String[] { String.valueOf(id) });
	    
	    values = new ContentValues();
	    values.put(SENSOR_ROOM, 0);
	    db.update(TABLE_SENSOR, values, SENSOR_ROOM + " = ?",
	            new String[] { String.valueOf(id) });
	    
	    db.delete(TABLE_ROOM, ID + " = ?",
	            new String[] { String.valueOf(id) });
	}
	
	public void deleteAllRoom() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_ROOM, null, null);
	}
	
	public List<Object> getActSensCamForRoom(int roomId) {
	    List<Object> actSensCamList = new ArrayList<Object>();
	    String selectQueryAct = "SELECT  * FROM " + TABLE_ACTUATOR +" WHERE "+ACTUATOR_ROOM+"="+roomId;
	    String selectQuerySens = "SELECT  * FROM " + TABLE_SENSOR +" WHERE "+SENSOR_ROOM+"="+roomId;
	    String selectQueryCams = "SELECT  * FROM " + TABLE_CAM +" WHERE "+CAM_ROOM+"="+roomId;
	 
	    Log.i(Utilities.TAG, selectQueryAct);
	    Log.i(Utilities.TAG, selectQuerySens);
	    Log.i(Utilities.TAG, selectQueryCams);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c_act = db.rawQuery(selectQueryAct, null);
	    Cursor c_sens = db.rawQuery(selectQuerySens, null);
	    Cursor c_cams = db.rawQuery(selectQueryCams, null);
	    
	    // looping through all rows and adding to list
	    
	    if (c_sens.moveToFirst()) {
	    	// sensors
	        do {
	            SensorDB sens = new SensorDB();
	            sens.setId(c_sens.getInt((c_sens.getColumnIndex(ID))));
	            sens.setNumber((c_sens.getInt(c_sens.getColumnIndex(NUMBER))));
	            sens.setName(c_sens.getString(c_sens.getColumnIndex(NAME)));
	            sens.setRoomId(c_sens.getInt(c_sens.getColumnIndex(SENSOR_ROOM)));
	 
	            // adding to actuator list
	            actSensCamList.add(sens);
	        } while (c_sens.moveToNext());
	    }
	    
	    if (c_act.moveToFirst()) {
	        // actuators
	    	do {
	            ActuatorDB act = new ActuatorDB();
	            act.setId(c_act.getInt((c_act.getColumnIndex(ID))));
	            act.setNumber((c_act.getInt(c_act.getColumnIndex(NUMBER))));
	            act.setName(c_act.getString(c_act.getColumnIndex(NAME)));
	            act.setRoomId(c_act.getInt(c_act.getColumnIndex(ACTUATOR_ROOM)));
	 
	            // adding to actuator list
	            actSensCamList.add(act);
	        } while (c_act.moveToNext());
	    }
	    
	    if (c_cams.moveToFirst()) {
	    	// cameras
	        do {
	            CamDB cam = new CamDB();
			    cam.setId(c_cams.getInt(c_cams.getColumnIndex(ID)));
			    cam.setName(c_cams.getString(c_cams.getColumnIndex(NAME)));
			    cam.setUrl(c_cams.getString(c_cams.getColumnIndex(CAM_URL)));
			    cam.setUsername(c_cams.getString(c_cams.getColumnIndex(CAM_USERNAME)));
			    cam.setPassword(c_cams.getString(c_cams.getColumnIndex(CAM_PASSWORD)));
			    cam.setRoomId(c_cams.getInt(c_cams.getColumnIndex(CAM_ROOM)));
			    cam.setStream(c_cams.getInt(c_cams.getColumnIndex(CAM_STREAM)));
	 
	            // adding to actuator list
	            actSensCamList.add(cam);
	        } while (c_cams.moveToNext());
	    }
	 
	    return actSensCamList;
	}
	
	public List<SensorDB> getSensForRoom(int roomId) {
	    List<SensorDB> sensList = new ArrayList<SensorDB>();
	    String selectQuerySens = "SELECT  * FROM " + TABLE_SENSOR +" WHERE "+SENSOR_ROOM+"="+roomId;
	 
	    Log.i(Utilities.TAG, selectQuerySens);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c_sens = db.rawQuery(selectQuerySens, null);
	    	    
	    if (c_sens.moveToFirst()) {
	    	// sensors
	        do {
	            SensorDB sens = new SensorDB();
	            sens.setId(c_sens.getInt((c_sens.getColumnIndex(ID))));
	            sens.setNumber((c_sens.getInt(c_sens.getColumnIndex(NUMBER))));
	            sens.setName(c_sens.getString(c_sens.getColumnIndex(NAME)));
	            sens.setRoomId(c_sens.getInt(c_sens.getColumnIndex(SENSOR_ROOM)));
	 
	            // adding to actuator list
	            sensList.add(sens);
	        } while (c_sens.moveToNext());
	    }
	 
	    return sensList;
	}
	
	public List<ActuatorDB> getActForRoom(int roomId) {
	    List<ActuatorDB> actList = new ArrayList<ActuatorDB>();
	    String selectQueryAct = "SELECT  * FROM " + TABLE_ACTUATOR +" WHERE "+ACTUATOR_ROOM+"="+roomId;
	 
	    Log.i(Utilities.TAG, selectQueryAct);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c_act = db.rawQuery(selectQueryAct, null);
	    
	    // looping through all rows and adding to list
	    if (c_act.moveToFirst()) {
	        // actuators
	    	do {
	            ActuatorDB act = new ActuatorDB();
	            act.setId(c_act.getInt((c_act.getColumnIndex(ID))));
	            act.setNumber((c_act.getInt(c_act.getColumnIndex(NUMBER))));
	            act.setName(c_act.getString(c_act.getColumnIndex(NAME)));
	            act.setRoomId(c_act.getInt(c_act.getColumnIndex(ACTUATOR_ROOM)));
	 
	            // adding to actuator list
	            actList.add(act);
	        } while (c_act.moveToNext());
	    }
	    
	    return actList;
	}
	
	//-------------------------------------
	// CAM Methods
	//-------------------------------------
	
	public long createCam(CamDB cam) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NAME, cam.getName());
	    values.put(CAM_URL, cam.getUrl());
	    values.put(CAM_USERNAME, cam.getUsername());
	    values.put(CAM_PASSWORD, cam.getPassword());
	    values.put(CAM_ROOM, cam.getRoomId());
	    values.put(CAM_STREAM, cam.getStream());
	 
	    // insert row
	    long cam_id = db.insert(TABLE_CAM, null, values);
	 
	    return cam_id;
	}
	
	public CamDB getCam(int id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_CAM + " WHERE "
	            + ID + " = " + id;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null && c.getCount() > 0) {
	        c.moveToFirst();
	 
		    CamDB cam = new CamDB();
		    cam.setId(c.getInt(c.getColumnIndex(ID)));
		    cam.setName(c.getString(c.getColumnIndex(NAME)));
		    cam.setUrl(c.getString(c.getColumnIndex(CAM_URL)));
		    cam.setUsername(c.getString(c.getColumnIndex(CAM_USERNAME)));
		    cam.setPassword(c.getString(c.getColumnIndex(CAM_PASSWORD)));
		    cam.setRoomId(c.getInt(c.getColumnIndex(CAM_ROOM)));
		    cam.setStream(c.getInt(c.getColumnIndex(CAM_STREAM)));
		    
		    return cam;
	    } else {
	    	return null;
	    }
	}
	
	public List<CamDB> getAllCams() {
	    List<CamDB> cams = new ArrayList<CamDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_CAM;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	            CamDB cam = new CamDB();
	            cam.setId(c.getInt((c.getColumnIndex(ID))));
	            cam.setName(c.getString(c.getColumnIndex(NAME)));
	            cam.setUrl(c.getString(c.getColumnIndex(CAM_URL)));
	            cam.setUsername(c.getString(c.getColumnIndex(CAM_USERNAME)));
			    cam.setPassword(c.getString(c.getColumnIndex(CAM_PASSWORD)));
			    cam.setRoomId(c.getInt(c.getColumnIndex(CAM_ROOM)));
			    cam.setStream(c.getInt(c.getColumnIndex(CAM_STREAM)));
	 
	            // adding to room list
	            cams.add(cam);
	        } while (c.moveToNext());
	    }
	 
	    return cams;
	}
	
	public int updateCam(CamDB cam) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NAME, cam.getName());
	    values.put(CAM_URL, cam.getUrl());
	    values.put(CAM_USERNAME, cam.getUsername());
	    values.put(CAM_PASSWORD, cam.getPassword());
	    values.put(CAM_ROOM, cam.getRoomId());
	    values.put(CAM_STREAM, cam.getStream());
	 
	    // updating row
	    return db.update(TABLE_CAM, values, ID + " = ?",
	            new String[] { String.valueOf(cam.getId()) });
	}
	
	public void deleteCam(int id) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    
	    db.delete(TABLE_CAM, ID + " = ?",
	            new String[] { String.valueOf(id) });
	}
	
	public void deleteAllCam() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    
	    db.delete(TABLE_CAM, null, null);
	}
	
	//-------------------------------------
	// SCRIPT Methods
	//-------------------------------------
	
	public long createScript(ScriptDB script) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NUMBER, script.getNumber());
	    values.put(NAME, script.getName());
	 
	    // insert row
	    long script_id = db.insert(TABLE_SCRIPT, null, values);
	 
	    return script_id;
	}
	
	public ScriptDB getScript(int number) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_SCRIPT + " WHERE "
	            + NUMBER + " = " + number;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null && c.getCount() > 0) {
	        c.moveToFirst();
	 
		    ScriptDB script = new ScriptDB();
		    script.setId(c.getInt(c.getColumnIndex(ID)));
		    script.setNumber((c.getInt(c.getColumnIndex(NUMBER))));
		    script.setName(c.getString(c.getColumnIndex(NAME)));
		    script.setRoomId(c.getInt(c.getColumnIndex(SCRIPT_ROOM)));
		 
		    return script;
	    } else {
	    	return null;
	    }
		   
	}
	
	public List<ScriptDB> getAllScripts() {
	    List<ScriptDB> scripts = new ArrayList<ScriptDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_SCRIPT;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	            ScriptDB script = new ScriptDB();
	            script.setId(c.getInt((c.getColumnIndex(ID))));
	            script.setNumber((c.getInt(c.getColumnIndex(NUMBER))));
	            script.setName(c.getString(c.getColumnIndex(NAME)));
	            script.setRoomId(c.getInt(c.getColumnIndex(SCRIPT_ROOM)));
	 
	            // adding to timer list
	            scripts.add(script);
	        } while (c.moveToNext());
	    }
	 
	    return scripts;
	}
	
	public int updateScript(ScriptDB script) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NAME, script.getName());
	    values.put(SCRIPT_ROOM, script.getRoomId());
	 
	    // updating row
	    return db.update(TABLE_SCRIPT, values, NUMBER + " = ?",
	            new String[] { String.valueOf(script.getNumber()) });
	}
	
	public void deleteScript(int number) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_SCRIPT, NUMBER + " = ?",
	            new String[] { String.valueOf(number) });
	}
	
	public void deleteAllScript() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_SCRIPT, null, null);
	}
	
	//-------------------------------------
	// POSITION Methods
	//-------------------------------------
	
	public long createPosition(PositionDB position) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(NAME, position.getName());
	    values.put(POS_LON, position.getLon());
	    values.put(POS_LAT, position.getLat());
	    values.put(POS_ON_ENTRY, position.isOnEntry() ? 1 : 0);
	    values.put(POS_ACT_NUMBER, position.getActNumber());
	    values.put(POS_ACT_VALUE, position.getValue());
	    values.put(POS_LAST_ON_ENTRY, position.isLastOnEntry());
	    values.put(POS_RADIUS, position.getRadius());
	 
	    // insert row
	    long pos_id = db.insert(TABLE_POSITION, null, values);
	 
	    return pos_id;
	}
	
	public PositionDB getPosition(int id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_POSITION + " WHERE "
	            + ID + " = " + id;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null && c.getCount() > 0) {
	        c.moveToFirst();
	 
		    PositionDB position = new PositionDB();
		    position.setId(c.getInt(c.getColumnIndex(ID)));
		    position.setName(c.getString(c.getColumnIndex(NAME)));
		    position.setLat(c.getFloat(c.getColumnIndex(POS_LAT)));
		    position.setLon(c.getFloat(c.getColumnIndex(POS_LON)));
		    position.setOnEntry(c.getInt(c.getColumnIndex(POS_ON_ENTRY)) == 1 ? true : false);
		    position.setValue(c.getFloat(c.getColumnIndex(POS_ACT_VALUE)));
		    position.setLastOnEntry(c.getInt(c.getColumnIndex(POS_LAST_ON_ENTRY)) == 1 ? true : false);
		    position.setRadius(c.getInt(c.getColumnIndex(POS_RADIUS)));
		 
		    return position;
	    } else {
	    	return null;
	    }
		   
	}
	
	public List<PositionDB> getAllPositions() {
	    List<PositionDB> positions = new ArrayList<PositionDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_POSITION;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	        	PositionDB position = new PositionDB();
			    position.setId(c.getInt(c.getColumnIndex(ID)));
			    position.setName(c.getString(c.getColumnIndex(NAME)));
			    position.setLat(c.getFloat(c.getColumnIndex(POS_LAT)));
			    position.setLon(c.getFloat(c.getColumnIndex(POS_LON)));
			    position.setOnEntry(c.getInt(c.getColumnIndex(POS_ON_ENTRY)) == 1 ? true : false);
			    position.setValue(c.getFloat(c.getColumnIndex(POS_ACT_VALUE)));
			    position.setActNumber(c.getInt(c.getColumnIndex(POS_ACT_NUMBER)));
			    position.setLastOnEntry(c.getInt(c.getColumnIndex(POS_LAST_ON_ENTRY)) == 1 ? true : false);
			    position.setRadius(c.getInt(c.getColumnIndex(POS_RADIUS)));
	 
	            // adding to position list
	            positions.add(position);
	        } while (c.moveToNext());
	    }
	 
	    return positions;
	}
	
	public int updatePosition(PositionDB position) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    if (position.getName() != null && !position.getName().equals("")) values.put(NAME, position.getName());
	    values.put(POS_LON, position.getLon());
	    values.put(POS_LAT, position.getLat());
	    values.put(POS_ON_ENTRY, position.isOnEntry() ? 1 : 0);
	    values.put(POS_ACT_NUMBER, position.getActNumber());
	    values.put(POS_ACT_VALUE, position.getValue());
	    values.put(POS_LAST_ON_ENTRY, position.isLastOnEntry() ? 1 : 0);
	    values.put(POS_RADIUS, position.getRadius());
	 
	    // updating row
	    return db.update(TABLE_POSITION, values, ID + " = ?",
	            new String[] { String.valueOf(position.getId()) });
	}
	
	public void deletePosition(int id) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_POSITION, ID + " = ?",
	            new String[] { String.valueOf(id) });
	}
	
	public void deleteAllPosition() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_POSITION, null, null);
	}
	
	//-------------------------------------
	// STATISTIC Methods
	//-------------------------------------
	
	public long createStatistic(StatisticDB statistic) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(STAT_SENS_NUMBER, statistic.getSensorNumber());
	    values.put(STAT_ACT_NUMBER, statistic.getActuatorNumber());
	    values.put(STAT_TIMESTAMP, statistic.getTimestamp());
	    values.put(STAT_VALUE, statistic.getValue());
	 
	    // insert row
	    long stat_id = db.insert(TABLE_STATISTIC, null, values);
	 
	    return stat_id;
	}
	
	public StatisticDB getStatistic(int id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_STATISTIC + " WHERE "
	            + ID + " = " + id;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null && c.getCount() > 0) {
	        c.moveToFirst();
	 
		    StatisticDB statistic = new StatisticDB();
		    statistic.setId(c.getInt(c.getColumnIndex(ID)));
		    statistic.setSensorNumber(c.getInt(c.getColumnIndex(STAT_SENS_NUMBER)));
		    statistic.setActuatorNumber(c.getInt(c.getColumnIndex(STAT_ACT_NUMBER)));
		    statistic.setTimestamp(c.getLong(c.getColumnIndex(STAT_TIMESTAMP)));
		    statistic.setValue(c.getDouble(c.getColumnIndex(STAT_VALUE)));
		 
		    return statistic;
	    } else {
	    	return null;
	    }
		   
	}
	
	public List<StatisticDB> getAllStatistics() {
	    List<StatisticDB> statistics = new ArrayList<StatisticDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_STATISTIC;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	        	StatisticDB statistic = new StatisticDB();
			    statistic.setId(c.getInt(c.getColumnIndex(ID)));
			    statistic.setSensorNumber(c.getInt(c.getColumnIndex(STAT_SENS_NUMBER)));
			    statistic.setActuatorNumber(c.getInt(c.getColumnIndex(STAT_ACT_NUMBER)));
			    statistic.setTimestamp(c.getLong(c.getColumnIndex(STAT_TIMESTAMP)));
			    statistic.setValue(c.getDouble(c.getColumnIndex(STAT_VALUE)));
	 
	            // adding to position list
	            statistics.add(statistic);
	        } while (c.moveToNext());
	    }
	 
	    return statistics;
	}
	
	public ArrayList<StatisticDB> getStatisticsForSensorFromRange(int sensNumber, long begin, long end) {
	    ArrayList<StatisticDB> statistics = new ArrayList<StatisticDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_STATISTIC;
	    selectQuery += " WHERE "+STAT_SENS_NUMBER+" == "+sensNumber+" AND "+STAT_TIMESTAMP+" >= "+begin+" AND "+STAT_TIMESTAMP+" <= "+end;
	    selectQuery += " ORDER BY "+STAT_TIMESTAMP+" ASC";
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	        	StatisticDB statistic = new StatisticDB();
			    statistic.setId(c.getInt(c.getColumnIndex(ID)));
			    statistic.setSensorNumber(c.getInt(c.getColumnIndex(STAT_SENS_NUMBER)));
			    statistic.setTimestamp(c.getLong(c.getColumnIndex(STAT_TIMESTAMP)));
			    statistic.setValue(c.getDouble(c.getColumnIndex(STAT_VALUE)));
	 
	            // adding to position list
	            statistics.add(statistic);
	        } while (c.moveToNext());
	    }
	 
	    return statistics;
	}
	
	public ArrayList<StatisticDB> getStatisticsForActuatorFromRange(int actNumber, long begin, long end) {
	    ArrayList<StatisticDB> statistics = new ArrayList<StatisticDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_STATISTIC;
	    selectQuery += " WHERE "+STAT_ACT_NUMBER+" == "+actNumber+" AND "+STAT_TIMESTAMP+" >= "+begin+" AND "+STAT_TIMESTAMP+" <= "+end;
	    selectQuery += " ORDER BY "+STAT_TIMESTAMP+" ASC";
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	        	StatisticDB statistic = new StatisticDB();
			    statistic.setId(c.getInt(c.getColumnIndex(ID)));
			    statistic.setSensorNumber(c.getInt(c.getColumnIndex(STAT_SENS_NUMBER)));
			    statistic.setTimestamp(c.getLong(c.getColumnIndex(STAT_TIMESTAMP)));
			    statistic.setValue(c.getDouble(c.getColumnIndex(STAT_VALUE)));
	 
	            // adding to position list
	            statistics.add(statistic);
	        } while (c.moveToNext());
	    }
	 
	    return statistics;
	}
	
	public int updateStatistics(StatisticDB statistic) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    if (statistic.getSensorNumber() != -1) values.put(STAT_SENS_NUMBER, statistic.getSensorNumber());
	    if (statistic.getActuatorNumber() != -1) values.put(STAT_ACT_NUMBER, statistic.getActuatorNumber());
	    if (statistic.getTimestamp() != -1) values.put(STAT_TIMESTAMP, statistic.getTimestamp());
	    if (statistic.getValue() != -1) values.put(STAT_VALUE, statistic.getValue());
	 
	    // updating row
	    return db.update(TABLE_STATISTIC, values, ID + " = ?",
	            new String[] { String.valueOf(statistic.getId()) });
	}
	
	public void deleteStatistic(int id) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_STATISTIC, ID + " = ?",
	            new String[] { String.valueOf(id) });
	}
	
	public void deleteAllStatistics() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_STATISTIC, null, null);
	}
	
	//-------------------------------------
	// STATISTIC_RANGE Methods
	//-------------------------------------
	
	public long createStatisticRange(StatisticRangeDB statisticRange) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(STAT_RANGE_SENS_NUMBER, statisticRange.getSensorNumber());
	    values.put(STAT_RANGE_ACT_NUMBER, statisticRange.getActuatorNumber());
	    values.put(STAT_RANGE_FROM, statisticRange.getFrom());
	    values.put(STAT_RANGE_TO, statisticRange.getTo());
	 
	    // insert row
	    long stat_id = db.insert(TABLE_STATISTIC_RANGE, null, values);
	 
	    return stat_id;
	}
	
	public StatisticRangeDB getStatisticRange(int id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_STATISTIC_RANGE + " WHERE "
	            + ID + " = " + id;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null && c.getCount() > 0) {
	        c.moveToFirst();
	 
		    StatisticRangeDB statisticRange = new StatisticRangeDB();
		    statisticRange.setId(c.getInt(c.getColumnIndex(ID)));
		    statisticRange.setSensorNumber(c.getInt(c.getColumnIndex(STAT_RANGE_SENS_NUMBER)));
		    statisticRange.setActuatorNumber(c.getInt(c.getColumnIndex(STAT_RANGE_ACT_NUMBER)));
		    statisticRange.setFrom(c.getLong(c.getColumnIndex(STAT_RANGE_FROM)));
		    statisticRange.setTo(c.getLong(c.getColumnIndex(STAT_RANGE_TO)));
		 
		    return statisticRange;
	    } else {
	    	return null;
	    }
		   
	}
	
	public List<StatisticRangeDB> getAllStatisticRanges() {
	    List<StatisticRangeDB> statisticRanges = new ArrayList<StatisticRangeDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_STATISTIC_RANGE;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	        	StatisticRangeDB statisticRange = new StatisticRangeDB();
	        	statisticRange.setId(c.getInt(c.getColumnIndex(ID)));
			    statisticRange.setSensorNumber(c.getInt(c.getColumnIndex(STAT_RANGE_SENS_NUMBER)));
			    statisticRange.setActuatorNumber(c.getInt(c.getColumnIndex(STAT_RANGE_ACT_NUMBER)));
			    statisticRange.setFrom(c.getLong(c.getColumnIndex(STAT_RANGE_FROM)));
			    statisticRange.setTo(c.getLong(c.getColumnIndex(STAT_RANGE_TO)));
	 
	            // adding to position list
	            statisticRanges.add(statisticRange);
	        } while (c.moveToNext());
	    }
	 
	    return statisticRanges;
	}
	
	public List<StatisticRangeDB> getStatisticRangesForSensorFromTo(int sensNumber, long from, long to) {
	    List<StatisticRangeDB> statisticRanges = new ArrayList<StatisticRangeDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_STATISTIC_RANGE;
	    // Range ragt von links in meine Range hinein
	    selectQuery += " WHERE "+STAT_RANGE_SENS_NUMBER+" == "+sensNumber+" AND (("+STAT_RANGE_FROM+" <= "+from+" AND "+STAT_RANGE_TO+" >= "+from;
	    // Range ist in meiner Range Teilstueck
	    selectQuery += ") OR ("+STAT_RANGE_FROM+" >= "+from+" AND "+ STAT_RANGE_TO+ " <= "+to+"))";
	    selectQuery += " ORDER BY "+STAT_RANGE_FROM+" ASC";
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	        	StatisticRangeDB statisticRange = new StatisticRangeDB();
	        	statisticRange.setId(c.getInt(c.getColumnIndex(ID)));
			    statisticRange.setSensorNumber(c.getInt(c.getColumnIndex(STAT_RANGE_SENS_NUMBER)));
			    statisticRange.setFrom(c.getLong(c.getColumnIndex(STAT_RANGE_FROM)));
			    statisticRange.setTo(c.getLong(c.getColumnIndex(STAT_RANGE_TO)));
	 
	            // adding to position list
	            statisticRanges.add(statisticRange);
	        } while (c.moveToNext());
	    }
	 
	    return statisticRanges;
	}
	
	public List<StatisticRangeDB> getStatisticRangesForActuatorFromTo(int actNumber, long from, long to) {
	    List<StatisticRangeDB> statisticRanges = new ArrayList<StatisticRangeDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_STATISTIC_RANGE;
	    // Range ragt von links in meine Range hinein
	    selectQuery += " WHERE "+STAT_RANGE_ACT_NUMBER+" == "+actNumber+" AND (("+STAT_RANGE_FROM+" <= "+from+" AND "+STAT_RANGE_TO+" >= "+from;
	    // Range ist in meiner Range Teilstueck
	    selectQuery += ") OR ("+STAT_RANGE_FROM+" >= "+from+" AND "+ STAT_RANGE_TO+ " <= "+to+"))";
	    selectQuery += " ORDER BY "+STAT_RANGE_FROM+" ASC";
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	        	StatisticRangeDB statisticRange = new StatisticRangeDB();
	        	statisticRange.setId(c.getInt(c.getColumnIndex(ID)));
			    statisticRange.setSensorNumber(c.getInt(c.getColumnIndex(STAT_RANGE_SENS_NUMBER)));
			    statisticRange.setFrom(c.getLong(c.getColumnIndex(STAT_RANGE_FROM)));
			    statisticRange.setTo(c.getLong(c.getColumnIndex(STAT_RANGE_TO)));
	 
	            // adding to position list
	            statisticRanges.add(statisticRange);
	        } while (c.moveToNext());
	    }
	 
	    return statisticRanges;
	}
	
	public int updateStatisticRanges(StatisticRangeDB statisticRange) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    if (statisticRange.getSensorNumber() != -1) values.put(STAT_RANGE_SENS_NUMBER, statisticRange.getSensorNumber());
	    if (statisticRange.getActuatorNumber() != -1) values.put(STAT_RANGE_ACT_NUMBER, statisticRange.getActuatorNumber());
	    if (statisticRange.getFrom() != -1) values.put(STAT_RANGE_FROM, statisticRange.getFrom());
	    if (statisticRange.getTo() != -1) values.put(STAT_RANGE_TO, statisticRange.getTo());
	 
	    // updating row
	    return db.update(TABLE_STATISTIC_RANGE, values, ID + " = ?",
	            new String[] { String.valueOf(statisticRange.getId()) });
	}
	
	public void deleteStatisticRange(int id) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_STATISTIC_RANGE, ID + " = ?",
	            new String[] { String.valueOf(id) });
	}
	
	public void deleteAllStatisticRanges() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_STATISTIC_RANGE, null, null);
	}
	
	//-------------------------------------
	// GRAPHS Methods
	//-------------------------------------
	
	public long createGraph(GraphsDB graph) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(GRAPHS_ACT_NUMBER, graph.getActNumber());
	    values.put(GRAPHS_SENS_NUMBER, graph.getSensNumber());
	    values.put(GRAPHS_SORTING, graph.getSorting());
	 
	    // insert row
	    long script_id = db.insert(TABLE_GRAPHS, null, values);
	 
	    return script_id;
	}
	
	public GraphsDB getGraph(int id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	 
	    String selectQuery = "SELECT  * FROM " + TABLE_GRAPHS + " WHERE "
	            + ID + " = " + id;
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    if (c != null && c.getCount() > 0) {
	        c.moveToFirst();
	 
		    GraphsDB graph = new GraphsDB();
		    graph.setId(c.getInt(c.getColumnIndex(ID)));
		    graph.setSensNumber((c.getInt(c.getColumnIndex(GRAPHS_SENS_NUMBER))));
		    graph.setActNumber((c.getInt(c.getColumnIndex(GRAPHS_ACT_NUMBER))));
		    graph.setSorting((c.getInt(c.getColumnIndex(GRAPHS_SORTING))));
		 
		    return graph;
	    } else {
	    	return null;
	    }
		   
	}
	
	public List<GraphsDB> getAllGraphs() {
	    List<GraphsDB> graphs = new ArrayList<GraphsDB>();
	    String selectQuery = "SELECT  * FROM " + TABLE_GRAPHS + " ORDER BY "+GRAPHS_SORTING+" ASC";
	 
	    Log.i(Utilities.TAG, selectQuery);
	 
	    SQLiteDatabase db = this.getReadableDatabase();
	    Cursor c = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (c.moveToFirst()) {
	        do {
	        	GraphsDB graph = new GraphsDB();
			    graph.setId(c.getInt(c.getColumnIndex(ID)));
			    graph.setSensNumber((c.getInt(c.getColumnIndex(GRAPHS_SENS_NUMBER))));
			    graph.setActNumber((c.getInt(c.getColumnIndex(GRAPHS_ACT_NUMBER))));
			    graph.setSorting((c.getInt(c.getColumnIndex(GRAPHS_SORTING))));
	 
	            // adding to graph list
	            graphs.add(graph);
	        } while (c.moveToNext());
	    }
	 
	    return graphs;
	}
	
	public int updateGraph(GraphsDB graph) {
	    SQLiteDatabase db = this.getWritableDatabase();
	 
	    ContentValues values = new ContentValues();
	    values.put(GRAPHS_ACT_NUMBER, graph.getActNumber());
	    values.put(GRAPHS_SENS_NUMBER, graph.getSensNumber());
	    values.put(GRAPHS_SORTING, graph.getSorting());
	 
	    // updating row
	    return db.update(TABLE_GRAPHS, values, ID + " = ?",
	            new String[] { String.valueOf(graph.getId()) });
	}
	
	public void deleteGraph(int id) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_GRAPHS, ID + " = ?",
	            new String[] { String.valueOf(id) });
	}
	
	public void deleteAllGraph() {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_GRAPHS, null, null);
	}
}