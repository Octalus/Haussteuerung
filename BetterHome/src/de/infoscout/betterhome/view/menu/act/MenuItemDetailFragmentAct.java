package de.infoscout.betterhome.view.menu.act;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.remote.Http;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Script;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.ActuatorDB;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.adapter.ActuatorSensorCameraAdapter;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.act.edit.MenuItemDetailActivityEditAct;
import de.infoscout.betterhome.view.menu.act.edit.MenuItemDetailFragmentEditAct;
import de.infoscout.betterhome.view.menu.graph.MenuItemDetailActivityGraph;
import de.infoscout.betterhome.view.menu.graph.MenuItemDetailFragmentGraph;
import de.infoscout.betterhome.view.utils.Utilities;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityAct} on handsets.
 */
public class MenuItemDetailFragmentAct extends ListFragment implements OnRefreshListener {
	
	// Das Xsone Objekt fï¿½r diese Aktivity
	private Xsone myXsone;
	private List<XS_Object> act_list;
	private PullToRefreshLayout mPullToRefreshLayout;
	private DatabaseStorage db;
	private FragmentActivity activity;
	private boolean tablet = false;
	private Dialog dialog;
	
	private Handler myHandler;
	private boolean go = true;

	public MenuItemDetailFragmentAct() {
	}
	
