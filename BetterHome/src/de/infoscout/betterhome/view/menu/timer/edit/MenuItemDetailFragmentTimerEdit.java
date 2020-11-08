package de.infoscout.betterhome.view.menu.timer.edit;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Timer;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.Function;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.TimePickerDialogFragment;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailFragmentTimer;
import de.infoscout.betterhome.view.utils.Utilities;

public class MenuItemDetailFragmentTimerEdit extends Fragment {
		
	// Das Xsone Objekt fï¿½r diese Aktivity
	private Xsone myXsone;
	private static FragmentActivity activity;
	private List<XS_Object> act_list = new ArrayList<XS_Object>();
	private boolean tablet = false;
	private Dialog dialog;
	private int timerNumber;
	private Timer timer;
	private boolean initialLoad = true;
	
	public MenuItemDetailFragmentTimerEdit() {
		
	}
	
	private boolean isDeactivatingWorking() {
		String[] firmware = RuntimeStorage.getMyXsone().getFirmware().split("\\.");
		int firstNumber = Integer.parseInt(firmware[0]);
		int lastNumbers = Integer.parseInt(firmware[firmware.length-1]);
		if (lastNumbers <= 4493 && firstNumber < 4) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		activity = this.getActivity();
		if (activity.findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		myXsone = RuntimeStorage.getMyXsone();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_menuitem_detail_timer_add_edit, container, false); 
		act_list = myXsone.getMyActiveActuatorList(true, null);
		
		return view;
	}
	
