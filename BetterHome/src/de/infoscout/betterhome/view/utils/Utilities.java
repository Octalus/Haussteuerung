package de.infoscout.betterhome.view.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.controller.utils.Translator;
import de.infoscout.betterhome.model.device.Actuator;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ArrayAdapter;

public class Utilities {
	public static final String TAG = "de.infoscout.betterhome";
	public static final String PLAN_IMAGE_FILENAME = "plan_image";

	public static boolean alertChecker(String type, double value, String name, Context context){
		boolean alert = false;
		double alert_value = 100.0;
		
		Log.i(Utilities.TAG, "alertChecker:");
		Log.i(Utilities.TAG, "type="+type+", value="+value+", name="+name);
		
		if (type.equals(Translator.SMOKE_DETECTOR_KEY) && value == alert_value && RuntimeStorage.isSmokeDetectorAlarm()){
			alert = true;
		} else if (type.equals(Translator.HEAT_DETECTOR_KEY) && value == alert_value && RuntimeStorage.isHeatDetectorAlarm()){
			alert = true;
		} else if (type.equals(Translator.WATER_DETECTOR_KEY) && value == alert_value && RuntimeStorage.isWaterDetectorAlarm()){
			alert = true;
		} else if (type.equals(Translator.WINDOW_BREAK_KEY) && value == alert_value && RuntimeStorage.isWindowBreakAlarm()){
			alert = true;
		} else if (type.equals(Translator.FENCE_DETECTOR_KEY) && value == alert_value && RuntimeStorage.isFenceDetectorAlarm()){
			alert = true;
		} else if (type.equals(Translator.MOTION_KEY) && value == alert_value && RuntimeStorage.isMotionDetectorAlarm()){
			alert = true;
		}
		
		return alert;
	}
	
	public static String trimName19(String input){
		String result = input.trim();
		if (input.length() > 19){
			result = result.substring(0, 18);
		}
		
		return result;
	}
	
	public static String fillWithZero(int number) {
		String out = null;
		
		if (number < 10) {
			out = "0"+number;
		} else {
			out = ""+number;
		}
		
		return out;
	}
	
	public static Object readFileToObject(File file) {
		Object o = null;
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			o = ois.readObject();
			String inhalt = o.toString();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
			}
			