	/**
	 * Die OnCreate Funtion stellt den Einstiegspunkt dieser Activity dar. Alle
	 * Aktuatoren werden nochmals einzeln aus der XS1 ausgelesen (Zustand etc)
	 * um die einzelnen ToggleButtons und die Beschriftung entsprechend setzen
	 * zu kï¿½nnen.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();
		db = new DatabaseStorage(this.getActivity());
		activity = this.getActivity();
		if (((Activity)activity).findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_menuitem_detail_act, container, false);
	}
	
	
	
	@Override
	public void onDestroyView() {
		go = false;
		super.onDestroyView();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activityoptions, menu);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.addmakro:
				if (myXsone.getFeatures().contains("C")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
	        		
	        		// Get the layout inflater
	        	    LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
	        	    View view = inflater.inflate(R.layout.dialog_act_edit, null);
	        	    builder.setView(view);
	        	    					
	        	    final EditText nameView = (EditText)(view.findViewById(R.id.dialog_name_text));
	        	    			        	
	        	    builder.setTitle(R.string.makroadd);
		        	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	               // User clicked OK button
		        	        	   String newName = nameView.getText().toString();
		        	        	   
		        	        	   AddScriptActuator asa = new AddScriptActuator();
		        	        	   asa.execute(new String[] {newName});
		        	        	   
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
				}
								
				return true;
			case android.R.id.home:
				NavUtils.navigateUpTo((Activity)activity, new Intent(activity,
						MenuItemListActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
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
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		getListView().setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						Object o = getListAdapter().getItem(position);
						
						if (o instanceof Actuator) {
							Actuator act = (Actuator) o;
							if (act.isMakro()){
							
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
					@SuppressLint("InflateParams")
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View v, int position, long id) {
						
						final Actuator act = (Actuator)getListAdapter().getItem(position);
						final View listElementView = v;
						
						AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
						
						final String[] elements;
						
						elements = new String[3];
						elements[0] = getString(R.string.rename);
						elements[1] = getString(R.string.remove);
						if (act.getActuatorDB() == null || !act.getActuatorDB().isUseFunction()) {
							elements[2] = getString(R.string.functions);
						} else {
							elements[2] = getString(R.string.no_functions);
						}
						
						
		        	    builder.setItems(elements, new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface diag, int which) {
		                    	                	
		                    	switch (which) {
		                    		case 0 : 	AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
		    		        		
					    		        		// Get the layout inflater
					    		        	    LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
					    		        	    View view = inflater.inflate(R.layout.dialog_act_edit, null);
					    		        	    builder.setView(view);
					    		        	    
					    		        	    final EditText nameView = (EditText)view.findViewById(R.id.dialog_name_text);
					    		        	    final TextView listtext = (TextView)listElementView.findViewById(R.id.text); 
					    		        	    nameView.setText(listtext.getText());
					    		        	    			        	
					    		        	    builder.setTitle(R.string.dialog_title_act);
					    			        	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					    			        	           public void onClick(DialogInterface dialog, int id) {
					    			        	               // User clicked OK button
					    			        	        	   //String newName = Utilities.trimName19(nameView.getText().toString());
					    			        	        	   String newName = nameView.getText().toString();
					    			        	        	   
					    			        	        	   Actuator testAct = myXsone.getActiveActuator(newName);
					    			        	        	   XS_Object testObject = myXsone.getActiveObject(newName);
					    			        	        	   
					    			        	        	   if (testAct == null && testObject == null){
					    			   								(new SetActuatorDB(act)).execute(newName);
					    			        	        	   
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
								        		AlertDialog dialogedit = builder.create();
								        		
								        		dialogedit.show();
					    		        	    
		                    					break;
		                    			
		                    		case 1 :    if (act.isMakro()) {
			                    					dialog = ProgressDialog.show(activity, "",
				            						activity.getString(R.string.delete_makro), true, false);
				            						dialog.show();
				            						
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
				            						
				            						if (script != null){
				            							(new DeleteScript(script.getNumber())).execute();
				            							(new DeleteActuator(act.getNumber())).execute();
				            						} else {
				            							XsError.printError(activity, "Script nicht gefunden!");
				            							dialog.dismiss();
				            						}
		                    					} else {
		                    						Toast.makeText(activity,"Actuator nicht entfernbar!",Toast.LENGTH_LONG).show();
		                    					}
				            						
		                    					break;
		                    		case 2 :    
		                    					boolean useFunctions = act.getActuatorDB() != null ? act.getActuatorDB().isUseFunction() : false;
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
		super.onResume();
		
		go = true;
		myHandler = new Handler();
		myHandler.postDelayed(myRunnable, 0);
	}
	
	@Override
    public void onRefreshStarted(View view) {
		new GetDataTask().execute();
    }
	
	// -------------------- XsOne / DB interactions ---------------------------
	
	private class AddScriptActuator extends AsyncTask<String, Boolean, Boolean> {
		private String name;
		private int nummer;
		
		public AddScriptActuator(){
		}
		
		@Override
		protected Boolean doInBackground(String... newName) {
			this.name = newName[0];
			
			// Erstellen eines programming actuators
     	   // 0=type; 1=system; 2=name; 3=hc1; 4=hc2; 5=address
     	   String[] newActuator = new String[22];
     	   newActuator[0]="switch";
     	   newActuator[1]="virtual";
     	   newActuator[2]=Utilities.trimName19("M_"+name.replace(" ", "_"));
     	   newActuator[3]="0";
     	   newActuator[4]="0";
     	   newActuator[5]="0";
     	   newActuator[6]="AN";
     	   newActuator[7]="on";
     	   newActuator[8]="";
     	   newActuator[9]="";
     	   newActuator[10]="AUS";
     	   newActuator[11]="off";
     	   newActuator[12]="";
     	   newActuator[13]="";
     	   newActuator[14]=null;
     	   newActuator[15]=null;
     	   newActuator[16]=null;
     	   newActuator[17]=null;
     	   newActuator[18]=null;
     	   newActuator[19]=null;
     	   newActuator[20]=null;
     	   newActuator[21]=null;
     	   int act_num = myXsone.add_Actuator(name, newActuator, activity);
     	   
     	   // Erstellen eines Scripts mit MakroBodyConverter
     	   MakroBodyConverter mbc = new MakroBodyConverter(null);
     	   mbc.setBody("if(@"+act_num+"==100){\n}");
     	   
     	   
     	   // 0=name; 1=type; 2=body
     	   String[] newScript = new String[3];
     	   newScript[0]=Utilities.trimName19("S_"+name.replace(" ", "_"));
     	   newScript[1]="onchange";
     	   newScript[2]=mbc.writeBody();
     	   nummer = myXsone.add_Script(newScript, activity);
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			if (result) {
				if (tablet){
					// commit Add fragment
					MenuItemDetailFragmentEditAct fragment = new MenuItemDetailFragmentEditAct();
					
					Bundle args= new Bundle();
			        args.putInt("makroNummer", nummer);
			        fragment.setArguments(args);
					
					activity.getSupportFragmentManager().beginTransaction()
							.replace(R.id.menuitem_detail_container, fragment)
							.addToBackStack(null)
							.commit();
				} else {
					Intent intent = new Intent(activity, MenuItemDetailActivityEditAct.class);
					intent.putExtra("makroNummer", nummer);
					startActivity(intent);
				}
			}
		}
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
		private List<RoomDB> rooms;
		private ActuatorSensorCameraAdapter adapter;
		private List<Object> ordered_act_list;
		
		@Override
		protected String[] doInBackground(Void... params) {
			
			// Liste neu holen
			tmp = Http.getInstance().get_list_actuators();
			
			if (tmp == null || myXsone == null) {
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
				
				rooms = db.getAllRooms();
				
				db.closeDB();
			}
			
			ordered_act_list = new ArrayList<Object>();
			Actuator act;
			
			if (rooms != null) {	
				RoomDB room;
				for (int r=0; r<rooms.size(); r++){
					room = rooms.get(r);
					for (int a=0; a<act_list.size(); a++){
						act = (Actuator)act_list.get(a);
						if (act.getActuatorDB() != null && act.getActuatorDB().getRoomId() == room.getId()){
							ordered_act_list.add(act);
						}
					}
				}
			}
			if (act_list != null){
				// actuator without room
				for (int a=0; a<act_list.size(); a++){
					act = (Actuator)act_list.get(a);
					if ( (act.getActuatorDB() != null && act.getActuatorDB().getRoomId() == 0) || (act.getActuatorDB() == null) ){
						ordered_act_list.add(act);
					}
				}
			}
			
			if (getActivity() != null && ordered_act_list.size() > 0){
				adapter = new ActuatorSensorCameraAdapter(getActivity(), R.layout.list_item_sens_act_cam, ordered_act_list);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			if (getActivity() != null && adapter != null){
				// Die Liste ausgeben
				setListAdapter(adapter);
			}
	
			mPullToRefreshLayout.setRefreshComplete();
				
			return;

		}
	}
	
	private class SetActuatorDB extends AsyncTask<String, Void, String[]> {
    	Actuator actuator;
    	
    	public SetActuatorDB(Actuator act){
    		this.actuator=act;
    	}
		
		@Override
		protected String[] doInBackground(String... params) {
			String newName = params[0];
			
			ActuatorDB actuatorDB = actuator.getActuatorDB();
     	   if (actuatorDB != null){
     		
            	actuatorDB.setName(newName);
            	db.updateActuator(actuatorDB);
            	actuator.setActuatorDB(actuatorDB);
            } else {
            	actuatorDB = new ActuatorDB();
            	actuatorDB.setName(newName);
            	actuatorDB.setNumber(actuator.getNumber());
            	db.createActuator(actuatorDB);
            	actuator.setActuatorDB(actuatorDB);
            	
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
		
	private class DeleteScript extends AsyncTask<Void, Boolean, Boolean> {
		private int number;
		
		public DeleteScript(int num){
			this.number=num;
		}
		
		@Override
		protected Boolean doInBackground(Void... data) {
			boolean ret = myXsone.delete_Script(number); 
			
			DatabaseStorage db = new DatabaseStorage(activity);
			db.deleteScript(number);
			
			return ret;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result) {
				Toast.makeText(activity, getString(R.string.delete_actuator_success),
						Toast.LENGTH_LONG).show();
			}
			// Sonst erfolgt ein Hinweistext
			else {
				XsError.printError(activity);
			}
			
			db.closeDB();
			
		}
	}
	
	private class DeleteActuator extends AsyncTask<Void, Boolean, Boolean> {
		private int number;
		
		public DeleteActuator(int num){
			this.number=num;
		}
		
		@Override
		protected Boolean doInBackground(Void... data) {
			boolean ret = myXsone.delete_Actuator(number); 
			
			DatabaseStorage db = new DatabaseStorage(activity);
			db.deleteActuator(number);
			
			return ret;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			// falls bisher alles ok war kann der Prozess beendet werden
			if (result) {
				Toast.makeText(activity, getString(R.string.delete_makro_success),
						Toast.LENGTH_LONG).show();
				
				new GetDataTask().execute();
								
			}
			// Sonst erfolgt ein Hinweistext
			else {
				XsError.printError(activity);
			}
			
			db.closeDB();
		}
	}
	

	/**
	 * Der Tab übernimmt die Aktionen des Tabhost für Menu und Back Button
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// is activity withing a tabactivity
		if (getActivity().getParent() != null) {
			return getActivity().getParent().onKeyDown(keyCode, event);
		}
		return false;
	}
}
