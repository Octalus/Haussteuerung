package de.infoscout.betterhome.view.menu.rule;

import android.content.Context;
import android.widget.Toast;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;

public class RuleBodyConverter {
	private String body;
	private Xsone myXsone;
	private Context context;
	
	// IF
	private String condition_s;
	private XS_Object condition_comparer_left;
	private String condition_operator;
	
	private int condition_comparer_type = 0;		// 0.. Wert, 1.. Actuator, 2.. Sensor
	private XS_Object condition_comparer_right_XS;
	private double condition_comparer_right_value;
	
	// THEN
	private String then_s;
	private XS_Object then_left;
	private double then_right;
	
	// ELSE
	private String else_s;
	private XS_Object else_left;
	private double else_right;
	
	public RuleBodyConverter(Context c, String body){
		this.body = body;
		this.context = c;
		myXsone = RuntimeStorage.getMyXsone();
		/*if (body != null) {
			try {
				convert();
			} catch (StringIndexOutOfBoundsException e) {
				Toast.makeText(context, context.getString(R.string.script_invalid), Toast.LENGTH_LONG).show();
			}
		}*/
	}
	
	public String writeBody(){
		StringBuffer out = new StringBuffer();
		
		out.append("if(");
		// IF
		out.append(getStringFromXsObject(condition_comparer_left)+condition_operator);
		if (condition_comparer_type == 0){
			// Wert
			out.append(condition_comparer_right_value);
		} else if (condition_comparer_type == 1) {
			// Actuator
			out.append(getStringFromXsObject(condition_comparer_right_XS));
		} else if (condition_comparer_type == 2) {
			// Sensor
			out.append(getStringFromXsObject(condition_comparer_right_XS));
		}
		
		out.append("){\n");
		// THEN
		out.append(getStringFromXsObject(then_left)+"="+then_right);
		
		out.append(";\n}else{\n");
		// ELSE
		out.append(getStringFromXsObject(else_left)+"="+else_right);
		
		out.append(";\n}");
		
		return out.toString();
	}
	
	public void convert() throws Exception {
		int cond_start = body.indexOf("(");
		int cond_end = body.lastIndexOf(")");
		condition_s = body.substring(cond_start+1, cond_end);
		extract_condition(condition_s);
		
		int then_start = body.indexOf("{");
		int then_end = body.indexOf("}");
		// cut also \n
		then_s = body.substring(then_start+2, then_end-1);
		extract_then_else(then_s, true);
		
		
		int else_start = body.lastIndexOf("{");
		int else_end = body.lastIndexOf("}");
		// cut also \n
		else_s = body.substring(else_start+2, else_end-1);
		extract_then_else(else_s, false);
		
	}
	
	private void extract_condition(String condition){
		if (condition.contains("<=")){
			condition_operator = "<=";
		} else if (condition.contains(">=")){
			condition_operator = ">=";
		} else if (condition.contains("==")){
			condition_operator = "==";
		} else if (condition.contains("!=")){
			condition_operator = "!=";
		} else if (condition.contains("<")){
			condition_operator = "<";
		} else if (condition.contains(">")){
			condition_operator = ">";
		}
		
		if (condition_operator != null){
			String[] comparer = condition.split(condition_operator);
			String condition_comparer_left_s = comparer[0];
			String condition_comparer_right_s = comparer[1];
			
			condition_comparer_left = getXsObject(condition_comparer_left_s);
			if (condition_comparer_right_s.startsWith("@")){
				condition_comparer_type = 1;
				condition_comparer_right_XS = getXsObject(condition_comparer_right_s);
			} else if (condition_comparer_right_s.startsWith("`")){
				condition_comparer_type = 2;
				condition_comparer_right_XS = getXsObject(condition_comparer_right_s);
			} else {
				condition_comparer_type = 0;
				condition_comparer_right_value = Double.parseDouble(condition_comparer_right_s);
			}
		}
	}
	
	private void extract_then_else(String then_else_s, boolean then_part){
		String[] pair = then_else_s.split("=");
		String part_left = pair[0];
		String part_right = pair[1].substring(0, pair[1].length()-1);
		
		if (then_part){
			then_left = getXsObject(part_left);
			then_right = Double.parseDouble(part_right);
		} else {
			else_left = getXsObject(part_left);
			else_right = Double.parseDouble(part_right);
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

	public XS_Object getCondition_comparer_left() {
		return condition_comparer_left;
	}

	public void setCondition_comparer_left(XS_Object condition_comparer_left) {
		this.condition_comparer_left = condition_comparer_left;
	}

	public String getCondition_operator() {
		return condition_operator;
	}
	
	public String getCondition_operator_words() {
		if (condition_operator.equals("<=")){
			return context.getString(R.string.smallerequal);
		} else if (condition_operator.equals(">=")){
			return context.getString(R.string.greaterequal);
		} else if (condition_operator.equals("==")){
			return context.getString(R.string.equal);
		} else if (condition_operator.equals("!=")){
			return context.getString(R.string.unequal);
		} else if (condition_operator.equals("<")){
			return context.getString(R.string.smaller);
		} else if (condition_operator.equals(">")){
			return context.getString(R.string.greater);
		}
		
		return null;
	}
	
	public void setCondition_operator_words(String word){
		if (word.equals(context.getString(R.string.smallerequal))){
			condition_operator = "<=";
		} else if (word.equals(context.getString(R.string.greaterequal))){
			condition_operator = ">=";
		} else if (word.equals(context.getString(R.string.equal))){
			condition_operator = "==";
		} else if (word.equals(context.getString(R.string.unequal))){
			condition_operator = "!=";
		} else if (word.equals(context.getString(R.string.smaller))){
			condition_operator = "<";
		} else if (word.equals(context.getString(R.string.greater))){
			condition_operator = ">";
		}
	}

	public void setCondition_operator(String condition_operator) {
		this.condition_operator = condition_operator;
	}

	public double getCondition_comparer_right_value() {
		return condition_comparer_right_value;
	}

	public void setCondition_comparer_right_value(double condition_comparer_right_value) {
		this.condition_comparer_right_value = condition_comparer_right_value;
	}

	public int getCondition_comparer_type() {
		return condition_comparer_type;
	}

	public void setCondition_comparer_type(int condition_comparer_type) {
		this.condition_comparer_type = condition_comparer_type;
	}

	public XS_Object getCondition_comparer_right_XS() {
		return condition_comparer_right_XS;
	}

	public void setCondition_comparer_right_XS(XS_Object condition_comparer_right_XS) {
		this.condition_comparer_right_XS = condition_comparer_right_XS;
	}

	public XS_Object getThen_left() {
		return then_left;
	}

	public void setThen_left(XS_Object then_left) {
		this.then_left = then_left;
	}

	public double getThen_right() {
		return then_right;
	}

	public void setThen_right(double then_right) {
		this.then_right = then_right;
	}

	public XS_Object getElse_left() {
		return else_left;
	}

	public void setElse_left(XS_Object else_left) {
		this.else_left = else_left;
	}

	public double getElse_right() {
		return else_right;
	}

	public void setElse_right(double else_right) {
		this.else_right = else_right;
	}
	

}
