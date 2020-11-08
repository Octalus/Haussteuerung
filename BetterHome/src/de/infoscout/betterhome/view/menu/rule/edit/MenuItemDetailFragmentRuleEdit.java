package de.infoscout.betterhome.view.menu.rule.edit;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Script;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.menu.rule.MenuItemDetailFragmentRule;
import de.infoscout.betterhome.view.menu.rule.RuleBodyConverter;
import de.infoscout.betterhome.view.utils.Utilities;

public class MenuItemDetailFragmentRuleEdit extends Fragment {
		
	// Das Xsone Objekt fï¿½r diese Aktivity
	private Xsone myXsone;
	private static FragmentActivity activity;
	private List<XS_Object> act_list = new ArrayList<XS_Object>();
	private List<XS_Object> sens_list = new ArrayList<XS_Object>();
	private boolean tablet = false;
	private Dialog dialog;
	private ArrayAdapter<String> adapter_sens;
	private ArrayAdapter<String> adapter_act;
	private ArrayAdapter<String> adapter_sens_if_comparer;
	private ArrayAdapter<String> adapter_act_if_comparer;
	
	private RuleBodyConverter converter;
	
	private int scriptNumber;
	private Script script;

	public MenuItemDetailFragmentRuleEdit() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		activity = this.getActivity();
		if (activity.findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		myXsone = RuntimeStorage.getMyXsone();
		act_list = myXsone.getMyActiveActuatorList(true, null);
		sens_list = myXsone.getMyActiveSensorList();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_menuitem_detail_rule_add_edit, container, false); 
		
		act_list = myXsone.getMyActiveActuatorList(true, null);
		
		return view;
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	
		super.onViewCreated(view, savedInstanceState);
		
		Bundle args = getArguments();
		scriptNumber = args.getInt("scriptNumber");

		// ---------------------------- Script and Converter ---------------
		
		script = myXsone.getScript(scriptNumber);
		
		converter = new RuleBodyConverter(activity, script.getBody());
		try {
			converter.convert();
		} catch (Exception e) {
			Toast.makeText(activity, activity.getString(R.string.script_invalid), Toast.LENGTH_LONG).show();
		}
		
		// ---------------------------- VORBEFÜLLEN ------------------------
		
		// Array mit Namen anlegen für Spinner Adapter Actuator
		int current_act_pos_if = -1;
		int current_act_pos_then = -1;
		int current_act_pos_else = -1;
		int current_act_pos_comparer_right = -1;
		String[] act_names = new String[act_list.size()];
		for (int i = 0; i < act_list.size(); i++) {
			act_names[i] = ((Actuator) act_list.get(i)).getAppname();
			if (converter.getCondition_comparer_left() != null && converter.getCondition_comparer_left().getNumber() == ((Actuator) act_list.get(i)).getNumber()){
				current_act_pos_if = i;
			}
			
			if (converter.getThen_left() != null && converter.getThen_left().getNumber() == ((Actuator) act_list.get(i)).getNumber()){
				current_act_pos_then = i;
			}
			
			if (converter.getElse_left() != null && converter.getElse_left().getNumber() == ((Actuator) act_list.get(i)).getNumber()){
				current_act_pos_else = i;
			}
			
			if (converter.getCondition_comparer_right_XS() != null && converter.getCondition_comparer_right_XS().getNumber() == ((Actuator) act_list.get(i)).getNumber()){
				current_act_pos_comparer_right = i;
			}
			
		}
		
