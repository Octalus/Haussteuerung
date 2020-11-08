package de.infoscout.betterhome.view.menu.act.edit;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.remote.Http;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Script;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.adapter.ActuatorMakroAdapter;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.act.ActValueHolder;
import de.infoscout.betterhome.view.menu.act.MakroBodyConverter;
import de.infoscout.betterhome.view.menu.act.MenuItemDetailActivityAct;
import de.infoscout.betterhome.view.utils.Utilities;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityAct} on handsets.
 */
public class MenuItemDetailFragmentEditAct extends ListFragment implements OnRefreshListener {
	
	// Das Xsone Objekt f�r diese Aktivity
	private Xsone myXsone;
	private DatabaseStorage db;
	private Activity activity;
	private PullToRefreshLayout mPullToRefreshLayout;
	//private String makroName;
	private int makroNummer;
	private Script makro = null;
	private boolean tablet = false;

	public MenuItemDetailFragmentEditAct() {
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
		
		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();
		db = new DatabaseStorage(this.getActivity());
		activity = this.getActivity();
		
		if (((Activity)activity).findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		
		Bundle args = getArguments();
		//makroName = args.getString("makroName");
		makroNummer = args.getInt("makroNummer");
		
		makro = getCurrentMakro();
		
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_menuitem_detail_act, container, false);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.activityoptions, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.addmakro:
	            // Ansteuerung
				AlertDialog.Builder builderact = new AlertDialog.Builder(activity);
				
				// hole alle Actuatoren, die schon im Makro sind
				MakroBodyConverter mbc = new MakroBodyConverter( makro.getBody());
				List<ActValueHolder> currentActValues = mbc.getActuator_list();
				
				List<Actuator> currentActs = null;
				if (currentActValues != null){
					// bringe sie in separate List
					currentActs = new ArrayList<Actuator>();
					for (int i=0; i<currentActValues.size(); i++){
						currentActs.add(currentActValues.get(i).getActuator());
					}
				}
					
				// hole alle Actuatoren, die keine Makros und die noch nicht Teil der Liste sind
				final List<XS_Object> actuators = myXsone.getMyActiveActuatorList(false, currentActs);
				
				String[] act_names = new String[actuators.size()];
				
				for (int i=0; i< actuators.size(); i++){
					act_names[i] = ((Actuator)actuators.get(i)).getAppname();
				}
	
    			builderact.setItems(act_names, new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int which) {
    					Actuator selected_act = (Actuator)actuators.get(which);
    					
    					double value = selected_act.getValue();
    					
    					// functions default disabled
    					int functionNummer = -1;
    					
    					(new AssignActToMakro(value, functionNummer)).execute(selected_act);
    				}
    			});
	        	
        	    builderact.setTitle(R.string.actadd);
        	    
        	    // Get the AlertDialog from create()
        		AlertDialog dialogact = builderact.create();
        		
        		dialogact.show();
	                	
				return true;
			case android.R.id.home:
				NavUtils.navigateUpTo((Activity)activity, new Intent(activity,
						MenuItemDetailActivityAct.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	
		super.onViewCreated(view, savedInstanceState);
		
		ViewGroup viewGroup = (ViewGroup) view;
		  
		if (!tablet) {
			LayoutAnimationController controller 
			   = AnimationUtils.loadLayoutAnimation(
			     this.getActivity(), R.anim.list_layout_controller);
			  getListView().setLayoutAnimation(controller);
		}   
		  
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
		
		getListView().setOnItemLongClickListener(
				new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View v, int position, long id) {
						
						AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
		        				        	    
		        	    final ActValueHolder actValue = (ActValueHolder)getListAdapter().getItem(position);
								        	    			        	
		        	    builder.setTitle(R.string.editact);
		        	    
		        	    final String[] elements = new String[2];
		        	    elements[0] = getString(R.string.remove);
		        	    if (actValue.getFunctionNummer() == -1) {
		        	    	elements[1] = getString(R.string.functions);
		        	    } else {
		        	    	elements[1] = getString(R.string.no_functions);
		        	    }
		        	    
		        	    builder.setItems(elements, new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	                	
		                    	switch (which) {
		                    		case 0 :    (new DeleteActuator()).execute(actValue);
					    		        	    
		                    					break;
		                    		case 1 :	// switch to functions
		                    					if (actValue.getFunctionNummer() == -1) {
		                    					
			                    					actValue.setFunctionNummer(1);
			                    					actValue.setValue(-1);
		                    					} else {
		                    						actValue.setFunctionNummer(-1);
			                    					actValue.setValue(actValue.getActuator().getNewvalue());
		                    					}
			                    				
			                    				(new SetEditActuator(actValue.getActuator(), actValue.getValue(), actValue.getFunctionNummer(), 
			                    							actValue.getMakroNummer(), null, null, null, null, null)).execute(activity);
			                    					
		                    					(new GetDataTask()).execute();
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
		new GetDataTask().execute();
	}
	
	@Override
    public void onRefreshStarted(View view) {
		new GetDataTask().execute();
    }
	
	
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		private ActuatorMakroAdapter adapter;
		private List<ActValueHolder> actvalues;
		private List<RoomDB> rooms;
		
		@Override
		protected String[] doInBackground(Void... params) {
			
			makro = Http.getInstance().get_config_script(makro);
			
			if (makro == null) {
				XsError.printError(getActivity());
			} else {
				
				makro.setScriptDB(db.getScript(makro.getNumber()));
				myXsone.add_RemObj(makro);
	
				rooms = db.getAllRooms();
				db.closeDB();
			}
			
			
			MakroBodyConverter mbc = new MakroBodyConverter(makro.getBody());
			actvalues = mbc.getActuator_list(); 
			
			ArrayList<ActValueHolder> ordered_act_list = new ArrayList<ActValueHolder>();
			
			// load DB data for actuators
			if (actvalues != null) {
				int number;
				for (int i=0; i< actvalues.size(); i++) {
					number = actvalues.get(i).getActuator().getNumber();
					(actvalues.get(i).getActuator()).setActuatorDB(db.getActuator(number));
				}
						
				
				Actuator act;
				RoomDB room;
				for (int r=0; r<rooms.size(); r++){
					room = rooms.get(r);
					for (int a=0; a<actvalues.size(); a++){
						act = actvalues.get(a).getActuator();
						if (act.getActuatorDB() != null && act.getActuatorDB().getRoomId() == room.getId()){
							ordered_act_list.add(new ActValueHolder(act, actvalues.get(a).getValue(), makro.getNumber(), actvalues.get(a).getFunctionNummer()));
						}
					}
				}
				// actuator without room
				for (int a=0; a<actvalues.size(); a++){
					act = actvalues.get(a).getActuator();
					if ( (act.getActuatorDB() != null && act.getActuatorDB().getRoomId() == 0) || (act.getActuatorDB() == null) ){
						ordered_act_list.add(new ActValueHolder(act, actvalues.get(a).getValue(), makro.getNumber(), actvalues.get(a).getFunctionNummer()));
					}
				}
			}
			
			if (getActivity() != null && ordered_act_list.size() > 0){
				adapter = new ActuatorMakroAdapter(getActivity(), R.layout.list_item_sens_act_cam, ordered_act_list);
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			if (getActivity() != null){
				// Die Liste ausgeben
				setListAdapter(adapter);
			}
	
			mPullToRefreshLayout.setRefreshComplete();
			
			MakroBodyConverter mbc = new MakroBodyConverter(makro.getBody());
			String title = mbc.getCondition_actuator_left().getAppname();
			
			activity.setTitle(title);
			
			db.closeDB();
			
			return;

		}
	}
	
	private Script getCurrentMakro(){
		Script m = myXsone.getScript(makroNummer);
		
		if (m == null) Log.e(Utilities.TAG, "Makro not found!");
		
		return m;
	}
	
	private class AssignActToMakro extends AsyncTask<Actuator, Void, Boolean> {
    	private double value;
    	private int functionNummer;
		
    	public AssignActToMakro(double value, int functionNummer){
    		this.value = value;
    		this.functionNummer = functionNummer;
    	}
		
		@Override
		protected Boolean doInBackground(Actuator... params) {
			Actuator actuator = params[0];
			
			makro = getCurrentMakro();
			
			String body = makro.getBody();
			MakroBodyConverter mbc = new MakroBodyConverter(body);
			
			mbc.addActToList(actuator, value, functionNummer);
			
			String newBody = mbc.writeBody();

			String[] script_params = {makro.getName(), "onchange", newBody };
			
			boolean setted = myXsone.edit_Script(makro.getNumber(), script_params, activity);
			
			if (setted) {
				makro = Http.getInstance().get_config_script(makro);
				
				if (makro == null) {
					XsError.printError(getActivity());
				} else {
					
					makro.setScriptDB(db.getScript(makro.getNumber()));
					myXsone.add_RemObj(makro);
		
					db.closeDB();
				}
			}
			
			
			return setted;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			new GetDataTask().execute();
			
				
			return;

		}
	}
	
	private class DeleteActuator extends AsyncTask<ActValueHolder, Void, Boolean> {
		
    	public DeleteActuator(){
    	}
		
		@Override
		protected Boolean doInBackground(ActValueHolder... params) {
			ActValueHolder actValue = params[0];
			
			makro = getCurrentMakro();
			
			String body = makro.getBody();
			MakroBodyConverter mbc = new MakroBodyConverter(body);
			
			mbc.removeActFromList(actValue.getActuator().getNumber());
			
			String newBody = mbc.writeBody();

			String[] script_params = {makro.getName(), "onchange", newBody };
			
			boolean setted = myXsone.edit_Script(makro.getNumber(), script_params, activity);
			
			if (setted) {
				makro = Http.getInstance().get_config_script(makro);
				
				if (makro == null) {
					XsError.printError(getActivity());
				} else {
					
					makro.setScriptDB(db.getScript(makro.getNumber()));
					myXsone.add_RemObj(makro);
		
					db.closeDB();
				}
			}
			
			
			return setted;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			
			new GetDataTask().execute();
			
				
			return;

		}
	}
}
