package de.infoscout.betterhome.controller.utils;

import de.infoscout.betterhome.R;
import android.content.Context;

public class Translator {
	public static final String TEMPERATUR_KEY = "temperature";
	public static final String HYGROMETER_KEY = "hygrometer";
	public static final String SMOKE_DETECTOR_KEY = "smokedetector";
	public static final String WINDOW_OPEN_KEY = "windowopen";
	public static final String DOOR_OPEN_KEY = "dooropen";
	public static final String REMOTE_COMTROL_KEY = "remotecontrol";
	public static final String BAROMETER_KEY = "barometer";
	public static final String WIND_SPEED_KEY = "windspeed";
	public static final String WIND_DIRECTION_KEY = "winddirection";
	public static final String WIND_VARIANCE_KEY = "windvariance";
	public static final String LIGHT_KEY = "light";
	public static final String PYRANOMETER_KEY = "pyranometer";
	public static final String RAIN_KEY = "rain";
	public static final String RAIN_INTENSITY_KEY = "rainintensity";
	public static final String RAIN_1H_KEY = "rain_1h";
	public static final String RAIN_24H_KEY = "rain_24h";
	public static final String SOIL_TEMP_KEY = "soiltemp";
	public static final String SOIL_MOISTURE_KEY = "soilmoisture";
	public static final String LEAF_WETNESS_KEY = "leafwetness";
	public static final String WATER_LEVEL_KEY = "waterlevel";
	public static final String MOTION_KEY = "motion";
	public static final String PRESENCE_KEY = "presence";
	public static final String HEAT_DETECTOR_KEY = "heatdetector";
	public static final String WATER_DETECTOR_KEY = "waterdetector";
	public static final String AIR_QUALITY_KEY = "air_quality";
	public static final String WINDOW_BREAK_KEY = "windowbreak";
	public static final String DOOR_BELL_KEY = "doorbell";
	public static final String ALARM_MAT_KEY = "alarmmat";
	public static final String LIGHT_BARRIER_KEY = "lightbarrier";
	public static final String FENCE_DETECTOR_KEY = "fencedetector";
	public static final String MAIL_KEY = "mail";
	public static final String GAS_CO_KEY = "gas_co";
	public static final String GAS_BUTAN_KEY = "gas_butan";
	public static final String GAS_METHAN_KEY = "gas_methan";
	public static final String GAS_PROPAN_KEY = "gas_propan";
	public static final String WIND_GUST_KEY = "windgust";
	public static final String UV_INDEX_KEY = "uv_index";
	public static final String POWER_CONSUMPTION_KEY = "pwr_consump";
	public static final String POWER_PEAK_KEY = "pwr_peak";
	public static final String WATER_CONSUMPTION_KEY = "wtr_consump";
	public static final String WATER_PEAK_KEY = "wtr_peak";
	public static final String GAS_CONSUMPTION_KEY = "gas_consump";
	public static final String GAS_PEAK_KEY = "gas_peak";
	public static final String OIL_CONSUMPTION_KEY = "oil_consump";
	public static final String OIL_PEAK_KEY = "oil_peak";
	
