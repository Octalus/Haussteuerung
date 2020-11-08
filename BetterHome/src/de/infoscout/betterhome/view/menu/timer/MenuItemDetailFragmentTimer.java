package de.infoscout.betterhome.view.menu.timer;

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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
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
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.remote.Http;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Timer;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.TimerDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.adapter.TimerAdapter;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.timer.add.MenuItemDetailActivityTimerAdd;
import de.infoscout.betterhome.view.menu.timer.add.MenuItemDetailFragmentTimerAdd;
import de.infoscout.betterhome.view.menu.timer.edit.MenuItemDetailActivityTimerEdit;
import de.infoscout.betterhome.view.menu.timer.edit.MenuItemDetailFragmentTimerEdit;
import de.infoscout.betterhome.view.utils.Utilities;

public class MenuItemDetailFragmentTimer extends ListFragment implements OnRefreshListener {
		
	// Das Xsone Objekt f�r diese Aktivity
	private Xsone myXsone;
	private PullToRefreshLayout mPullToRefreshLayout;
	// Liste der auszugebenden Strings
	private List<XS_Object> timer_list;
	private DatabaseStorage db;
	private FragmentActivity activity;
	private boolean tablet = false;

	public MenuItemDetailFragmentTimer() {
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		db = new DatabaseStorage(this.getActivity());
		activity = this.getActivity();
		if (activity.findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_menuitem_detail_timer, container, false); 
		
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
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);		
		
		// Einzelklick auf Listenelement
		getListView().setOnItemClickListener( 
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
						Object o = getListAdapter().getItem(position);
						if (o instanceof Timer) {
							Log.d(Utilities.TAG, ((XS_Object) o).getName());
							
							(new GetConfigTimer()).execute((Timer)o);
						}
					}
				});
		// Langer Klick auf Listenelement
		getListView().setOnItemLongClickListener(
				new AdapterView.OnItemLongClickListener() {
					@SuppressLint("InflateParams")
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View v, int position, long id) {
						
						AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
		        				        	    
		        	    final Timer timer = (Timer)getListAdapter().getItem(position);
								        	    			        	
		        	    builder.setTitle(R.string.timeredit);
		    		        		
		        		// Get the layout inflater
		        	    LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
		        	    View view = inflater.inflate(R.layout.dialog_timer_edit, null);
		        	    builder.setView(view);
		        	    					    		        	    
		        	    final EditText nameView = (EditText)view.findViewById(R.id.dialog_name_text);
		        	    nameView.setText(timer.getAppname());
		        	    
		        	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	               // User clicked OK button
		        	        	   String newName = nameView.getText().toString();
		        	        	   
	    		        	    	TimerDB timerdb = timer.getTimerDB();
	    		        	    	
	    		        	    	if (timerdb != null) {
	    		        	    		timerdb.setName(newName);
	    		        	    	} else {
	    		        	    		timerdb = new TimerDB();
	    		        	    		timerdb.setName(newName);
	    		        	    		timerdb.setNumber(timer.getNumber());
	    		        	    	}
	    		        	    	(new SetTimerDB(timer)).execute(timerdb);
		    		        	    
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
					    
		        		return true;
					}
				});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addtimer:
			if (tablet){
				// commit Add fragment
				MenuItemDetailFragmentTimerAdd fragment = new MenuItemDetailFragmentTimerAdd();
				activity.getSupportFragmentManager().beginTransaction()
						.replace(R.id.menuitem_detail_container, fragment)
						.addToBackStack(null)
						.commit();
			} else {
				Intent intent = new Intent(activity, MenuItemDetailActivityTimerAdd.class);
				startActivity(intent);
			}
			return true;
			
		case android.R.id.home:
			NavUtils.navigateUpTo(activity, new Intent(activity,
					MenuItemListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.timeroptions, menu);
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
	
	// -------------- XSOne / DB interactions -----------------------------------------

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		private List<XS_Object> tmp;
		
		
		@Override
		protected String[] doInBackground(Void... params) {
			
			// Liste neu holen
			//tmp = Http.getInstance().get_detailed_list_timers();
			tmp = Http.getInstance().get_list_timers();
			
			if (tmp == null || myXsone == null) {
				XsError.printError(getActivity());
			} else {
				myXsone.add_RemObj(tmp);

				// Die Liste der Timer holen
				List<XS_Object> global_timer_list = myXsone.getMyTimerList();
				timer_list = new ArrayList<XS_Object>();
				
				// Liste der DB Timer holen
				List<TimerDB> dbTimers = db.getAllTimers();
				
				int number;
				Timer t;
				TimerDB tdb;
				if (global_timer_list != null && dbTimers != null){
					for (int i=0; i<global_timer_list.size(); i++) {
						t = (Timer)global_timer_list.get(i);
						number = t.getNumber();
						
						tdb = null;
						for (int j=0; j<dbTimers.size(); j++){
							TimerDB tdb_inner = dbTimers.get(j);
							if (tdb_inner.getNumber() == number) {
								tdb = tdb_inner;
								break;
							}
						}
						
						//tdb = db.getTimer(number);
						
						if (t.getType().equals("time") || (tdb != null && tdb.isInactive()) || t.getType().equals("sunrise") || t.getType().equals("sunset")) {
							t.setTimerDB(tdb);
							timer_list.add(t);
						}
					}
				}
				db.closeDB();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			if (getActivity() != null && timer_list != null && timer_list.size() > 0) {
				TimerAdapter adapter = new TimerAdapter(getActivity(), R.layout.list_item_timer, timer_list);
				
				setListAdapter(adapter);
			} else {
				XsError.printError(getActivity());
			}
				
			mPullToRefreshLayout.setRefreshComplete();
			
			return;

		}
	}
	
	private class SetTimerDB extends AsyncTask<TimerDB, Void, String[]> {
		private Timer timer;
		
		public SetTimerDB(Timer timer){
			this.timer=timer;
		}
	
		@Override
		protected String[] doInBackground(TimerDB... params) {
			TimerDB timerdb = params[0];
			
			if (timer.getTimerDB() != null){
				db.updateTimer(timerdb);
			} else {
				db.createTimer(timerdb);
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

	private class GetConfigTimer extends AsyncTask<Timer, Void, String[]> {
		private Timer timer;
		
		public GetConfigTimer(){
		}
	
		@Override
		protected String[] doInBackground(Timer... params) {
			timer = params[0];
			
			timer = Http.getInstance().get_config_timer(timer);
			
			if (timer == null) {
				XsError.printError(getActivity());
			} else {
				myXsone.add_RemObj(timer);
	
				// Die Liste der Sensoren holen
				timer_list = myXsone.getMyActiveTimerList();
				
				for (int i=0; i<timer_list.size(); i++) {
					if (timer.getNumber() == timer_list.get(i).getNumber()){
						((Timer)timer_list.get(i)).setTimerDB(db.getTimer(timer.getNumber()));
					}
				}
				db.closeDB();
			}
			
			return null;
		}
	
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			// Timer edit
			if (tablet){
				// commit Edit fragment
				MenuItemDetailFragmentTimerEdit fragment = new MenuItemDetailFragmentTimerEdit();
				
				Bundle args= new Bundle();
		        args.putInt("timerNumber", timer.getNumber());
		        fragment.setArguments(args);
				
				activity.getSupportFragmentManager().beginTransaction()
						.replace(R.id.menuitem_detail_container, fragment)
						.addToBackStack(null)
						.commit();
			} else {
				Intent intent = new Intent(getActivity(), MenuItemDetailActivityTimerEdit.class);
				intent.putExtra("timerNumber", timer.getNumber());
				startActivity(intent);
			}
			
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
