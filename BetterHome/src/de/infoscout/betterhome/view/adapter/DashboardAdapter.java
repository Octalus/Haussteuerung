package de.infoscout.betterhome.view.adapter;

import java.util.List;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.utils.Translator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.view.utils.Utilities;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DashboardAdapter extends BaseAdapter{
	private Context context;
	private final List<Object> sens_list;
 
	public DashboardAdapter(Context context, List<Object> sens_list) {
		this.context = context;
		this.sens_list = sens_list;
	}
	
	@Override
    public int getViewTypeCount() {                 
        return getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
 
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView = new View(context);
 
		if (convertView == null) {
			
			Object item = sens_list.get(position);
			
			if (item instanceof Sensor) {
			
				Sensor sensor = (Sensor)item;
	 
				// get layout from dashboard_element.xml
				gridView = inflater.inflate(R.layout.dashboard_element, null);
				
				// get layout element
				LinearLayout layout = (LinearLayout) gridView.findViewById(R.id.dashboard_element_layout);
				layout.setBackgroundResource(R.drawable.dashboardbackground);
				
				// set image based on selected text
				ImageView imageView = (ImageView) gridView
						.findViewById(R.id.grid_item_image);
				
				// set value into textview
				TextView textViewValue = (TextView) gridView
						.findViewById(R.id.grid_item_value);
				String wert = "";
				if (sensor.getUnit().equals("boolean")){
					wert = sensor.getValue() == 0.0 ? "Zu" : "Auf";
				} else {
					wert = sensor.getValue()+" "+sensor.getUnit();
				}
				
				SpannableString spanString = new SpannableString(wert);
				spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
				
				textViewValue.setText(spanString);
				
				/**
	             * Icon
	             */
				
	            String type = sensor.getType();
	            double value = sensor.getValue();
	            int bildId = Utilities.getImageForSensorType(type, value);
	            
	            if (bildId != -1){
	            	imageView.setImageResource(bildId);
	            } else {
	            	imageView.setVisibility(View.INVISIBLE);
	            }
	            
			} else if (item instanceof RoomDB) {
				RoomDB room = (RoomDB)item;
				
				// get layout from dashboard_element.xml
				gridView = inflater.inflate(R.layout.dashboard_element, null);
				
				// get layout element
				LinearLayout layout = (LinearLayout) gridView.findViewById(R.id.dashboard_element_layout);
				//layout.setBackgroundResource(R.drawable.dashboardbackgroundroom);
	  
				// set image based on selected text
				ImageView imageView = (ImageView) gridView
						.findViewById(R.id.grid_item_image);
				
				// set value into textview
				TextView textViewValue = (TextView) gridView
						.findViewById(R.id.grid_item_value);
				String wert = room.getName();
								
				SpannableString spanString = new SpannableString(wert);
				spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
				
				textViewValue.setText(spanString);
				//textViewValue.setTextColor(context.getResources().getColor(R.color.white));
				
				/**
	             * Icon
	             */
				
	            imageView.setImageResource(R.drawable.rooms);
			}
 
		} else {
			gridView = (View) convertView;
		}
 
		return gridView;
	}
 
	@Override
	public int getCount() {
		return sens_list.size();
	}
 
	@Override
	public Object getItem(int position) {
		return sens_list.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
}
