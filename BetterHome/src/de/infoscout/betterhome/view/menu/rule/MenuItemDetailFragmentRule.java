package de.infoscout.betterhome.view.menu.rule;

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
import android.widget.TextView;
import android.widget.Toast;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.remote.Http;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Script;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.ScriptDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.adapter.ScriptAdapter;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.act.MenuItemDetailActivityAct;
import de.infoscout.betterhome.view.menu.rule.create.MenuItemDetailActivityRuleCreate;
import de.infoscout.betterhome.view.menu.rule.create.MenuItemDetailFragmentRuleCreate;
import de.infoscout.betterhome.view.menu.rule.edit.MenuItemDetailActivityRuleEdit;
import de.infoscout.betterhome.view.menu.rule.edit.MenuItemDetailFragmentRuleEdit;
import de.infoscout.betterhome.view.utils.Utilities;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityAct} on handsets.
 */
public class MenuItemDetailFragmentRule extends ListFragment implements OnRefreshListener {
	
	// Das Xsone Objekt fï¿½r diese Aktivity
	private Xsone myXsone;
	private List<XS_Object> script_list;
	private PullToRefreshLayout mPullToRefreshLayout;
	private FragmentActivity activity;
	private DatabaseStorage db;
	private boolean tablet = false;

	public MenuItemDetailFragmentRule() {
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
		activity = this.getActivity();
		db = new DatabaseStorage(this.getActivity());
		if (activity.findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_menuitem_detail_rule, container, false);
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
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						
						Object o = getListAdapter().getItem(position);
						if (o instanceof Script) {
							Log.d(Utilities.TAG, ((XS_Object) o).getName());
							
							(new GetConfigScript()).execute((Script)o);
						}
					}
				});
		
		getListView().setOnItemLongClickListener(
				new AdapterView.OnItemLongClickListener() {
					@SuppressLint("InflateParams")
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View v, int position, long id) {
						
						AlertDialog.Builder builder = new AlertDialog.Builder(((Activity)activity));
		        		
		        		// Get the layout inflater
		        	    LayoutInflater inflater = ((Activity)activity).getLayoutInflater();
		        	    View view = inflater.inflate(R.layout.dialog_script_edit, null);
		        	    builder.setView(view);
		        	    
		        	    final Script script = (Script)getListAdapter().getItem(position);
							
		        	    final EditText nameView = (EditText)view.findViewById(R.id.dialog_name_text);
		        	    final TextView listtext = (TextView)v.findViewById(R.id.text1); 
		        	    nameView.setText(listtext.getText());
		        	    			        	
		        	    builder.setTitle(R.string.dialog_title_script);
			        	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			        	           public void onClick(DialogInterface dialog, int id) {
			        	               // User clicked OK button
			        	        	   String newName = nameView.getText().toString();
			        	        	   
			   								(new SetScriptDB(script)).execute(newName);
			        	        	   
			   								listtext.setText(newName);
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
				});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.addrule:
				if (tablet){
					// commit Add fragment
					MenuItemDetailFragmentRuleCreate fragment = new MenuItemDetailFragmentRuleCreate();
					activity.getSupportFragmentManager().beginTransaction()
							.replace(R.id.menuitem_detail_container, fragment)
							.addToBackStack(null)
							.commit();
				} else {
					Intent intent = new Intent(activity, MenuItemDetailActivityRuleCreate.class);
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
		inflater.inflate(R.menu.ruleoptions, menu);
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
		private List<XS_Object> tmp;
		private ScriptAdapter adapter;
		
		@Override
		protected String[] doInBackground(Void... params) {
			
			// Liste neu holen
			//tmp = Http.getInstance().get_detailed_list_scripts();
			tmp = Http.getInstance().get_list_scripts();
			
			if (tmp == null || myXsone == null) {
				XsError.printError(getActivity());
			} else {
				myXsone.add_RemObj(tmp);

				// Die Liste der Aktuatoren holen
				script_list = myXsone.getMyActiveScriptList(false);
			}
			
			if (script_list != null) {
				// load DB data for scripts
				int number;
				for (int i=0; i< script_list.size(); i++) {
					number = script_list.get(i).getNumber();
					((Script)script_list.get(i)).setScriptDB(db.getScript(number));
				}
			}
			
			if (getActivity() != null && script_list != null && script_list.size() > 0){
				adapter = new ScriptAdapter(getActivity(), R.layout.list_item_script, script_list);
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
			
			db.closeDB();
				
			return;

		}
	}
	
	private class SetScriptDB extends AsyncTask<String, Void, String[]> {
    	Script script;
    	
    	public SetScriptDB(Script script){
    		this.script=script;
    	}
		
		@Override
		protected String[] doInBackground(String... params) {
			String newName = params[0];
			
			ScriptDB scriptDB = script.getScriptDB();
     	   if (scriptDB != null){
     		
     		    scriptDB.setName(newName);
            	db.updateScript(scriptDB);
            } else {
            	scriptDB = new ScriptDB();
            	scriptDB.setName(newName);
            	scriptDB.setNumber(script.getNumber());
            	db.createScript(scriptDB);
            	script.setScriptDB(scriptDB);
            	
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
	
	private class GetConfigScript extends AsyncTask<Script, Void, String[]> {
		private Script script;
		
		public GetConfigScript(){
		}
	
		@Override
		protected String[] doInBackground(Script... params) {
			script = params[0];
			
			script = Http.getInstance().get_config_script(script);
			
			if (script == null) {
				XsError.printError(getActivity());
			} else {
				myXsone.add_RemObj(script);
	
				// Die Liste der Skripte holen
				script_list = myXsone.getMyActiveScriptList(false);
				
				for (int i=0; i<script_list.size(); i++) {
					if (script.getNumber() == script_list.get(i).getNumber()){
						((Script)script_list.get(i)).setScriptDB(db.getScript(script.getNumber()));
					}
				}
				db.closeDB();
			}
			
			return null;
		}
	
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
						
			if (script != null) {
				
				RuleBodyConverter converter = new RuleBodyConverter(activity, script.getBody());
				
				if (script.getBody() != null) {
					try {
						converter.convert();
						
						// Script edit
						if (tablet){
							// commit Edit fragment
							MenuItemDetailFragmentRuleEdit fragment = new MenuItemDetailFragmentRuleEdit();
							
							Bundle args= new Bundle();
					        args.putInt("scriptNumber", script.getNumber());
					        fragment.setArguments(args);
							
							activity.getSupportFragmentManager().beginTransaction()
									.replace(R.id.menuitem_detail_container, fragment)
									.addToBackStack(null)
									.commit();
						} else {
							Intent intent = new Intent(getActivity(), MenuItemDetailActivityRuleEdit.class);
							intent.putExtra("scriptNumber", script.getNumber());
							startActivity(intent);
						}
						
					} catch (Exception e) {
						Toast.makeText(activity, activity.getString(R.string.script_invalid), Toast.LENGTH_LONG).show();
					}
				}
			} else {
				XsError.printError(getActivity());
			}
			
			
			
			return;
	
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
