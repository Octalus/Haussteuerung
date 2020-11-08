package com.android.xs.controller.remote;

import java.util.List;

import com.android.xs.model.device.Actuator;
import com.android.xs.model.device.Script;
import com.android.xs.model.device.Sensor;
import com.android.xs.model.device.Timer;

import android.net.Uri;

/**
 * 
 * @author Viktor Mayer
 * 
 */
public class CommandBuilder {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private static String ip;
	@SuppressWarnings("unused")
	private static String user;
	@SuppressWarnings("unused")
	private static String pass;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funtkionen
	 ***********************************************************************************************************************************************************/

	/**
	 * 
	 * @param cmd
	 * @return
	 */
	public static Uri buildUri(String cmd) {

		if (cmd.equals("get_config_info")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_config_info");
			return b.build();
			// return new Uri.Builder().scheme("http").authority(ip)
			// .path("control").appendQueryParameter("callback", "cname")
			// // .appendQueryParameter("user", user)
			// // .appendQueryParameter("pwd", pass)
			// .appendQueryParameter("cmd", "get_config_info").build();
		}

		else if (cmd.equals("get_date_time")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_date_time");
			return b.build();
			// return new Uri.Builder().scheme("http").authority(ip)
			// .path("control").appendQueryParameter("callback", "cname")
			// // .appendQueryParameter("user", user)
			// // .appendQueryParameter("pwd", pass)
			// .appendQueryParameter("cmd", "get_date_time").build();
		}

		else if (cmd.equals("get_list_actuators")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder().scheme("http").authority(ip)
			b.path("control").appendQueryParameter("callback", "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_list_actuators");
			return b.build();
		}

		else if (cmd.equals("get_list_functions")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_list_functions");
			return b.build();
		}

		else if (cmd.equals("get_list_scripts")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_list_scripts");
			return b.build();
		}

		else if (cmd.equals("get_list_sensors")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_list_sensors");
			return b.build();
		}

		else if (cmd.equals("get_list_systems")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_list_systems");
			return b.build();
		}

		else if (cmd.equals("get_list_timers")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_list_timers");
			return b.build();
		}

		else if (cmd.equals("get_protocol_info")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_protocol_info");
			return b.build();
		}

		else if (cmd.equals("get_types_actuators")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_types_actuators");
			return b.build();
		}

		else if (cmd.equals("get_types_sensors")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_types_sensors");
			return b.build();
		}

		else if (cmd.equals("get_types_timers")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "get_types_timers");
			return b.build();
		}

