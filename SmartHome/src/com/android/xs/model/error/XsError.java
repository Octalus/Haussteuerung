package com.android.xs.model.error;

import com.android.xs.view.SmartHomeActivity;

import android.content.Context;
import android.widget.Toast;

/**
 * Die Klasse Error stellt das enum dar, mit dem die Fehlernummer der XS antwort
 * auf einen String gemappt werden kann, sodass dieser ausgegegeben oder in den
 * Log eingetragen wird
 * 
 * @author Viktor Mayer
 * 
 */
public class XsError {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/
	// Der Fehlertext
	private final String err;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Konstruktor, der die Fehlernummer entgegen nimmt und den Text
	 * entsprechend setzt
	 * 
	 * @param num
	 *            - Die Fehlernummer
	 */
	public XsError(int num) {
		switch (num) {
		case 1:
			this.err = "invalid command";
			break;
		case 2:
			this.err = "cmd type missing";
			break;
		case 3:
			this.err = "number/name not found";
			break;
		case 4:
			this.err = "duplicate name";
			break;
		case 5:
			this.err = "invalid system";
			break;
		case 6:
			this.err = "invalid function";
			break;
		case 7:
			this.err = "invalid date/time";
			break;
		case 8:
			this.err = "object not vound";
			break;
		case 9:
			this.err = "type not virtual";
			break;
		case 10:
			this.err = "syntax error";
			break;
		case 11:
			this.err = "error time range";
			break;
		case 12:
			this.err = "protocol version mismatch";
			break;
		default:
			this.err = "unknown error";
			break;
		}
	}
	
	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/
	
	/**
	 * gibt den Fehler als lesbaren String zurück
	 * 
	 * @return - Der Fehler als lesbarer String
	 */
	public String getError() {
		return this.err;
	}
	
	/**
	 * Pauschaler Fehler, wird aus mehreren Activites aufgerufen
	 * 
	 * @param con
	 *            - Der Context der Activity
	 */
	public static void printError(Context con) {
		Toast.makeText(con, "Verbindungsfehler..", Toast.LENGTH_SHORT).show();
	}
	
	public static void printError(Context con, String error) {
		Toast.makeText(con, error, Toast.LENGTH_SHORT).show();
	}
	
	public static void printError(final String error){
		SmartHomeActivity.smarthomeactivity.runOnUiThread(new Runnable() {
			  public void run() {
			    Toast.makeText(SmartHomeActivity.smarthomeactivity, error, Toast.LENGTH_SHORT).show();
			  }
			});
	}

}