			try {
				fis.close();
			} catch (IOException e) {
			}
		}
		
		return o;
	}
	
	public static void saveObjectToFile(File file, Object o) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try {
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(o);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
			}
			
			try {
				fos.close();
			} catch (IOException e) {
			}
		}
	}
	
	public static byte[] readFile(File file) throws IOException {

	    byte []buffer = new byte[(int) file.length()];
	    InputStream ios = null;
	    try {
	        ios = new FileInputStream(file);
	        if ( ios.read(buffer) == -1 ) {
	            throw new IOException("EOF reached while trying to read the whole file");
	        }        
	    } finally { 
	        try {
	             if ( ios != null ) 
	                  ios.close();
	        } catch ( IOException e) {
	        }
	    }

	    return buffer;
	}
	
	public static String[] getTemperatureList() {
		String[] vals = {"12.0 °C", "13.0 °C", "14.0 °C", "15.0 °C", "16.0 °C", "17.0 °C", "18.0 °C", "19.0 °C", "19.5 °C", "20.0 °C", "20.5 °C", "21.0 °C", "21.5 °C", "22.0 °C", "22.5 °C", "23.0 °C", "23.5 °C", "24.0 °C", "24.5 °C", "25.0 °C"};
		return vals;
	}
	
	public static ArrayAdapter<String> getTemperatureSpinnerAdapter(Context context) {
		String[] vals = getTemperatureList();
    	ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, vals);
    	
    	return spinnerAdapter;
	}
	
	public static String getValueFromTemperature(String temperature){
		return temperature.substring(0, temperature.length()-3);
	}
	
	public static int getImageForActuatorType(Actuator actuator) {
		int resId = -1;
		
		// Temperatur
		if (actuator.getType().equals("temperature")){
        	resId = R.drawable.heizung;
        
        // Dimmer
		} else if (actuator.isDimmable()) {
			resId = R.drawable.dimmer;
		
		// Makro
		} else if (actuator.isMakro()) {
			resId = R.drawable.link;
		
		// Rolladen
		} else if (actuator.getType().equals("shutter")){
			resId = R.drawable.rolladen;
		
		// Schalter
		} else {
			resId = R.drawable.schalter;
		}
		
		return resId;
	}
	
	public static int getImageForSensorType(String type, double value) {
		int resId = -1;
		
        // Temperatur
        if (type.equals(Translator.TEMPERATUR_KEY)){
        	resId = R.drawable.hot01;
        
        // Feuchtigkeit
        } else if (type.equals(Translator.HYGROMETER_KEY)){
        	resId = R.drawable.rain03;
        
        // Rauchmelder
        } else if (type.equals(Translator.SMOKE_DETECTOR_KEY)) {
        	resId = R.drawable.rauchmelder;
        
        // Fenster
        } else if (type.equals(Translator.WINDOW_OPEN_KEY)){
        	if (value == 0.0){
        		resId = R.drawable.window_close;
        	} else {
        		resId = R.drawable.window_open;
        	}
        
        // Tuer
        } else if (type.equals(Translator.DOOR_OPEN_KEY)){
        	if (value == 0.0){
        		resId = R.drawable.door_close;
        	} else {
        		resId = R.drawable.door_open;
        	}
        
        // Fernbedienung
        } else if (type.equals(Translator.REMOTE_COMTROL_KEY)){
        	resId = R.drawable.remote_control;
        	
        // Luftdruck
        } else if (type.equals(Translator.BAROMETER_KEY)){
        	resId = R.drawable.barometer;
        
        // Windgeschwindigkeit
        } else if (type.equals(Translator.WIND_SPEED_KEY)){
        	resId = R.drawable.windspeed;
        	
        // Windrichtung
        } else if (type.equals(Translator.WIND_DIRECTION_KEY)){
        	resId = R.drawable.wind_richtung;
        	
        // Windabweichung
        } else if (type.equals(Translator.WIND_VARIANCE_KEY)){
        	resId = R.drawable.windspeed;
        	
        // Licht
        } else if (type.equals(Translator.LIGHT_KEY)){
        	resId = R.drawable.light;
        	
        // Sonneneinstrahlung
        } else if (type.equals(Translator.PYRANOMETER_KEY)){
        	resId = R.drawable.solar;
        	
        // Regen / Regenstraerke / Regen/h / Regen/Tag
        } else if (type.equals(Translator.RAIN_KEY) || type.equals(Translator.RAIN_INTENSITY_KEY) || type.equals(Translator.RAIN_1H_KEY) || type.equals(Translator.RAIN_24H_KEY)){
        	resId = R.drawable.rain;
        	
        // Erdboden Temperatur
        } else if (type.equals(Translator.SOIL_TEMP_KEY)){
        	resId = R.drawable.hot01;
        	
        // Erdboden Feuchtigkeit
        } else if (type.equals(Translator.SOIL_MOISTURE_KEY)){
        	resId = R.drawable.rain;
        	
        // Blattfeuchtigkeit
        } else if (type.equals(Translator.LEAF_WETNESS_KEY)){
        	resId = R.drawable.rain;
        	
        // Wasserstand
        } else if (type.equals(Translator.WATER_LEVEL_KEY)){
        	resId = R.drawable.waterlavel;
        	
        // Bewegung
        } else if (type.equals(Translator.MOTION_KEY)){
        	resId = R.drawable.motion;
        	
        // Anwesenheit
        } else if (type.equals(Translator.PRESENCE_KEY)){
        	resId = R.drawable.presence;
        	
        // Hitzemelder
        } else if (type.equals(Translator.HEAT_DETECTOR_KEY)){
        	resId = R.drawable.rauchmelder;
        	
        // Wassermelder
        } else if (type.equals(Translator.WATER_DETECTOR_KEY)){
        	resId = R.drawable.water2;
        	
        // Luftqualitaet
        } else if (type.equals(Translator.AIR_QUALITY_KEY)){
        	resId = R.drawable.air_quality;
        	
        // Fensterbruch
        } else if (type.equals(Translator.WINDOW_BREAK_KEY)){
        	resId = R.drawable.window_break;
        	
        // Tuerklingel
        } else if (type.equals(Translator.DOOR_BELL_KEY) || type.equals(Translator.ALARM_MAT_KEY)){
        	resId = R.drawable.doorbell;
        	
        // Lichtschranke
        } else if (type.equals(Translator.LIGHT_BARRIER_KEY)){
        	resId = R.drawable.lichtschranke;
        	
        // Zaunsicherung
        } else if (type.equals(Translator.FENCE_DETECTOR_KEY)){
        	resId = R.drawable.fence_detector;
        	
        // Mail
        } else if (type.equals(Translator.MAIL_KEY)){
        	if (value == 0.0){
        		resId = R.drawable.mail_close;
        	} else {
        		resId = R.drawable.mail_open;
        	}
        	
        // Gas
        } else if (type.equals(Translator.GAS_CO_KEY) || type.equals(Translator.GAS_BUTAN_KEY) || type.equals(Translator.GAS_METHAN_KEY) || type.equals(Translator.GAS_PROPAN_KEY)){
        	resId = R.drawable.gas_128;
        	
        // Windboehe
        } else if (type.equals(Translator.WIND_GUST_KEY)){
        	resId = R.drawable.wind;
        	
        // UV Index
        } else if (type.equals(Translator.UV_INDEX_KEY)){
        	resId = R.drawable.uv_index;
       
        // Energieverbrauch / Energiespitze
        } else if (type.equals(Translator.POWER_CONSUMPTION_KEY) || type.equals(Translator.POWER_PEAK_KEY)){
        	resId = R.drawable.thunder;
        	
        // Wasserverbrauch / Wasserspitze
        } else if (type.equals(Translator.WATER_CONSUMPTION_KEY) || type.equals(Translator.WATER_PEAK_KEY)){
        	resId = R.drawable.waterconsumption;
        	
        // Gasverbrauch / Gasspitze
        } else if (type.equals(Translator.GAS_CONSUMPTION_KEY) || type.equals(Translator.GAS_PEAK_KEY)){
        	resId = R.drawable.gas_icon;
        	
        // Oelverbrauch / Oelspitze
        } else if (type.equals(Translator.OIL_CONSUMPTION_KEY) || type.equals(Translator.OIL_PEAK_KEY)){
        	resId = R.drawable.oil;
  
        }
        
        return resId;
	}
	
	public static String getWertForSensor(String unit, double value, String type, Context context) {
		String wert = "";
        if (unit.equals("boolean")){
        	if (value == 0.0) {
        		if (type.equals("windowopen") || type.equals("dooropen")){
        			wert = context.getString(R.string.close);
        		} else if (type.equals("smokedetector") || type.equals("heatdetector")){
        			wert = context.getString(R.string.normal);
        		} else if (type.equals("rain")){
        			wert = context.getString(R.string.no);
        		} else {
        			wert = context.getString(R.string.off);
        		}
        	} else {
        		if (type.equals("windowopen") || type.equals("dooropen")){
        			wert = context.getString(R.string.open);
        		} else if (type.equals("smokedetector") || type.equals("heatdetector")){
        			wert = context.getString(R.string.alarm);
        		} else if (type.equals("rain")){
        			wert = context.getString(R.string.yes);
        		} else {
        			wert = context.getString(R.string.on);
        		}
        	}
        } else {
        	wert = value+" "+unit;
        }
        
		return wert;
	}
	
	public static String getWertForActuator(String unit, double value, String type, Context context) {
		String wert = "";
        if (type.equals("switch") ) { 
			if (value == 0.0) {
				wert = context.getString(R.string.off);
			} else if (value == 100.0) {
				wert = context.getString(R.string.on);
			} else {
				wert = String.valueOf(((double) Math.round(value * 10)) / 10)
						+ " " + unit;
			}
        } else if (type.equals("shutter")) {
        	if (value == 100) {
        		wert = context.getString(R.string.close);
        	} else if (value == 0){
        		wert = context.getString(R.string.open);
        	}
		} else {
			wert = String.valueOf(((double) Math.round(value * 10)) / 10)
					+ " " + unit;
		} 
        
		return wert;
	}
	
	public static boolean isPointInRoom(double[] vertx, double[] verty, double testx, double testy)
	{
	    int nvert = vertx.length;
	    int i, j;
	    boolean c = false;
	    for (i = 0, j = nvert-1; i < nvert; j = i++) {
	        if ( ((verty[i]>testy) != (verty[j]>testy)) &&
	                (testx < (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) )
	            c = !c;
	    }
	    return c;
	}
	
	public static int[] getMiddlePointOfRoom(double[] vertx, double[] verty) {
		
	    double[] centroid = { 0, 0 };
	    int totalPoints = vertx.length;
	    for (int i = 0; i < totalPoints; i++) {
	        centroid[0] += vertx[i];
	        centroid[1] += verty[i];
	    }

	    centroid[0] = centroid[0] / totalPoints;
	    centroid[1] = centroid[1] / totalPoints;
	    
	    int [] ret = {(int)centroid[0], (int)centroid[1]}; 
	    
	    return ret;
	}
	
	public static void clearBackStack(FragmentActivity activity) {
	    FragmentManager manager = activity.getSupportFragmentManager();
	    if (manager.getBackStackEntryCount() > 0) {
	        FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
	         manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
	    }
	}
}