	/** This handles the message send from TimePickerDialogFragment on setting Time */
    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message me){
            /** Creating a bundle object to pass currently set Time to the fragment */
            Bundle b = me.getData();
 
            /** Getting the Hour of day from bundle */
            int hour = b.getInt("set_hour");
 
            /** Getting the Minute of the hour from bundle */
            int minute = b.getInt("set_minute");
            
            String h = hour<10 ? "0"+hour : ""+hour;
    		String m = minute<10 ? "0"+minute : ""+minute;
            ((EditText) activity.findViewById(R.id.text_time_tim)).setText(h+":"+m+" "+activity.getString(R.string.time_oclock));
        }
    };
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		// Array mit Namen anlegen für Spinner Adapter
		String[] act_names = new String[act_list.size()];
		for (int i = 0; i < act_list.size(); i++) {
			act_names[i] = ((Actuator) act_list.get(i)).getAppname();
		}

		EditText time = (EditText) activity.findViewById(R.id.text_time_tim);
		time.setInputType(InputType.TYPE_NULL);
		
		time.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){
					/** Creating a bundle object to pass currently set time to the fragment */
	                Bundle b = new Bundle();
	 
	                /** Adding currently set hour to bundle object */
	                b.putInt("set_hour", timer.getHour());
	 
	                /** Adding currently set minute to bundle object */
	                b.putInt("set_minute", timer.getMinute());
	 
	                /** Instantiating TimePickerDialogFragment */
	                TimePickerDialogFragment timePicker = new TimePickerDialogFragment(mHandler);
	 
	                /** Setting the bundle object on timepicker fragment */
	                timePicker.setArguments(b);
	 
	                /** Getting fragment manger for this activity */
	                FragmentManager fm = activity.getSupportFragmentManager();
	 
	                /** Starting a fragment transaction */
	                FragmentTransaction ft = fm.beginTransaction();
	 
	                /** Adding the fragment object to the fragment transaction */
	                ft.add(timePicker, "time_picker");
	 
	                /** Opening the TimePicker fragment */
	                ft.commit();
		            return true;
		        }
				return false;
			}
		});
		
		// Beim Act Spinner muss auf ï¿½nderungen reagiert werden. Der Funktion
		// Spinner hängt vom Wert hier ab
		Spinner act_sp = (Spinner) activity.findViewById(R.id.spinner_tim_act);
		ArrayAdapter<String> adapter_act = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, act_names);
		act_sp.setAdapter(adapter_act);
		act_sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// ist ein Element gewählt muss der Funktionsspinner gesetzt
				// werden
				// zuerst muss das gewählte Objekt geholt werden
				if (arg1 != null){
					String name = ((TextView) arg1).getText().toString();
					
					Actuator comp = myXsone.getActiveActuator(name);
					
					// Dann müssen alle Funktionen ausgelesen werden falls nicht
					// null und als String[] gesetzt
					ArrayList<String> fn_list = new ArrayList<String>();
					if (comp.getMyFunction() != null) {
						for (Function f : comp.getMyFunction()) {
							if (!f.getType().equals("disabled"))
								fn_list.add(f.getDsc());
						}
					} else
						fn_list.add(activity.getString(R.string.empty));
	
					// Nun kann der Spinner geholt und die Werte gesetzt werden
					Spinner fnk_sp = (Spinner) activity.findViewById(R.id.spinner_tim_func);
					ArrayAdapter<String> adapter_act = new ArrayAdapter<String>(
							activity, android.R.layout.simple_spinner_dropdown_item,
							fn_list);
					fnk_sp.setAdapter(adapter_act);
					
					if (initialLoad){
						int fnct_nrb = timer.getFunction();						
						fnk_sp.setSelection(fnct_nrb-1);
						initialLoad=false;
					}
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		Bundle args = getArguments();
		timerNumber = args.getInt("timerNumber");

		// ---------------------------- VORBEFÜLLEN ---------------
		timer = myXsone.getTimer(timerNumber);
		
		// Aufgrund Timer Daten die Views fuellen
		EditText name = (EditText) activity.findViewById(R.id.text_tim_name);
        name.setText(timer.getAppname());
		
		CheckBox mo = (CheckBox) activity.findViewById(R.id.cb_tin_mo);
		mo.setChecked(timer.isMonday());
		CheckBox di = (CheckBox) activity.findViewById(R.id.cb_tim_di);
		di.setChecked(timer.isTuesday());
		CheckBox mi = (CheckBox) activity.findViewById(R.id.cb_tim_mi);
		mi.setChecked(timer.isWednesday());
		CheckBox don = (CheckBox) activity.findViewById(R.id.cb_tim_do);
		don.setChecked(timer.isThursday());
		CheckBox fr = (CheckBox) activity.findViewById(R.id.cb_tim_fr);
		fr.setChecked(timer.isFriday());
		CheckBox sa = (CheckBox) activity.findViewById(R.id.cb_tim_sa);
		sa.setChecked(timer.isSaturday());
		CheckBox so = (CheckBox) activity.findViewById(R.id.cb_tim_so);
		so.setChecked(timer.isSunday());
		EditText zeit = (EditText) activity.findViewById(R.id.text_time_tim);
		zeit.setText((timer.getHour()<10?"0"+timer.getHour():timer.getHour())+":"+(timer.getMinute()<10?"0"+timer.getMinute():timer.getMinute()) +" "+activity.getString(R.string.time_oclock));
		
		Spinner actuator = (Spinner) activity.findViewById(R.id.spinner_tim_act);
		int position = 0;
		for (int i=0; i<actuator.getAdapter().getCount();i++){
			String realName = timer.getActuator();
			String appName = ((Actuator)myXsone.getActiveObject(realName)).getAppname();
			if (actuator.getAdapter().getItem(i).toString().equals(appName)){
				position = i;
			}
		}
		actuator.setSelection(position);
		
		//----------------------
		// Alle Funktionen aufgrund vorbelegtem Actuator finden
		
		ArrayList<String> fn_list = new ArrayList<String>();
		Actuator comp = (Actuator)act_list.get(position);
		if (comp.getMyFunction() != null) {
			for (Function f : comp.getMyFunction()) {
				if (!f.getType().equals("disabled"))
					fn_list.add(f.getDsc());
			}
		} else
			fn_list.add("leer");

		// Nun kann der Spinner geholt und die Werte gesetzt werden
		Spinner fnk_sp = (Spinner) activity.findViewById(R.id.spinner_tim_func);
		ArrayAdapter<String> adapter_fnct = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item,
				fn_list);
		fnk_sp.setAdapter(adapter_fnct);
		
		//----------------------
		fnk_sp.setSelection(timer.getFunction()-1);
		// ----------------------------
		
		// den Button holen
		Button button = (Button) activity.findViewById(R.id.button_tim);
		// dem Button die Click Action hinzu fï¿½gen
		button.setOnClickListener(new OnClickListener() {

			// der Button wurde gedrï¿½ckt
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				EditText name = (EditText) activity.findViewById(R.id.text_tim_name);
				
				if (!name.equals("")){
					// Ladevorgang anzeigen
					dialog = ProgressDialog.show(activity, "",
							activity.getString(R.string.change_timer), true, false);
					dialog.show();
					
					Switch deactiveSwitch = (Switch) activity.findViewById(R.id.text_inactive_button);
					
					boolean disabled = false;
					
					if (isDeactivatingWorking() && deactiveSwitch.isChecked()){
						disabled = true;
					} 

					// Alle Werte auslesen
					ArrayList<String> timerData = new ArrayList<String>();
					
					timerData.add(Utilities.trimName19(name.getText().toString()));
					
					if (disabled) {
						timerData.add("disabled");
					} else {
						Spinner sunrisetime = (Spinner)activity.findViewById(R.id.spinner_tim_sun_rise_time);
						int position = sunrisetime.getSelectedItemPosition();
						
						switch (position) {
							case 0 : 	timerData.add("time");
										EditText zeit = (EditText) activity.findViewById(R.id.text_time_tim);
										timerData.add(zeit.getText().toString().substring(0, 2));
										timerData.add(zeit.getText().toString().substring(3, 5));
										break;
							case 1 : 	timerData.add("sunrise");
										timerData.add("00");
										timerData.add("00");
										break;
							case 2 : 	timerData.add("sunset");
										timerData.add("00");
										timerData.add("00");
										break;
						}
					}
					
					Spinner act = (Spinner) activity.findViewById(R.id.spinner_tim_act);
					
					XS_Object obj = myXsone.getActiveActuator(act.getSelectedItem().toString());
					timerData.add(Utilities.trimName19(obj.getName()));
					
					Spinner fnkt = (Spinner) activity.findViewById(R.id.spinner_tim_func);
					timerData.add(String.valueOf(fnkt.getSelectedItemPosition()+1));
					
					// Die Checkboxen der Wochentage prüfen
					CheckBox cb;
					cb = (CheckBox) activity.findViewById(R.id.cb_tin_mo);
					if (cb.isChecked())
						timerData.add("Mo");
					cb = (CheckBox) activity.findViewById(R.id.cb_tim_di);
					if (cb.isChecked())
						timerData.add("Tu");
					cb = (CheckBox) activity.findViewById(R.id.cb_tim_mi);
					if (cb.isChecked())
						timerData.add("We");
					cb = (CheckBox) activity.findViewById(R.id.cb_tim_do);
					if (cb.isChecked())
						timerData.add("Th");
					cb = (CheckBox) activity.findViewById(R.id.cb_tim_fr);
					if (cb.isChecked())
						timerData.add("Fr");
					cb = (CheckBox) activity.findViewById(R.id.cb_tim_sa);
					if (cb.isChecked())
						timerData.add("Sa");
					cb = (CheckBox) activity.findViewById(R.id.cb_tim_so);
					if (cb.isChecked())
						timerData.add("Su");
					
					// Starten
					EditTimer editor = new EditTimer(timerNumber, disabled);
					editor.execute(timerData);
				} else {
					Toast.makeText(activity, activity.getString(R.string.name_missing), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		Button deleteButton = (Button) activity.findViewById(R.id.button_tim_delete);
		deleteButton.setOnClickListener(new OnClickListener() {
			// der Button wurde gedrï¿½ckt
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				// Ladevorgang anzeigen
				dialog = ProgressDialog.show(activity, "",
						activity.getString(R.string.delete_timer), true, false);
				dialog.show();
				
				// Starten
				DeleteTimer editor = new DeleteTimer(timerNumber);
				editor.execute();
			}
		});
		
		Switch deactiveSwitch = (Switch) activity.findViewById(R.id.text_inactive_button);
		
		if (!isDeactivatingWorking()) {
			deactiveSwitch.setVisibility(View.GONE);
			activity.findViewById(R.id.textView3).setVisibility(View.GONE);
		} else {
		
			deactiveSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					
					if (isChecked){
						CheckBox mo = (CheckBox) activity.findViewById(R.id.cb_tin_mo);
						mo.setEnabled(false);
						CheckBox di = (CheckBox) activity.findViewById(R.id.cb_tim_di);
						di.setEnabled(false);
						CheckBox mi = (CheckBox) activity.findViewById(R.id.cb_tim_mi);
						mi.setEnabled(false);
						CheckBox don = (CheckBox) activity.findViewById(R.id.cb_tim_do);
						don.setEnabled(false);
						CheckBox fr = (CheckBox) activity.findViewById(R.id.cb_tim_fr);
						fr.setEnabled(false);
						CheckBox sa = (CheckBox) activity.findViewById(R.id.cb_tim_sa);
						sa.setEnabled(false);
						CheckBox so = (CheckBox) activity.findViewById(R.id.cb_tim_so);
						so.setEnabled(false);
						
						EditText zeit = (EditText) activity.findViewById(R.id.text_time_tim);
						zeit.setEnabled(false);
						
						Spinner actuator = (Spinner) activity.findViewById(R.id.spinner_tim_act);
						actuator.setEnabled(false);
						
						Spinner fnk_sp = (Spinner) activity.findViewById(R.id.spinner_tim_func);
						fnk_sp.setEnabled(false);
					} else {
						CheckBox mo = (CheckBox) activity.findViewById(R.id.cb_tin_mo);
						mo.setEnabled(true);
						CheckBox di = (CheckBox) activity.findViewById(R.id.cb_tim_di);
						di.setEnabled(true);
						CheckBox mi = (CheckBox) activity.findViewById(R.id.cb_tim_mi);
						mi.setEnabled(true);
						CheckBox don = (CheckBox) activity.findViewById(R.id.cb_tim_do);
						don.setEnabled(true);
						CheckBox fr = (CheckBox) activity.findViewById(R.id.cb_tim_fr);
						fr.setEnabled(true);
						CheckBox sa = (CheckBox) activity.findViewById(R.id.cb_tim_sa);
						sa.setEnabled(true);
						CheckBox so = (CheckBox) activity.findViewById(R.id.cb_tim_so);
						so.setEnabled(true);
						
						EditText zeit = (EditText) activity.findViewById(R.id.text_time_tim);
						zeit.setEnabled(true);
						
						Spinner actuator = (Spinner) activity.findViewById(R.id.spinner_tim_act);
						actuator.setEnabled(true);
						
						Spinner fnk_sp = (Spinner) activity.findViewById(R.id.spinner_tim_func);
						fnk_sp.setEnabled(true);
					}
				}
			});
			deactiveSwitch.setChecked(timer.getTimerDB() != null ? timer.getTimerDB().isInactive() : false);
		}
		
		// Spinner fuer Wechsel Sonnenuntergang/-aufgang und Zeit
		String[] tim_switch = {activity.getString(R.string.time), activity.getString(R.string.sunrise), activity.getString(R.string.sunset)};
		Spinner sunrisesettime = (Spinner) activity.findViewById(R.id.spinner_tim_sun_rise_time);
		ArrayAdapter<String> adapter_sunriseset_time = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, tim_switch);
		sunrisesettime.setAdapter(adapter_sunriseset_time);
		
		sunrisesettime.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				if (arg1 != null){
					String type = ((TextView) arg1).getText().toString();
					
					if (!type.equals(activity.getString(R.string.time))) {
						activity.findViewById(R.id.tableRow8).setVisibility(View.GONE);
					} else {
						activity.findViewById(R.id.tableRow8).setVisibility(View.VISIBLE);
					}
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		String type = timer.getType();
		
		if (type.equals("time")) {
			sunrisesettime.setSelection(0);
		} else if (type.equals("sunrise")) {
			sunrisesettime.setSelection(1);
		} else if (type.equals("sunset")) {
			sunrisesettime.setSelection(2);
		} else {
			// default time
			sunrisesettime.setSelection(0);
		}
	}
	
	// ---------------------- XsOne / DB interactions -------------------------------
	private class EditTimer extends AsyncTask<List<String>, Boolean, Boolean> {
		private int number;
		private boolean disabled;
		
		public EditTimer(int num, boolean disabled){
			this.number=num;
			this.disabled = disabled;
		}
		
		@Override
		protected Boolean doInBackground(List<String>... data) {
			return myXsone.edit_Timer(number, data[0], activity, disabled);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result) {
				Toast.makeText(activity, activity.getString(R.string.change_timer_success),
						Toast.LENGTH_LONG).show();
				
				if (tablet) {
					MenuItemDetailFragmentTimer fragment = new MenuItemDetailFragmentTimer();
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
	
	private class DeleteTimer extends AsyncTask<List<String>, Boolean, Boolean> {
		private int number;
		
		public DeleteTimer(int num){
			this.number=num;
		}
		
		@Override
		protected Boolean doInBackground(List<String>... data) {
			boolean ret = myXsone.delete_Timer(number); 
			
			DatabaseStorage db = new DatabaseStorage(activity);
			db.deleteTimer(number);
			
			db.closeDB();
			return ret;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result) {
				Toast.makeText(activity, getString(R.string.delete_timer_success),
						Toast.LENGTH_LONG).show();
			}
			// Sonst erfolgt ein Hinweistext
			else {
				XsError.printError(activity);
			}
			
			if (tablet) {
				MenuItemDetailFragmentTimer fragment = new MenuItemDetailFragmentTimer();
				activity.getSupportFragmentManager().beginTransaction()
						.replace(R.id.menuitem_detail_container, fragment).commit();
			} else {
				activity.finish();
			}
		}
	}
	
}
