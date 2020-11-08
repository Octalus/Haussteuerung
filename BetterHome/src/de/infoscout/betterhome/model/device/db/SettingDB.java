package de.infoscout.betterhome.model.device.db;

import java.io.Serializable;

public class SettingDB implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5797017962963051498L;
	private String key;
	private String value;
	
	public SettingDB(){
	}
	
	public SettingDB(String key, String value){
		this.key=key;
		this.value=value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
