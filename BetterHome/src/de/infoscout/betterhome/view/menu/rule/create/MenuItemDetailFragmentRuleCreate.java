package de.infoscout.betterhome.view.menu.rule.create;

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
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.menu.rule.MenuItemDetailFragmentRule;
import de.infoscout.betterhome.view.menu.rule.RuleBodyConverter;
import de.infoscout.betterhome.view.utils.Utilities;

public class MenuItemDetailFragmentRuleCreate extends Fragment {
		
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
	
	
	public MenuItemDetailFragmentRuleCreate() {
		
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
						
		// Array mit Namen anlegen für Spinner Adapter Actuator
		String[] act_names = new String[act_list.size()];
		for (int i = 0; i < act_list.size(); i++) {
			act_names[i] = ((Actuator) act_list.get(i)).getAppname();
		}
		
		adapter_act = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, act_names);
		adapter_act_if_comparer = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, act_names);
		
		// Array mit Namen anlegen für Spinner Sensor Adapter
		String[] sens_names = new String[sens_list.size()];
		for (int i = 0; i < sens_list.size(); i++) {
			sens_names[i] = ((Sensor) sens_list.get(i)).getAppname();
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
		final EditText comparerIfValue = (EditText)activity.findViewById(R.id.text_if_comparer);
		final Spinner comparerIfActSens = (Spinner)activity.findViewById(R.id.spinnerIfComparer);
		
		
		final Spinner spinnerThen = (Spinner)activity.findViewById(R.id.spinnerThenAct);
		final Spinner spinnerElse = (Spinner)activity.findViewById(R.id.spinnerElseAct);
		
		final Button buttonOK = (Button)activity.findViewById(R.id.button_script);
		final Button buttonDelete = (Button)activity.findViewById(R.id.button_script_delete);
		
		final Spinner spinnerView = (Spinner)activity.findViewById(R.id.spinner_if_comparer);
		final SeekBar seekbarView = (SeekBar)activity.findViewById(R.id.seekBar_if_comparer);
		final TextView seekbarText = (TextView)activity.findViewById(R.id.seekBarText_if_comparer);
		final Switch switchView = (Switch)activity.findViewById(R.id.switch_if_comparer);
		
		final Spinner spinnerElseView = (Spinner)activity.findViewById(R.id.spinner_else_comparer);
		final SeekBar seekbarElseView = (SeekBar)activity.findViewById(R.id.seekBar_else_comparer);
		final TextView seekbarElseText = (TextView)activity.findViewById(R.id.seekBarText_else_comparer);
		final Switch switchElseView = (Switch)activity.findViewById(R.id.switch_else_comparer);
		
		actuatorRadio.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//if (v.isSelected()){
					spinnerIf.setAdapter(adapter_act);
				//}
				return false;
			}
		});
		sensorRadio.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//if (v.isSelected()){
					spinnerIf.setAdapter(adapter_sens);
				//}
				return false;
			}
		});
		
		spinnerIf.setAdapter(adapter_sens);
		
		// Spinner Operations
		String[] operations = {getString(R.string.smallerequal), getString(R.string.smaller), getString(R.string.greaterequal), getString(R.string.greater), getString(R.string.equal), getString(R.string.unequal)};
		ArrayAdapter<String> adapter_operation = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, operations);
		operatorIf.setAdapter(adapter_operation);
		
		// Spinner Comparer Types
		String[] comparer_types = {getString(R.string.value), getString(R.string.actuator), getString(R.string.sensor)};
		final ArrayAdapter<String> adapter_comparer_type = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, comparer_types);
		typeComparerIf.setAdapter(adapter_comparer_type);
		typeComparerIf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				switch (arg2) {
					case 0 : 	// Value
								comparerIfActSens.setVisibility(View.GONE);
								comparerIfValue.setVisibility(View.VISIBLE);
								break;
					case 1 : 	// actuator
								comparerIfActSens.setVisibility(View.VISIBLE);
								comparerIfValue.setVisibility(View.GONE);
								
								comparerIfActSens.setAdapter(adapter_act_if_comparer);
								comparerIfActSens.setSelection(0);
								break;
					case 2 : 	// sensor
								comparerIfActSens.setVisibility(View.VISIBLE);
								comparerIfValue.setVisibility(View.GONE);
								
								comparerIfActSens.setAdapter(adapter_sens_if_comparer);
								comparerIfActSens.setSelection(0);
								break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
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
		        	
				} else if (actuator.isDimmable()){
					// Dimmer
					spinnerView.setVisibility(View.GONE);
					seekbarText.setVisibility(View.VISIBLE);
					seekbarView.setVisibility(View.VISIBLE);
					switchView.setVisibility(View.GONE);
					
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
		        	// Marko
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
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
				
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
		        	
				} else if (actuator.isDimmable()){
					// Dimmer
					spinnerElseView.setVisibility(View.GONE);
					seekbarElseText.setVisibility(View.VISIBLE);
					seekbarElseView.setVisibility(View.VISIBLE);
					switchElseView.setVisibility(View.GONE);
					
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
		        	// Marko
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
				}
				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
				
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
				} else if (switchView.getVisibility() == View.VISIBLE) {
					// button
					valueThen = switchView.isChecked() == true ? "100" : "0";
				} else {
					// makro
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
					if (!comparerIfValue.getText().toString().equals("") || typeComparerIf.getSelectedItemPosition() > 0){
						if (!valueThen.equals("")){
							if (!valueElse.equals("")){
								dialog = ProgressDialog.show(activity, "",
										activity.getString(R.string.add_script), true, false);
								dialog.show();
								
								RuleBodyConverter converter = new RuleBodyConverter(activity, null);
								
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
									converter.setCondition_comparer_right_value(Double.parseDouble(comparerIfValue.getText().toString()));
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
								String type = "onchange";
								String newBody = converter.writeBody();
								
								AddScript editScript = new AddScript();
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
		buttonDelete.setVisibility(View.INVISIBLE);
	}
	
	// -------------------- XsOne / DB interactions ---------------------------
	
	private class AddScript extends AsyncTask<String[], Boolean, Integer> {
		
		public AddScript(){
		}
		
		@Override
		protected Integer doInBackground(String[]... data) {
			return myXsone.add_Script(data[0], activity);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result != -1) {
				Toast.makeText(activity, activity.getString(R.string.add_script_success),
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
}
