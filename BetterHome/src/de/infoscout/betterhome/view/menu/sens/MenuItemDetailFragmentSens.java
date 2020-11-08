package de.infoscout.betterhome.view.menu.sens;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.remote.Http;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.model.device.db.SensorDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.adapter.ActuatorSensorCameraAdapter;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.graph.MenuItemDetailActivityGraph;
import de.infoscout.betterhome.view.menu.graph.MenuItemDetailFragmentGraph;
import de.infoscout.betterhome.view.menu.rule.create.MenuItemDetailFragmentRuleCreate;

public class MenuItemDetailFragmentSens extends ListFragment implements OnRefreshListener {
		
	// Das Xsone Objekt f�r diese Aktivity
	private Xsone myXsone;
	// Liste der auszugebenden Strings
	private List<XS_Object> sens_list;
	private FragmentActivity activity;
	private PullToRefreshLayout mPullToRefreshLayout;
	private DatabaseStorage db;
	private boolean tablet = false;
	
	private Handler myHandler = null;
	private boolean go = true;

	public MenuItemDetailFragmentSens() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		activity = this.getActivity();
		
		if (((Activity)activity).findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		
		db = new DatabaseStorage(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_menuitem_detail_sens, container, false); 
		
		return view;
	}
	
	@Override
	public void onDestroyView() {
		go = false;
		super.onDestroyView();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	
		super.onViewCreated(view, savedInstanceState);
		
		if (!tablet) {
			LayoutAnimationController controller 
			   = AnimationUtils.loadLayoutAnimation(
			     this.getActivity(), R.anim.list_layout_controller);
			  getListView().setLayoutAnimation(controller);
		}
		
		  ViewGroup viewGroup = (ViewGroup) view;
		  
          // As we're using a ListFragment we create a PullToRefreshLayout manually
          mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

          // We can now setup the PullToRefreshLayout
          ActionBarPullToRefresh.from(getActivity())
                  // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
                  .insertLayoutInto(viewGroup)
                  // Here we mark just the ListView and it's Empty View as pullable
                  .theseChildrenArePullable(android.R.id.list, android.R.id.empty)
                  .listener(this)
                  .setup(mPullToRefreshLayout);
	}
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		getListView().setOnItemClickListener( 
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
						Object o = getListAdapter().getItem(position);
						if (o instanceof XS_Object && myXsone.getFeatures().contains("D")) {
							// Graph anzeigen
							if (tablet) {
								// commit Add fragment
								MenuItemDetailFragmentGraph fragment = new MenuItemDetailFragmentGraph();
								
								Bundle args= new Bundle();
						        args.putInt("sensorNumber", ((XS_Object) o).getNumber());
						        fragment.setArguments(args);
								
						        activity.getSupportFragmentManager().beginTransaction()
										.replace(R.id.menuitem_detail_container, fragment)
										.addToBackStack(null)
										.commit();
							} else {
								Intent intent = new Intent(activity, MenuItemDetailActivityGraph.class);
								intent.putExtra("sensorNumber", ((XS_Object) o).getNumber());
								startActivity(intent);
							}
						}
					}
				});
		getListView().setOnItemLongClickListener(
				new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View v, int position, long id) {
						
						AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
		        		
		        		// Get the layout inflater
		        	    LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
		        	    View view = inflater.inflate(R.layout.dialog_sens_edit, null);
		        	    builder.setView(view);
		        	    
		        	    final Sensor sens = (Sensor)getListAdapter().getItem(position);
							
		        	    final EditText nameView = (EditText)view.findViewById(R.id.dialog_name_text);
		        	    final TextView listtext = (TextView)v.findViewById(R.id.text1); 
		        	    nameView.setText(listtext.getText());
		        	    			        	
		        	    builder.setTitle(R.string.dialog_title_sens);
			        	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			        	           public void onClick(DialogInterface dialog, int id) {
			        	               // User clicked OK button
			        	        	   String newName = nameView.getText().toString();
			        	        	   
			        	        	   Sensor testSens = myXsone.getActiveSensor(newName);
			        	        	   XS_Object testObject = myXsone.getActiveObject(newName);
			        	        	   
			        	        	   if (testSens == null && testObject == null){
			        	        	   
				   							(new SetSensorDB(sens)).execute(newName);
				        	        	   
				   							listtext.setText(newName);
			        	        	   } else {
			        	        		   Toast.makeText(activity, activity.getString(R.string.name_invalid), Toast.LENGTH_LONG).show();
			        	        	   }
			        	           }
			        	       });
			        	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			        	           public void onClick(DialogInterface dialog, int id) {
			        	               // User cancelled the dialog
			        	           }
			        	       });
		        	    
		        	    // Get the AlertDialog from create()
		        		AlertDialog dialog = builder.create();
		        		
		        		dialog.show();
						
						return true;
					}
				});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		go = true;
		
		myHandler = new Handler();
		myHandler.postDelayed(myRunnable, 0);
	}
	
	@Override
    public void onRefreshStarted(View view) {
		new GetDataTask().execute();
    }
	
	

	// ---------------- XsOne / DB interactions -----------------------
	
	private final Runnable myRunnable = new Runnable()
	{
	    public void run()

	    {
	    	if (go) {
		    	//Toast.makeText(activity,"Refresh",Toast.LENGTH_SHORT).show();
		    	(new GetDataTask()).execute();
		        myHandler.postDelayed(myRunnable, RuntimeStorage.getRefreshSeconds() * 1000);            
	    	}
	    }

	};
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		private List<XS_Object> tmp;
		private List<RoomDB> rooms;
		private ActuatorSensorCameraAdapter adapter;
		private List<Object> ordered_sens_list;
		
		@Override
		protected String[] doInBackground(Void... params) {
			
			// Liste neu holen
			tmp = Http.getInstance().get_list_sensors(activity);
			
			if (tmp == null || myXsone == null) {
				XsError.printError(getActivity());
			} else {
				//sens_list.clear();
				
				myXsone.add_RemObj(tmp);

				// Die Liste der Sensoren holen
				sens_list = myXsone.getMyActiveSensorList();
				
				// load DB data for sensors
				for (int i=0; i< sens_list.size(); i++) {
					int number = sens_list.get(i).getNumber();
					((Sensor)sens_list.get(i)).setSensorDB(db.getSensor(number));
				}
				
				rooms = db.getAllRooms();
				
				db.closeDB();
				
			}
			
			ordered_sens_list = new ArrayList<Object>();
			Sensor sens;
			RoomDB room;
			if (rooms != null){
				for (int r=0; r<rooms.size(); r++){
					room = rooms.get(r);
					for (int s=0; s<sens_list.size(); s++){
						sens = (Sensor)sens_list.get(s);
						if (sens.getSensorDB() != null && sens.getSensorDB().getRoomId() == room.getId()){
							ordered_sens_list.add(sens);
						}
					}
				}
			}
			
			if (sens_list != null) {
				// sensors without room
				for (int s=0; s<sens_list.size(); s++){
					sens = (Sensor)sens_list.get(s);
					if ( (sens.getSensorDB() != null && sens.getSensorDB().getRoomId() == 0) || (sens.getSensorDB() == null) ){
						ordered_sens_list.add(sens);
					}
				}
			}
			
			if (getActivity() != null && ordered_sens_list.size() > 0){
				adapter = new ActuatorSensorCameraAdapter(getActivity(), R.layout.list_item_sens_act_cam, ordered_sens_list);
			}
				
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			if (getActivity() != null && adapter != null){
				setListAdapter(adapter);
			}
			
			
			mPullToRefreshLayout.setRefreshComplete();
			
			return;

		}
	}
	
	private class SetSensorDB extends AsyncTask<String, Void, String[]> {
    	Sensor sensor;
    	
    	public SetSensorDB(Sensor sens){
    		this.sensor=sens;
    	}
		
		@Override
		protected String[] doInBackground(String... params) {
			String newName = params[0];
			
			SensorDB sensorDB = sensor.getSensorDB();
     	   if (sensorDB != null){
     		   sensorDB.setName(newName);
            	db.updateSensor(sensorDB);
            	sensor.setSensorDB(sensorDB);
            } else {
            	sensorDB = new SensorDB();
            	sensorDB.setName(newName);
            	sensorDB.setNumber(sensor.getNumber());
            	db.createSensor(sensorDB);
            	sensor.setSensorDB(sensorDB);
            	
            }
     	   db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
				
			return;

		}
	}
	
}
