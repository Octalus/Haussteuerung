package de.infoscout.betterhome.view.menu.room;

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
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.view.adapter.RoomAdapter;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.room.edit.MenuItemDetailActivityRoomEdit;
import de.infoscout.betterhome.view.menu.room.edit.MenuItemDetailFragmentRoomEdit;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailActivityTimer;
import de.infoscout.betterhome.view.utils.Utilities;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityTimer} on handsets.
 */
public class MenuItemDetailFragmentRoom extends ListFragment implements OnRefreshListener {
		
	// Das Xsone Objekt f�r diese Aktivity
	private PullToRefreshLayout mPullToRefreshLayout;
	private DatabaseStorage db;
	
	private boolean tablet = false;
	private FragmentActivity activity;
	
	public MenuItemDetailFragmentRoom() {
		
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
		
		if (activity.findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_menuitem_detail_room, container, false); 
		
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
						if (o instanceof RoomDB) {
							if (tablet) {
								MenuItemDetailFragmentRoomEdit fragment = new MenuItemDetailFragmentRoomEdit();
								
								Bundle args= new Bundle();
						        args.putInt("roomId", ((RoomDB) o).getId());
						        fragment.setArguments(args);
								
								activity.getSupportFragmentManager().beginTransaction()
										.replace(R.id.menuitem_detail_container, fragment, Utilities.TAG)
										.addToBackStack(null)
										.commit();
							} else {
								// Room edit Activity
								Intent intent = new Intent(getActivity(), MenuItemDetailActivityRoomEdit.class);
								intent.putExtra("roomId", ((RoomDB) o).getId());
								intent.putExtra("roomName", ((RoomDB) o).getName());
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
		        				        	    
		        	    final RoomDB room = (RoomDB)getListAdapter().getItem(position);
								        	    			        	
		        	    builder.setTitle(R.string.editroom);
		        	    
		        	    final String[] elements = {getString(R.string.rename), getString(R.string.remove)};
		        	    builder.setItems(elements, new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	                	
		                    	switch (which) {
		                    		case 0 : 	AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
		    		        		
					    		        		// Get the layout inflater
					    		        	    LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
					    		        	    View view = inflater.inflate(R.layout.dialog_room_add, null);
					    		        	    builder.setView(view);
					    		        	    					    		        	    
					    		        	    final EditText nameView = (EditText)view.findViewById(R.id.dialog_name_text);
					    		        	    nameView.setText(room.getName());
					    		        	    
					    		        	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								        	           public void onClick(DialogInterface dialog, int id) {
								        	               // User clicked OK button
								        	        	   String newName = nameView.getText().toString();
								        	        	   
								        	        	   if (!newName.equals("")){
								    		        	    	room.setName(newName);
								    		        	    	(new UpdateRoomDB()).execute(room);
								        	        	   } else {
								        	        		   Toast.makeText(activity, activity.getString(R.string.name_missing), Toast.LENGTH_LONG).show();
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
		                    			
		                    		case 1 :    (new DeleteRoomDB()).execute(room);
					    		        	    
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(activity, new Intent(activity,
					MenuItemListActivity.class));
			return true;
		case R.id.addroom:
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    		
    		// Get the layout inflater
    	    LayoutInflater inflater = activity.getLayoutInflater();
    	    View view = inflater.inflate(R.layout.dialog_room_add, null);
    	    builder.setView(view);
    	    
    	    final EditText nameView = (EditText)view.findViewById(R.id.dialog_name_text);
    	    	        	
    	    builder.setTitle(R.string.roomadd);
        	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	               // User clicked OK button
        	        	   String newName = nameView.getText().toString();
        	        	   
        	        	   if (!newName.equals("")){
   								(new SetRoomDB()).execute(newName);
        	        	   } else {
        	        		   Toast.makeText(activity, activity.getString(R.string.name_missing), Toast.LENGTH_LONG).show();
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
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.roomoptions, menu);
	}
	
	private class SetRoomDB extends AsyncTask<String, Void, String[]> {
    	
    	public SetRoomDB(){
    	}
		
		@Override
		protected String[] doInBackground(String... params) {
			String newName = params[0].trim();
			
			RoomDB room = new RoomDB();
			room.setName(newName);
     	   
            db.createRoom(room);
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

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {		
		private List<RoomDB> room_list;
		
		@Override
		protected String[] doInBackground(Void... params) {
			room_list = db.getAllRooms();
			
			db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			if (room_list.size() > 0) {
				RoomAdapter adapter = new RoomAdapter(activity, R.layout.list_item_room, room_list);
				setListAdapter(adapter);
			}
				
			mPullToRefreshLayout.setRefreshComplete();
			
			return;

		}
	}
	
	private class UpdateRoomDB extends AsyncTask<RoomDB, Void, String[]> {
		
		@Override
		protected String[] doInBackground(RoomDB... params) {
			RoomDB room = params[0];
			db.updateRoom(room);
			
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
	
private class DeleteRoomDB extends AsyncTask<RoomDB, Void, String[]> {
		
		@Override
		protected String[] doInBackground(RoomDB... params) {
			RoomDB room = params[0];
			db.deleteRoom(room.getId());
			
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
