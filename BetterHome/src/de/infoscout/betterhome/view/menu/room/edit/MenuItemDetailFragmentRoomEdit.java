package de.infoscout.betterhome.view.menu.room.edit;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.remote.Http;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Script;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.ActuatorDB;
import de.infoscout.betterhome.model.device.db.CamDB;
import de.infoscout.betterhome.model.device.db.SensorDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.adapter.ActuatorSensorCameraAdapter;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.act.edit.MenuItemDetailActivityEditAct;
import de.infoscout.betterhome.view.menu.act.edit.MenuItemDetailFragmentEditAct;
import de.infoscout.betterhome.view.menu.cam.edit.MenuItemDetailActivityCamShow;
import de.infoscout.betterhome.view.menu.cam.edit.MenuItemDetailFragmentCamShow;
import de.infoscout.betterhome.view.menu.graph.MenuItemDetailActivityGraph;
import de.infoscout.betterhome.view.menu.graph.MenuItemDetailFragmentGraph;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailActivityTimer;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityTimer} on handsets.
 */
public class MenuItemDetailFragmentRoomEdit extends ListFragment implements OnRefreshListener {
		
	// Das Xsone Objekt f�r diese Aktivity
	private PullToRefreshLayout mPullToRefreshLayout;
	private DatabaseStorage db;
	private Xsone myXsone;
	private int roomId;
	private FragmentActivity activity;
	
	private boolean tablet = false;
	
	List<CamDB> cam_list;
	
	private Handler myHandler = null;
	private boolean go = true;

	public MenuItemDetailFragmentRoomEdit() {
		
	}
	
