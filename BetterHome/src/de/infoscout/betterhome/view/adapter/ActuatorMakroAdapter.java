package de.infoscout.betterhome.view.adapter;

import java.util.List;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Script;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.Function;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.menu.act.ActValueHolder;
import de.infoscout.betterhome.view.menu.act.MakroBodyConverter;
import de.infoscout.betterhome.view.menu.act.edit.SetEditActuator;
import de.infoscout.betterhome.view.pojo.SeparatorHolder;
import de.infoscout.betterhome.view.utils.Utilities;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ActuatorMakroAdapter extends ArrayAdapter<ActValueHolder>{
	Context context; 
    int layoutResourceId;    
    List<ActValueHolder> data = null;
    boolean spinnerClicked = false;
    private DatabaseStorage db;
    List<RoomDB> rooms;
    SeparatorHolder[] cellStates;
    
    private final int ID_BUFFER = 500;
    
    public ActuatorMakroAdapter(Context context, int layoutResourceId, List<ActValueHolder> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.cellStates = new SeparatorHolder[data.size()];
        
        db = new DatabaseStorage(context);
        rooms = db.getAllRooms();
        db.closeDB();
    }
    
    private RoomDB getRoomById(int id){
    	for (int i=0; i<rooms.size(); i++){
    		if (rooms.get(i).getId() == id){
    			return rooms.get(i);
    		}
    	}
    	return null;
    }
    
    private String getRoomNameById(int id){
    	RoomDB room = getRoomById(id);
    	
    	if (room == null){
    		return context.getResources().getString(R.string.notAssigned);
    	} else {
    		return room.getName();
    	}
    }
    
    private int getRoomIdFromObject(Object object) {
    	
    	if (object instanceof Actuator){
    		Actuator actuator = (Actuator)object;
    		return actuator.getActuatorDB() != null ? (actuator.getActuatorDB().getRoomId()) : 0;
    	}
    	
    	return 0;
    }

    @Override
    public int getViewTypeCount() {                 
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    
    @SuppressLint("ClickableViewAccessibility")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ActuatorSensorHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ActuatorSensorHolder();
            
            holder.facet_actuator = (RelativeLayout)row.findViewById(R.id.facet_actuator);
            holder.facet_actuator_functions = (RelativeLayout)row.findViewById(R.id.facet_actuator_functions);
            holder.facet_sensor = (RelativeLayout)row.findViewById(R.id.facet_sensor);
            holder.facet_camera = (RelativeLayout)row.findViewById(R.id.facet_camera);
            
            holder.seperator = (TextView)row.findViewById(R.id.grouping);
            
            // ACTUATOR
            holder.switchButton = (Switch)row.findViewById(R.id.switch1);
            holder.button = (Button)row.findViewById(R.id.button1);
            holder.seekbar = (SeekBar)row.findViewById(R.id.seekBar1);
            holder.textAct = (TextView)row.findViewById(R.id.text);
            holder.spinner = (Spinner)row.findViewById(R.id.spinner1);
            holder.newValueText = (TextView)row.findViewById(R.id.newValueText);
            holder.seekbarText = (TextView)row.findViewById(R.id.seekBarText);
            holder.shutterDown = (ImageButton)row.findViewById(R.id.shutterDown);
            holder.shutterUp = (ImageButton)row.findViewById(R.id.shutterUp);
            
            Object image = row.findViewById(R.id.iconact);
            if (image != null){
            	holder.imgIconAct = (ImageView)image;
            } else {
            	holder.imgIconAct = null;
            }
            
            // ACTUATOR Functions
            holder.text_functions = (TextView)row.findViewById(R.id.text_functions);
            holder.button_function1 = (Button)row.findViewById(R.id.functionButton1);
            holder.button_function2 = (Button)row.findViewById(R.id.functionButton2);
            holder.button_function3 = (Button)row.findViewById(R.id.functionButton3);
            holder.button_function4 = (Button)row.findViewById(R.id.functionButton4);
            
            image = row.findViewById(R.id.iconact_functions);
            if (image != null){
            	holder.iconact_functions = (ImageView)image;
            } else {
            	holder.iconact_functions = null;
            }
            
            row.setTag(holder);
        }
        else
        {
            holder = (ActuatorSensorHolder)row.getTag();
        }
        
        /*
         * Data 
         */
        
        ActValueHolder actvalue = data.get(position);
        
        /**
         * Seperator
         */
        if (cellStates[position] == null) {
	        int roomid = getRoomIdFromObject(actvalue.getActuator());
	        SeparatorHolder sh = null;
	        if (position == 0){
	        	// seperator
	        	sh = new SeparatorHolder(true, getRoomNameById(roomid));
	        } else {
	        	int prev_roomid = getRoomIdFromObject(data.get(position-1).getActuator());
	        	
	        	if (prev_roomid != roomid){
	        		sh = new SeparatorHolder(true, getRoomNameById(roomid));
	        	} else {
	        		sh = new SeparatorHolder(false, null);
	        	}
	        }
	        cellStates[position] = sh;
        }
        holder.seperator.setVisibility(cellStates[position].getNeedSeparator() ? View.VISIBLE : View.GONE);
        holder.seperator.setText(cellStates[position].getNeedSeparator() ? cellStates[position].getText() : "");
             
        
        // ACTUATOR Data
        if (actvalue.getActuator() instanceof Actuator) {
        
	        final Actuator actuator = (Actuator)actvalue.getActuator();
	        final double value = actvalue.getValue();
	        final int functionNummer = actvalue.getFunctionNummer();
	        final int makroNummer = actvalue.getMakroNummer();
	        
	        if (functionNummer != -1) {
		        holder.facet_actuator.setVisibility(View.GONE);
		        holder.facet_actuator_functions.setVisibility(View.VISIBLE);
	        } else {
	        	holder.facet_actuator.setVisibility(View.VISIBLE);
		        holder.facet_actuator_functions.setVisibility(View.GONE);
	        }	        
	        
	        holder.imgIconAct.setImageResource(Utilities.getImageForActuatorType(actuator));
	        holder.iconact_functions.setImageResource(Utilities.getImageForActuatorType(actuator));
	        
	        holder.facet_sensor.setVisibility(View.GONE);
	        holder.facet_camera.setVisibility(View.GONE);
	        
	        if (actuator.getType().equals("temperature")){
	        	
	        	// spinner
	        	holder.button.setVisibility(View.GONE);
	        	holder.switchButton.setVisibility(View.GONE);
	        	holder.seekbar.setVisibility(View.GONE);
	        	holder.seekbarText.setVisibility(View.GONE);
	        	holder.spinner.setVisibility(View.VISIBLE);
	        	holder.shutterDown.setVisibility(View.GONE);
	        	holder.shutterUp.setVisibility(View.GONE);
	        	
	        	
	        	holder.newValueText.setVisibility(View.GONE);
	        		        	
	        	
	        	ArrayAdapter<String> spinnerAdapter = Utilities.getTemperatureSpinnerAdapter(context);
	        	holder.spinner.setAdapter(spinnerAdapter);
	
	        	int pos=spinnerAdapter.getPosition(String.valueOf((double)actuator.getValue() + " °C"));
	            holder.spinner.setSelection(pos);
	            
	            holder.spinner.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// Wirklich neuer Wert selektiert und nicht nur Reload der Liste
						spinnerClicked = true;
						return false;
					}
					
				});
					
				
	            
	        	holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
						if (spinnerClicked) {
							// Der gesetzte Wert wird geholt
							String selected_val = (String) parent.getItemAtPosition(pos);

							// remove °C from end
							selected_val = Utilities.getValueFromTemperature(selected_val);
							
							double val = Double.parseDouble(selected_val);
							
							(new SetEditActuator(actuator, val, -1, makroNummer, view, null, null, null, null)).execute(context);
							Log.i(Utilities.TAG, context.getString(R.string.send_new_temp) +" "+val);
							spinnerClicked = false;
							
							TextView newVal = (TextView)((View)parent.getParent()).findViewById(R.id.newValueText);
							newVal.setVisibility(View.VISIBLE);
			        		newVal.setTextColor(Color.rgb(0, 166, 209));
			        		newVal.setText(context.getString(R.string.new_s)+" : "+val+" °C");
						}
					}
	
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
	        	
	        } else if (actuator.isDimmable()){
	        	
	        	// dimmer
	        	holder.button.setVisibility(View.GONE);
	        	holder.switchButton.setVisibility(View.GONE);
	        	holder.seekbar.setVisibility(View.VISIBLE);
	        	holder.seekbarText.setVisibility(View.VISIBLE);
	        	holder.spinner.setVisibility(View.GONE);
	        	holder.newValueText.setVisibility(View.GONE);
	        	holder.shutterDown.setVisibility(View.GONE);
	        	holder.shutterUp.setVisibility(View.GONE);
	        	
	        	holder.seekbarText.setText((int) value+" %");
	        	holder.seekbarText.setId(actuator.getNumber() + ID_BUFFER);
	        	holder.seekbar.setProgress((int) value);
	        	holder.seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	        		
	        		
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
											
						TextView tv = (TextView) ((Activity)context).findViewById(actuator.getNumber() + ID_BUFFER);
						if (tv != null) tv.setText(seekBar.getProgress() + " %");
					}
	
					public void onStartTrackingTouch(SeekBar seekBar) {
	
					}
	
					
					public void onStopTrackingTouch(SeekBar seekBar) {
						(new SetEditActuator(actuator, seekBar.getProgress(), -1, makroNummer, seekBar, null, null, null, null)).execute(context);
						
					}
				});
	        	
	        } else {
	        	
	        	// button
	        	holder.button.setVisibility(View.GONE);
	        	holder.switchButton.setVisibility(View.VISIBLE);
	        	holder.seekbar.setVisibility(View.GONE);
	        	holder.seekbarText.setVisibility(View.GONE);
	        	holder.spinner.setVisibility(View.GONE);
	        	holder.newValueText.setVisibility(View.GONE);
	        	holder.shutterDown.setVisibility(View.GONE);
	        	holder.shutterUp.setVisibility(View.GONE);
	        	
	        	// switeched from getValue to getNewvalue for shutter problem
	        	if (actuator.getType().equals("shutter")){
	        		holder.switchButton.setChecked(value==100.0 ? true : false);
	        	} else {
	        		holder.switchButton.setChecked(value==100.0 ? true : false);
	        	}
	        	
	        	holder.switchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	        	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	        	    	int functionNummer = -1;
	        	    	
	        	    	if (isChecked) {
							(new SetEditActuator(actuator, 100.0, functionNummer, makroNummer, buttonView, null, null, null, null)).execute(context);
						} else {
							(new SetEditActuator(actuator, 0.0, functionNummer, makroNummer, buttonView, null, null, null, null)).execute(context);
						}
	        	    }
	        	});
	        }
	        
	        /**
	         * Functions
	         */
	        List<Function> functions = actuator.getMyFunction();
	        
	        handleFunctionButton(holder.button_function1, functions.get(0), 1, value, 
	        		holder.button_function1, holder.button_function2, holder.button_function3, holder.button_function4, actuator, makroNummer, functionNummer);
	        handleFunctionButton(holder.button_function2, functions.get(1), 2, value, 
	        		holder.button_function1, holder.button_function2, holder.button_function3, holder.button_function4, actuator, makroNummer, functionNummer);
	        handleFunctionButton(holder.button_function3, functions.get(2), 3, value, 
	        		holder.button_function1, holder.button_function2, holder.button_function3, holder.button_function4, actuator, makroNummer, functionNummer);
	        handleFunctionButton(holder.button_function4, functions.get(3), 4, value, 
	        		holder.button_function1, holder.button_function2, holder.button_function3, holder.button_function4, actuator, makroNummer, functionNummer);
	       
	        
	        /**
	         * Text
	         */        
	        
	        holder.textAct.setText(actuator.getAppname());
	        holder.text_functions.setText(actuator.getAppname());
	        
        }
        
        return row;
    }
    
    private void handleFunctionButton(Button fctButton, Function function, int num, double value,
    		Button functionButton1, Button functionButton2, Button functionButton3, Button functionButton4, Actuator act, 
    		int makroNummer, int functionNummer) {
    	if (function.getDsc() == null || function.getDsc().equals("")){
        	fctButton.setVisibility(View.GONE);
        } else {
        	fctButton.setVisibility(View.VISIBLE);
        	fctButton.setText(function.getDsc());
        	final double val = value;
        	final Integer number = num;
        	final int makroNr = makroNummer;
        	final Actuator actuator = act;
        	final Button fncb1 = functionButton1;
        	final Button fncb2 = functionButton2;
        	final Button fncb3 = functionButton3;
        	final Button fncb4 = functionButton4;
        	fctButton.setOnClickListener(new OnClickListener() {
	        	@Override
	        	public void onClick(View v) {
	        		
	        		// add to script
	        		(new SetEditActuator(actuator, val, number, makroNr, null, fncb1, fncb2, fncb3, fncb4)).execute(context);
	        	}
	        });
	        
	        if (functionNummer == num) {
	        	
	        	fctButton.setTextColor(context.getResources().getColor(R.color.infoscout_blue));
	        	
	        } else {
	        	fctButton.setTextColor(context.getResources().getColor(R.color.grey));
	        }
        }
    }
    
    static class ActuatorSensorHolder
    {
    	TextView seperator;
    	
    	// Actuator
    	RelativeLayout facet_actuator;
    	
    	ImageView imgIconAct;
    	Switch switchButton;
    	Button button;
        Spinner spinner;
        TextView newValueText;
        SeekBar seekbar;
        TextView seekbarText;
        TextView textAct;
        ImageButton shutterDown;
        ImageButton shutterUp;
        
        // Acutator Functions
        RelativeLayout facet_actuator_functions;
        
        ImageView iconact_functions;
        TextView text_functions;
        Button button_function1;
        Button button_function2;
        Button button_function3;
        Button button_function4;
        
        // Sensor
        RelativeLayout facet_sensor;
        
        // Camera
        RelativeLayout facet_camera;
    }
    
}
