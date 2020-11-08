package de.infoscout.betterhome.view.menu.pos;

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
import de.infoscout.betterhome.model.device.db.PositionDB;
import de.infoscout.betterhome.view.adapter.PositionAdapter;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.pos.create.MenuItemDetailActivityPositionCreate;
import de.infoscout.betterhome.view.menu.pos.create.MenuItemDetailFragmentPositionCreate;
import de.infoscout.betterhome.view.menu.pos.edit.MenuItemDetailActivityPositionEdit;
import de.infoscout.betterhome.view.menu.pos.edit.MenuItemDetailFragmentPositionEdit;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailActivityTimer;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityTimer} on handsets.
 */
public class MenuItemDetailFragmentPosition extends ListFragment implements OnRefreshListener {
		
	// Das Xsone Objekt f�r diese Aktivity
	private PullToRefreshLayout mPullToRefreshLayout;
	private DatabaseStorage db;
	
	private boolean tablet = false;
	private FragmentActivity activity;
	
	private String TAG = MenuItemDetailFragmentPosition.class.getSimpleName();

	public MenuItemDetailFragmentPosition() {
		
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
		
		View view = inflater.inflate(R.layout.fragment_menuitem_detail_position, container, false); 
		
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
						if (o instanceof PositionDB) {
							if (tablet) {
								MenuItemDetailFragmentPositionEdit fragment = new MenuItemDetailFragmentPositionEdit();
								
								Bundle args= new Bundle();
						        args.putInt("positionId", ((PositionDB) o).getId());
						        args.putString("positionName", ((PositionDB) o).getName());
						        args.putInt("positionActNrb", ((PositionDB) o).getActNumber());
						        args.putDouble("positionValue", ((PositionDB) o).getValue());
						        args.putDouble("positionLon", ((PositionDB) o).getLon());
						        args.putDouble("positionLat", ((PositionDB) o).getLat());
						        args.putBoolean("positionOnEntry", ((PositionDB) o).isOnEntry());
						        args.putInt("positionRadius", ((PositionDB) o).getRadius());
						        fragment.setArguments(args);
								
								activity.getSupportFragmentManager().beginTransaction()
										.replace(R.id.menuitem_detail_container, fragment, TAG)
										.addToBackStack(null)
										.commit();
							} else {
								// Position edit Activity
								Intent intent = new Intent(getActivity(), MenuItemDetailActivityPositionEdit.class);
								intent.putExtra("positionId", ((PositionDB) o).getId());
								intent.putExtra("positionName", ((PositionDB) o).getName());
								intent.putExtra("positionActNrb", ((PositionDB) o).getActNumber());
								intent.putExtra("positionValue", ((PositionDB) o).getValue());
								intent.putExtra("positionLon", ((PositionDB) o).getLon());
								intent.putExtra("positionLat", ((PositionDB) o).getLat());
								intent.putExtra("positionOnEntry", ((PositionDB) o).isOnEntry());
								intent.putExtra("positionRadius", ((PositionDB) o).getRadius());
								startActivity(intent);
							}
						}
					}
				});
		
		getListView().setOnItemLongClickListener(
				new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View v, int pos, long id) {
						
						final PositionDB position = (PositionDB)getListAdapter().getItem(pos);
						
						AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
		    		        		
		        		// Get the layout inflater
		        	    LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
		        	    View view = inflater.inflate(R.layout.dialog_position_add, null);
		        	    builder.setView(view);
		        	    					    		        	    
		        	    final EditText nameView = (EditText)view.findViewById(R.id.dialog_name_text);
		        	    nameView.setText(position.getName());
		        	    
		        	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		        	           public void onClick(DialogInterface dialog, int id) {
		        	               // User clicked OK button
		        	        	   String newName = nameView.getText().toString();
		        	        	   
		        	        	   if (!newName.equals("")){
		    		        	    	position.setName(newName);
		    		        	    	(new UpdatePositionDB()).execute(position);
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
		case R.id.addposition:
			if (tablet){
				// commit Add fragment
				MenuItemDetailFragmentPositionCreate fragment = new MenuItemDetailFragmentPositionCreate();
				activity.getSupportFragmentManager().beginTransaction()
						.replace(R.id.menuitem_detail_container, fragment)
						.addToBackStack(null)
						.commit();
			} else {
				Intent intent = new Intent(activity, MenuItemDetailActivityPositionCreate.class);
				startActivity(intent);
			}
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.positionoptions, menu);
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {		
		private List<PositionDB> position_list;
		
		@Override
		protected String[] doInBackground(Void... params) {
			position_list = db.getAllPositions();
			
			db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			if (position_list.size() > 0) {
				PositionAdapter adapter = new PositionAdapter(getActivity(), R.layout.list_item_position, position_list);
				setListAdapter(adapter);
			}
			
			mPullToRefreshLayout.setRefreshComplete();
			
			return;

		}
	}
	
	private class UpdatePositionDB extends AsyncTask<PositionDB, Void, String[]> {
		
		@Override
		protected String[] doInBackground(PositionDB... params) {
			PositionDB position = params[0];
			db.updatePosition(position);
			
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
