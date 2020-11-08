package com.android.xs.view.setting;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Xsone;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 
 * @author Viktor Mayer
 *
 */
public class GInfo extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	// Das Xsone Objekt für diese Aktivity
	private Xsone myXsone;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();

		TextView tv = new TextView(this);

		// Alle Infos aus dem Xsone Objekt ausgeben
		String info = "";
		info += "Gerätename: " + myXsone.getDeviceName() + "\n";
		info += "Hardware: " + myXsone.getHardware() + "\n";
		info += "Bootloader: " + myXsone.getBootloader() + "\n";
		info += "Firmware: " + myXsone.getFirmware() + "\n";
		info += "Systeme: " + myXsone.getSystem() + "\n";
		info += "Max Sender: " + myXsone.getMaxActuators() + "\n";
		info += "Max Empfänger: " + myXsone.getMaxSensors() + "\n";
		info += "Max Zeitschalter: " + myXsone.getMaxTimers() + "\n";
		info += "Max Skripte: " + myXsone.getMaxScripts() + "\n";
		info += "Max Räume: " + myXsone.getMaxRooms() + "\n";
		info += "Laufzeit: " + myXsone.getUptime() + "\n";
		info += "Features: " + myXsone.getFeatures() + "\n";
		info += "Mac-Adresse: " + myXsone.getMac() + "\n";
		info += "Auto-IP: " + myXsone.getAutoip() + "\n";
		info += "IP-Adresse: " + myXsone.getMyIpSetting().getIp() + "\n";
		info += "Subnetz: " + myXsone.getMyIpSetting().getNetmask() + "\n";
		info += "Gateway: " + myXsone.getMyIpSetting().getGateway() + "\n";
		info += "DNS: " + myXsone.getMyIpSetting().getDns() + "\n";

		tv.setText(info);
		setContentView(tv);

	}

}
