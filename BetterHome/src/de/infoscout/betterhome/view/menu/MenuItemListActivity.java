package de.infoscout.betterhome.view.menu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.PersistantStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.db.ActuatorDB;
import de.infoscout.betterhome.model.device.db.CamDB;
import de.infoscout.betterhome.model.device.db.GraphsDB;
import de.infoscout.betterhome.model.device.db.PositionDB;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.model.device.db.ScriptDB;
import de.infoscout.betterhome.model.device.db.SensorDB;
import de.infoscout.betterhome.model.device.db.SettingDB;
import de.infoscout.betterhome.model.device.db.TimerDB;
import de.infoscout.betterhome.view.AlertSettingsActivity;
import de.infoscout.betterhome.view.DonationActivity;
import de.infoscout.betterhome.view.FileChooser;
import de.infoscout.betterhome.view.InitializeActivity;
import de.infoscout.betterhome.view.SettingsActivity;
import de.infoscout.betterhome.view.FileChooser.FileSelectedListener;
import de.infoscout.betterhome.view.information.InformationActivity;
import de.infoscout.betterhome.view.menu.act.MenuItemDetailActivityAct;
import de.infoscout.betterhome.view.menu.act.MenuItemDetailFragmentAct;
import de.infoscout.betterhome.view.menu.cam.MenuItemDetailActivityCam;
import de.infoscout.betterhome.view.menu.cam.MenuItemDetailFragmentCam;
import de.infoscout.betterhome.view.menu.pos.MenuItemDetailActivityPosition;
import de.infoscout.betterhome.view.menu.pos.MenuItemDetailFragmentPosition;
import de.infoscout.betterhome.view.menu.room.MenuItemDetailActivityRoom;
import de.infoscout.betterhome.view.menu.room.MenuItemDetailFragmentRoom;
import de.infoscout.betterhome.view.menu.rule.MenuItemDetailActivityRule;
import de.infoscout.betterhome.view.menu.rule.MenuItemDetailFragmentRule;
import de.infoscout.betterhome.view.menu.sens.MenuItemDetailActivitySens;
import de.infoscout.betterhome.view.menu.sens.MenuItemDetailFragmentSens;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailActivityTimer;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailFragmentTimer;
import de.infoscout.betterhome.view.utils.Utilities;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * An activity representing a list of MeuItems. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link MenuItemDetailActivityAct} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link MenuItemListFragment} and the item details (if present) is a
 * {@link MenuItemDetailFragmentAct}.
 * <p>
 * This activity also implements the required
 * {@link MenuItemListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class MenuItemListActivity extends FragmentActivity implements
		MenuItemListFragment.Callbacks {
	
	private static final int POSITION_INITIALISATION = 0;
	private static final int POSITION_INFORMATION = 1;
	private static final int POSITION_ALERT = 2;
	private static final int POSITION_SETTINGS = 3;
	private static final int POSITION_IMPORT_EXPORT = 4;
	private static final int POSITION_DONATION = 5;
	
	private static final String FOLDER_BETTER_HOME = "BetterHome";

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean tablet;
	
	private DrawerLayout mDrawerLayout;
	private LinearLayout mDrawerLinearLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private String[] mOptionTitles;
	private FragmentActivity activity;
	
	private Dialog dialog = null;
	
	private DatabaseStorage db;
	private Xsone myXsone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		activity = this;
		db = new DatabaseStorage(activity);
		myXsone = RuntimeStorage.getMyXsone();
		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		
		// Keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menuitem_list);
		
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		
		// Set a custom shadow that overlays the main content when the drawer opens
	    //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

	    // ActionBarDrawerToggle ties together the the proper interactions
	    // between the sliding drawer and the action bar app icon
	    mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.drawer_name) {
	        public void onDrawerClosed(View view) {
	        	getActionBar().setTitle(getString(R.string.app_name));
	            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	        }

	        public void onDrawerOpened(View drawerView) {
	            getActionBar().setTitle(getString(R.string.drawer_name));
	            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
	        }
	    };
	    mDrawerLayout.setDrawerListener(mDrawerToggle);
	    mDrawerList = (ListView) findViewById(R.id.left_drawer);
	    mDrawerLinearLayout = (LinearLayout) findViewById(R.id.drawerLinearLayout);
	    mOptionTitles = getResources().getStringArray(R.array.drawer_options);
	    
	    int[] icons = new int[] {
	    		R.drawable.ic_action_accounts,
	    		R.drawable.ic_action_about,
	    		R.drawable.alertmenu,
	    		R.drawable.ic_action_settings,
	    		R.drawable.ic_action_import_export,
	    		R.drawable.ic_action_favorite
	    };
	    
	    // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
 
        for(int i=0;i<mOptionTitles.length;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("text", mOptionTitles[i]);
            hm.put("icon", Integer.toString(icons[i]) );
            aList.add(hm);
        }
        
        // Keys used in Hashmap
        String[] from = { "text","icon" };
 
        // Ids of views in listview_layout
        int[] to = { R.id.text1,R.id.iconmenu};
	    
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.list_item_drawer, from, to);
 
	    
	    // Set the adapter for the list view
        mDrawerList.setAdapter(adapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	    
	    ImageView infoscout_link = (ImageView)findViewById(R.id.infoscout_link);
	    infoscout_link.setOnClickListener(new View.OnClickListener(){
	        public void onClick(View v){
	            Intent intent = new Intent();
	            intent.setAction(Intent.ACTION_VIEW);
	            intent.addCategory(Intent.CATEGORY_BROWSABLE);
	            intent.setData(Uri.parse("http://infoscout.kg"));
	            startActivity(intent);
	        }
	    });
	  
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    getActionBar().setHomeButtonEnabled(true);
	    getActionBar().setDisplayShowHomeEnabled(true);
	    	    
		if (findViewById(R.id.menuitem_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			tablet = true;
			
			// Kreise mit Sensoren
			MenuItemDetailFragmentInitTabletCircles fragmentCircles = new MenuItemDetailFragmentInitTabletCircles();
			
			// Kreise mit Sensoren
			MenuItemDetailFragmentInitTabletPlan fragmentPlan = new MenuItemDetailFragmentInitTabletPlan();
			
			
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.menuitem_detail_container, fragmentPlan).commit();

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((MenuItemListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.menuitem_list))
					.setActivateOnItemClick(true);
		}		
	}
	
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    // Sync the toggle state after onRestoreInstanceState has occurred.
	    mDrawerToggle.syncState();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (tablet) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.optionsmenu, menu);
		}
		
		// TODO : working?
		if (myXsone == null || myXsone.getFeatures() == null || !myXsone.getFeatures().contains("D")) {
			menu.removeItem(R.id.graphs);
		}
			
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.graphs:
				Utilities.clearBackStack(this);
				
				MenuItemDetailFragmentInitTabletGraphs fragmentGraphs = new MenuItemDetailFragmentInitTabletGraphs();
				
				activity.getSupportFragmentManager().beginTransaction()
					.replace(R.id.menuitem_detail_container, fragmentGraphs)
					.addToBackStack(null)
					.commit();
				
				break;
			case R.id.plan:
				Utilities.clearBackStack(this);
				
				MenuItemDetailFragmentInitTabletCircles fragmentCircles = new MenuItemDetailFragmentInitTabletCircles();
				MenuItemDetailFragmentInitTabletPlan fragmentPlan = new MenuItemDetailFragmentInitTabletPlan();
							
				activity.getSupportFragmentManager().beginTransaction()
					.replace(R.id.menuitem_detail_container, fragmentPlan)
					.addToBackStack(null)
					.commit();
				
				break;
			case R.id.fullscreen:
				
				View listView = activity.findViewById(R.id.menuitem_list);
				int visibility = listView.getVisibility();
				if (visibility == View.VISIBLE) {
					listView.setVisibility(View.GONE);
				} else {
					listView.setVisibility(View.VISIBLE);
				}
				
				break;
			/*case R.id.statreset:
				(new DeleteStatsDB()).execute();
				
				break;*/
		}
		
		if(mDrawerToggle.onOptionsItemSelected(item))
	    {
	        return true;
	    }
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Callback method from {@link MenuItemListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(int id) {
		
		if (tablet) {
			Utilities.clearBackStack(this);
		}
		
		if (id == MenuItemListContent.ACTUATORS){
			if (tablet) {
				MenuItemDetailFragmentAct fragment = new MenuItemDetailFragmentAct();
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.menuitem_detail_container, fragment, Utilities.TAG)
				.addToBackStack(null)
				.commit();
			} else {
				Intent detailIntent = new Intent(this, MenuItemDetailActivityAct.class);
				startActivity(detailIntent);
			}
		}
		
		if (id == MenuItemListContent.SENSORS){
			if (tablet) {
				MenuItemDetailFragmentSens fragment = new MenuItemDetailFragmentSens();
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.menuitem_detail_container, fragment, Utilities.TAG)
				.addToBackStack(null)
				.commit();
			} else {
				Intent detailIntent = new Intent(this, MenuItemDetailActivitySens.class);
				startActivity(detailIntent);
			}
		}
		
		if (id == MenuItemListContent.TIMERS){
			if (tablet) {
				MenuItemDetailFragmentTimer fragment = new MenuItemDetailFragmentTimer();
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.menuitem_detail_container, fragment, Utilities.TAG)
				.addToBackStack(null)
				.commit();
			} else {
				Intent detailIntent = new Intent(this, MenuItemDetailActivityTimer.class);
				startActivity(detailIntent);
			}
		}
		
		if (id == MenuItemListContent.ROOMS){
			if (tablet) {
				MenuItemDetailFragmentRoom fragment = new MenuItemDetailFragmentRoom();
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.menuitem_detail_container, fragment, Utilities.TAG)
				.addToBackStack(null)
				.commit();
			} else {
				Intent detailIntent = new Intent(this, MenuItemDetailActivityRoom.class);
				startActivity(detailIntent);
			}
		}
		
		if (id == MenuItemListContent.SURVEILLANCES){
			if (tablet) {
				MenuItemDetailFragmentCam fragment = new MenuItemDetailFragmentCam();
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.menuitem_detail_container, fragment, Utilities.TAG)
				.addToBackStack(null)
				.commit();
			} else {
				Intent detailIntent = new Intent(this, MenuItemDetailActivityCam.class);
				startActivity(detailIntent);
			}
		}
		
		if (id == MenuItemListContent.RULES){
			if (tablet) {
				MenuItemDetailFragmentRule fragment = new MenuItemDetailFragmentRule();
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.menuitem_detail_container, fragment, Utilities.TAG)
						.addToBackStack(null)
						.commit();
			} else {
				Intent detailIntent = new Intent(this, MenuItemDetailActivityRule.class);
				startActivity(detailIntent);
			}
		}
		
		if (id == MenuItemListContent.POSITIONS){
			if (tablet) {
				MenuItemDetailFragmentPosition fragment = new MenuItemDetailFragmentPosition();
				getSupportFragmentManager().beginTransaction()
				.replace(R.id.menuitem_detail_container, fragment, Utilities.TAG)
				.addToBackStack(null)
				.commit();
			} else {
				Intent detailIntent = new Intent(this, MenuItemDetailActivityPosition.class);
				startActivity(detailIntent);
			}
		}
	}
	
	public void removeCurrentFragment()
	{
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	
		Fragment currentFrag =  getSupportFragmentManager().findFragmentById(R.id.menuitem_detail_container);
		
		transaction.remove(currentFrag).commit();
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
				
	    @SuppressWarnings("rawtypes")
		@Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}
	
	private Dialog createImportExportDialog() {
		Dialog dialog = new Dialog(activity);
		dialog.setTitle(R.string.import_export);
		
		dialog.setContentView(R.layout.dialog_import_export);
		
		Button buttonImport = (Button)dialog.findViewById(R.id.buttonImport);
		Button buttonExport = (Button)dialog.findViewById(R.id.buttonExport);
		
		buttonImport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FileChooser fileChooser = new FileChooser(activity, FOLDER_BETTER_HOME);
				fileChooser.setFileListener(new FileSelectedListener() {
				    @SuppressWarnings("unchecked")
					@Override 
				    public void fileSelected(final File file) {
				        // do something with the file
				    	System.out.println(file.getAbsolutePath());
				    	
						// read the file object and fill DB
				    	ArrayList<Object> appContent = (ArrayList<Object>)Utilities.readFileToObject(file);
				    	(new ImportDB()).execute(appContent);
				    	
				    }
				});
				String[] extensions = {"export"};
				fileChooser.setExtension(extensions);
				fileChooser.showDialog();
				
			}
		});
		
		buttonExport.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar now = Calendar.getInstance();
				String filename = now.get(Calendar.YEAR) + "" + Utilities.fillWithZero(now.get(Calendar.MONTH)+1) + "" 
								+ Utilities.fillWithZero(now.get(Calendar.DAY_OF_MONTH)) + "_" 
								+ Utilities.fillWithZero(now.get(Calendar.HOUR_OF_DAY)) + "" 
								+ Utilities.fillWithZero(now.get(Calendar.MINUTE)) + "" 
								+ Utilities.fillWithZero(now.get(Calendar.SECOND)) + "_BetterHome.export"; 
				
				File folder = new File(Environment.getExternalStorageDirectory() + "/" + FOLDER_BETTER_HOME);
				folder.mkdirs();
				
				File newFile = new File(folder.getAbsolutePath() + "/" + filename);
				boolean succ;
				try {
					
					succ = newFile.createNewFile();
					if (succ) {
						// write content to file
						(new ExportDB()).execute(newFile);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		
		return dialog;
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		
		
		if (position == POSITION_INITIALISATION) {
			startActivity(new Intent(MenuItemListActivity.this, InitializeActivity.class));
		} else
		if (position == POSITION_INFORMATION) {
			startActivity(new Intent(MenuItemListActivity.this, InformationActivity.class));
		} else 
		if (position == POSITION_ALERT) {
			// Alarms
			if (myXsone != null && myXsone.getFeatures() != null && myXsone.getFeatures().contains("B")) {
				startActivity(new Intent(MenuItemListActivity.this, AlertSettingsActivity.class));
			} else {
				Toast.makeText(activity, activity.getResources().getString(R.string.option_B_missing), Toast.LENGTH_LONG).show();
			}
		} else
		if (position == POSITION_SETTINGS) {
			// konfig
			startActivity(new Intent(MenuItemListActivity.this, SettingsActivity.class));
		} else
		if (position == POSITION_IMPORT_EXPORT) {
			// import/export
			dialog = createImportExportDialog();
			dialog.show();
		} else
		if (position == POSITION_DONATION) {
			// donation
			startActivity(new Intent(MenuItemListActivity.this, DonationActivity.class));
		}
		
		// Highlight the selected item, update the title, and close the drawer
	    mDrawerList.setItemChecked(position, true);
	    setTitle(mOptionTitles[position]);
	    
	    mDrawerLayout.closeDrawer(mDrawerLinearLayout);
	}
	
	
	
	private class DeleteStatsDB extends AsyncTask<String, Void, String[]> {		
		
		@Override
		protected String[] doInBackground(String... params) {
			db.deleteAllStatisticRanges();
			db.deleteAllStatistics();

			db.closeDB();
						
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			return;
		}
	}
	
	private class ExportDB extends AsyncTask<File, Void, Boolean> {		
		private File newFile = null;
	
		@Override
		protected Boolean doInBackground(File... params) {
			newFile = params[0];
			
			ArrayList<Object> appContent = new ArrayList<Object>();
			
			// Actuators
			ArrayList<ActuatorDB> allActuators = (ArrayList<ActuatorDB>)db.getAllActuators();
			appContent.add(allActuators);
			
			// Sensors
			ArrayList<SensorDB> allSensors = (ArrayList<SensorDB>)db.getAllSensors();
			appContent.add(allSensors);
			
			// Cams
			ArrayList<CamDB> allCams = (ArrayList<CamDB>)db.getAllCams();
			appContent.add(allCams);
			
			// Graphs
			ArrayList<GraphsDB> allGraphs = (ArrayList<GraphsDB>)db.getAllGraphs();
			appContent.add(allGraphs);
			
			// Positions
			ArrayList<PositionDB> allPositions = (ArrayList<PositionDB>)db.getAllPositions();
			appContent.add(allPositions);
			
			// Timer
			ArrayList<TimerDB> allTimers = (ArrayList<TimerDB>)db.getAllTimers();
			appContent.add(allTimers);
			
			// Settings
			//List<SettingDB> allSettings = db.getAllSettings();
			//appContent.add(allSettings);
			
			// Scripts
			ArrayList<ScriptDB> allScripts = (ArrayList<ScriptDB>)db.getAllScripts();
			appContent.add(allScripts);
			
			// Rooms
			ArrayList<RoomDB> allRomms = (ArrayList<RoomDB>)db.getAllRooms();
			appContent.add(allRomms);
			
			// Image plan
			byte[] image = (byte[])PersistantStorage.getInstance(activity).getData(Utilities.PLAN_IMAGE_FILENAME);
			if (image != null) {
				appContent.add(image);
			}

			db.closeDB();
			
			Utilities.saveObjectToFile(newFile, appContent);
						
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				dialog.cancel();
				Toast.makeText(activity, activity.getResources().getString(R.string.file_exported) +" : "+newFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
				
			}
				
			return;
		}
	}
	
	private class ImportDB extends AsyncTask<ArrayList<Object>, Void, Boolean> {		
		private ArrayList<Object> appContent = null;
		private Bitmap bmp = null;
		
		@Override
		protected Boolean doInBackground(ArrayList<Object>... params) {
			appContent = params[0];
			
			if (appContent != null) {
				
				// reset DB
				db.deleteAllStatisticRanges();
				db.deleteAllStatistics();
				db.deleteAllActuator();
				db.deleteAllSensor();
				db.deleteAllCam();
				db.deleteAllGraph();
				db.deleteAllPosition();
				db.deleteAllTimer();
				db.deleteAllScript();
				db.deleteAllRoom();
				
				
				for (int i=0; i<appContent.size(); i++) {
					Object content = appContent.get(i);
					
					if (content instanceof byte[]) {
						byte[] data = (byte[])content;
						PersistantStorage.getInstance(activity).saveData(data, Utilities.PLAN_IMAGE_FILENAME);
						
						ImageView imageView = (ImageView) activity.findViewById(R.id.plan_image);		
						if (imageView != null) {
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inMutable = true;
							bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
							
						}	
					} else if (content instanceof List<?>) {
						List<Object> list = (List<Object>)content;
						
						for (int j=0; j<list.size(); j++) {
							Object element = list.get(j);
							
							if (element instanceof ActuatorDB) {
								db.createActuator((ActuatorDB)element);
							} else if (element instanceof SensorDB) {
								db.createSensor((SensorDB)element);
							} else if (element instanceof CamDB) {
								db.createCam((CamDB)element);
							} else if (element instanceof GraphsDB) {
								db.createGraph((GraphsDB)element);
							} else if (element instanceof PositionDB) {
								db.createPosition((PositionDB)element);
							} else if (element instanceof TimerDB) {
								db.createTimer((TimerDB)element);
							} else if (element instanceof ScriptDB) {
								db.createScript((ScriptDB)element);
							} else if (element instanceof RoomDB) {
								db.createRoomWithId((RoomDB)element);
							}
						}
						
					}
				}
				
			}
			

			db.closeDB();
						
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if (bmp != null) {
					ImageView imageView = (ImageView) activity.findViewById(R.id.plan_image);
					imageView.setImageBitmap(bmp);
				}
				dialog.cancel();
				Toast.makeText(activity, activity.getResources().getString(R.string.file_imported), Toast.LENGTH_LONG).show();
			}
			
			return;
		}
	}
}
