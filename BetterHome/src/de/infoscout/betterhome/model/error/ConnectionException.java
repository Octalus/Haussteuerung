package de.infoscout.betterhome.model.error;

/**
 * Eigene Exception für die XS1 App. Dient der Behandlung unterschiedlicher
 * Verbindungsfehler
 * 
 * @author Viktor Mayer
 * 
 */
public class ConnectionException extends Exception {

	/**
	 * Die UID
	 */
	private static final long serialVersionUID = -5725734588690121957L;

	/**
	 * Der Konstruktor, der die Fehlermeldung als Parameter bekommt
	 * 
	 * @param fehlermeldung
	 */
	public ConnectionException(String fehlermeldung) {
		super(fehlermeldung);
	}
}
