package de.infoscout.betterhome.view.adapter;

import java.util.List;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.model.device.db.RoomDB;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RoomAdapter extends ArrayAdapter<RoomDB>{
	Context context; 
    int layoutResourceId;    
    List<RoomDB> data = null;
    
    public RoomAdapter(Context context, int layoutResourceId, List<RoomDB> data) {
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
        RoomHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new RoomHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.text1);
            
            row.setTag(holder);
        }
        else
        {
            holder = (RoomHolder)row.getTag();
        }
        
        final RoomDB room = (RoomDB)data.get(position);
        
        holder.txtTitle.setText(room.getName());
        
        
        return row;
    }
    
    static class RoomHolder
    {
        TextView txtTitle;
    }
}