	/**
	 * Die OnCreate Funtion stellt den Einstiegspunkt dieser Activity dar. Alle
	 * Aktuatoren werden nochmals einzeln aus der XS1 ausgelesen (Zustand etc)
	 * um die einzelnen ToggleButtons und die Beschriftung entsprechend setzen
	 * zu k�nnen.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		db = new DatabaseStorage(this.getActivity());
		
		activity = this.getActivity();
		
		if (((Activity)activity).findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		
		Bundle args = getArguments();
		roomId = args.getInt("roomId");
		myXsone = RuntimeStorage.getMyXsone();
		
		(new GetAllCams()).execute();

		setHasOptionsMenu(true);
	}
	
	@Override
	public void onDestroyView() {
		go = false;
		super.onDestroyView();
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.roomeditoptions, menu);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(activity, new Intent(activity,
					MenuItemListActivity.class));
			return true;
		case R.id.addelemt:
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    		
    	    String[] elements = {getString(R.string.actuator), getString(R.string.surveillance), getString(R.string.sensor)};
    	    
    	    if (!myXsone.getFeatures().contains("B")) {
    	    	// Sensoren weg
    	    	elements = new String[2];
    	    	elements[0] = getString(R.string.actuator);
    	    	elements[1] = getString(R.string.surveillance);
    	    }
    	    
    	    builder.setItems(elements, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	                	
                	switch (which) {
                		case 0 : 	// Ansteuerung
                					AlertDialog.Builder builderact = new AlertDialog.Builder(activity);
                		
                					final List<XS_Object> actuators = myXsone.getMyActiveActuatorList(true, null);
                					
                					String[] act_names = new String[actuators.size()];
                					
                					for (int i=0; i< actuators.size(); i++){
                						act_names[i] = ((Actuator)actuators.get(i)).getAppname();
                					}
                		
                	    			builderact.setItems(act_names, new DialogInterface.OnClickListener() {
                	    				public void onClick(DialogInterface dialog, int which) {
                	    					Actuator selected_act = (Actuator)actuators.get(which);
                	    					
                	    					(new AssignActToRoom()).execute(selected_act);
                	    				}
                	    			});
            	    	        	
                	        	    builderact.setTitle(R.string.actadd);
                	        	    
                	        	    // Get the AlertDialog from create()
                	        		AlertDialog dialogact = builderact.create();
                	        		
                	        		dialogact.show();
                	    			
                			
                					break;
                		case 2 : 	// Sensor
                					AlertDialog.Builder buildersens = new AlertDialog.Builder(activity);
                		
			    					final List<XS_Object> sensors = myXsone.getMyActiveSensorList();
			    					
			    					String[] sens_names = new String[sensors.size()];
			    					
			    					for (int i=0; i< sensors.size(); i++){
			    						sens_names[i] = ((Sensor)sensors.get(i)).getAppname();
			    					}
			    		
			    	    			buildersens.setItems(sens_names, new DialogInterface.OnClickListener() {
			    	    				public void onClick(DialogInterface dialog, int which) {
			    	    					Sensor selected_sens = (Sensor)sensors.get(which);
			    	    					
			    	    					(new AssignSensToRoom()).execute(selected_sens);
			    	    				}
			    	    			});
				    	        	
			    	        	    buildersens.setTitle(R.string.sensadd);
			    	        	    
			    	        	    // Get the AlertDialog from create()
			    	        		AlertDialog dialogsens = buildersens.create();
			    	        		
			    	        		dialogsens.show();
                			
                					break;
                		case 1 : 	String[] surv_names = new String[cam_list.size()];
		                			
		                			for (int i=0; i< cam_list.size(); i++){
		                				surv_names[i] = ((CamDB)cam_list.get(i)).getName();
		                			}
		                			
		                			// Ueberwachung
		                			AlertDialog.Builder buildersurv = new AlertDialog.Builder(activity);
		                        	
		                    	    buildersurv.setTitle(R.string.camadd);
		
		                    	    buildersurv.setItems(surv_names, new DialogInterface.OnClickListener() {
		                				public void onClick(DialogInterface dialog, int which) {
		                					CamDB selected_surv = (CamDB)cam_list.get(which);
		                					
		                					(new AssignCamToRoom()).execute(selected_surv);
		                				}
		                			});
		                    	    
		                    	    // Get the AlertDialog from create()
		                    		AlertDialog dialogsurv = buildersurv.create();
		                    		dialogsurv.show();
			    					
                					break;
                		default : break;
                	}
                	
                }});
    	    	        	
    	    builder.setTitle(R.string.elementadd);
    	    
    	    // Get the AlertDialog from create()
    		AlertDialog dialog = builder.create();
    		
    		dialog.show();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_menuitem_detail_room_edit, container, false); 
		
		return view;
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
		
	
	/**
	 * Die OnCreate Funtion stellt den Einstiegspunkt dieser Activity dar. Alle
	 * Aktuatoren werden nochmals einzeln aus der XS1 ausgelesen (Zustand etc)
	 * um die einzelnen ToggleButtons und die Beschriftung entsprechend setzen
	 * zu k�nnen.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		getListView().setOnItemClickListener( 
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
						Object o = getListAdapter().getItem(position);
						if (o instanceof Sensor && myXsone.getFeatures().contains("D")) {	
							
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
								// Graph anzeigen
								Intent intent = new Intent(activity, MenuItemDetailActivityGraph.class);
								intent.putExtra("sensorNumber", ((Sensor) o).getNumber());
								startActivity(intent);
							}
						}
						
						if (o instanceof CamDB) {
							
							if (tablet) {
								// commit Add fragment
								MenuItemDetailFragmentCamShow fragment = new MenuItemDetailFragmentCamShow();
								
								Bundle args= new Bundle();
						        args.putString("camName", ((CamDB) o).getName());
						        args.putString("camUrl", ((CamDB) o).getUrl());
						        args.putString("camUser", ((CamDB) o).getUsername());
						        args.putString("camPass", ((CamDB) o).getPassword());
						        args.putInt("stream", ((CamDB) o).getStream());
						        fragment.setArguments(args);
								
								activity.getSupportFragmentManager().beginTransaction()
										.replace(R.id.menuitem_detail_container, fragment)
										.addToBackStack(null)
										.commit();
							} else {
								// Cam edit Activity
								Intent intent = new Intent(getActivity(), MenuItemDetailActivityCamShow.class);
								intent.putExtra("camName", ((CamDB) o).getName());
								intent.putExtra("camUrl", ((CamDB) o).getUrl());
								intent.putExtra("camUser", ((CamDB) o).getUsername());
								intent.putExtra("camPass", ((CamDB) o).getPassword());
								intent.putExtra("stream", ((CamDB) o).getStream());
								startActivity(intent);
							}
						}
						
						if (o instanceof Actuator) {
							Actuator act = (Actuator)o;
							
							if (act.isMakro()) {
								String scriptName = act.getName().substring(2, act.getName().length());
								Script script = null;
								
								List<XS_Object> scriptList = myXsone.getMyActiveScriptList(true);
								for (int i=0; i<scriptList.size(); i++){
									String sName = scriptList.get(i).getName();
									if (scriptName.equals(sName.substring(2, sName.length()))) {
										script = (Script)scriptList.get(i);
										break;
									}
								}
								
								if (tablet){
									// commit Add fragment
									MenuItemDetailFragmentEditAct fragment = new MenuItemDetailFragmentEditAct();
									
									Bundle args= new Bundle();
							        args.putInt("makroNummer", script.getNumber());
							        fragment.setArguments(args);
									
									activity.getSupportFragmentManager().beginTransaction()
											.replace(R.id.menuitem_detail_container, fragment)
											.addToBackStack(null)
											.commit();
								} else {
									Intent intent = new Intent(activity, MenuItemDetailActivityEditAct.class);
									intent.putExtra("makroNummer", script.getNumber());
									startActivity(intent);
								}
							} else if (myXsone.getFeatures().contains("D")) {
								// kein Makro -> Graph!
								// Graph anzeigen
								if (tablet) {
									// commit Add fragment
									MenuItemDetailFragmentGraph fragment = new MenuItemDetailFragmentGraph();
									
									Bundle args= new Bundle();
							        args.putInt("actuatorNumber", act.getNumber());
							        fragment.setArguments(args);
									
							        activity.getSupportFragmentManager().beginTransaction()
											.replace(R.id.menuitem_detail_container, fragment)
											.addToBackStack(null)
											.commit();
								} else {
									Intent intent = new Intent(activity, MenuItemDetailActivityGraph.class);
									intent.putExtra("actuatorNumber", act.getNumber());
									startActivity(intent);
								}
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
		        				        	    
		        	    final Object list_item = getListAdapter().getItem(position);
								        	    			        	
		        	    builder.setTitle(R.string.editroomitem);
		        	    
		        	    String elem[] = new String [2];
		        	    elem[0] = getString(R.string.rename);
		        	    elem[1] = getString(R.string.release);
		        	    if (list_item instanceof Actuator){
		        	    	Actuator act = (Actuator)list_item;
		        	    	
		        	    	elem = new String[3];
		        	    	elem[0] = getString(R.string.rename);
			        	    elem[1] = getString(R.string.release);
			        	    if (act.getActuatorDB() == null || !act.getActuatorDB().isUseFunction()) {
			        	    	elem[2] = getString(R.string.functions);
							} else {
								elem[2] = getString(R.string.no_functions);
							}
		        	    }
		        	    
		        	    final String[] elements = elem;
		        	    builder.setItems(elements, new DialogInterface.OnClickListener() {
		                    @SuppressLint("InflateParams")
							public void onClick(DialogInterface dialog, int which) {
		                    	
		                    	Sensor sens = null;
	    		        	    Actuator act = null;
	    		        	    CamDB cam = null;
	    		        	    if (list_item instanceof Sensor){
	    		        	    	sens = (Sensor)list_item;
	    		        	    } else
	    		        	    if (list_item instanceof Actuator){
	    		        	    	act = (Actuator)list_item;
	    		        	    } else
	    		        	    if (list_item instanceof CamDB){
	    		        	    	cam = (CamDB)list_item;
	    		        	    }
	    		        	    
		                    	switch (which) {
		                    		case 0 : 	AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
		    		        		
					    		        		// Get the layout inflater
					    		        	    LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
					    		        	    View view = inflater.inflate(R.layout.dialog_room_item_edit, null);
					    		        	    builder.setView(view);
					    		        	    
					    		        	    
					    		        	    
					    		        	    final EditText nameView = (EditText)view.findViewById(R.id.dialog_name_text);
					    		        	    String name = act != null ? act.getAppname() : ( sens != null ? sens.getAppname() : cam.getName()); 
					    		        	    
					    		        	    nameView.setText(name);
					    		        	    
					    		        	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								        	           public void onClick(DialogInterface dialog, int id) {
								        	               // User clicked OK button
								        	        	   String newName = nameView.getText().toString();
								        	        	   
								        	        	   Actuator testAct = myXsone.getActiveActuator(newName);
								        	        	   Sensor testSens = myXsone.getActiveSensor(newName);
								        	        	   XS_Object testObject = myXsone.getActiveObject(newName);
								        	        	   
								        	        	   if (list_item instanceof Sensor){
								        	        		   if (testSens == null && testObject == null){
									    		        	    	SensorDB sensdb = ((Sensor)list_item).getSensorDB();
									    		        	    	sensdb.setName(newName);
									    		        	    	(new UpdateSensDB()).execute(sensdb);
								        	        		   } else {
								        	        			   Toast.makeText(activity, activity.getString(R.string.name_invalid), Toast.LENGTH_LONG).show();
								        	        		   }
								    		        	    	
								    		        	    } else
								    		        	    if (list_item instanceof Actuator){
								    		        	    	if (testAct == null && testObject == null){
									    		        	    	ActuatorDB actdb = ((Actuator)list_item).getActuatorDB();
									    		        	    	actdb.setName(newName);
									    		        	    	(new UpdateActDB()).execute(actdb);
								    		        	    	} else {
								    		        	    		Toast.makeText(activity, activity.getString(R.string.name_invalid), Toast.LENGTH_LONG).show();
								    		        	    	}
								    		        	    	
								    		        	    } else
								    		        	    if (list_item instanceof CamDB){
								    		        	    	CamDB camdb = (CamDB)list_item;
								    		        	    	camdb.setName(newName);
								    		        	    	(new UpdateCamDB()).execute(camdb);
								    		        	    }
								   							
								        	           }
								        	       });
									        	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
									        	           public void onClick(DialogInterface dialog, int id) {
									        	               // User cancelled the dialog
									        	           }
									        	       });
								        	    
								        	    // Get the AlertDialog from create()
								        		AlertDialog dialogedit = builder.create();
								        		
								        		dialogedit.show();
					    		        	    
		                    					break;
		                    			
		                    		case 1 : 	if (list_item instanceof Sensor){
					    		        	    	SensorDB sensdb = ((Sensor)list_item).getSensorDB();
					    		        	    	sensdb.setRoomId(0);
					    		        	    	(new UpdateSensDB()).execute(sensdb);
					    		        	    } else
					    		        	    if (list_item instanceof Actuator){
					    		        	    	ActuatorDB actdb = ((Actuator)list_item).getActuatorDB();
					    		        	    	actdb.setRoomId(0);
					    		        	    	(new UpdateActDB()).execute(actdb);
					    		        	    }
					                    		if (list_item instanceof CamDB){
					    		        	    	CamDB camdb = (CamDB)list_item;
					    		        	    	camdb.setRoomId(0);
					    		        	    	(new UpdateCamDB()).execute(camdb);
					    		        	    }
		                    					break;
		                    		case 2:		boolean useFunctions = act.getActuatorDB() != null ? act.getActuatorDB().isUseFunction() : false;
			                					if (useFunctions) {
			                						(new SetFunctionDB(act)).execute(false);
			                					} else {
			                						(new SetFunctionDB(act)).execute(true);
			                					}
		                    					break;
		                    	}
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
		go = true;
		
		myHandler = new Handler();
		myHandler.postDelayed(myRunnable, 0);
		
		super.onResume();
	}
	
	
	@Override
    public void onRefreshStarted(View view) {
		new GetDataTask().execute();
    }
	
	private final Runnable myRunnable = new Runnable()
	{
	    public void run()

	    {
	    	if (go) {
		    	//Toast.makeText(activity,"Refresh",Toast.LENGTH_SHORT).show();
		    	myXsone = RuntimeStorage.getMyXsone();
		    	
		    	if (myXsone != null) {
		    		
		    		(new GetDataTask()).execute();
			        myHandler.postDelayed(myRunnable, RuntimeStorage.getRefreshSeconds() * 1000);
		    	}
	    	} 
	    }

	};
	

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {		
		private List<Object> items_list;
		private ActuatorSensorCameraAdapter adapter;
		private List<XS_Object> tmp;
		private List<XS_Object> sens_list;
		private List<XS_Object> act_list;
		
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
			if (myXsone != null) {
			
				// read sensors remote
				readSensorsRemote();
				
				// read acutators remote
				readActuatorsRemote();
				
				// read all items for room from DB
				items_list = new ArrayList<Object>();
				
				List<Object> items_list_db = db.getActSensCamForRoom(roomId);
				
							
				SensorDB sensordb;
				ActuatorDB actuatordb;
				CamDB camdb;
				for (int i=0; i< items_list_db.size(); i++){
					Object item = items_list_db.get(i);
					
					if (item instanceof ActuatorDB){
						actuatordb = (ActuatorDB)item;
						
						Actuator a = myXsone.getActuator(actuatordb.getNumber());
						if (a != null) {
							items_list.add(a);
						}
							
					} else
					if (item instanceof SensorDB){
						sensordb = (SensorDB)item;
						
						Sensor s = myXsone.getSensor(sensordb.getNumber());
						if (s != null) {
							items_list.add(s);
						}
					} else
					if (item instanceof CamDB){
						camdb = (CamDB)item;
						items_list.add(camdb);
					}
				}
				
				if (items_list.size() > 0 && getActivity() != null) {
					adapter = new ActuatorSensorCameraAdapter(getActivity(), R.layout.list_item_sens_act_cam, items_list);
				}
			} else {
				XsError.printError(getActivity());
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
				
			//RoomEditAdapter adapter = new RoomEditAdapter(getActivity(), R.layout.list_item_room_edit, items_list);
			if (getActivity() != null){
				setListAdapter(adapter);
			}
			
			if (db != null)
				db.closeDB();
			
			mPullToRefreshLayout.setRefreshComplete();
			
			return;

		}
	}
	
	private class UpdateActDB extends AsyncTask<ActuatorDB, Void, String[]> {		
		
		@Override
		protected String[] doInBackground(ActuatorDB... params) {
			ActuatorDB actdb = params[0];
			db.updateActuator(actdb);
			db.closeDB();
						
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			new GetDataTask().execute();
			return;
		}
	}
	
	private class UpdateSensDB extends AsyncTask<SensorDB, Void, String[]> {		
		
		@Override
		protected String[] doInBackground(SensorDB... params) {
			SensorDB sensdb = params[0];
			db.updateSensor(sensdb);
			db.closeDB();
						
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			new GetDataTask().execute();	
			return;
		}
	}
	
private class UpdateCamDB extends AsyncTask<CamDB, Void, String[]> {		
		
		@Override
		protected String[] doInBackground(CamDB... params) {
			CamDB camdb = params[0];
			db.updateCam(camdb);
			db.closeDB();
						
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			new GetDataTask().execute();	
			return;
		}
	}
	
	private class AssignActToRoom extends AsyncTask<Actuator, Void, String[]> {
    	
    	public AssignActToRoom(){
    	}
		
		@Override
		protected String[] doInBackground(Actuator... params) {
			Actuator actuator = params[0];
			
			if (actuator.getActuatorDB() == null){
				// ActuatorDB neu erstellen!
				ActuatorDB act_db = new ActuatorDB();
				act_db.setName(actuator.getName());
				act_db.setNumber(actuator.getNumber());
				act_db.setRoomId(roomId);
				
				db.createActuator(act_db);
				
				actuator.setActuatorDB(act_db);
			} else {
				// ActuatorDB updaten
				ActuatorDB act_db = actuator.getActuatorDB();
				act_db.setRoomId(roomId);
				
				db.updateActuator(act_db);
				
				actuator.setActuatorDB(act_db);
			}
			
     	    db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			new GetDataTask().execute();
				
			return;

		}
	}

	private class AssignSensToRoom extends AsyncTask<Sensor, Void, String[]> {
		
		public AssignSensToRoom(){
		}
		
		@Override
		protected String[] doInBackground(Sensor... params) {
			Sensor sensor = params[0];
			
			if (sensor.getSensorDB() == null){
				// SensorDB neu erstellen!
				SensorDB sens_db = new SensorDB();
				sens_db.setName(sensor.getName());
				sens_db.setNumber(sensor.getNumber());
				sens_db.setRoomId(roomId);
				
				db.createSensor(sens_db);
				
				sensor.setSensorDB(sens_db);
			} else {
				// SensorDB updaten
				SensorDB sens_db = sensor.getSensorDB();
				sens_db.setRoomId(roomId);
				
				db.updateSensor(sens_db);
				
				sensor.setSensorDB(sens_db);
			}
			
	 	    db.closeDB();
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			new GetDataTask().execute();
				
			return;

		}
		
	}
	
	private class AssignCamToRoom extends AsyncTask<CamDB, Void, String[]> {
		
		public AssignCamToRoom(){
		}
		
		@Override
		protected String[] doInBackground(CamDB... params) {
			CamDB camera = params[0];
			
			if (camera != null){
				// CamDB updaten
				camera.setRoomId(roomId);
				
				db.updateCam(camera);
			}
	 	    db.closeDB();
			
			return null;
		}
	

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			new GetDataTask().execute();
				
			return;
	
		}
	}
	
		
	
	private class GetAllCams extends AsyncTask<Void, Void, String[]> {
		
		public GetAllCams(){
		}
		
		@Override
		protected String[] doInBackground(Void... params) {
			cam_list = db.getAllCams();
			db.closeDB();
			
			return null;
		}
	}
	
	private class SetFunctionDB extends AsyncTask<Boolean, Void, String[]> {
    	Actuator actuator;
    	
    	public SetFunctionDB(Actuator act){
    		this.actuator=act;
    	}
		
		@Override
		protected String[] doInBackground(Boolean... params) {
			boolean useFunction = params[0];
			
			ActuatorDB actuatorDB = actuator.getActuatorDB();
     	   if (actuatorDB != null){
     		
            	actuatorDB.setUseFunction(useFunction);
            	db.updateActuator(actuatorDB);
            	actuator.setActuatorDB(actuatorDB);
            } else {
            	actuatorDB = new ActuatorDB();
            	actuatorDB.setName(actuator.getAppname());
            	actuatorDB.setNumber(actuator.getNumber());
            	actuatorDB.setUseFunction(useFunction);
            	db.createActuator(actuatorDB);
            	actuator.setActuatorDB(actuatorDB);
            	
            }
     	   db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			(new GetDataTask()).execute();
				
			return;

		}
	}

	
	/**
	 * Der Tab �bernimmt die Aktionen des Tabhost f�r Menu und Back Button
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// is activity withing a tabactivity
		if (getActivity().getParent() != null) {
			return getActivity().getParent().onKeyDown(keyCode, event);
		}
		return false;
	}
	
}
