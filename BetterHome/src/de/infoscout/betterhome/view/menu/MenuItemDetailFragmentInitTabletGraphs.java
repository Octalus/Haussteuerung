package de.infoscout.betterhome.view.menu;

import java.util.Calendar;
import java.util.List;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.AorS_Object;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.GraphsDB;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.graph.MenuItemDetailFragmentGraph;
import de.infoscout.betterhome.view.menu.graph.ReadDrawStatistics;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityAct} on handsets.
 */
public class MenuItemDetailFragmentInitTabletGraphs extends Fragment {
	private FragmentActivity activity;
	private Xsone myXsone;
	
	private LinearLayout[] graphs;
	
	private Handler myHandler = null;
	private boolean go = true;
	private ProgressDialog dialog;

	private List<GraphsDB> graphsList;
	private DatabaseStorage db;
	
	private ImageView delGraph1;
	private ImageView delGraph2;
	private ImageView delGraph3;
	private ImageView delGraph4;
	private ImageView openGraph1;
	private ImageView openGraph2;
	private ImageView openGraph3;
	private ImageView openGraph4;
	
	private Calendar from = null;
	private Calendar to = null;
		
	private final static int MAX_LABELS_X = 9;
	private final static int MAX_LABELS_Y = 15;
	private final static boolean CUT_DOWN_POINTS = true;
	private final static boolean SHORT_LABELS = true;
	private final static int MAX_POINTS = 60;
	private final static float SIZE_POINTS = 1f;
	private final static boolean SCALABLE = false;
	private final static boolean DRAW_BIG_VALUE = true;
	
	public MenuItemDetailFragmentInitTabletGraphs() {
	}
	
