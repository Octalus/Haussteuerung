package com.android.xs.view;

import java.util.LinkedList;

import com.android.xs.controller.storage.RuntimeStorage;
import com.android.xs.controller.usage.MakroController;
import com.android.xs.model.device.Xsone;
import com.android.xs.model.device.components.Makro;
import com.android.xs.model.error.XsError;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Viktor Mayer
 * 
 */
public class Makros_Frame extends ListActivity {

	/**
	 * Variablen
	 ***********************************************************************************************************************************************************/

	private Xsone myXsone = null;
	private LinkedList<String> makro_list = new LinkedList<String>();
	// Abfrage, ob Element gelï¿½scht werden soll
	private AlertDialog ad;
	private ProgressDialog dialog1;

	/**
	 * Konstruktoren
	 ***********************************************************************************************************************************************************/

	/**
	 * Funktionen
	 ***********************************************************************************************************************************************************/

	/**
	 * Hier wird eine Liste mit allen Makros der XS1 ausgegeben. Einfaches
	 * klicken auf ein Element fï¿½hrt das makro aus, wï¿½hrend langes klicken zu
	 * einer Abfrage fï¿½hrt, ob das Makro gelï¿½scht werden soll
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Das Grundlayout des tabs ist ein TableLayout, welches Scrollbar ist.
		setContentView(R.layout.tab_list_layout);

		// Das Xsone Objekt mit allen Daten holen aus dem Hauptprozess, welches
		// diese bereit stellt
		myXsone = RuntimeStorage.getMyXsone();

		ad = new AlertDialog.Builder(this).create();

		// Die Optionen setzen
		if (myXsone.getMyMakroList().size() > 0) {
			for (Makro m : myXsone.getMyMakroList()) {
				makro_list.add(m.getName());
			}
		} else
			Toast.makeText(this, "keine Makros definiert!", Toast.LENGTH_SHORT)
					.show();

		// Die Liste ausgeben
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.list_item_medium, makro_list);
		setListAdapter(adapter);

		final ListView lv = getListView();
		
		/**
		 * Der einfache click listener zum Ausfï¿½hren des Makros
		 */
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// geklickte Makro ausfï¿½hren
				dialog1 = ProgressDialog.show(Makros_Frame.this, "",
						"Makro wird ausgeführt...", true, false);
				dialog1.show();
				new ExecuteMakro().execute((int) id);
			}
		});

		/**
		 * der long click listener zum lï¿½schen eines Makros
		 */
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {

				ad.setCancelable(false); // This blocks the 'BACK' button
				ad.setTitle("Hinweis");
				ad.setMessage("Makro löschen?");
				// Die beiden Auswahlbuttons setzen
				ad.setButton(AlertDialog.BUTTON_POSITIVE, "Ja", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// Dialog schlieï¿½en
						dialog.dismiss();
						// Makro lï¿½schen
						myXsone.getMyMakroList().remove(arg2);
						MakroController.getInstance().saveMakros();
						finish();
					}
				});
				ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Nein", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// Dialog schließen
						dialog.dismiss();
					}
				});
				ad.show();
				return true;
			}
		});
	}

	/**
	 * Die Klasse führt das makro als eigenen Task aus. Dabei wird eine
	 * Ladeanzeige gezeigt
	 * 
	 * @author Viktor Mayer
	 * 
	 */
	private class ExecuteMakro extends AsyncTask<Integer, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Integer... params) {
			return myXsone.getMyMakroList().get((int) params[0]).replay();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(getBaseContext(), "Makro wurde ausgeführt!",
						Toast.LENGTH_SHORT).show();
				// Status invalidieren
				RuntimeStorage.setStatusValid(false);
			} else
				XsError.printError(getBaseContext());
			if (dialog1.isShowing())
				dialog1.dismiss();
		}
	}
}
