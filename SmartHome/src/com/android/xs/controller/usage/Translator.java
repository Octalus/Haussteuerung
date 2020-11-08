package com.android.xs.controller.usage;

/**
 * Dient der Übersetzung, bisher jedoch noch nicht eingesetzt
 * @author Viktor Mayer
 *
 */
public class Translator {
	
	/**
	 * Dient der Ausgabe von deutschen Begriffen, wird derzeit nicht genutzt
	 * 
	 * @param value
	 * @return
	 */
	public static String translate(String value) {

		if (value.equals("Temperatur"))
			return "temperature";
		else if (value.equals("Schalter"))
			return "switch";
		else if (value.equals("Dimmer"))
			return "dimmer";
		else if (value.equals("an"))
			return "on";
		else if (value.equals("aus"))
			return "off";
		else if (value.equals("deaktiviert"))
			return "disabled";
		else
			return value;
	}

}
