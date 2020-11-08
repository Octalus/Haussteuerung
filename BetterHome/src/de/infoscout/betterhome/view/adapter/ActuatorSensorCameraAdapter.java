package de.infoscout.betterhome.view.adapter;

import java.util.Calendar;
import java.util.List;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.components.Function;
import de.infoscout.betterhome.model.device.db.CamDB;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.pojo.SeparatorHolder;
import de.infoscout.betterhome.view.utils.Utilities;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.StyleSpan;
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

public class ActuatorSensorCameraAdapter extends ArrayAdapter<Object>{
	Context context; 
    int layoutResourceId;    
    List<Object> data = null;
    boolean spinnerClicked = false;
    private DatabaseStorage db;
    List<RoomDB> rooms;
    
    
    SeparatorHolder[] cellStates;
    
    private final int ID_BUFFER = 500;
    
    public ActuatorSensorCameraAdapter(Context context, int layoutResourceId, List<Object> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        
        this.cellStates = new SeparatorHolder[data.size()];
        
        db = new DatabaseStorage(context);

        (new GetRooms()).execute();
    }
    
    private RoomDB getRoomById(int id){
    	if (rooms != null) {
	    	for (int i=0; i<rooms.size(); i++){
	    		if (rooms.get(i).getId() == id){
	    			return rooms.get(i);
	    		}
	    	}
    	} else {
    		(new GetRooms()).execute();
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
    	
    	if (object instanceof Sensor){
    		Sensor sensor = (Sensor)object;
    		return sensor.getSensorDB() != null ? (sensor.getSensorDB().getRoomId()) : 0;
    	}
    	
    	if (object instanceof CamDB){
    		CamDB cam = (CamDB)object;
    		return cam.getRoomId();
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
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ActuatorSensorHolder holder = null;
        
        final int posFinal = position;
        
        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ActuatorSensorHolder();
            
            holder.facet_actuator = (RelativeLayout)convertView.findViewById(R.id.facet_actuator);
            holder.facet_actuator_functions = (RelativeLayout)convertView.findViewById(R.id.facet_actuator_functions);
            holder.facet_sensor = (RelativeLayout)convertView.findViewById(R.id.facet_sensor);
            holder.facet_camera = (RelativeLayout)convertView.findViewById(R.id.facet_camera);
            
            holder.seperator = (TextView)convertView.findViewById(R.id.grouping);
            
            // ACTUATOR
            holder.switchButton = (Switch)convertView.findViewById(R.id.switch1);
            holder.seekbar = (SeekBar)convertView.findViewById(R.id.seekBar1);
            holder.button = (Button)convertView.findViewById(R.id.button1);
            holder.textAct = (TextView)convertView.findViewById(R.id.text);
            holder.spinner = (Spinner)convertView.findViewById(R.id.spinner1);
            holder.newValueText = (TextView)convertView.findViewById(R.id.newValueText);
            holder.seekbarText = (TextView)convertView.findViewById(R.id.seekBarText);
            holder.shutterDown = (ImageButton)convertView.findViewById(R.id.shutterDown);
            holder.shutterUp = (ImageButton)convertView.findViewById(R.id.shutterUp);
            
            Object image = convertView.findViewById(R.id.iconact);
            if (image != null){
            	holder.imgIconAct = (ImageView)image;
            } else {
            	holder.imgIconAct = null;
            }
            
            // ACTUATOR Functions
            holder.text_functions = (TextView)convertView.findViewById(R.id.text_functions);
            holder.button_function1 = (Button)convertView.findViewById(R.id.functionButton1);
            holder.button_function2 = (Button)convertView.findViewById(R.id.functionButton2);
            holder.button_function3 = (Button)convertView.findViewById(R.id.functionButton3);
            holder.button_function4 = (Button)convertView.findViewById(R.id.functionButton4);
            
            image = convertView.findViewById(R.id.iconact_functions);
            if (image != null){
            	holder.iconact_functions = (ImageView)image;
            } else {
            	holder.iconact_functions = null;
            }
            
            
            // SENSOR
            holder.imgIconSens = (ImageView)convertView.findViewById(R.id.iconsens);
            holder.txtSens = (TextView)convertView.findViewById(R.id.text1);
            holder.wert = (TextView)convertView.findViewById(R.id.wert);
            holder.datum = (TextView)convertView.findViewById(R.id.datum);
            holder.status = (TextView)convertView.findViewById(R.id.status);
            
            // Camera
            holder.txtCam = (TextView)convertView.findViewById(R.id.textCam);
            
            convertView.setTag(holder);
        }
        else
        {
            holder = (ActuatorSensorHolder)convertView.getTag();
        }
        
        /*
         * Data 
         */
        
        Object sens_act_cam = data.get(position);
        
        /**
         * Seperator
         */
        if (cellStates[position] == null) {
	        int roomid = getRoomIdFromObject(sens_act_cam);
	        SeparatorHolder sh = null;
	        if (position == 0){
	        	// seperator
	        	sh = new SeparatorHolder(true, getRoomNameById(roomid));
	        } else {
	        	int prev_roomid = getRoomIdFromObject(data.get(position-1));
	        	
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
        if (sens_act_cam instanceof Actuator) {
        
	        Actuator actuator = (Actuator)sens_act_cam;
	        
	        if (actuator.getActuatorDB() != null && actuator.getActuatorDB().isUseFunction()) {
		        holder.facet_actuator.setVisibility(View.GONE);
		        holder.facet_actuator_functions.setVisibility(View.VISIBLE);
	        } else {
	        	holder.facet_actuator.setVisibility(View.VISIBLE);
		        holder.facet_actuator_functions.setVisibility(View.GONE);
	        }
		        
	        holder.facet_sensor.setVisibility(View.GONE);
	        holder.facet_camera.setVisibility(View.GONE);
	        holder.imgIconAct.setImageResource(Utilities.getImageForActuatorType(actuator));
	        holder.iconact_functions.setImageResource(Utilities.getImageForActuatorType(actuator));
	        	        	        
	        if (actuator.getType().equals("temperature")){
	        		        	
	        	// spinner
	        	holder.button.setVisibility(View.GONE);
	        	holder.switchButton.setVisibility(View.GONE);
	        	holder.seekbar.setVisibility(View.GONE);
	        	holder.seekbarText.setVisibility(View.GONE);
	        	holder.spinner.setVisibility(View.VISIBLE);
	        	holder.shutterDown.setVisibility(View.GONE);
	        	holder.shutterUp.setVisibility(View.GONE);
	        	
	        	
	        	if (actuator.getValue() != actuator.getNewvalue()){
	        		holder.newValueText.setVisibility(View.VISIBLE);
	        		holder.newValueText.setTextColor(Color.rgb(0, 166, 209));
	        		holder.newValueText.setText(context.getString(R.string.new_s)+" : "+((int)actuator.getNewvalue())+" °C");
	        	} else {
	        		holder.newValueText.setVisibility(View.GONE);
	        	}
	        	
	        	ArrayAdapter<String> spinnerAdapter = Utilities.getTemperatureSpinnerAdapter(context);
	        	holder.spinner.setAdapter(Utilities.getTemperatureSpinnerAdapter(context));
	
	        	int pos=spinnerAdapter.getPosition(String.valueOf((double)actuator.getValue() + " °C"));
	            holder.spinner.setSelection(pos);
	            
	            holder.spinner.setOnTouchListener(new OnTouchListener() {
					
					@SuppressLint("ClickableViewAccessibility")
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
							
							Actuator actuator = (Actuator)data.get(posFinal);
							
							(new SetActuator(actuator, val, true, view, null, null)).execute();
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
	        	
	        	holder.seekbarText.setText((int) actuator.getValue()+" %");
	        	holder.seekbarText.setId(actuator.getNumber() + ID_BUFFER);
	        	holder.seekbar.setProgress((int) actuator.getValue());
	        	holder.seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
	        		
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						Actuator actuator = (Actuator)data.get(posFinal);
						TextView tv = (TextView) ((Activity)context).findViewById(actuator.getNumber() + ID_BUFFER);
						if (tv != null) tv.setText(seekBar.getProgress() + " %");
					}
	
					public void onStartTrackingTouch(SeekBar seekBar) {
	
					}
	
					public void onStopTrackingTouch(SeekBar seekBar) {
						Actuator actuator = (Actuator)data.get(posFinal);
						(new SetActuator(actuator, seekBar.getProgress(), true, seekBar, null, null)).execute();
					}
				});
	        	
	        } else if (actuator.isMakro()){
	        	
	        	// button
	        	holder.button.setVisibility(View.VISIBLE);
	        	holder.switchButton.setVisibility(View.GONE);
	        	holder.seekbar.setVisibility(View.GONE);
	        	holder.seekbarText.setVisibility(View.GONE);
	        	holder.spinner.setVisibility(View.GONE);
	        	holder.newValueText.setVisibility(View.GONE);
	        	holder.shutterDown.setVisibility(View.GONE);
	        	holder.shutterUp.setVisibility(View.GONE);
	        	
	        	holder.button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Actuator actuator = (Actuator)data.get(posFinal);
						(new SetActuator(actuator, 100.0, true, null, null, null)).execute();
						//(new SetMakro(actuator, true)).execute();
					}
				});
	        	
	        } else if (actuator.getType().equals("shutter")){
	        	
	        	// shutter 2 buttons up/down
	        	holder.button.setVisibility(View.GONE);
	        	holder.switchButton.setVisibility(View.GONE);
	        	holder.seekbar.setVisibility(View.GONE);
	        	holder.seekbarText.setVisibility(View.GONE);
	        	holder.spinner.setVisibility(View.GONE);
	        	holder.newValueText.setVisibility(View.GONE);
	        	holder.shutterDown.setVisibility(View.VISIBLE);
	        	holder.shutterUp.setVisibility(View.VISIBLE);
	        	
	        	// switched from getValue to getNewvalue for shutter problem
	        	holder.shutterDown.setImageResource(actuator.getNewvalue()==0.0 ? R.drawable.arrow_down_active : R.drawable.arrow_down_inactive);
	        	holder.shutterUp.setImageResource(actuator.getNewvalue()==100.0 ? R.drawable.arrow_up_active : R.drawable.arrow_up_inactive);
	        	
	        	final ImageButton sDown = holder.shutterDown;
	        	final ImageButton sUp = holder.shutterUp;
	        	
	        	holder.shutterDown.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Actuator actuator = (Actuator)data.get(posFinal);
						(new SetActuator(actuator, 0.0, true, null, sUp, sDown)).execute();
					}
				});
	        	
	        	holder.shutterUp.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Actuator actuator = (Actuator)data.get(posFinal);
						(new SetActuator(actuator, 100.0, true, null, sUp, sDown)).execute();
					}
				});
	        	
	        } else {
	        	
	        	// switch
	        	holder.button.setVisibility(View.GONE);
	        	holder.switchButton.setVisibility(View.VISIBLE);
	        	holder.seekbar.setVisibility(View.GONE);
	        	holder.seekbarText.setVisibility(View.GONE);
	        	holder.spinner.setVisibility(View.GONE);
	        	holder.newValueText.setVisibility(View.GONE);
	        	holder.shutterDown.setVisibility(View.GONE);
	        	holder.shutterUp.setVisibility(View.GONE);
	        	
	        	
	        	holder.switchButton.setChecked(actuator.getValue() > 0.0 ? true : false);
	        	
	        	holder.switchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	        	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	        	    	Actuator actuator = (Actuator)data.get(posFinal);
	        	    	if (isChecked) {
							(new SetActuator(actuator, 100.0, true, buttonView, null, null)).execute();
						} else {
							(new SetActuator(actuator, 0.0, true, buttonView, null, null)).execute();
						}
	        	    }
	        	});
	        }
	        
	        /**
	         * Functions
	         */
	        List<Function> functions = actuator.getMyFunction();
	        
	        handleFunctionButton(holder.button_function1, functions.get(0), 1, actuator, 
	        		holder.button_function1, holder.button_function2, holder.button_function3, holder.button_function4);
	        handleFunctionButton(holder.button_function2, functions.get(1), 2, actuator, 
	        		holder.button_function1, holder.button_function2, holder.button_function3, holder.button_function4);
	        handleFunctionButton(holder.button_function3, functions.get(2), 3, actuator, 
	        		holder.button_function1, holder.button_function2, holder.button_function3, holder.button_function4);
	        handleFunctionButton(holder.button_function4, functions.get(3), 4, actuator, 
	        		holder.button_function1, holder.button_function2, holder.button_function3, holder.button_function4);
	       
	        
	        /**
	         * Text
	         */        
	        
	        holder.textAct.setText(actuator.getAppname());
	        holder.text_functions.setText(actuator.getAppname());
        }
        
        // SENSOR Data
        if (sens_act_cam instanceof Sensor) {
        	final Sensor sensor = (Sensor)sens_act_cam;
        	
        	holder.facet_actuator.setVisibility(View.GONE);
        	holder.facet_actuator_functions.setVisibility(View.GONE);
            holder.facet_sensor.setVisibility(View.VISIBLE);
            holder.facet_camera.setVisibility(View.GONE);
            
            /**
             * Icon
             */
            String type = sensor.getType();
            int bildId = Utilities.getImageForSensorType(type, sensor.getValue());
            
            if (bildId != -1){
            	holder.imgIconSens.setImageResource(bildId);
            } else {
            	holder.imgIconSens.setVisibility(View.INVISIBLE);
            }
            
            /**
             * Name
             */
            holder.txtSens.setText(sensor.getAppname());
            
            /**
             * Wert
             */
            String unit = sensor.getUnit();
            double value = sensor.getValue();
            
            String wert = Utilities.getWertForSensor(unit, value, type, context);
            
        	if ((unit.equals("boolean")) && (value == 100.0) && (type.equals("smokedetector") || type.equals("heatdetector"))) {
        		// Bei Alarm ROT
        		holder.wert.setTextColor(Color.RED);
        	}
        	
        	SpannableString spanString = new SpannableString(wert);
			spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            	
            holder.wert.setText(spanString);
            
            /**
             * Datum
             */
            String datestamp = "";
            String recv = "";
            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(sensor.getUtime() * 1000);
            
            Calendar today = Calendar.getInstance();
            
            long diffMillis = Math.abs(today.getTimeInMillis() - time.getTimeInMillis());
            
            if (today.get(Calendar.YEAR) == time.get(Calendar.YEAR)
            		  && today.get(Calendar.DAY_OF_YEAR) == time.get(Calendar.DAY_OF_YEAR) && (diffMillis > 3600000)) {
            	
            	datestamp = context.getString(R.string.today);
            	String minute = time.get(Calendar.MINUTE) < 10 ? "0"+time.get(Calendar.MINUTE) : ""+time.get(Calendar.MINUTE);
        		recv = datestamp +" "+time.get(Calendar.HOUR_OF_DAY)+":"+minute+" "+context.getString(R.string.time_oclock);
        		
            } else if (today.get(Calendar.YEAR) == time.get(Calendar.YEAR)
          		  && today.get(Calendar.DAY_OF_YEAR) == time.get(Calendar.DAY_OF_YEAR) && (diffMillis <= 3600000) && (diffMillis >= 60000)) {
            	
            	// nur eine Stunde Differenz
            	recv = context.getResources().getString(R.string.before)+ " " + ((long)(diffMillis/(1000*60))) + " " + context.getResources().getString(R.string.minutes_long);
            
            } else if (today.get(Calendar.YEAR) == time.get(Calendar.YEAR)
            		  && today.get(Calendar.DAY_OF_YEAR) == time.get(Calendar.DAY_OF_YEAR) && (diffMillis < 60000)) {
              	
              	// nur unter einer Minute Differenz
              	recv = context.getResources().getString(R.string.now);
              		
            } else {
    	        int month = time.get(Calendar.MONTH);
    	        switch (month){
    		        case Calendar.JANUARY : datestamp=context.getString(R.string.jan); break;
    	        	case Calendar.FEBRUARY : datestamp=context.getString(R.string.feb); break;
    	        	case Calendar.MARCH : datestamp=context.getString(R.string.mar); break;
    	        	case Calendar.APRIL : datestamp=context.getString(R.string.apr); break;
    	        	case Calendar.MAY : datestamp=context.getString(R.string.may); break;
    	        	case Calendar.JUNE : datestamp=context.getString(R.string.jun); break;
    	        	case Calendar.JULY : datestamp=context.getString(R.string.jul); break;
    	        	case Calendar.AUGUST : datestamp=context.getString(R.string.aug); break;
    	        	case Calendar.SEPTEMBER : datestamp=context.getString(R.string.sep); break;
    	        	case Calendar.OCTOBER : datestamp=context.getString(R.string.oct); break;
    	        	case Calendar.NOVEMBER : datestamp=context.getString(R.string.nov); break;
    	        	case Calendar.DECEMBER : datestamp=context.getString(R.string.dec); break;
    	        	default : datestamp=context.getString(R.string.jan); break;
    	        }
    	        
    	        int weekday = time.get(Calendar.DAY_OF_WEEK);
    	        String weekdayStamp;
    	        switch (weekday) {
    	        	case Calendar.MONDAY : weekdayStamp=context.getString(R.string.mo); break;
    	        	case Calendar.TUESDAY : weekdayStamp=context.getString(R.string.tu); break;
    	        	case Calendar.WEDNESDAY : weekdayStamp=context.getString(R.string.we); break;
    	        	case Calendar.THURSDAY : weekdayStamp=context.getString(R.string.th); break;
    	        	case Calendar.FRIDAY : weekdayStamp=context.getString(R.string.fr); break;
    	        	case Calendar.SATURDAY : weekdayStamp=context.getString(R.string.sa); break;
    	        	case Calendar.SUNDAY : weekdayStamp=context.getString(R.string.so); break;
    	        	default : weekdayStamp=context.getString(R.string.mo); break;
    	        }
    	        
    	        datestamp = weekdayStamp+", "+time.get(Calendar.DAY_OF_MONTH)+". "+datestamp+" "+time.get(Calendar.YEAR);
    	        String minute = time.get(Calendar.MINUTE) < 10 ? "0"+time.get(Calendar.MINUTE) : ""+time.get(Calendar.MINUTE); 
        		recv = datestamp +" "+time.get(Calendar.HOUR_OF_DAY)+":"+minute+" "+context.getString(R.string.time_oclock);
            }
            
            holder.datum.setText(recv);
            
            /**
             * Status
             */
            String status = sensor.getStatus();
            if (!status.equals("unknown")){
            	holder.status.setText("Status = "+status);
            	if (status.equals("ok")){
            		holder.status.setTextColor(Color.GREEN);
            	} else {
            		holder.status.setTextColor(Color.RED);
            	}
            }
        }
        
        // CAMERA Data
        if (sens_act_cam instanceof CamDB) {
        	CamDB cam = (CamDB)sens_act_cam;
        	
        	holder.facet_actuator.setVisibility(View.GONE);
        	holder.facet_actuator_functions.setVisibility(View.GONE);
            holder.facet_sensor.setVisibility(View.GONE);
            holder.facet_camera.setVisibility(View.VISIBLE);
        	
        	holder.txtCam.setText(cam.getName());
        }
        
        return convertView;
    }
    
    private void handleFunctionButton(Button fctButton, Function function, int num, Actuator act,
    		Button functionButton1, Button functionButton2, Button functionButton3, Button functionButton4) {
    	if (function.getDsc() == null || function.getDsc().equals("")){
        	fctButton.setVisibility(View.GONE);
        } else {
        	fctButton.setVisibility(View.VISIBLE);
        	fctButton.setText(function.getDsc());
        	final Actuator actuator = act;
        	final Integer number = num;
        	final Button fncb1 = functionButton1;
        	final Button fncb2 = functionButton2;
        	final Button fncb3 = functionButton3;
        	final Button fncb4 = functionButton4;
        	fctButton.setOnClickListener(new OnClickListener() {
	        	@Override
	        	public void onClick(View v) {
	        		
	        		(new FunctionExecution(actuator, fncb1, fncb2, fncb3, fncb4)).execute(number);
	        	}
	        });
	        
	        if ((actuator.getNewvalue() > 0.0 && function.getType().equals("on"))  || 
	        	(actuator.getNewvalue() == 0.0 &&  function.getType().equals("off")) ||
	        	(function.getDsc().startsWith(((int)actuator.getNewvalue())+"") )) {
	        	
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
        
        ImageView imgIconSens;
        TextView txtSens;
        TextView wert;
        TextView datum;
        TextView status;
        
        // Camera
        RelativeLayout facet_camera;
        
        TextView txtCam;
    }
    
    private class SetActuator extends AsyncTask<Void, Void, Boolean> {
		private Actuator actuator;
		private double new_value;
		private double old_value;
		private boolean remote;
		private Switch switchView = null;
		private SeekBar seekbarView = null;
		private Spinner spinnerView = null;
		
		private ImageButton sUp = null;
		private ImageButton sDown = null;
    	
    	public SetActuator(Actuator act, double val, boolean rem, View view, ImageButton sUp, ImageButton sDown){
			this.actuator=act;
			this.new_value=val;
			this.remote=rem;
			if (view != null){
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
			this.sUp = sUp;
			this.sDown = sDown;
		}
		
		
		@Override
		protected Boolean doInBackground(Void... params) {
			old_value = actuator.getValue();
			boolean setted = actuator.setValue(new_value, remote);
			
			return setted;
		}

		@Override
		protected void onPostExecute(Boolean setted) {
			super.onPostExecute(setted);
			
			if (!setted){
				actuator.setValue(old_value, false);
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
			
			if (sUp != null && sDown != null) {
				double val = actuator.getValue();
				if (val == 100.0) {
					sUp.setImageResource(R.drawable.arrow_up_active);
					sDown.setImageResource(R.drawable.arrow_down_inactive);
				} else {
					sUp.setImageResource(R.drawable.arrow_up_inactive);
					sDown.setImageResource(R.drawable.arrow_down_active);
				}
			}
			
			return;

		}
	}
    
    private class GetRooms extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			rooms = db.getAllRooms();
			db.closeDB();
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean setted) {
			super.onPostExecute(setted);
			
			return;

		}
	}
    
    private class FunctionExecution extends AsyncTask<Integer, Void, Boolean> {
		private Actuator actuator = null;
		private int number = -1;
		private Button button_function1;
		private Button button_function2;
		private Button button_function3;
		private Button button_function4;
		
    	
    	public FunctionExecution(Actuator act, Button button_function1, Button button_function2, Button button_function3, Button button_function4) {
    		actuator = act;
    		this.button_function1 = button_function1;
    		this.button_function2 = button_function2;
    		this.button_function3 = button_function3;
    		this.button_function4 = button_function4;
    	}
    	
		@Override
		protected Boolean doInBackground(Integer... params) {
			number = params[0];
			actuator.doFunction(number);
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean setted) {
			super.onPostExecute(setted);
			
			switch (number) {
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
			
			return;

		}
	}
    
}
