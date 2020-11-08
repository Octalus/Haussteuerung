package com.android.xs.model.device;

import com.android.xs.model.device.components.XS_Object;

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
	};
	
	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/


}
