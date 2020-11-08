package de.infoscout.betterhome.view.adapter;

import java.util.List;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.model.device.db.PositionDB;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PositionAdapter extends ArrayAdapter<PositionDB>{
	Context context; 
    int layoutResourceId;    
    List<PositionDB> data = null;
    
    public PositionAdapter(Context context, int layoutResourceId, List<PositionDB> data) {
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
    public View getView(int pos, View convertView, ViewGroup parent) {
        View row = convertView;
        PositionHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new PositionHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.text1);
            
            row.setTag(holder);
        }
        else
        {
            holder = (PositionHolder)row.getTag();
        }
        
        final PositionDB position = (PositionDB)data.get(pos);
        
        holder.txtTitle.setText(position.getName());
        
        
        return row;
    }
    
    static class PositionHolder
    {
        TextView txtTitle;
    }
}
