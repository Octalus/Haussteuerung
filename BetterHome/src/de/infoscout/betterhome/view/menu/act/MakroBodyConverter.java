package de.infoscout.betterhome.view.menu.act;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;

public class MakroBodyConverter {
	private String body;
	private Xsone myXsone;
	
	// IF
	private String condition_s;
	private Actuator condition_actuator_left;
	
	// THEN
	private String then_s;
	private List<ActValueHolder> actuator_list;
	
	public MakroBodyConverter(String body){
		this.body = body;
		myXsone = RuntimeStorage.getMyXsone();
		if (body != null) convert(true);
	}
	
	public String writeBody(){
		convert(false);
		StringBuffer out = new StringBuffer();
		
		out.append("if(");
		// IF
		out.append(getStringFromXsObject(condition_actuator_left)+"==100");
		
		out.append("){\n");
		// THEN
		if (actuator_list != null) {
			ActValueHolder holder;
			for (int i=0; i<actuator_list.size(); i++){
				holder = actuator_list.get(i);
				
				if (holder.getFunctionNummer() == -1) {
					// normal statement
					out.append(getStringFromXsObject(holder.getActuator())+"="+holder.getValue());
					
				} else {
					// function statement
					out.append(getStringFromXsObject(holder.getActuator())+"."+holder.getFunctionNummer());
				}
				out.append(";\n");
			}
		}
		out.append("}");
		
		
		
		return out.toString();
		
	}
	
	private void convert(boolean innerBody){
		int cond_start = body.indexOf("(");
		int cond_end = body.lastIndexOf(")");
		condition_s = body.substring(cond_start+1, cond_end);
		extract_condition(condition_s);
		
		int then_start = body.indexOf("{");
		int then_end = body.indexOf("}");
		// cut also \n
		if (then_end - then_start >= 3){
			then_s = body.substring(then_start+2, then_end-1);
		} else {
			then_s = null;
		}
		if (innerBody) extract_then(then_s);
	}
	
	private void extract_condition(String condition){
		String[] comparer = condition.split("==");
		String condition_comparer_left_s = comparer[0];
		
		condition_actuator_left = (Actuator)getXsObject(condition_comparer_left_s);
	}
	
	private void extract_then(String then_s){
		
		actuator_list = new ArrayList<ActValueHolder>();
		
		if (then_s!=null){
			String[] makro_parts = then_s.split("\n");
			
			String[] pair;
			String part_left;
			String part_right;
			for (int i=0; i< makro_parts.length; i++) {
				
				ActValueHolder new_act_val = null;
				
				if (makro_parts[i].contains("=")) {
					// normal statement with =
					pair = makro_parts[i].split("=");
					part_left = pair[0];
					part_right = pair[1].substring(0, pair[1].length()-1);
					
					new_act_val = new ActValueHolder();
					new_act_val.setActuator((Actuator)getXsObject(part_left));
					new_act_val.setValue(Double.parseDouble(part_right));
				} else {
					// function statement
					pair = makro_parts[i].split(Pattern.quote("."));
					part_left = pair[0];
					part_right = pair[1].substring(0, pair[1].length()-1);
					
					new_act_val = new ActValueHolder();
					new_act_val.setActuator((Actuator)getXsObject(part_left));
					new_act_val.setFunctionNummer((Integer.parseInt(part_right)));
				}
					
				actuator_list.add(new_act_val);
			}
		}
		
	}
	
	public void addActToList(Actuator act, double val, int functionNummer){
		ActValueHolder new_act_val = new ActValueHolder();
		new_act_val.setActuator(act);
		new_act_val.setValue(val);
		new_act_val.setFunctionNummer(functionNummer);
		
		actuator_list.add(new_act_val);
	}
	
	public boolean removeActFromList(int number){
		
		int index = -1;
		for (int i=0; i<actuator_list.size(); i++){
			if (actuator_list.get(i).getActuator().getNumber() == number) {
				index = i;
			}
		}
		
		if (index != -1){
			actuator_list.remove(index);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean editActInList(int number, double value, int functionNummer){
		
		int index = -1;
		for (int i=0; i<actuator_list.size(); i++){
			if (actuator_list.get(i).getActuator().getNumber() == number) {
				index = i;
			}
		}
		
		if (index != -1){
			actuator_list.get(index).setValue(value);
			actuator_list.get(index).setFunctionNummer(functionNummer);
			return true;
		} else {
			return false;
		}
	}
	
	private XS_Object getXsObject(String object_s){
		
		if (object_s.startsWith("@")){
			// ACTUATOR
			int number = Integer.parseInt(object_s.substring(1, object_s.length()));
			return myXsone.getActuator(number);
		} else if (object_s.startsWith("`")){
			// SENSOR
			int number = Integer.parseInt(object_s.substring(1, object_s.length()));
			return myXsone.getSensor(number);
		}
		
		return null;
	}
	
	private String getStringFromXsObject(XS_Object object){
		if (object instanceof Actuator){
			return "@"+object.getNumber();
		} else if (object instanceof Sensor){
			return "`"+object.getNumber();
		}
		
		return null;
	}
	
	// ------------------------------------------------------------------------------------------------

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCondition_s() {
		return condition_s;
	}

	public void setCondition_s(String condition_s) {
		this.condition_s = condition_s;
	}

	public Actuator getCondition_actuator_left() {
		return condition_actuator_left;
	}

	public void setCondition_actuator_left(Actuator condition_actuator_left) {
		this.condition_actuator_left = condition_actuator_left;
	}

	public String getThen_s() {
		return then_s;
	}

	public void setThen_s(String then_s) {
		this.then_s = then_s;
	}

	public List<ActValueHolder> getActuator_list() {
		return actuator_list;
	}

	public void setActuator_list(List<ActValueHolder> actuator_list) {
		this.actuator_list = actuator_list;
	}

}
