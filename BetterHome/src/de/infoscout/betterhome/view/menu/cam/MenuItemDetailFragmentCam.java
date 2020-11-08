package de.infoscout.betterhome.view.menu.cam;

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
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.CamDB;
import de.infoscout.betterhome.view.adapter.ActuatorSensorCameraAdapter;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.cam.edit.MenuItemDetailActivityCamShow;
import de.infoscout.betterhome.view.menu.cam.edit.MenuItemDetailFragmentCamShow;
import de.infoscout.betterhome.view.menu.graph.MenuItemDetailFragmentGraph;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailActivityTimer;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityTimer} on handsets.
 */
public class MenuItemDetailFragmentCam extends ListFragment implements OnRefreshListener {
		
	// Das Xsone Objekt f�r diese Aktivity
	private PullToRefreshLayout mPullToRefreshLayout;
	private DatabaseStorage db;
	
	private boolean tablet = false;
	private FragmentActivity activity;
	
	public MenuItemDetailFragmentCam() {
		
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
		
		if (!tablet) {
			activity.getActionBar().show();
		}
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_menuitem_detail_cam, container, false); 
		
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
					}
				});
		
		getListView().setOnItemLongClickListener(
				new AdapterView.OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View v, int position, long id) {
						
						AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
		        				        	    
		        	    final CamDB cam = (CamDB)getListAdapter().getItem(position);
								        	    			        	
		        	    builder.setTitle(R.string.camedit);
		        	    
		        	    final String[] elements = {"Editieren", "Entfernen"};
		        	    builder.setItems(elements, new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	                	
		                    	switch (which) {
		                    		case 0 : 	AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
		    		        		
					    		        		// Get the layout inflater
					    		        	    LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
					    		        	    
					    		        	    View view;
					    		        	    if (cam.getStream() == 1){
					    		        	        view = inflater.inflate(R.layout.dialog_cam_add_stream, null);
					    		        	        builder.setTitle(R.string.camstreamedit);
					    		        	    } else {
					    		        	    	view = inflater.inflate(R.layout.dialog_cam_add_webpage, null);
					    		        	    	builder.setTitle(R.string.camwebpageedit);
					    		        	    }
					    		        	    builder.setView(view);
					    		        	    					    		        	    
					    		        	    final EditText nameView = (EditText)view.findViewById(R.id.dialog_name_text);
					    		        	    final EditText urlView = (EditText)view.findViewById(R.id.dialog_url_text);
					    		        	    final EditText userView = (EditText)view.findViewById(R.id.dialog_user_text);
					    		        	    final EditText passView = (EditText)view.findViewById(R.id.dialog_pass_text);
					    		        	    
					    		        	    nameView.setText(cam.getName());
					    		        	    urlView.setText(cam.getUrl());
					    		        	    userView.setText(cam.getUsername());
					    		        	    passView.setText(cam.getPassword());
					    		        	    
					    		        	    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
								        	           public void onClick(DialogInterface dialog, int id) {
								        	               // User clicked OK button
								        	        	   
								        	        	   String newName = nameView.getText().toString();
								        	        	   String newUrl = urlView.getText().toString();
								        	        	   String newUser = userView.getText().toString();
								        	        	   String newPass = passView.getText().toString();
								        	        	   
								        	        	   if (!newName.equals("")){
				                        	        		   if (!newUrl.equals("")){
								        	        	   
									    		        	    	cam.setName(newName);
									    		        	    	cam.setUrl(newUrl);
									    		        	    	cam.setUsername(newUser);
									    		        	    	cam.setPassword(newPass);
									    		        	    	
									    		        	    	(new UpdateCamDB()).execute(cam);
				                        	        		   } else {
				                        	        			   Toast.makeText(activity, activity.getString(R.string.url_missing), Toast.LENGTH_LONG).show();
				                        	        		   }
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
		                    			
		                    		case 1 :    (new DeleteCamDB()).execute(cam);
					    		        	    
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
		case R.id.addcam:
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    		
    	    final String[] elements = {getString(R.string.stream), getString(R.string.webpage)};
    	    builder.setItems(elements, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                	                	
                	switch (which) {
                		case 0 : 	// Stream
		                			AlertDialog.Builder builderStream = new AlertDialog.Builder(activity);
		                    		
		                    		// Get the layout inflater
		                    	    LayoutInflater inflaterStream = activity.getLayoutInflater();
		                    	    View viewStream = inflaterStream.inflate(R.layout.dialog_cam_add_stream, null);
		                    	    builderStream.setView(viewStream);
		                    	    
		                    	    final EditText nameViewStream = (EditText)viewStream.findViewById(R.id.dialog_name_text);
		                    	    final EditText urlViewStream = (EditText)viewStream.findViewById(R.id.dialog_url_text);
		                    	    final EditText userViewStream = (EditText)viewStream.findViewById(R.id.dialog_user_text);
		                    	    final EditText passViewStream = (EditText)viewStream.findViewById(R.id.dialog_pass_text);
		                    	    	        	
		                    	    builderStream.setTitle(R.string.addstream);
		                    	    builderStream.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		                        	           public void onClick(DialogInterface dialog, int id) {
		                        	        	   
		                        	        	   if (!nameViewStream.getText().toString().equals("")){
		                        	        		   if (!urlViewStream.getText().toString().equals("")){
		                        	        	   
				                        	               // User clicked OK button
				                        	        	   CamDB cam = new CamDB();
				                        	   				cam.setName(nameViewStream.getText().toString());
				                        	        	    cam.setUrl(urlViewStream.getText().toString());
				                        	        	    cam.setUsername(userViewStream.getText().toString());
				                        	        	    cam.setPassword(passViewStream.getText().toString());
				                        	        	    cam.setStream(1);
				                        	        	   	   
				                   							(new SetCamDB()).execute(cam);
		                        	        		   } else {
		                        	        			   Toast.makeText(activity, activity.getString(R.string.url_missing), Toast.LENGTH_LONG).show();
		                        	        		   }
		                        	        	   } else {
		                        	        		   Toast.makeText(activity, activity.getString(R.string.name_missing), Toast.LENGTH_LONG).show();
		                        	        	   }
		                        	           }
		                        	       });
		                    	    builderStream.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		                        	           public void onClick(DialogInterface dialog, int id) {
		                        	               // User cancelled the dialog
		                        	           }
		                        	       });
		                    	    
		                    	    // Get the AlertDialog from create()
		                    		AlertDialog dialogStream = builderStream.create();
		                    		
		                    		dialogStream.show();
                					break;
                		case 1 : 	// Website
		                			AlertDialog.Builder builderPage = new AlertDialog.Builder(activity);
		                    		
		                    		// Get the layout inflater
		                    	    LayoutInflater inflaterPage = activity.getLayoutInflater();
		                    	    View viewPage = inflaterPage.inflate(R.layout.dialog_cam_add_webpage, null);
		                    	    builderPage.setView(viewPage);
		                    	    
		                    	    final EditText nameViewPage = (EditText)viewPage.findViewById(R.id.dialog_name_text);
		                    	    final EditText urlViewPage = (EditText)viewPage.findViewById(R.id.dialog_url_text);
		                    	    final EditText userViewPage = (EditText)viewPage.findViewById(R.id.dialog_user_text);
		                    	    final EditText passViewPage = (EditText)viewPage.findViewById(R.id.dialog_pass_text);
		                    	    	        	
		                    	    builderPage.setTitle(R.string.addstream);
		                    	    builderPage.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		                        	           public void onClick(DialogInterface dialog, int id) {
		                        	               // User clicked OK button
		                        	        	   if (!nameViewPage.getText().toString().equals("")){
		                        	        		   if (!urlViewPage.getText().toString().equals("")){
				                        	        	   CamDB cam = new CamDB();
				                        	   				cam.setName(nameViewPage.getText().toString());
				                        	        	    cam.setUrl(urlViewPage.getText().toString());
				                        	        	    cam.setUsername(userViewPage.getText().toString());
				                        	        	    cam.setPassword(passViewPage.getText().toString());
				                        	        	    cam.setStream(0);
				                        	        	   	   
				                   							(new SetCamDB()).execute(cam);
		                        	        		   } else {
		                        	        			   Toast.makeText(activity, activity.getString(R.string.url_missing), Toast.LENGTH_LONG).show();
		                        	        		   }
		                        	        	   } else {
		                        	        		   Toast.makeText(activity, activity.getString(R.string.name_missing), Toast.LENGTH_LONG).show();
		                        	        	   }
		                        	           }
		                        	       });
		                    	    builderPage.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		                        	           public void onClick(DialogInterface dialog, int id) {
		                        	               // User cancelled the dialog
		                        	           }
		                        	       });
		                    	    
		                    	    // Get the AlertDialog from create()
		                    		AlertDialog dialogWebpage = builderPage.create();
		                    		
		                    		dialogWebpage.show();
                			
                					break;
                		default : break;
                	}
                	
                }});
    	    	        	
    	    builder.setTitle(R.string.camadd);
    	    
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
		inflater.inflate(R.menu.camoptions, menu);
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {		
		private List<CamDB> cam_list;
		
		@Override
		protected String[] doInBackground(Void... params) {
			cam_list = db.getAllCams();
			
			db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			List<Object> items_list = new ArrayList<Object>();
			
			for (int i=0; i<cam_list.size(); i++){
				items_list.add(cam_list.get(i));
			}
				
			//CamAdapter adapter = new CamAdapter(getActivity(), R.layout.list_item_cam, cam_list);
			if (items_list.size() > 0  && getActivity() != null){
				ActuatorSensorCameraAdapter adapter = new ActuatorSensorCameraAdapter(getActivity(), R.layout.list_item_sens_act_cam, items_list);
				setListAdapter(adapter);
			}
			
			mPullToRefreshLayout.setRefreshComplete();
			
			return;

		}
	}
	
private class SetCamDB extends AsyncTask<CamDB, Void, String[]> {
    	
    	public SetCamDB(){
    	}
		
		@Override
		protected String[] doInBackground(CamDB... params) {
			
			CamDB cam = params[0];
			
            db.createCam(cam);
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
		CamDB cam = params[0];
		db.updateCam(cam);
		
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

private class DeleteCamDB extends AsyncTask<CamDB, Void, String[]> {
	
	@Override
	protected String[] doInBackground(CamDB... params) {
		CamDB cam = params[0];
		db.deleteCam(cam.getId());
		
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
