package de.infoscout.betterhome.view.menu.act.edit;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Script;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.menu.act.MakroBodyConverter;
import de.infoscout.betterhome.view.utils.Utilities;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

public class SetEditActuator extends AsyncTask<Context, Void, Boolean>{
	private Context context = null;
	private Actuator actuator;
	private int functionNummer;
	private int makroNummer;
	private double new_value;
	private Switch switchView = null;
	private SeekBar seekbarView = null;
	private Spinner spinnerView = null;
	
	private Button button_function1 = null;
	private Button button_function2 = null;
	private Button button_function3 = null;
	private Button button_function4 = null;
	
	public SetEditActuator(Actuator act, double val, int functionNummer, int makroNummer, View view, 
			Button button_function1, Button button_function2, Button button_function3, Button button_function4){
		
		this.actuator=act;
		this.new_value=val;
		this.functionNummer=functionNummer;
		this.makroNummer=makroNummer;
		
		this.button_function1 = button_function1;
		this.button_function2 = button_function2;
		this.button_function3 = button_function3;
		this.button_function4 = button_function4;
		
		if (view != null) {
			if (view.getClass().isInstance(Switch.class)){
				this.switchView = (Switch)view;
			}
			if (view.getClass().isInstance(SeekBar.class)){
				this.seekbarView = (SeekBar)view;
			}
			if (view.getClass().isInstance(Spinner.class)){
				this.spinnerView = (Spinner)view;
			}
		}
	}
	
	private Script getCurrentMakro(Xsone myXsone){
		Script m = myXsone.getScript(makroNummer);
		
		if (m == null) Log.e(Utilities.TAG, "Makro not found!");
		
		return m;
	}
	
	
	@Override
	protected Boolean doInBackground(Context... params) {
		this.context = params[0];
		Xsone myXsone = RuntimeStorage.getMyXsone();
		
		Script makro = getCurrentMakro(myXsone);
		
		String body = makro.getBody();
		MakroBodyConverter mbc = new MakroBodyConverter(body);
		mbc.editActInList(actuator.getNumber(), new_value, functionNummer);
		
		String newBody = mbc.writeBody();
		
		String[] script_params = {makro.getName(), "onchange", newBody };
		
		boolean setted = myXsone.edit_Script(makro.getNumber(), script_params, context);
		
		
		return setted;
	}

	@Override
	protected void onPostExecute(Boolean setted) {
		super.onPostExecute(setted);
		
		if (!setted){
			XsError.printError((Activity)context);
		}
		
		if (switchView != null){
			if (actuator.getValue() == 100.0){
				switchView.setChecked(true);
			} else {
				switchView.setChecked(false);
			}
		}
		
		if (seekbarView != null){
			seekbarView.setProgress((int)actuator.getValue());
			
		}
		
		if (spinnerView != null){
			spinnerView.setSelection((int)actuator.getValue());
		}
		
		if (button_function1 != null && button_function2 != null && button_function3 != null && button_function4 != null) {
			// handle function buttons
			switch (functionNummer) {
			case 1 : 	button_function1.setTextColor(context.getResources().getColor(R.color.infoscout_blue));
			        	button_function2.setTextColor(context.getResources().getColor(R.color.grey));
			        	button_function3.setTextColor(context.getResources().getColor(R.color.grey));
			        	button_function4.setTextColor(context.getResources().getColor(R.color.grey));
						break;
			case 2 : 	button_function1.setTextColor(context.getResources().getColor(R.color.grey));
			        	button_function2.setTextColor(context.getResources().getColor(R.color.infoscout_blue));
			        	button_function3.setTextColor(context.getResources().getColor(R.color.grey));
			        	button_function4.setTextColor(context.getResources().getColor(R.color.grey));
						break;
			case 3 : 	button_function1.setTextColor(context.getResources().getColor(R.color.grey));
			        	button_function2.setTextColor(context.getResources().getColor(R.color.grey));
			        	button_function3.setTextColor(context.getResources().getColor(R.color.infoscout_blue));
			        	button_function4.setTextColor(context.getResources().getColor(R.color.grey));
						break;
			case 4 : 	button_function1.setTextColor(context.getResources().getColor(R.color.grey));
			        	button_function2.setTextColor(context.getResources().getColor(R.color.grey));
			        	button_function3.setTextColor(context.getResources().getColor(R.color.grey));
			        	button_function4.setTextColor(context.getResources().getColor(R.color.infoscout_blue));
						break;
					
			}
		}
		
		return;

	}
}
