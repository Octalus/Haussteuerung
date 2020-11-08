package de.infoscout.betterhome.view.menu.timer.add;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.Function;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.TimePickerDialogFragment;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailFragmentTimer;
import de.infoscout.betterhome.view.utils.Utilities;

public class MenuItemDetailFragmentTimerAdd extends Fragment {
		
	// Das Xsone Objekt fï¿½r diese Aktivity
	private Xsone myXsone;
	private static FragmentActivity activity;
	private List<XS_Object> act_list = new ArrayList<XS_Object>();
	private boolean tablet = false;
	private Dialog dialog;

	public MenuItemDetailFragmentTimerAdd() {
		
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
		time.setText("12:00 "+activity.getString(R.string.time_oclock));
		time.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_UP){							
					/** Creating a bundle object to pass currently set time to the fragment */
	                Bundle b = new Bundle();
	 
	                /** Adding currently set hour to bundle object */
	                b.putInt("set_hour", 12);
	 
	                /** Adding currently set minute to bundle object */
	                b.putInt("set_minute", 0);
	 
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
		
		// Die Spinner befüllen
		
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
					
					Actuator comp = null;
					for (XS_Object a : act_list) {
						if (((Actuator)a).getAppname().equals(name)) {
							comp = (Actuator)a;
							break;
						}
					}
					
					ArrayList<String> fn_list = new ArrayList<String>();
					if (comp != null && comp.getMyFunction() != null) {
						for (Function f : comp.getMyFunction()) {
							if (!f.getType().equals("disabled"))
								fn_list.add(f.getDsc());
						}
					} else {
						fn_list.add("leer");
					}
	
					// Nun kann der Spinner geholt und die Werte gesetzt werden
					Spinner fnk_sp = (Spinner) activity.findViewById(R.id.spinner_tim_func);
					ArrayAdapter<String> adapter_act = new ArrayAdapter<String>(
							activity, android.R.layout.simple_spinner_dropdown_item,
							fn_list);
					fnk_sp.setAdapter(adapter_act);
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		
		// den Button holen
		Button deleteButton = (Button) activity.findViewById(R.id.button_tim_delete);
		deleteButton.setVisibility(Button.INVISIBLE);
		
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
							activity.getString(R.string.add_timer), true, false);
					dialog.show();

					// Alle Werte auslesen
					ArrayList<String> timerData = new ArrayList<String>();
										
					timerData.add(Utilities.trimName19(name.getText().toString()));
					
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
					
					Spinner act = (Spinner) activity.findViewById(R.id.spinner_tim_act);
					
					XS_Object obj = myXsone.getActiveActuator(act.getSelectedItem().toString());
					timerData.add(Utilities.trimName19(obj.getName()));
					
					Spinner fnkt = (Spinner) activity.findViewById(R.id.spinner_tim_func);
					timerData.add(String.valueOf(fnkt.getSelectedItemPosition()));
					
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
					new addTim().execute(timerData);
				} else {
					Toast.makeText(activity, activity.getString(R.string.name_missing), Toast.LENGTH_LONG).show();
				}
			}
		});
		
		Switch deactiveSwitch = (Switch) activity.findViewById(R.id.text_inactive_button);
		deactiveSwitch.setVisibility(View.GONE);
		
		// Spinner fuer Wechsel Sonnenuntergang/-aufgang und Zeit
		String[] tim_switch = {activity.getString(R.string.time), activity.getString(R.string.sunrise), activity.getString(R.string.sunset)};
		Spinner sunrisesettime = (Spinner) activity.findViewById(R.id.spinner_tim_sun_rise_time);
		ArrayAdapter<String> adapter_sunriseset_time = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, tim_switch);
		sunrisesettime.setAdapter(adapter_sunriseset_time);
		sunrisesettime.setSelection(0); // default = time
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
	}
	
	// -------------- XsOne / DB interactions --------------------------------
	private class addTim extends AsyncTask<List<String>, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(List<String>... data) {
			return myXsone.add_Timer(data[0], activity);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result) {
				Toast.makeText(activity, activity.getString(R.string.add_timer_success),
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
}
