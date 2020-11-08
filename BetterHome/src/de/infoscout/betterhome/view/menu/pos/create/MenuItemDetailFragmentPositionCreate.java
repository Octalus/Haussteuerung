package de.infoscout.betterhome.view.menu.pos.create;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.intent.LocationHelper;
import de.infoscout.betterhome.controller.service.ProximityAlertService;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.PositionDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.pos.MenuItemDetailFragmentPosition;
import de.infoscout.betterhome.view.menu.timer.MenuItemDetailActivityTimer;
import de.infoscout.betterhome.view.utils.Utilities;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityTimer} on handsets.
 */
@SuppressLint("SetJavaScriptEnabled")
public class MenuItemDetailFragmentPositionCreate extends Fragment {
		
	// Das Xsone Objekt fï¿½r diese Aktivity
	private Xsone myXsone;
	private static FragmentActivity activity;
	private List<XS_Object> act_list = new ArrayList<XS_Object>();
	private ArrayAdapter<String> adapter_act;
	private boolean tablet = false;
	
	private LocationManager locationManager;
	private TextView pos_info;
	private double latitude = 0;
	private double longitude = 0;
	private GoogleMap map;
	private volatile Location currentBestLocation;
	// Dialog für Ladevorgang
	private Dialog dialog;
	private MyLocationListener gps_listener;
	private MyLocationListener net_listener;

	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final int SEARCH_TIME = 1000 * 10;
	
	private Actuator actuator;
	private DatabaseStorage db;
	
	private int radius;
		
	public MenuItemDetailFragmentPositionCreate() {
		
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
		activity = this.getActivity();
		if (activity.findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		myXsone = RuntimeStorage.getMyXsone();
		act_list = myXsone.getMyActiveActuatorList(true, null);
		db = new DatabaseStorage(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_menuitem_detail_position_add_edit, container, false); 
		
		act_list = myXsone.getMyActiveActuatorList(true, null);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	
		super.onViewCreated(view, savedInstanceState);
		
		map = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map))
		        .getMap();
						
		// Array mit Namen anlegen für Spinner Adapter Actuator
		String[] act_names = new String[act_list.size()];
		for (int i = 0; i < act_list.size(); i++) {
			act_names[i] = ((Actuator) act_list.get(i)).getAppname();
		}
		
		adapter_act = new ArrayAdapter<String>(
				activity, android.R.layout.simple_spinner_dropdown_item, act_names);
		
		
		final Button buttonLoadPos = (Button)activity.findViewById(R.id.pos_button1);
		final RadioButton exitRadio = (RadioButton)activity.findViewById(R.id.radioleave);
		//final RadioButton enterRadio = (RadioButton)activity.findViewById(R.id.radioentry);
		
		final Spinner spinnerAct = (Spinner)activity.findViewById(R.id.spinner_act);
		
		final Spinner spinner = (Spinner)activity.findViewById(R.id.spinner);
		final Switch switcher = (Switch)activity.findViewById(R.id.switcher);
		final TextView seekbarText = (TextView)activity.findViewById(R.id.seekBarText);
		final SeekBar seekbar = (SeekBar)activity.findViewById(R.id.seekBar);
		
		final Spinner radiusSpinner = (Spinner)activity.findViewById(R.id.spinner_radius);
		
		final Button buttonOK = (Button)activity.findViewById(R.id.pos_save);
		buttonOK.setVisibility(View.INVISIBLE);
		final Button buttonDelete = (Button)activity.findViewById(R.id.pos_del);
		
		
		// ---------- Spinners ---------------------
		spinnerAct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				actuator = myXsone.getActiveActuator(adapter_act.getItem(position));
				
				if (actuator.getType().equals("temperature")){
					// Spinner
					spinner.setVisibility(View.VISIBLE);
					seekbarText.setVisibility(View.GONE);
					seekbar.setVisibility(View.GONE);
					switcher.setVisibility(View.GONE);
					
					ArrayAdapter<String> spinnerAdapter = Utilities.getTemperatureSpinnerAdapter(activity);
		        	spinner.setAdapter(spinnerAdapter);
		        	
				} else if (actuator.isDimmable()){
					// Dimmer
					spinner.setVisibility(View.GONE);
					seekbarText.setVisibility(View.VISIBLE);
					seekbar.setVisibility(View.VISIBLE);
					switcher.setVisibility(View.GONE);
					
		        	seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
							seekbarText.setText(seekBar.getProgress() + " %");
						}
		
						public void onStartTrackingTouch(SeekBar seekBar) {
						}
		
