package com.android.xs.view.setting;

import com.android.xs.view.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TextView;

public class About extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

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

		setContentView(R.layout.tab_table_layout);

		TableLayout table = (TableLayout) findViewById(R.id.tab_table);

		TextView tv = new TextView(this);
		tv.setText("Autor: Viktor Mayer\n" + "Version: "
				+ this.getString(R.string.version)
				+ "\n"
				+ "App zur Steuerung der XS1\n"
				+ "Vollversion\n"
				+ "\n"
				+ "\n"
				+ "Anwendungshinweise:\n"
				+ "\n"
				+ "Die Anwendung fragt beim ersten Start nach einer Verbindung zur XS1. Dies ist über das lokale WLAN (IP) oder aber über einen DynDns"
				+ "Anbieter (InetAdresse) möglich. Zur Nutzung ist eine Verbindung jedoch zwingend erforderlich.\n"
				+ "\n"
				+ "Die Tabs:\n"
				+ "\n"
				+ "Aktuatoren: Hier werden alle eingerichteten Aktuatoren gezeigt."
				+ "Die meißten Aktuatoren werden über einen Schalter gesteuert. Dimmer werden über einen Schieberegler dargestellt."
				+ "Temperatursteuerungen werden über einen Auswahlspinner eingestellt."
				+ "Durch langes klicken auf den Namen des aktuatoren können auch die definierten Funktionen gewählt werden."
				+ "Beim erneuten Aufruf des Tabs wird dieses nur aktualisiert, soweit man über WLAN verbunden ist um die Netzlast im 3G Modus gering zu halten. "
				+ "\n\n Makros: Durch drücken "
				+ "von Menü gelangt man zu den Makros. Hier können neue Makros aufgezeichnet, die Aufzeichnung beendet, oder die Liste der Makros betrachtet werden. "
				+ "Nach dem Betätigen der Aufzeichnung werden alle Aktionen (Buttons oder Funktionsaufrufe) im System protokolliert. Nach dem Stoppen der Aufzeichnung "
				+ "Kann das Makro benannt werden und ist dann im Gerät gespeichert."
				+ "Makros werden durch einfaches klick auf den Namen wiedergegeben. Durch langes klicken auf den Namen des Makros kann dieses gelöscht werden.\n"
				+ "\n"
				+ "Sensoren: Hier werden alle Sensorwerte dar gestellt. Die Aktualisierung erfolgt manuell.\n"
				+ "\n"
				+ "Timer: Hier werden alle Timer dar gestellt. Die Aktualisierung erfolgt manuell.\n"
				+ "\n"
				+ "Skripte: Hier werden alle Skripte dar gestellt. Die Aktualisierung erfolgt manuell.\n"
				+ "\n"
				+ "Optionen: Hier werden mehere Einstellmöglichkeiten geboten.\n\n"
				+ "	Geräteinfo: Zeigt die Informationen, welche aus der XS1 ausgelesen wurden.\n\n"
				+ "	Konfiguration: Hierüber kann eine Verbindung neu eingerichtet werden.\n\n"
				+ "	Hinzufügen: Hier können neue Objekte in die XS1 geschrieben werden."
				+ " Diese können Aktuatoren, Sensoren, Timer oder Skripte sein. Ggf. müssen hier Herstellerspezifische Daten eingegeben werden."
				+ " Ab der Firmwareversion 3 des XS1 wird hier zusätzlich die Möglichkeit geboten die Codes anhand der Sender anzulernen.\n\n"
				+ "	Entfernen: Hier können bestehende Objekte aus der XS1 gelöscht werden.\n\n"
				+ "	Abonnement: Ein Dienst wird gestartet, welcher alle Aktivitäten der XS1 aufzeichnet und in dem Fenster ausgibt. Dieser läuft nach Wunsch auch nach"
				+ "	Beendigung der Anwendung weiter. Beim Betätigen von 'Zurück' erfolgt eine Abfrage, ob der Dienst im Hintergrund fortgeführt werden soll.\n\n"
				+ "   Positions-Tool: !EXPERIMENTEL! Hier kann für die aktuelle Position ein bestimmtes Makro beim Verlassen oder Betreten automatisch ausgeführt werden." 
				+ " Nähert sich der Nutzer der gesetzten Position auf 1KM oder verlässt den Radius von 1KM (je nach Einstellung) wird das gewähle Makro getriggert." 
				+ " Die Anwendung muss dabei nicht laufen.\n\n" 
				+ "   Alarm-Action: Hier können Makros zu bestimmten Uhrzeiten getriggert werden. So können z.B. jeden Morgen bestimmte Lichter und Geräte automatisch angeschalten werden." 
				+ " Auch nach einem Neustart des Gerätes bleiben die Alarme erhalten.\n\n"
				+ " Über: Zeigt diese Infoseite an.\n" 
				+ "\n" 
				+ "Widget: Das Widget ermöglicht eine Sprachsteuerung der Anwendung. Um Kommandos optimal erkennen zu können müssen diese in einer bestimmten Form gesprochen werden\n" 
				+ "  Für Makros gilt das Schema ['Makro'] + [Makroname]\n" 
				+ "  Für Aktuatoren gilt das Schema [Aktuatorname] + ['an'|'anschalten'|'aus'|'ausschalten'|Wert] + ['Prozent'|'Grad' -> Optional]\n" 
				+ "  Für Sensoren gilt das Schema [Sensorname] + ['abfragen']\n");
		table.addView(tv);

	}

}