	/**
	 * Die OnCreate Funtion stellt den Einstiegspunkt dieser Activity dar. Alle
	 * Aktuatoren werden nochmals einzeln aus der XS1 ausgelesen (Zustand etc)
	 * um die einzelnen ToggleButtons und die Beschriftung entsprechend setzen
	 * zu k�nnen.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		activity = this.getActivity();
		myXsone = RuntimeStorage.getMyXsone();
		db = new DatabaseStorage(this.getActivity());
		
		graphs = new LinearLayout[4];
		
		setHasOptionsMenu(true);
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_menuitem_initial_tab_graphs, container, false);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.graphsoptions, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.addgraph:
				if (graphsList.size() < 4) {
					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		    		
		    	    final String[] elements = {getString(R.string.actuator), getString(R.string.sensor)};
		    	    builder.setItems(elements, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                	                	
		                	switch (which) {
		                		case 0 : 	// Ansteuerung
		                					AlertDialog.Builder builderact = new AlertDialog.Builder(activity);
		                		
		                					final List<XS_Object> actuators = myXsone.getMyActiveActuatorList(true, null);
		                					
		                					String[] act_names = new String[actuators.size()];
		                					
		                					for (int i=0; i< actuators.size(); i++){
		                						act_names[i] = ((Actuator)actuators.get(i)).getAppname();
		                					}
		                		
		                	    			builderact.setItems(act_names, new DialogInterface.OnClickListener() {
		                	    				public void onClick(DialogInterface d, int which) {
		                	    					Actuator selected_act = (Actuator)actuators.get(which);
		                	    					
		                	    					(new AssignGraphToDB()).execute(selected_act);
		                	    				}
		                	    			});
		            	    	        	
		                	        	    builderact.setTitle(R.string.actadd);
		                	        	    
		                	        	    // Get the AlertDialog from create()
		                	        		AlertDialog dialogact = builderact.create();
		                	        		
		                	        		dialogact.show();
		                	    			
		                			
		                					break;
		                		case 1 : 	// Sensor
		                					AlertDialog.Builder buildersens = new AlertDialog.Builder(activity);
		                		
					    					final List<XS_Object> sensors = myXsone.getMyActiveSensorList();
					    					
					    					String[] sens_names = new String[sensors.size()];
					    					
					    					for (int i=0; i< sensors.size(); i++){
					    						sens_names[i] = ((Sensor)sensors.get(i)).getAppname();
					    					}
					    		
					    	    			buildersens.setItems(sens_names, new DialogInterface.OnClickListener() {
					    	    				public void onClick(DialogInterface dialog, int which) {
					    	    					Sensor selected_sens = (Sensor)sensors.get(which);
					    	    					
					    	    					(new AssignGraphToDB()).execute(selected_sens);
					    	    				}
					    	    			});
						    	        	
					    	        	    buildersens.setTitle(R.string.sensadd);
					    	        	    
					    	        	    // Get the AlertDialog from create()
					    	        		AlertDialog dialogsens = buildersens.create();
					    	        		
					    	        		dialogsens.show();
		                			
		                					break;
		                		default : break;
		                	}
		                	
		                }});
		    	    	        	
		    	    builder.setTitle(R.string.elementadd);
		    	    
		    	    // Get the AlertDialog from create()
		    		AlertDialog dialog = builder.create();
		    		
		    		dialog.show();
				} else {
					Toast.makeText(activity,"Es existieren schon 4 Graphen auf dem Dashboard!",Toast.LENGTH_SHORT).show();
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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		graphs[0] = (LinearLayout) activity.findViewById(R.id.graph1);
		graphs[1] = (LinearLayout) activity.findViewById(R.id.graph2);
		graphs[2] = (LinearLayout) activity.findViewById(R.id.graph3);
		graphs[3] = (LinearLayout) activity.findViewById(R.id.graph4);
			       
		
		graphs[0].setOnDragListener(new MyDragListener());
		graphs[1].setOnDragListener(new MyDragListener());
		graphs[2].setOnDragListener(new MyDragListener());
		graphs[3].setOnDragListener(new MyDragListener());
		
		delGraph1 = (ImageView) activity.findViewById(R.id.delButton1);
		delGraph1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(activity, "",
						activity.getString(R.string.fetch_data), true, false);
				dialog.show();
				
				GraphsDB graph = graphsList.get(0);
				(new DeleteGraphFromDB()).execute(graph.getId());
			}
		});
		
		delGraph2 = (ImageView) activity.findViewById(R.id.delButton2);
		delGraph2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(activity, "",
						activity.getString(R.string.fetch_data), true, false);
				dialog.show();
				
				GraphsDB graph = graphsList.get(1);
				(new DeleteGraphFromDB()).execute(graph.getId());
			}
		});
		
		delGraph3 = (ImageView) activity.findViewById(R.id.delButton3);
		delGraph3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(activity, "",
						activity.getString(R.string.fetch_data), true, false);
				dialog.show();
				
				GraphsDB graph = graphsList.get(2);
				(new DeleteGraphFromDB()).execute(graph.getId());
			}
		});
		
		
		delGraph4 = (ImageView) activity.findViewById(R.id.delButton4);
		delGraph4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog = ProgressDialog.show(activity, "",
						activity.getString(R.string.fetch_data), true, false);
				dialog.show();
				
				GraphsDB graph = graphsList.get(3);
				(new DeleteGraphFromDB()).execute(graph.getId());
			}
		});
		
		openGraph1 = (ImageView) activity.findViewById(R.id.openButton1);
		openGraph1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GraphsDB graph = graphsList.get(0);
				
				// commit Add fragment
				MenuItemDetailFragmentGraph fragment = new MenuItemDetailFragmentGraph();
				
				Bundle args= new Bundle();
				if (graph.getActNumber() > 0) {
					args.putInt("actuatorNumber", graph.getActNumber());
					args.putInt("sensorNumber", -1);
				} else if (graph.getSensNumber() > 0) {
					args.putInt("sensorNumber", graph.getSensNumber());
					args.putInt("actuatorNumber", -1);
				}
		        fragment.setArguments(args);
				
		        activity.getSupportFragmentManager().beginTransaction()
						.replace(R.id.menuitem_detail_container, fragment)
						.addToBackStack(null)
						.commit();
			}
		});
		
		openGraph2 = (ImageView) activity.findViewById(R.id.openButton2);
		openGraph2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GraphsDB graph = graphsList.get(1);
				
				// commit Add fragment
				MenuItemDetailFragmentGraph fragment = new MenuItemDetailFragmentGraph();
				
				Bundle args= new Bundle();
				if (graph.getActNumber() > 0) {
					args.putInt("actuatorNumber", graph.getActNumber());
					args.putInt("sensorNumber", -1);
				} else if (graph.getSensNumber() > 0) {
					args.putInt("sensorNumber", graph.getSensNumber());
					args.putInt("actuatorNumber", -1);
				}
		        fragment.setArguments(args);
				
		        activity.getSupportFragmentManager().beginTransaction()
						.replace(R.id.menuitem_detail_container, fragment)
						.addToBackStack(null)
						.commit();
			}
		});
		
		openGraph3 = (ImageView) activity.findViewById(R.id.openButton3);
		openGraph3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GraphsDB graph = graphsList.get(2);
				
				// commit Add fragment
				MenuItemDetailFragmentGraph fragment = new MenuItemDetailFragmentGraph();
				
				Bundle args= new Bundle();
				if (graph.getActNumber() > 0) {
					args.putInt("actuatorNumber", graph.getActNumber());
					args.putInt("sensorNumber", -1);
				} else if (graph.getSensNumber() > 0) {
					args.putInt("sensorNumber", graph.getSensNumber());
					args.putInt("actuatorNumber", -1);
				}
		        fragment.setArguments(args);
				
		        activity.getSupportFragmentManager().beginTransaction()
						.replace(R.id.menuitem_detail_container, fragment)
						.addToBackStack(null)
						.commit();
			}
		});
		
		openGraph4 = (ImageView) activity.findViewById(R.id.openButton4);
		openGraph4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				GraphsDB graph = graphsList.get(3);
				
				// commit Add fragment
				MenuItemDetailFragmentGraph fragment = new MenuItemDetailFragmentGraph();
				
				Bundle args= new Bundle();
				if (graph.getActNumber() > 0) {
					args.putInt("actuatorNumber", graph.getActNumber());
					args.putInt("sensorNumber", -1);
				} else if (graph.getSensNumber() > 0) {
					args.putInt("sensorNumber", graph.getSensNumber());
					args.putInt("actuatorNumber", -1);
				}
		        fragment.setArguments(args);
				
		        activity.getSupportFragmentManager().beginTransaction()
						.replace(R.id.menuitem_detail_container, fragment)
						.addToBackStack(null)
						.commit();
			}
		});
	}
	
	@Override
	public void onResume() {
		dialog = ProgressDialog.show(activity, "",
				activity.getString(R.string.fetch_data), true, false);
		dialog.show();
		
		System.out.println("onResume graphs");
		go=true;
		myHandler = new Handler();
		myHandler.postDelayed(myRunnable, 0);
		
		super.onResume();
	}
	
	@Override
	public void onDestroyView() {
		go = false;
		super.onDestroyView();
	}
	
	private final Runnable myRunnable = new Runnable()
	{
	    public void run()
	    {
	    	if (go) {
		    	//Toast.makeText(activity,"Refresh",Toast.LENGTH_SHORT).show();
	    		
				from = Calendar.getInstance();
	        	from.add(Calendar.DAY_OF_YEAR, -1);
	        	to = Calendar.getInstance();
	        	
	    		(new GetGraph()).execute();
		        myHandler.postDelayed(myRunnable, RuntimeStorage.getRefreshSeconds() * 1000);
	    	}
	    }
	};
	
	private class GetGraph extends AsyncTask<Calendar, Void, String[]> {
		@Override
		protected String[] doInBackground(Calendar... params) {
			graphsList = db.getAllGraphs();
			
			db.closeDB();
			
			if (graphsList.size() > 0) {
				GraphsDB graph1 = graphsList.get(0);
				
				AorS_Object actSens = null;
				if (graph1.getActNumber() > 0) {
					actSens = myXsone.getActuator(graph1.getActNumber());
				} else if (graph1.getSensNumber() > 0) {
					actSens = myXsone.getSensor(graph1.getSensNumber());
				}
				
				ReadDrawStatistics readDrawStatistics = new ReadDrawStatistics(actSens, from, to, (FragmentActivity)activity, graphs[0]);
				
				if (graphsList.size() == 1)
					readDrawStatistics.setDialog(dialog);
				
				readDrawStatistics.setNumHorLabel(MAX_LABELS_X);
				readDrawStatistics.setNumVerLabel(MAX_LABELS_Y);
				readDrawStatistics.setMaxPoints(MAX_POINTS);
				readDrawStatistics.setSizePoints(SIZE_POINTS);
				readDrawStatistics.setCutStats(CUT_DOWN_POINTS);
				readDrawStatistics.setShortLabels(SHORT_LABELS);
				readDrawStatistics.setScalable(SCALABLE);
				readDrawStatistics.setDrawBigValue(DRAW_BIG_VALUE);
				readDrawStatistics.execute();
				
			} 
				
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			if (graphsList.size() > 0) {
				graphs[0].setVisibility(View.VISIBLE);
			} else {
				graphs[0].setVisibility(View.INVISIBLE);
				graphs[1].setVisibility(View.INVISIBLE);
				graphs[2].setVisibility(View.INVISIBLE);
				graphs[3].setVisibility(View.INVISIBLE);
				
				dialog.cancel();
			}
			
			(new GetGraph2()).execute();
			return;
		}
	}
	
	private class GetGraph2 extends AsyncTask<Calendar, Void, String[]> {	
		
		@Override
		protected String[] doInBackground(Calendar... params) {
			
			if (graphsList.size() > 1) {
				GraphsDB graph2 = graphsList.get(1);
				
				AorS_Object actSens = null;
				if (graph2.getActNumber() > 0) {
					actSens = myXsone.getActuator(graph2.getActNumber());
				} else if (graph2.getSensNumber() > 0) {
					actSens = myXsone.getSensor(graph2.getSensNumber());
				}
			
				ReadDrawStatistics readDrawStatistics = new ReadDrawStatistics(actSens, from, to, (FragmentActivity)activity, graphs[1]);

				if (graphsList.size() == 2)
					readDrawStatistics.setDialog(dialog);
				
				readDrawStatistics.setNumHorLabel(MAX_LABELS_X);
				readDrawStatistics.setNumVerLabel(MAX_LABELS_Y);
				readDrawStatistics.setMaxPoints(MAX_POINTS);
				readDrawStatistics.setSizePoints(SIZE_POINTS);
				readDrawStatistics.setCutStats(CUT_DOWN_POINTS);
				readDrawStatistics.setShortLabels(SHORT_LABELS);
				readDrawStatistics.setScalable(SCALABLE);
				readDrawStatistics.setDrawBigValue(DRAW_BIG_VALUE);
				readDrawStatistics.execute();
			} 
        	
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);

			if (graphsList.size() > 1) {
				graphs[1].setVisibility(View.VISIBLE);
			} else {
				graphs[1].setVisibility(View.INVISIBLE);
				graphs[2].setVisibility(View.INVISIBLE);
				graphs[3].setVisibility(View.INVISIBLE);
			}
						
			(new GetGraph3()).execute();
			return;
		}
	}
	
	private class GetGraph3 extends AsyncTask<Calendar, Void, String[]> {
		@Override
		protected String[] doInBackground(Calendar... params) {

			if (graphsList.size() > 2) {
				GraphsDB graph3 = graphsList.get(2);
				
				AorS_Object actSens = null;
				if (graph3.getActNumber() > 0) {
					actSens = myXsone.getActuator(graph3.getActNumber());
				} else if (graph3.getSensNumber() > 0) {
					actSens = myXsone.getSensor(graph3.getSensNumber());
				}
			
				ReadDrawStatistics readDrawStatistics = new ReadDrawStatistics(actSens, from, to, (FragmentActivity)activity, graphs[2]);

				if (graphsList.size() == 3)
					readDrawStatistics.setDialog(dialog);
				
				readDrawStatistics.setNumHorLabel(MAX_LABELS_X);
				readDrawStatistics.setNumVerLabel(MAX_LABELS_Y);
				readDrawStatistics.setMaxPoints(MAX_POINTS);
				readDrawStatistics.setSizePoints(SIZE_POINTS);
				readDrawStatistics.setCutStats(CUT_DOWN_POINTS);
				readDrawStatistics.setShortLabels(SHORT_LABELS);
				readDrawStatistics.setScalable(SCALABLE);
				readDrawStatistics.setDrawBigValue(DRAW_BIG_VALUE);
				readDrawStatistics.execute();
			} 
        	
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			if (graphsList.size() > 2) {
				graphs[2].setVisibility(View.VISIBLE);
			} else {
				graphs[2].setVisibility(View.INVISIBLE);
				graphs[3].setVisibility(View.INVISIBLE);
			}
			
			(new GetGraph4()).execute();
			return;
		}
	}
	
	private class GetGraph4 extends AsyncTask<Calendar, Void, String[]> {
		
		@Override
		protected String[] doInBackground(Calendar... params) {

			if (graphsList.size() > 3) {
				GraphsDB graph4 = graphsList.get(3);
				
				AorS_Object actSens = null;
				if (graph4.getActNumber() > 0) {
					actSens = myXsone.getActuator(graph4.getActNumber());
				} else if (graph4.getSensNumber() > 0) {
					actSens = myXsone.getSensor(graph4.getSensNumber());
				}
			
				ReadDrawStatistics readDrawStatistics = new ReadDrawStatistics(actSens, from, to, (FragmentActivity)activity, graphs[3]);
				readDrawStatistics.setDialog(dialog);
				readDrawStatistics.setNumHorLabel(MAX_LABELS_X);
				readDrawStatistics.setNumVerLabel(MAX_LABELS_Y);
				readDrawStatistics.setMaxPoints(MAX_POINTS);
				readDrawStatistics.setSizePoints(SIZE_POINTS);
				readDrawStatistics.setCutStats(CUT_DOWN_POINTS);
				readDrawStatistics.setShortLabels(SHORT_LABELS);
				readDrawStatistics.setScalable(SCALABLE);
				readDrawStatistics.setDrawBigValue(DRAW_BIG_VALUE);
				readDrawStatistics.execute();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			if (graphsList.size() > 3) {
				graphs[3].setVisibility(View.VISIBLE);
			} else {
				graphs[3].setVisibility(View.INVISIBLE);
			}
			
			return;

		}
	}
	
	private class AssignGraphToDB extends AsyncTask<AorS_Object, Void, String[]> {
			
			public AssignGraphToDB(){
			}
			
			private int getMaxSorting() {
				int max = -1;
				for (int i=0; i<graphsList.size(); i++) {
					if (graphsList.get(i).getSorting() > max) max = graphsList.get(i).getSorting(); 
				}
				
				return max;
			}
			
			@Override
			protected String[] doInBackground(AorS_Object... params) {
				AorS_Object sensAct = params[0];
				
				if (sensAct != null){
					// Graphs updaten
					GraphsDB graph = new GraphsDB();
					if (sensAct instanceof Sensor) {
						graph.setActNumber(-1);
						graph.setSensNumber(sensAct.getNumber());
						graph.setSorting(getMaxSorting()+1);
					} else if (sensAct instanceof Actuator){
						graph.setActNumber(sensAct.getNumber());
						graph.setSensNumber(-1);
						graph.setSorting(getMaxSorting()+1);
					}
					db.createGraph(graph);
				}
		 	    db.closeDB();
				
				return null;
			}
		
			@Override
			protected void onPostExecute(String[] result) {
				super.onPostExecute(result);
				
				dialog = ProgressDialog.show(activity, "",
						activity.getString(R.string.fetch_data), true, false);
				dialog.show();
				
				from = Calendar.getInstance();
	        	from.add(Calendar.DAY_OF_YEAR, -1);
	        	to = Calendar.getInstance();
	        	
	    		(new GetGraph()).execute();
				return;
			}
		}
	
	private class DeleteGraphFromDB extends AsyncTask<Integer, Void, String[]> {
		
		public DeleteGraphFromDB(){
		}
		
		@Override
		protected String[] doInBackground(Integer... params) {
			Integer id = params[0];
			
			if (id != null){
				// Graphs loeschen
				db.deleteGraph(id);
				
			}
	 	    db.closeDB();
			
			return null;
		}
	
		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			from = Calendar.getInstance();
        	from.add(Calendar.DAY_OF_YEAR, -1);
        	to = Calendar.getInstance();
       
    		(new GetGraph()).execute();
			return;
		}
	}
	
	 private class MyDragListener implements OnDragListener {
	    int enterShape = getResources().getColor(R.color.infoscout_blue_bar);
	    int normalShape = getResources().getColor(R.color.white);
	    
	    private void handleDragDropDB(int sourceOwnerId, int destOwnerId){
	    	int graph1Id = R.id.graph1;
	    	int graph2Id = R.id.graph2;
	    	int graph3Id = R.id.graph3;
	    	int graph4Id = R.id.graph4;
	    	
	    	int sourceIndex = -1;
	    	int destIndex = -1;
	    	if (sourceOwnerId == graph1Id) {
	    		sourceIndex = 0;
	    	} else if (sourceOwnerId == graph2Id) {
	    		sourceIndex = 1;
	    	} else if (sourceOwnerId == graph3Id) {
	    		sourceIndex = 2;
	    	} else if (sourceOwnerId == graph4Id) {
	    		sourceIndex = 3;
	    	}
	    	
	    	if (destOwnerId == graph1Id) {
	    		destIndex = 0;
	    	} else if (destOwnerId == graph2Id) {
	    		destIndex = 1;
	    	} else if (destOwnerId == graph3Id) {
	    		destIndex = 2;
	    	} else if (destOwnerId == graph4Id) {
	    		destIndex = 3;
	    	}
	    	
	    	(new ExchangeGraphInDB()).execute(sourceIndex, destIndex);
	    }
	    
	    @Override
	    public boolean onDrag(View v, DragEvent event) {
	      int action = event.getAction();
	      switch (action) {
	      case DragEvent.ACTION_DRAG_STARTED:
	        // do nothing
	        break;
	      case DragEvent.ACTION_DRAG_ENTERED:
	        v.setBackgroundColor(enterShape);
	        break;
	      case DragEvent.ACTION_DRAG_EXITED:
	        v.setBackgroundColor(normalShape);
	        break;
	      case DragEvent.ACTION_DROP:
	        // Dropped, reassign View to ViewGroup
	        View sourceView = (View) event.getLocalState();
	        ViewGroup sourceOwner = (ViewGroup) sourceView.getParent();
	        int sourceOwnerId = sourceOwner.getId();
	        
	        LinearLayout destOwner = (LinearLayout) v;
	        if (destOwner.getChildCount() > 1) {
	        	
		        View destView = destOwner.getChildAt(1);
		        int destOwnerId = destOwner.getId();
		        
		        if (!sourceOwner.equals(destOwner)) {
		        	sourceOwner.removeView(sourceView);
			        destOwner.removeView(destView);
			        destOwner.addView(sourceView);
			        sourceView.setVisibility(View.VISIBLE);
			        sourceOwner.addView(destView);
			        
			        handleDragDropDB(sourceOwnerId, destOwnerId);
		        } else {
		        	sourceView.setVisibility(View.VISIBLE);
		        }
	        } else {
	        	sourceView.setVisibility(View.VISIBLE);
	        }
	        break;
	      case DragEvent.ACTION_DRAG_ENDED:
	    	  v.setBackgroundColor(normalShape);
	      default:
	        break;
	      }
	      return true;
	    }
	} 
	 
	 private class ExchangeGraphInDB extends AsyncTask<Integer, Void, String[]> {
			
			public ExchangeGraphInDB(){
			}
			
			@Override
			protected String[] doInBackground(Integer... params) {
				Integer sourceIndex = params[0];
				Integer destIndex = params[1];
				
				if (sourceIndex != null && destIndex != null){
					
					GraphsDB graphSource = graphsList.get(sourceIndex);
					GraphsDB graphDest = graphsList.get(destIndex);
					
					int sortingSource = graphSource.getSorting();
					int sortingDest = graphDest.getSorting();
					
					graphSource.setSorting(sortingDest);
					graphDest.setSorting(sortingSource);
					
					db.updateGraph(graphSource);
					db.updateGraph(graphDest);
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
}