						public void onStopTrackingTouch(SeekBar seekBar) {
						}
					});
				} else if (actuator.isMakro()){
					spinner.setVisibility(View.GONE);
					seekbarText.setVisibility(View.GONE);
					seekbar.setVisibility(View.GONE);
					switcher.setVisibility(View.GONE);
				
				} else {
					// Button
					spinner.setVisibility(View.GONE);
					seekbarText.setVisibility(View.GONE);
					seekbar.setVisibility(View.GONE);
					switcher.setVisibility(View.VISIBLE);
				}
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {				
			}
		});
		spinnerAct.setAdapter(adapter_act);
		
		
		// ------------- Radius -----------------------
		
		String[] vals = {"100", "100", "150", "250", "500"};
    	final ArrayAdapter<String> radiusAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, vals);
    	radiusSpinner.setAdapter(radiusAdapter);
    	radiusSpinner.setSelection(2);
    	radiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    		@Override
    		public void onItemSelected(AdapterView<?> arg0, View arg1,
    				int pos, long arg3) {
    			radius = Integer.parseInt(radiusAdapter.getItem(pos));
    			if (currentBestLocation != null){
    				String provider = currentBestLocation.getProvider();
    				showMap(provider);
    			}
    		}
    		@Override
    		public void onNothingSelected(AdapterView<?> arg0) {
    			// TODO Auto-generated method stub
    			
    		}
    	});
		
		//-------------- Load Position ----------------
		
		pos_info = (TextView) activity.findViewById(R.id.pos_info);
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		buttonLoadPos.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog = ProgressDialog.show(activity, "", "Beste Position abrufen (" + SEARCH_TIME / 1000 + " Sek)...", true, false);
				dialog.show();

				// Updates von Netzwerk und GPS holen
				gps_listener = new MyLocationListener();
				net_listener = new MyLocationListener();
				if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) 
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, net_listener);
				if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gps_listener);

				new LocationWait().execute();
				
				buttonOK.setVisibility(View.VISIBLE);
			}
		});
		
		// --------------- Save Button -------------------
		
		buttonOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (latitude == 0 && longitude == 0) {
					XsError.printError(activity, "keine Position gesetzt!");
					return;
				}
				if (spinnerAct.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
					return;
				}

				// die Position
				AlertDialog.Builder alert = new AlertDialog.Builder(activity);

				alert.setTitle(activity.getString(R.string.new_position));
				alert.setMessage(activity.getString(R.string.give_name));

				// Set an EditText view to get user input
				final EditText input = new EditText(activity);
				alert.setView(input);

				alert.setPositiveButton(activity.getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String newName = input.getText().toString();
												
						PositionDB pos = new PositionDB();
						pos.setName(newName);
						pos.setActNumber(actuator.getNumber());
						pos.setLat(latitude);
						pos.setLon(longitude);
						pos.setOnEntry(exitRadio.isChecked() ? false : true);
						
						LocationHelper lh = new LocationHelper(activity);
						pos.setLastOnEntry(lh.isInProximity(latitude, longitude, radius));
						pos.setRadius(Integer.parseInt(radiusSpinner.getSelectedItem().toString()));
						
						String value = "";
						if (spinner.getVisibility() == View.VISIBLE){
							// spinner
							value = Utilities.getValueFromTemperature(spinner.getSelectedItem().toString());
						} else if (seekbar.getVisibility() == View.VISIBLE){
							// seekbar
							value = seekbar.getProgress()+"";
						} else if (switcher.getVisibility() == View.VISIBLE) {
							// button
							value = switcher.isChecked() == true ? "100" : "0";
						} else {
							// makro
							value = "100";
						}
						
						pos.setValue(Float.parseFloat(value));
							
						(new AddPosition()).execute(pos);
						
						

					}
				});
				alert.show();
			}

		});
		
		// --------------- Delete Position ---------------
		buttonDelete.setVisibility(View.GONE);
		
	}
	
	private void showMap(String provider){
		pos_info.setText("Provider: "+provider+", lat: " + latitude + ", long: " + longitude);
		
		LatLng destination = new LatLng(latitude, longitude);
		
		map.clear();
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setBuildingsEnabled(true);
				
		map.addMarker(new MarkerOptions().position(destination).title("Position"));
		
		CircleOptions circleOptions = new CircleOptions()
		  .center(destination)   //set center
		  .radius(radius)   //set radius in meters
		  .fillColor(Color.argb(100, 186, 213, 240))  //default
		  .strokeColor(Color.TRANSPARENT)
		  .strokeWidth(1);
		
		map.addCircle(circleOptions);
		
		// Move the camera instantly to hamburg with a zoom of 15.
	    //map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 4));

	    // Zoom in, animating the camera.
	    //map.animateCamera(CameraUpdateFactory.zoomTo(16), 3000, null);
	    	    
	    CameraPosition cameraPosition =
	            new CameraPosition.Builder()
	              .target(destination)
	                             .zoom(16)
	                             .tilt(30)
	                             .build();
	     
	          
	          map.animateCamera(
	            CameraUpdateFactory.newCameraPosition(cameraPosition), 3000, null);
	    
	}
	
	private void startService() {
				
		// Custom Proximity Alert
        Intent intent = new Intent(activity, ProximityAlertService.class);
        activity.startService(intent);
	}
	
	
		
		/** Checks whether two providers are the same */
		private boolean isSameProvider(String provider1, String provider2) {
			if (provider1 == null) {
				return provider2 == null;
			}
			return provider1.equals(provider2);
		}
		
		private void setBestLocation() {
			String provider = "no provider";

			if (currentBestLocation != null) {
				latitude = currentBestLocation.getLatitude();
				longitude = currentBestLocation.getLongitude();

				provider = currentBestLocation.getProvider();

				showMap(provider);

			} else
				pos_info.setText("Provider: " + provider + " Location: -");
		}
		
		/**
		 * Determines whether one Location reading is better than the current
		 * Location fix
		 * 
		 * @param location
		 *            The new Location that you want to evaluate
		 * @param currentBestLocation
		 *            The current Location fix, to which you want to compare the new
		 *            one
		 */
		protected boolean isBetterLocation(Location location, Location currentBestLocation) {
			if (currentBestLocation == null) {
				// A new location is always better than no location
				return true;
			}

			// Check whether the new location fix is newer or older
			long timeDelta = location.getTime() - currentBestLocation.getTime();
			boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
			boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
			boolean isNewer = timeDelta > 0;

			// If it's been more than two minutes since the current location, use
			// the new location
			// because the user has likely moved
			if (isSignificantlyNewer) {
				return true;
				// If the new location is more than two minutes older, it must be
				// worse
			} else if (isSignificantlyOlder) {
				return false;
			}

			// Check whether the new location fix is more or less accurate
			int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
			boolean isLessAccurate = accuracyDelta > 0;
			boolean isMoreAccurate = accuracyDelta < 0;
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;

			// Check if the old and new location are from the same provider
			boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

			// Determine location quality using a combination of timeliness and
			// accuracy
			if (isMoreAccurate) {
				return true;
			} else if (isNewer && !isLessAccurate) {
				return true;
			} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
				return true;
			}
			return false;
		}
		
		/************************************************************************************************************
		 * Innere Klassen
		 * 
		 * @author Octalus
		 *
		 */
		
		public class MyLocationListener implements LocationListener {
			public void onLocationChanged(Location location) {
				// // DEBUG
				// if (currentBestLocation != null) {
				// float distance = location.distanceTo(currentBestLocation);
				// Toast.makeText(Positioning.this, "Distance from Point:" +
				// distance, Toast.LENGTH_LONG).show();
				// }

				if (isBetterLocation(location, currentBestLocation)) {
					Toast t;
					if (currentBestLocation == null){
						t = Toast.makeText(activity, activity.getString(R.string.got_position), Toast.LENGTH_SHORT);
					} else {
						t = Toast.makeText(activity, activity.getString(R.string.got_better_position), Toast.LENGTH_SHORT);
					}
					
					currentBestLocation = location;
					
					if (!t.getView().isShown())
						t.show();
				}
			}

			public void onStatusChanged(String s, int i, Bundle b) {
			}

			public void onProviderDisabled(String s) {
			}

			public void onProviderEnabled(String s) {
			}
		}

		private class LocationWait extends AsyncTask<Void, Boolean, Boolean> {

			@Override
			protected Boolean doInBackground(Void... params) {
				// SLEEP FOR SEARCHING
				SystemClock.sleep(SEARCH_TIME);
				return null;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				locationManager.removeUpdates(gps_listener);
				locationManager.removeUpdates(net_listener);
				setBestLocation();
				dialog.dismiss();
			}
		}
		
		// -----------------------------------
		private class AddPosition extends AsyncTask<PositionDB, Boolean, Boolean> {
			
			public AddPosition(){
			}
			
			/**
			 * 
			 */
			@Override
			protected Boolean doInBackground(PositionDB... data) {
				PositionDB pos = data[0];
				
				long id = db.createPosition(pos);
				db.closeDB();
				
				return id > -1 ? true : false;
			}

			/**
			 * 
			 */
			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				
				// falls bisher alles ok war kann der Prozess beendet werden
				if (result) {					
					startService();
					
					Toast.makeText(activity, activity.getString(R.string.add_position_success),
							Toast.LENGTH_LONG).show();
					
					Toast.makeText(activity, activity.getString(R.string.pos_service_started),
							Toast.LENGTH_LONG).show();
					
					if (tablet) {
						MenuItemDetailFragmentPosition fragment = new MenuItemDetailFragmentPosition();
						activity.getSupportFragmentManager().beginTransaction()
								.replace(R.id.menuitem_detail_container, fragment).commit();
					} else {
						activity.finish();
					}
				}
				// Sonst erfolgt ein Hinweistext
				else {
					XsError.printError(activity);
					return;
				}
			}
		}
}
