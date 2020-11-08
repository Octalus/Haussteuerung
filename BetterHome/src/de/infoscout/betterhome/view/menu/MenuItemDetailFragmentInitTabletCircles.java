package de.infoscout.betterhome.view.menu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.remote.Http;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.adapter.DashboardAdapter;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.graph.MenuItemDetailActivityGraph;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityAct} on handsets.
 */
public class MenuItemDetailFragmentInitTabletCircles extends Fragment {
	private GridView gridView;
	private Activity activity;
	
	private Xsone myXsone;
	private DatabaseStorage db;
	
	private Handler myHandler = null;
	private boolean go = true;
	
	public MenuItemDetailFragmentInitTabletCircles() {
	}
	
	/**
	 * Die OnCreate Funtion stellt den Einstiegspunkt dieser Activity dar. Alle
	 * Aktuatoren werden nochmals einzeln aus der XS1 ausgelesen (Zustand etc)
	 * um die einzelnen ToggleButtons und die Beschriftung entsprechend setzen
	 * zu k�nnen.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		activity = this.getActivity();
		myXsone = RuntimeStorage.getMyXsone();
		db = new DatabaseStorage(this.getActivity());
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroyView() {
		go = false;
		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_menuitem_initial_tab_circles, container, false); 
						
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		
		gridView = (GridView) activity.findViewById(R.id.gridView1);
				
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
				int position, long id) {
				
				Object item = ((DashboardAdapter)((GridView)parent).getAdapter()).getItem(position);
				
				if (item instanceof Sensor && myXsone.getFeatures().contains("D")) {
					// open graph
					Intent intent = new Intent(activity, MenuItemDetailActivityGraph.class);
					intent.putExtra("sensorNumber", ((XS_Object) item).getNumber());
					startActivity(intent);
				}
			
			}
		});
		
		new GetDataTask().execute();
	}
	
	@Override
	public void onResume() {
		go = true;
		
		myHandler = new Handler();
		myHandler.postDelayed(myRunnable, 0);
		
		super.onResume();
	}
	
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
		private List<XS_Object> sens_list;
		private List<XS_Object> act_list;
		
		private List<RoomDB> rooms;
		private List<Object> ordered_sens_list;
		
		private void readSensorsRemote() {
			// Liste neu holen
			tmp = Http.getInstance().get_list_sensors(activity);
			
			if (tmp == null) {
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
				
			}
		}
		
		private void readActuatorsRemote() {
			// Liste neu holen
			tmp = Http.getInstance().get_list_actuators();
			
			if (tmp == null) {
				XsError.printError(getActivity());
			} else {
				myXsone.add_RemObj(tmp);

				// Die Liste der Aktuatoren holen
				act_list = myXsone.getMyActiveActuatorList(true, null);
				
				// load DB data for actuators
				int number;
				for (int i=0; i< act_list.size(); i++) {
					number = act_list.get(i).getNumber();
					((Actuator)act_list.get(i)).setActuatorDB(db.getActuator(number));
				}
			}
		}
		
		@Override
		protected String[] doInBackground(Void... params) {
			// read sensors remote
			readSensorsRemote();
			
			// read acutators remote
			//readActuatorsRemote();
			
			// Die Liste der Sensoren holen
			sens_list = myXsone.getMyActiveSensorList();
			
			rooms = db.getAllRooms();
			
			ordered_sens_list = new ArrayList<Object>();
			Sensor sens;
			RoomDB room;
			
			if (sens_list != null) {
				// sensors without room
				for (int s=0; s<sens_list.size(); s++){
					sens = (Sensor)sens_list.get(s);
					if ( (sens.getSensorDB() != null && sens.getSensorDB().getRoomId() == 0) || (sens.getSensorDB() == null) ){
						ordered_sens_list.add(sens);
					}
				}
			}
			
			if (rooms != null){
				for (int r=0; r<rooms.size(); r++){
					room = rooms.get(r);
					boolean room_added = false;
					for (int s=0; s<sens_list.size(); s++){
												
						sens = (Sensor)sens_list.get(s);
						if (sens.getSensorDB() != null && sens.getSensorDB().getRoomId() == room.getId()){
							if (!room_added) {
								// Raum an Anfang adden
								ordered_sens_list.add(room);
								room_added = true;
							}
							ordered_sens_list.add(sens);
						}
					}
				}
			}
			
			db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			DashboardAdapter dashboardAdapter = new DashboardAdapter(activity, ordered_sens_list);
				
			gridView.setAdapter(dashboardAdapter);
			
			return;

		}
	}
}
