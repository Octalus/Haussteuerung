package de.infoscout.betterhome.model.device;

import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.ScriptDB;

/**
 * 
 * @author Viktor Mayer
 *
 */
@SuppressWarnings("serial")
public class Script extends XS_Object{
	
	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/
	private ScriptDB scriptDB;
	private String body;
	private boolean makro = false;
	
	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	public Script(String name, int number, String type){
		this.setName(name);
		this.setNumber(number);
		this.setType(type);
	}
	
	public Script(){
		// Type ist standarm‰ﬂig disabled
		this.setType("disabled");
	}
	
	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/
	public String getAppname() {
		String name = getScriptDB() != null ? getScriptDB().getName() : getName();
		
		if (name.startsWith("S_")){
			name = name.substring(2, name.length());
		}
		
		name = name.replace("_", " ");
		
		return name;
	}

	public ScriptDB getScriptDB() {
		return scriptDB;
	}

	public void setScriptDB(ScriptDB scriptDB) {
		this.scriptDB = scriptDB;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	};
	
	public void setName(String name) {
		if (name.startsWith("S_")){
			this.setMakro(true);
		}
		this.name = name;
	}

	public boolean isMakro() {
		return makro;
	}

	public void setMakro(boolean makro) {
		this.makro = makro;
	}
	
	
	

}
