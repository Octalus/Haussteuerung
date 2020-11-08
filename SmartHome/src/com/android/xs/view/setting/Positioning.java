package com.android.xs.view.setting;

import java.util.ArrayList;
import java.util.List;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.Makro;
import com.android.xs.model.error.XsError;
import com.android.xs.view.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class Positioning extends Activity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	// Das Xsone Objekt für diese Aktivity
	private Xsone myXsone;
	private LocationManager locationManager;
	private TextView pos_info;
	private double latitude = 0;
	private double longitude = 0;
	private WebView html;
	private PendingIntent proximityIntent;
	private Intent intent;
	private volatile Location currentBestLocation;
	// Dialog für Ladevorgang
	private Dialog dialog;
	private MyLocationListener gps_listener;
	private MyLocationListener net_listener;

	private static final long POINT_RADIUS = 1000; // in Meters
	private static final long PROX_ALERT_EXPIRATION = -1;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final int SEARCH_TIME = 1000 * 20;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.positioning);

		myXsone = RuntimeStorage.getMyXsone();

		html = (WebView) findViewById(R.id.html);

		html.getSettings().setJavaScriptEnabled(true);
		html.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				html.loadUrl("javascript:(function() {document.getElementById('header').style.display = 'none';"
						+ "document.getElementById('ribbon-iw').style.display = 'none';" + "document.getElementById('map').style.height = '400px';"
						+ "document.getElementById('map').style.width = '" + html.getWidth() + "px';})()");
			}
		});

		// Liste mit Makros befüllen
		ArrayList<String> vals = new ArrayList<String>();
		final Spinner spin = (Spinner) findViewById(R.id.pos_spinner);
		final List<Makro> makros = myXsone.getMyMakroList();

		for (Makro m : makros) {
			vals.add(m.getName());
		}
		// Den Adapter für den Spinner anlegen
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, vals);
		spin.setAdapter(adapter);

		// Liste mit vorhandenen Alerts befüllen
		ArrayList<String> valss = new ArrayList<String>();
		final Spinner spinn = (Spinner) findViewById(R.id.alerts_spinner);
		final List<String> alerts = myXsone.getMyProxAlertsList();

		for (String m : alerts) {
			valss.add(m);
		}
		// Den Adapter für den Spinner anlegen
		final ArrayAdapter<String> adapterr = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, valss);
		spinn.setAdapter(adapterr);

		// Positionserfassung
		Button pos_button = (Button) findViewById(R.id.pos_button1);
		Button sav_button = (Button) findViewById(R.id.pos_save);
		Button del_button = (Button) findViewById(R.id.pos_del);
		pos_info = (TextView) findViewById(R.id.pos_info);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		pos_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				html.setVisibility(View.VISIBLE);
				dialog = ProgressDialog.show(Positioning.this, "", "Beste Position abrufen (" + SEARCH_TIME / 1000 + " Sek)...", true, false);
				dialog.show();

				// Updates von Netzwerk und GPS holen
				gps_listener = new MyLocationListener();
				net_listener = new MyLocationListener();
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, net_listener);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gps_listener);

				html.setVisibility(View.GONE);

				new LocationWait().execute(Positioning.this);
			}
		});

		sav_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (latitude == 0 && longitude == 0) {
					XsError.printError(getBaseContext(), "keine Position gesetzt!");
					return;
				}
				if (spin.getSelectedItemPosition() == Spinner.INVALID_POSITION || makros.size() == 0) {
					XsError.printError(getBaseContext(), "kein Makro gewählt!");
					return;
				}

				intent = new Intent("com.android.xs.controller.SEND_XS");

				// id des neuen Intents
				int id = myXsone.getMyProxAlertsList().size() + 1;

				// prüfen welcher radiobutton gewählt ist
				RadioButton leave = (RadioButton) findViewById(R.id.radioleave);

				// proximity reagiert beim betreten und verlassen! Das intent
				// muss dies unterscheiden!

				if (leave.isChecked()) {
					intent.putExtra("makrosactiononentry", false);
				}
				// hier kann der Proximty alert genutzt werden
				else {
					intent.putExtra("makrosactiononentry", true);
				}
				ArrayList<String> mnames = new ArrayList<String>();
				mnames.add(makros.get(spin.getSelectedItemPosition()).getName());
				intent.putStringArrayListExtra("makros", mnames);

				proximityIntent = PendingIntent.getActivity(getApplicationContext(), id, intent, 0);
				locationManager.addProximityAlert(latitude, longitude, POINT_RADIUS, PROX_ALERT_EXPIRATION, proximityIntent);

				// den Alert speichern
				AlertDialog.Builder alert = new AlertDialog.Builder(Positioning.this);

				alert.setTitle("Neuer Proximity Alert..");
				alert.setMessage("Proximity-Alert benennen:");

				// Set an EditText view to get user input
				final EditText input = new EditText(Positioning.this);
				alert.setView(input);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString();
						RuntimeStorage.getMyXsone().addProxAlert(value);
						adapterr.add(value);

						Toast.makeText(getBaseContext(), "gespeichert..", Toast.LENGTH_LONG).show();
					}
				});
				alert.show();
			}

		});

		del_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				int id = spinn.getSelectedItemPosition();

				Intent intent = new Intent("com.android.xs.controller.SEND_XS");
				PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), id, intent, 0);

				locationManager.removeProximityAlert(pi);
				locationManager.removeUpdates(pi);

				adapterr.remove(myXsone.getMyProxAlertsList().get(id));
				myXsone.removeProxAlert(id);
				Toast.makeText(getBaseContext(), "ProximityAlert aufgehoben..", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setBestLocation() {
		String provider = "no provider";

		if (currentBestLocation != null) {
			latitude = currentBestLocation.getLatitude();
			longitude = currentBestLocation.getLongitude();

			provider = currentBestLocation.getProvider();

			String url = "https://maps.google.com/maps?q=" + latitude + "," + longitude + "&num=1&t=m&z=14";

			html.loadUrl(url);

			pos_info.setText("Provider: " + provider + " lat: " + latitude + " long: " + longitude);

			html.setVisibility(View.VISIBLE);

		} else
			pos_info.setText("Provider: " + provider + " Location: no location!");
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

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/**
	 * Nur für DEBUG Zwecke.. die aktuelle Distanz wird hier permanent
	 * ausgegeben
	 * 
	 * @author Viktor Mayer
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
				currentBestLocation = location;
				Toast t = Toast.makeText(Positioning.this, "got a location!", Toast.LENGTH_SHORT);
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

	private class LocationWait extends AsyncTask<Positioning, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Positioning... params) {
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

}