	public static final String SWITCH_KEY = "switch";
	public static final String DIMMER_KEY = "dimmer";
	public static final String DOOR_KEY = "door";
	public static final String WINDOW_KEY = "window";
	
	
	public static String translateType(String type, Context context) {
		String translation = "";
				
		switch (type) {
			case SMOKE_DETECTOR_KEY : translation=context.getString(R.string.smokedetector); break;
			case MOTION_KEY : translation=context.getString(R.string.motiondetector); break;
			case HEAT_DETECTOR_KEY : translation=context.getString(R.string.heatdetector); break;
			case WATER_DETECTOR_KEY : translation=context.getString(R.string.waterdetector); break;
			case WINDOW_BREAK_KEY : translation=context.getString(R.string.windowbreak); break;
			case FENCE_DETECTOR_KEY : translation=context.getString(R.string.fencedetector); break;
			case TEMPERATUR_KEY : translation=context.getString(R.string.temperatur); break;
			case HYGROMETER_KEY : translation=context.getString(R.string.hygrometer); break;
			case WINDOW_OPEN_KEY : translation=context.getString(R.string.windowopen); break;
			case DOOR_OPEN_KEY : translation=context.getString(R.string.dooropen); break;
			case REMOTE_COMTROL_KEY : translation=context.getString(R.string.remotecontrol); break;
			case BAROMETER_KEY : translation=context.getString(R.string.barometer); break;
			case WIND_SPEED_KEY : translation=context.getString(R.string.windspeed); break;
			case WIND_DIRECTION_KEY : translation=context.getString(R.string.winddirection); break;
			case WIND_VARIANCE_KEY : translation=context.getString(R.string.windvariance); break;
			case LIGHT_KEY : translation=context.getString(R.string.light); break;
			case PYRANOMETER_KEY : translation=context.getString(R.string.pyranometer); break;
			case RAIN_KEY : translation=context.getString(R.string.rain); break;
			case RAIN_INTENSITY_KEY : translation=context.getString(R.string.rainintensity); break;
			case RAIN_1H_KEY : translation=context.getString(R.string.rain1h); break;
			case RAIN_24H_KEY : translation=context.getString(R.string.rain24h); break;
			case SOIL_TEMP_KEY : translation=context.getString(R.string.soiltemp); break;
			case SOIL_MOISTURE_KEY : translation=context.getString(R.string.soilmoisture); break;
			case LEAF_WETNESS_KEY : translation=context.getString(R.string.leafwetness); break;
			case WATER_LEVEL_KEY : translation=context.getString(R.string.waterlevel); break;
			case PRESENCE_KEY : translation=context.getString(R.string.presence); break;
			case AIR_QUALITY_KEY : translation=context.getString(R.string.airquality); break;
			case DOOR_BELL_KEY : translation=context.getString(R.string.doorbell); break;
			case ALARM_MAT_KEY : translation=context.getString(R.string.alarmmat); break;
			case LIGHT_BARRIER_KEY : translation=context.getString(R.string.lightbarrier); break;
			case MAIL_KEY : translation=context.getString(R.string.mail); break;
			case GAS_CO_KEY : translation=context.getString(R.string.gasco); break;
			case GAS_BUTAN_KEY : translation=context.getString(R.string.gasbutan); break;
			case GAS_METHAN_KEY : translation=context.getString(R.string.gasmethan); break;
			case GAS_PROPAN_KEY : translation=context.getString(R.string.gaspropan); break;
			case WIND_GUST_KEY : translation=context.getString(R.string.windgust); break;
			case UV_INDEX_KEY : translation=context.getString(R.string.uvindex); break;
			case POWER_CONSUMPTION_KEY : translation=context.getString(R.string.powerconsumption); break;
			case POWER_PEAK_KEY : translation=context.getString(R.string.powerpeak); break;
			case WATER_CONSUMPTION_KEY : translation=context.getString(R.string.waterconsumption); break;
			case WATER_PEAK_KEY : translation=context.getString(R.string.waterpeak); break;
			case GAS_CONSUMPTION_KEY : translation=context.getString(R.string.gasconsumption); break;
			case GAS_PEAK_KEY : translation=context.getString(R.string.gaspeak); break;
			case OIL_CONSUMPTION_KEY : translation=context.getString(R.string.oilconsumption); break;
			case OIL_PEAK_KEY : translation=context.getString(R.string.oilpeak); break;
			
			case SWITCH_KEY : translation=context.getString(R.string.switcher); break;
			case DIMMER_KEY : translation=context.getString(R.string.dimmer); break;
			case DOOR_KEY : translation=context.getString(R.string.door); break;
			case WINDOW_KEY : translation=context.getString(R.string.window); break;
			
			default : translation = context.getString(R.string.unknown);
		}
		
		return translation;
	}

}
