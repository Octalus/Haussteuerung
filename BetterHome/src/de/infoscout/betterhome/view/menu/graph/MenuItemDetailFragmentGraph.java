package de.infoscout.betterhome.view.menu.graph;

import java.util.Calendar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.AorS_Object;

public class MenuItemDetailFragmentGraph extends Fragment {
		
	// Das Xsone Objekt f�r diese Aktivity
	private Xsone myXsone;
	private static FragmentActivity activity;
	@SuppressWarnings("unused")
	private boolean tablet = false;
	
	private int initialPosition = 0;
	private ProgressDialog dialog;
	private LinearLayout graphView = null;
	private Spinner selectRange = null;
	private ArrayAdapter<String> spinnerAdapter;	
		
	private int sensorNumber;
	private int actuatorNumber;
	private AorS_Object actSens = null;
	
	
	private Handler myHandler = null;
	private boolean go = true;
	
	private final static int MAX_LABELS_X = 7;
	private final static int MAX_LABELS_Y = 18;
	private final static boolean CUT_DOWN_POINTS = false;
	private final static boolean SHORT_LABELS = false;
	private final static int MAX_POINTS = 100;
	private final static float SIZE_POINTS = 4f;
	private final static boolean SCALABLE = true;
		
	public MenuItemDetailFragmentGraph() {
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		activity = this.getActivity();
		
		Bundle args = getArguments();
		sensorNumber = args.getInt("sensorNumber");
		actuatorNumber = args.getInt("actuatorNumber");
		
		String[] mylist = {getString(R.string.day), getString(R.string.week), getString(R.string.month), getString(R.string.quarter)};
        spinnerAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, mylist);
		
		setHasOptionsMenu(true);
		
		if (activity.findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		myXsone = RuntimeStorage.getMyXsone();
		
        if (sensorNumber > 0) {
        	actSens = myXsone.getSensor(sensorNumber);
        } else if (actuatorNumber > 0) {
        	actSens = myXsone.getActuator(actuatorNumber);
        }
        
        
	}
		
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        
        inflater.inflate(R.menu.graphmenu, menu);
        
        MenuItem menuItem = menu.findItem(R.id.select_range);
        View view = menuItem.getActionView();
        
        if (view instanceof Spinner) {
        	selectRange = (Spinner)view;
                        
            selectRange.setAdapter(spinnerAdapter);
            
            selectRange.setOnItemSelectedListener(new OnItemSelectedListener() {
    			public void onItemSelected(AdapterView<?> arg0, View arg1,
    					int itemPosition, long arg3) {
    				            	
                	dialog = ProgressDialog.show(activity, "",
    						getString(R.string.fetch_data), true, false);
    				dialog.show();
                	
                	Calendar from = Calendar.getInstance();
                	switch (itemPosition){
                		case 0 :	// Tag
                					from.add(Calendar.DAY_OF_YEAR, -1);
                					break;
                		case 1 :	// Woche
                					from.add(Calendar.DAY_OF_YEAR, -7);
                					break;
                		case 2 :	// Monat
                					from.add(Calendar.DAY_OF_YEAR, -30);
                					break;
                		case 3 :	// Jahr
                					from.add(Calendar.DAY_OF_YEAR, -91);
                					break;
                	}
                	
                	
                	Calendar to = Calendar.getInstance();
                	
                	ReadDrawStatistics readDrawStatistics = new ReadDrawStatistics(actSens, from, to, (FragmentActivity)activity, graphView);
    				readDrawStatistics.setDialog(dialog);
    				readDrawStatistics.setNumHorLabel(MAX_LABELS_X);
    				readDrawStatistics.setNumVerLabel(MAX_LABELS_Y);
    				readDrawStatistics.setMaxPoints(MAX_POINTS);
    				readDrawStatistics.setSizePoints(SIZE_POINTS);
    				readDrawStatistics.setCutStats(CUT_DOWN_POINTS);
    				readDrawStatistics.setShortLabels(SHORT_LABELS);
    				readDrawStatistics.setScalable(SCALABLE);
    				readDrawStatistics.execute();
                	
    			}
    			
    			public void onNothingSelected(AdapterView<?> arg0) {
    			}
            });
            
            selectRange.setSelection(initialPosition);
        }
        
        super.onCreateOptionsMenu(menu, inflater);
    }
	
	
	@Override
	public void onDestroy() {
		go = false;
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		go = true;
		
		myHandler = new Handler();
		myHandler.postDelayed(myRunnable, 0);
		
		super.onResume();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_menuitem_detail_sens_graph, container, false); 
		
		graphView = (LinearLayout)view.findViewById(R.id.graph1);
		
		return view;
	}
	
	private final Runnable myRunnable = new Runnable()
	{
	    public void run()

	    {
	    	if (go) {
	        	
	        	Calendar from = Calendar.getInstance();
	        	
	        	int selectedIndex = 0;
	        	if (selectRange != null) {
	        		selectedIndex = selectRange.getSelectedItemPosition();
	        	} 
	        	
	        	switch (selectedIndex){
	        		case 0 :	// Tag
	        					from.add(Calendar.DAY_OF_YEAR, -1);
	        					break;
	        		case 1 :	// Woche
	        					from.add(Calendar.DAY_OF_YEAR, -7);
	        					break;
	        		case 2 :	// Monat
	        					from.add(Calendar.DAY_OF_YEAR, -30);
	        					break;
	        		case 3 :	// Jahr
	        					from.add(Calendar.DAY_OF_YEAR, -91);
	        					break;
	        	}
	        	
	        	Calendar to = Calendar.getInstance();
		    	
	        	ReadDrawStatistics readDrawStatistics = new ReadDrawStatistics(actSens, from, to, (FragmentActivity)activity, graphView);
				readDrawStatistics.setDialog(dialog);
				readDrawStatistics.setNumHorLabel(MAX_LABELS_X);
				readDrawStatistics.setNumVerLabel(MAX_LABELS_Y);
				readDrawStatistics.setMaxPoints(MAX_POINTS);
				readDrawStatistics.setSizePoints(SIZE_POINTS);
				readDrawStatistics.setCutStats(CUT_DOWN_POINTS);
				readDrawStatistics.setShortLabels(SHORT_LABELS);
				readDrawStatistics.setScalable(SCALABLE);
				readDrawStatistics.execute();
				
		        myHandler.postDelayed(myRunnable, RuntimeStorage.getRefreshSeconds() * 1000);       
	    	}
	    }
	};
}
