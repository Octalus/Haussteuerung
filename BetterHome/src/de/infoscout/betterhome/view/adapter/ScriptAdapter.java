package de.infoscout.betterhome.view.adapter;

import java.util.List;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.model.device.Script;
import de.infoscout.betterhome.model.device.components.XS_Object;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScriptAdapter extends ArrayAdapter<XS_Object>{
	Context context; 
    int layoutResourceId;    
    List<XS_Object> data = null;
    
    public ScriptAdapter(Context context, int layoutResourceId, List<XS_Object> data) {
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
        ScriptHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ScriptHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.text1);
            
            row.setTag(holder);
        }
        else
        {
            holder = (ScriptHolder)row.getTag();
        }
        
        final Script script = (Script)data.get(position);
        
        holder.txtTitle.setText(script.getAppname());
        
        
        return row;
    }
    
    static class ScriptHolder
    {
        TextView txtTitle;
    }
}
