package de.infoscout.betterhome.view.adapter;

import java.util.Calendar;
import java.util.List;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.model.device.Timer;
import de.infoscout.betterhome.model.device.components.XS_Object;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TimerAdapter extends ArrayAdapter<XS_Object>{
	Context context; 
    int layoutResourceId;    
    List<XS_Object> data = null;
    
    public TimerAdapter(Context context, int layoutResourceId, List<XS_Object> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
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
        View row = convertView;
        TimerHolder holder = null;
        
        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new TimerHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.text1);
            holder.nextTime = (TextView)row.findViewById(R.id.nextTime);
            Object image = row.findViewById(R.id.icontim);
            if (image != null){
            	holder.imgIcon = (ImageView)image;
            } else {
            	holder.imgIcon = null;
            }
            
            row.setTag(holder);
        } else {
            holder = (TimerHolder)row.getTag();
        }
        
        Timer timer = (Timer)data.get(position);
        
        holder.imgIcon.setImageResource(R.drawable.clock);
        
        holder.txtTitle.setText(timer.getAppname());
        
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(timer.getNext().getTime());
        Calendar today = Calendar.getInstance();
        
        String nextTimer = "";
        
        if (time.get(Calendar.YEAR) < 2106) {
        
	        String datestamp;
	        if (today.get(Calendar.YEAR) == time.get(Calendar.YEAR)
	      		  && today.get(Calendar.DAY_OF_YEAR) == time.get(Calendar.DAY_OF_YEAR)) {
	        	datestamp = context.getString(R.string.today);
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
		      }
	        
	        int hour = time.get(Calendar.HOUR_OF_DAY);
	                
	        String minute = time.get(Calendar.MINUTE) < 10 ? "0"+time.get(Calendar.MINUTE) : ""+time.get(Calendar.MINUTE); 
	        String hourS = hour < 10 ? "0"+hour : ""+hour;
			
	        nextTimer = datestamp +" "+hourS+":"+minute+" "+context.getString(R.string.time_oclock);
        } else {
        	nextTimer = context.getString(R.string.never);
        }
        holder.nextTime.setText(nextTimer);
        
        int color = context.getResources().getColor(R.color.infoscout_blue);
        holder.nextTime.setTextColor(color);
        
        return row;
    }
    
    static class TimerHolder
    {
    	ImageView imgIcon;
        TextView txtTitle;
        TextView nextTime;
    }
}