		else if (cmd.equals("subscribe")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "subscribe").appendQueryParameter("format", "htxt");
			return b.build();
		}

		return null;

	}

	/**
	 * 
	 * @param data
	 * @param num
	 * @param cmd
	 * @return
	 */
	public static Uri buildUri(String[] data, String num, String cmd) {

		if (cmd.equals("add_actuator")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder()
			// .scheme("http")
			// .authority(ip)
			b.path("control")
					.appendQueryParameter("callback", "cname")
					// .appendQueryParameter("user", user)
					// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("number", num).appendQueryParameter("type", data[0]).appendQueryParameter("system", data[1])
					.appendQueryParameter("name", data[2]).appendQueryParameter("hc1", data[3]).appendQueryParameter("hc2", data[4])
					.appendQueryParameter("address", data[5]).appendQueryParameter("initvalue", "").appendQueryParameter("function1.dsc", data[6])
					.appendQueryParameter("function1.type", data[7]).appendQueryParameter("function1.value", data[8])
					.appendQueryParameter("function1.time", data[9]).appendQueryParameter("function2.dsc", data[10])
					.appendQueryParameter("function2.type", data[11]).appendQueryParameter("function2.value", data[12])
					.appendQueryParameter("function2.time", data[13]).appendQueryParameter("function3.dsc", data[14])
					.appendQueryParameter("function3.type", data[15]).appendQueryParameter("function3.value", data[16])
					.appendQueryParameter("function3.time", data[17]).appendQueryParameter("function4.dsc", data[18])
					.appendQueryParameter("function4.type", data[19]).appendQueryParameter("function4.value", data[20])
					.appendQueryParameter("function4.time", data[21]).appendQueryParameter("log", "off")
					.appendQueryParameter("cmd", "set_config_actuator");
			return b.build();
		}

		else if (cmd.equals("add_script")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder().scheme("http").authority(ip)
			b.path("control")
					.appendQueryParameter("callback", "cname")
					// .appendQueryParameter("user", user)
					// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("number", num).appendQueryParameter("name", data[0]).appendQueryParameter("type", data[1])
					.appendQueryParameter("body", data[2]).appendQueryParameter("cmd", "set_config_script");
			return b.build();
		}

		else if (cmd.equals("add_sensor")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder()
			// .scheme("http")
			// .authority(ip)
			b.path("control")
					.appendQueryParameter("callback", "cname")
					// .appendQueryParameter("user", user)
					// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("number", num).appendQueryParameter("type", data[0]).appendQueryParameter("system", data[1])
					.appendQueryParameter("factor", data[2]).appendQueryParameter("offset", data[3]).appendQueryParameter("name", data[4])
					.appendQueryParameter("hc1", data[5]).appendQueryParameter("hc2", data[6]).appendQueryParameter("initvalue", "")
					.appendQueryParameter("address", data[7]).appendQueryParameter("log", "off").appendQueryParameter("cmd", "set_config_sensor");
			return b.build();
		}

		return null;
	}

	/**
	 * 
	 * @param data
	 * @param num
	 * @param cmd
	 * @return
	 */
	public static Uri buildUri(List<String> data, String num, String cmd) {

		if (cmd.equals("add_timer")) {
			// Die Wochentage sind ab 6 bir ende
			String weekdays = "";
			for (int i = 6; i < data.size(); i++) {
				weekdays += data.get(i) + ",";
			}

			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder()
			// .scheme("http")
			// .authority(ip)
			b.path("control")
					.appendQueryParameter("callback", "cname")
					// .appendQueryParameter("user", user)
					// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("number", String.valueOf(num)).appendQueryParameter("name", data.get(0))
					.appendQueryParameter("type", data.get(1)).appendQueryParameter("weekdays", weekdays).appendQueryParameter("hour", data.get(2))
					.appendQueryParameter("min", data.get(3)).appendQueryParameter("sec", "00").appendQueryParameter("actuator.name", data.get(4))
					.appendQueryParameter("actuator.function", data.get(5)).appendQueryParameter("earliest", "0").appendQueryParameter("offset", "0")
					.appendQueryParameter("latest", "0").appendQueryParameter("random", "0").appendQueryParameter("cmd", "set_config_timer");
			return b.build();
		}
		return null;
	}

	/**
	 * 
	 * @param num
	 * @param cmd
	 * @return
	 */
	public static Uri buildUri(String num, String cmd) {

		if (cmd.equals("get_state_actuator")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder().scheme("http").authority(ip)
			b.path("control").appendQueryParameter("callback", "cname").appendQueryParameter("cmd", "get_state_actuator")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("number", num);
			return b.build();
		}

		else if (cmd.equals("get_state_sensor")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder().scheme("http").authority(ip)
			b.path("control").appendQueryParameter("callback", "cname").appendQueryParameter("cmd", "get_state_sensor")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("number", num);
			return b.build();
		}

		else if (cmd.equals("learn")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			b.path("control").appendQueryParameter("callback", "cname")
			// return new
			// Uri.Builder().scheme("http").authority(ip).path("control").appendQueryParameter("callback",
			// "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "learn").appendQueryParameter("system", num);
			return b.build();
		}

		return null;
	}

	/**
	 * 
	 * @param Obj
	 * @param cmd
	 * @return
	 */
	public static Uri buildUri(Object Obj, String cmd) {

		if (cmd.equals("remove_object")) {
			int num = 0;
			String name = "";
			String command = "";
			if (Obj.getClass().equals(Actuator.class)) {
				num = ((Actuator) Obj).getNumber();
				name = "Actuator_" + num;
				command = "set_config_actuator";
			} else if (Obj.getClass().equals(Sensor.class)) {
				num = ((Sensor) Obj).getNumber();
				name = "Sensor_" + num;
				command = "set_config_sensor";
			} else if (Obj.getClass().equals(Timer.class)) {
				num = ((Timer) Obj).getNumber();
				name = "Timer_" + num;
				command = "set_config_timer";
			} else if (Obj.getClass().equals(Script.class)) {
				num = ((Script) Obj).getNumber();
				name = "Script_" + num;
				command = "set_config_script";
			} else
				return null;

			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder().scheme("http").authority(ip)
			b.path("control")
					.appendQueryParameter("callback", "cname")
					// .appendQueryParameter("user", user)
					// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("number", String.valueOf(num)).appendQueryParameter("type", "disabled").appendQueryParameter("name", name)
					.appendQueryParameter("cmd", command);
			return b.build();
		}
		return null;
	}

	/**
	 * 
	 * @param val
	 * @param num
	 * @param cmd
	 * @return
	 */
	public static Uri buildUri(String val, String num, String cmd) {

		if (cmd.equals("set_value_actuator")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder().scheme("http").authority(ip)
			b.path("control").appendQueryParameter("callback", "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "set_state_actuator").appendQueryParameter("number", num).appendQueryParameter("value", val);
			return b.build();
		}

		if (cmd.equals("set_function_actuator")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder().scheme("http").authority(ip)
			b.path("control").appendQueryParameter("callback", "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "set_state_actuator").appendQueryParameter("number", num).appendQueryParameter("function", val);
			return b.build();
		}

		if (cmd.equals("set_state_sensor")) {
			Uri.Builder b = Uri.parse("http://" + ip).buildUpon();
			// return new Uri.Builder().scheme("http").authority(ip)
			b.path("control").appendQueryParameter("callback", "cname")
			// .appendQueryParameter("user", user)
			// .appendQueryParameter("pwd", pass)
					.appendQueryParameter("cmd", "set_state_sensor").appendQueryParameter("number", num).appendQueryParameter("value", val);
			return b.build();
		}

		return null;
	}

	/**
	 * Getter und Setter
	 ***********************************************************************************************************************************************************/

	static void setIp(String data) {
		ip = data;
	}

	static void setUser(String data) {
		user = data;
	}

	static void setPass(String data) {
		pass = data;
	}

}