		adapter_act = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, act_names);
		adapter_act_if_comparer = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, act_names);
		
		// Array mit Namen anlegen für Spinner Sensor Adapter
		int current_sens_pos_if = -1;
		int current_sens_pos_comparer_right = -1;
		String[] sens_names = new String[sens_list.size()];
		for (int i = 0; i < sens_list.size(); i++) {
			sens_names[i] = ((Sensor) sens_list.get(i)).getAppname();
			if (converter.getCondition_comparer_left().getNumber() == ((Sensor) sens_list.get(i)).getNumber()){
				current_sens_pos_if = i;
			}
			
			if (converter.getCondition_comparer_right_XS().getNumber() == ((Sensor) sens_list.get(i)).getNumber()){
				current_sens_pos_comparer_right = i;
			}
		}
		
		adapter_sens = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, sens_names);
		adapter_sens_if_comparer = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, sens_names);
		
		final EditText nameText = (EditText)activity.findViewById(R.id.text_script_name);
		final RadioButton sensorRadio = (RadioButton)activity.findViewById(R.id.radioSensor);
		final RadioButton actuatorRadio = (RadioButton)activity.findViewById(R.id.radioActuator);

		final Spinner spinnerIf = (Spinner)activity.findViewById(R.id.spinnerIf);
		final Spinner operatorIf = (Spinner)activity.findViewById(R.id.spinnerOperator);
		final Spinner typeComparerIf = (Spinner)activity.findViewById(R.id.spinnerTypeComparer);
		final Spinner comparerIfActSens = (Spinner)activity.findViewById(R.id.spinnerIfComparer);

		final Spinner spinnerThen = (Spinner)activity.findViewById(R.id.spinnerThenAct);
		final EditText comparer_if = (EditText)activity.findViewById(R.id.text_if_comparer);
		final Spinner spinnerElse = (Spinner)activity.findViewById(R.id.spinnerElseAct);
		
		final Button buttonOK = (Button)activity.findViewById(R.id.button_script);
		final Button buttonDelete = (Button)activity.findViewById(R.id.button_script_delete);
		
		// Name
		nameText.setText(script.getAppname());
		
		// Radio Buttons + SpinnerIf
		if (converter.getCondition_comparer_left() instanceof Sensor){
			sensorRadio.setChecked(true);
			actuatorRadio.setChecked(false);
			
			spinnerIf.setAdapter(adapter_sens);
			spinnerIf.setSelection(current_sens_pos_if);
			
		} else if (converter.getCondition_comparer_left() instanceof Actuator){
			sensorRadio.setChecked(false);
			actuatorRadio.setChecked(true);
			
			spinnerIf.setAdapter(adapter_act);
			spinnerIf.setSelection(current_act_pos_if);
		}
		actuatorRadio.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//if (v.isSelected()){
					spinnerIf.setAdapter(adapter_act);
				//}
				return false;
			}
		});
		sensorRadio.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//if (v.isSelected()){
					spinnerIf.setAdapter(adapter_sens);
				//}
				return false;
			}
		});
		
		// Spinner Operations
		String[] operations = {getString(R.string.smallerequal), getString(R.string.smaller), getString(R.string.greaterequal), getString(R.string.greater), getString(R.string.equal), getString(R.string.unequal)};
		ArrayAdapter<String> adapter_operation = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, operations);
		operatorIf.setAdapter(adapter_operation);
		String currentOper = converter.getCondition_operator_words();
		int currentOperPos = 0;
		if (currentOper.equals(getString(R.string.smallerequal))) currentOperPos = 0;
		else if (currentOper.equals(getString(R.string.smaller))) currentOperPos = 1;
		else if (currentOper.equals(getString(R.string.greaterequal))) currentOperPos = 2;
		else if (currentOper.equals(getString(R.string.greater))) currentOperPos = 3;
		else if (currentOper.equals(getString(R.string.equal))) currentOperPos = 4;
		else if (currentOper.equals(getString(R.string.unequal))) currentOperPos = 5;
		operatorIf.setSelection(currentOperPos);
		
		final Spinner spinnerView = (Spinner)activity.findViewById(R.id.spinner_if_comparer);
		final SeekBar seekbarView = (SeekBar)activity.findViewById(R.id.seekBar_if_comparer);
		final TextView seekbarText = (TextView)activity.findViewById(R.id.seekBarText_if_comparer);
		final Switch switchView = (Switch)activity.findViewById(R.id.switch_if_comparer);
		
		final Spinner spinnerElseView = (Spinner)activity.findViewById(R.id.spinner_else_comparer);
		final SeekBar seekbarElseView = (SeekBar)activity.findViewById(R.id.seekBar_else_comparer);
		final TextView seekbarElseText = (TextView)activity.findViewById(R.id.seekBarText_else_comparer);
		final Switch switchElseView = (Switch)activity.findViewById(R.id.switch_else_comparer);
				
		// If Comparer
		// Spinner Comparer Types
		String[] comparer_types = {getString(R.string.value), getString(R.string.actuator), getString(R.string.sensor)};
		final ArrayAdapter<String> adapter_comparer_type = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, comparer_types);
		typeComparerIf.setAdapter(adapter_comparer_type);
		final int act_pos_comparer_right = current_act_pos_comparer_right;
		final int sens_pos_comparer_right = current_sens_pos_comparer_right;
		typeComparerIf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				switch (arg2) {
					case 0 : 	// Value
								comparerIfActSens.setVisibility(View.GONE);
								comparer_if.setVisibility(View.VISIBLE);
								break;
					case 1 : 	// actuator
								comparerIfActSens.setVisibility(View.VISIBLE);
								comparer_if.setVisibility(View.GONE);
								
								comparerIfActSens.setAdapter(adapter_act_if_comparer);
								if (act_pos_comparer_right != -1){
									comparerIfActSens.setSelection(act_pos_comparer_right);
								} else {
									comparerIfActSens.setSelection(0);
								}
								break;
					case 2 : 	// sensor
								comparerIfActSens.setVisibility(View.VISIBLE);
								comparer_if.setVisibility(View.GONE);
								
								comparerIfActSens.setAdapter(adapter_sens_if_comparer);
								if (sens_pos_comparer_right != -1){
									comparerIfActSens.setSelection(sens_pos_comparer_right);
								} else {
									comparerIfActSens.setSelection(0);
								}
								break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		if (converter.getCondition_comparer_type() == 0) {
			// Wert 
			typeComparerIf.setSelection(0);
			comparer_if.setText(converter.getCondition_comparer_right_value()+"");
		} else if (converter.getCondition_comparer_type() == 1) {
			// Actuator
			typeComparerIf.setSelection(1);
		} else if (converter.getCondition_comparer_type() == 2) {
			// Sensor
			typeComparerIf.setSelection(2);
		} 
		
		// Spinner THEN
		spinnerThen.setAdapter(adapter_act);
		spinnerThen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Actuator actuator = myXsone.getActiveActuator(adapter_act.getItem(arg2));
				
				if (actuator.getType().equals("temperature")){
					// Spinner
					spinnerView.setVisibility(View.VISIBLE);
					seekbarText.setVisibility(View.GONE);
					seekbarView.setVisibility(View.GONE);
					switchView.setVisibility(View.GONE);
					
					ArrayAdapter<String> spinnerAdapter = Utilities.getTemperatureSpinnerAdapter(activity);
		        	spinnerView.setAdapter(spinnerAdapter);
		        	
		        	int pos=spinnerAdapter.getPosition(String.valueOf((double)converter.getThen_right()) + " °C");
		            spinnerView.setSelection(pos);
		        	
				} else if (actuator.isDimmable()){
					// Dimmer
					spinnerView.setVisibility(View.GONE);
					seekbarText.setVisibility(View.VISIBLE);
					seekbarView.setVisibility(View.VISIBLE);
					switchView.setVisibility(View.GONE);
					
					seekbarText.setText((int) converter.getThen_right()+" %");
		        	seekbarView.setProgress((int) converter.getThen_right());
		        	seekbarView.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
							seekbarText.setText(seekBar.getProgress() + " %");
						}
		
						public void onStartTrackingTouch(SeekBar seekBar) {
						}
		
						public void onStopTrackingTouch(SeekBar seekBar) {
						}
					});
				} else if (actuator.isMakro()) {
					spinnerView.setVisibility(View.GONE);
					seekbarText.setVisibility(View.GONE);
					seekbarView.setVisibility(View.GONE);
					switchView.setVisibility(View.GONE);
		        	
				} else {
					// Button
					spinnerView.setVisibility(View.GONE);
					seekbarText.setVisibility(View.GONE);
					seekbarView.setVisibility(View.GONE);
					switchView.setVisibility(View.VISIBLE);
					
					switchView.setChecked(converter.getThen_right()==100.0 ? true : false);
				}
				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spinnerThen.setSelection(current_act_pos_then);
		
		// Spinner ELSE
		spinnerElse.setAdapter(adapter_act);
		spinnerElse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Actuator actuator = myXsone.getActiveActuator(adapter_act.getItem(arg2));
				
				if (actuator.getType().equals("temperature")){
					// Spinner
					spinnerElseView.setVisibility(View.VISIBLE);
					seekbarElseText.setVisibility(View.GONE);
					seekbarElseView.setVisibility(View.GONE);
					switchElseView.setVisibility(View.GONE);
					
					ArrayAdapter<String> spinnerAdapter = Utilities.getTemperatureSpinnerAdapter(activity);
		        	spinnerElseView.setAdapter(spinnerAdapter);
		        	
		        	int pos=spinnerAdapter.getPosition(String.valueOf((double)converter.getElse_right()) + " °C");
		            spinnerElseView.setSelection(pos);
		        	
				} else if (actuator.isDimmable()){
					// Dimmer
					spinnerElseView.setVisibility(View.GONE);
					seekbarElseText.setVisibility(View.VISIBLE);
					seekbarElseView.setVisibility(View.VISIBLE);
					switchElseView.setVisibility(View.GONE);
					
					seekbarElseText.setText((int) converter.getElse_right()+" %");
		        	seekbarElseView.setProgress((int) converter.getElse_right());
		        	seekbarElseView.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
							seekbarElseText.setText(seekBar.getProgress() + " %");
						}
		
						public void onStartTrackingTouch(SeekBar seekBar) {
						}
		
						public void onStopTrackingTouch(SeekBar seekBar) {
						}
					});
				} else if (actuator.isMakro()) {
					spinnerView.setVisibility(View.GONE);
					seekbarText.setVisibility(View.GONE);
					seekbarView.setVisibility(View.GONE);
					switchView.setVisibility(View.GONE);
		        	
				} else {
					// Button
					spinnerElseView.setVisibility(View.GONE);
					seekbarElseText.setVisibility(View.GONE);
					seekbarElseView.setVisibility(View.GONE);
					switchElseView.setVisibility(View.VISIBLE);
					
					switchElseView.setChecked(converter.getElse_right()==100.0 ? true : false);
				}
				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spinnerElse.setSelection(current_act_pos_else);
				
		// Save Button
		buttonOK.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				String valueThen = "";
				
				if (spinnerView.getVisibility() == View.VISIBLE){
					// spinner
					valueThen = spinnerView.getSelectedItem().toString();
					
					// remove °C from end
					valueThen = Utilities.getValueFromTemperature(valueThen);
				} else if (seekbarView.getVisibility() == View.VISIBLE){
					// seekbar
					valueThen = seekbarView.getProgress()+"";
				} else if (switchView.getVisibility() == View.VISIBLE){
					// button
					valueThen = switchView.isChecked() == true ? "100" : "0";
				} else {
					// Makro
					valueThen = "100";
				}
				
				String valueElse = "";
				
				if (spinnerElseView.getVisibility() == View.VISIBLE){
					// spinner
					valueElse = spinnerElseView.getSelectedItem().toString();
					
					// remove °C from end
					valueElse = Utilities.getValueFromTemperature(valueElse);
				} else if (seekbarElseView.getVisibility() == View.VISIBLE){
					// seekbar
					valueElse = seekbarElseView.getProgress()+"";
				} else if (switchElseView.getVisibility() == View.VISIBLE){
					// button
					valueElse = switchElseView.isChecked() == true ? "100" : "0";
				} else {
					// makro
					valueElse = "100";
				}
				
				
				if (!nameText.getText().toString().equals("")){
					if (!comparer_if.getText().toString().equals("") || typeComparerIf.getSelectedItemPosition() > 0){
						if (!valueThen.equals("")){
							if (!valueElse.equals("")){
								dialog = ProgressDialog.show(activity, "",
										activity.getString(R.string.change_script), true, false);
								dialog.show();
								
								String xsName = spinnerIf.getSelectedItem().toString();
								
								if (sensorRadio.isChecked()){
									converter.setCondition_comparer_left(myXsone.getActiveSensor(xsName));
								} else {
									converter.setCondition_comparer_left(myXsone.getActiveActuator(xsName));
								}
								
								converter.setCondition_operator_words(operatorIf.getSelectedItem().toString());
								
								if (typeComparerIf.getSelectedItemPosition() == 0) {
									// Wert
									converter.setCondition_comparer_type(0);
									converter.setCondition_comparer_right_value(Double.parseDouble(comparer_if.getText().toString()));
								} else if (typeComparerIf.getSelectedItemPosition() == 1) {
									// Actuator
									converter.setCondition_comparer_type(1);
									xsName = comparerIfActSens.getSelectedItem().toString();
									converter.setCondition_comparer_right_XS(myXsone.getActiveActuator(xsName));
								} else if (typeComparerIf.getSelectedItemPosition() == 2) {
									// Sensor
									converter.setCondition_comparer_type(2);
									xsName = comparerIfActSens.getSelectedItem().toString();
									converter.setCondition_comparer_right_XS(myXsone.getActiveSensor(xsName));
								}
								
								xsName = spinnerThen.getSelectedItem().toString();
								converter.setThen_left(myXsone.getActiveActuator(xsName));
								converter.setThen_right(Double.parseDouble(valueThen));
								
								xsName = spinnerElse.getSelectedItem().toString();
								converter.setElse_left(myXsone.getActiveActuator(xsName));
								converter.setElse_right(Double.parseDouble(valueElse));
								
								String name = Utilities.trimName19(nameText.getText().toString());
								String type = script.getType();
								String newBody = converter.writeBody();
								
								EditScript editScript = new EditScript(script.getNumber());
								String[] params = {name, type, newBody };
								editScript.execute(params);
							} else {
								Toast.makeText(activity, activity.getString(R.string.rule_elsevalue_missing), Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(activity, activity.getString(R.string.rule_thenvalue_missing), Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(activity, activity.getString(R.string.rule_comparervalue_missing), Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(activity, activity.getString(R.string.name_missing), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		// Delete Button
		buttonDelete.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(activity, "",
						activity.getString(R.string.delete_script), true, false);
				dialog.show();
				
				(new DeleteScript(script.getNumber())).execute();
				
			}
		});
	}
	
	// ------------------------ XsOne / DB interactions ----------------------------------
	
	private class EditScript extends AsyncTask<String[], Boolean, Boolean> {
		private int number;
		
		public EditScript(int num){
			this.number=num;
		}
		
		@Override
		protected Boolean doInBackground(String[]... data) {
			return myXsone.edit_Script(number, data[0], activity);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result) {
				Toast.makeText(activity, activity.getString(R.string.change_script_success),
						Toast.LENGTH_LONG).show();
				
				if (tablet) {
					MenuItemDetailFragmentRule fragment = new MenuItemDetailFragmentRule();
					activity.getSupportFragmentManager().beginTransaction()
							.replace(R.id.menuitem_detail_container, fragment).commit();
				} else {
					activity.finish();
				}
			}
			// Sonst erfolgt ein Hinweistext
			else {
				XsError.printError(activity);
				return;
			}
		}
	}
	
	private class DeleteScript extends AsyncTask<Void, Boolean, Boolean> {
		private int number;
		
		public DeleteScript(int num){
			this.number=num;
		}
		
		@Override
		protected Boolean doInBackground(Void... data) {
			boolean ret = myXsone.delete_Script(number); 
			
			DatabaseStorage db = new DatabaseStorage(activity);
			db.deleteScript(number);
			db.closeDB();
			
			return ret;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result) {
				Toast.makeText(activity, getString(R.string.delete_script_success),
						Toast.LENGTH_LONG).show();
			}
			// Sonst erfolgt ein Hinweistext
			else {
				XsError.printError(activity);
			}
			
			if (tablet) {
				MenuItemDetailFragmentRule fragment = new MenuItemDetailFragmentRule();
				activity.getSupportFragmentManager().beginTransaction()
						.replace(R.id.menuitem_detail_container, fragment).commit();
			} else {
				activity.finish();
			}
			
		
		}
	}
}
