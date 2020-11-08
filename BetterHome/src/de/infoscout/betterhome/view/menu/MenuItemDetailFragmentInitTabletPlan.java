package de.infoscout.betterhome.view.menu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuItem;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuWidget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.remote.Http;
import de.infoscout.betterhome.controller.storage.DatabaseStorage;
import de.infoscout.betterhome.controller.storage.PersistantStorage;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Actuator;
import de.infoscout.betterhome.model.device.Sensor;
import de.infoscout.betterhome.model.device.Xsone;
import de.infoscout.betterhome.model.device.components.Function;
import de.infoscout.betterhome.model.device.components.XS_Object;
import de.infoscout.betterhome.model.device.db.ActuatorDB;
import de.infoscout.betterhome.model.device.db.RoomDB;
import de.infoscout.betterhome.model.device.db.SensorDB;
import de.infoscout.betterhome.model.error.XsError;
import de.infoscout.betterhome.view.FileChooser;
import de.infoscout.betterhome.view.FileChooser.FileSelectedListener;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.menu.graph.MenuItemDetailFragmentGraph;
import de.infoscout.betterhome.view.utils.Utilities;

/**
 * A fragment representing a single MenuItem detail screen. This fragment is
 * either contained in a {@link MenuItemListActivity} in two-pane mode (on
 * tablets) or a {@link MenuItemDetailActivityAct} on handsets.
 */
public class MenuItemDetailFragmentInitTabletPlan extends Fragment {
	private FragmentActivity activity;
	
	private Xsone myXsone;
	private DatabaseStorage db;
	
	private List<RoomDB> rooms = null;
	private int roomId = -1;
	
	private RadialMenuWidget pieMenu;
	private ImageView imageView = null;
	
	private boolean assignRoom = false;
	private RoomDB roomToAssign = null;
	private int assignCounter = 1;
	
	private float pivotX;
	private float pivotY;
	
	private RadialMenuItem menuSensorItem = null;
	private RadialMenuItem menuActuatorItem = null;
	
	private final static float ZOOMED_ALPHA = 1.0f;
	
	private double newValue;
	private boolean isFunction = false;
	
	private AlertDialog.Builder builder;
	private AlertDialog pickerDialog;
	
	private Bitmap bmp;
	
	public MenuItemDetailFragmentInitTabletPlan() {
	}
	
	/**
	 * Die OnCreate Funtion stellt den Einstiegspunkt dieser Activity dar. Alle
	 * Aktuatoren werden nochmals einzeln aus der XS1 ausgelesen (Zustand etc)
	 * um die einzelnen ToggleButtons und die Beschriftung entsprechend setzen
	 * zu kï¿½nnen.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		activity = this.getActivity();
		myXsone = RuntimeStorage.getMyXsone();
		db = new DatabaseStorage(this.getActivity());
		
		(new GetRooms()).execute(false);
		
		setHasOptionsMenu(true);
		
		super.onCreate(savedInstanceState);
	}
	
	public void scaleView(View v, float startScale, float endScale, float pivotX, float pivotY) {
	    Animation anim = new ScaleAnimation(
	    		startScale, endScale, // Start and end values for the X axis scaling
	            startScale, endScale, // Start and end values for the Y axis scaling
	            Animation.RELATIVE_TO_SELF, pivotX, // Pivot point of X scaling
	            Animation.RELATIVE_TO_SELF, pivotY); // Pivot point of Y scaling
	    anim.setDuration(1500);
	    anim.setFillAfter(true); // Needed to keep the result of the animation
	    v.startAnimation(anim);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View planView = inflater.inflate(R.layout.fragment_menuitem_initial_tab_plan, container, false); 
		
		planView.setOnTouchListener(new View.OnTouchListener() {
            @Override
			public boolean onTouch(View v, MotionEvent event) {
            	
            	imageView = (ImageView)v.findViewById(R.id.plan_image);
            	
            	imageView.setScaleType(ImageView.ScaleType.MATRIX);
            	
            	if (assignRoom) {
            		switch (event.getAction()) {
		                case MotionEvent.ACTION_DOWN:
		                	
		                	int x = (int)event.getX();
		                	int y = (int)event.getY();
		                	
		                	String text = null;
		                	if (assignCounter == 1) {
		                		text = "(Punkt 1) : x = " + x + ", y = " + y;
		                	
		                	} else if (assignCounter == 2) {
		                		text = "(Punkt 2) : x = " + x + ", y = " + y;
		                	
		                	} else if (assignCounter == 3) {
		                		text = "(Punkt 3) : x = " + x + ", y = " + y;
		                	
		                	} else if (assignCounter == 4) {
		                		text = "(Punkt 4) : x = " + x + ", y = " + y;
		                	}
		                	
		                	// handle Punkt
		                	Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
		                	(new UpdateRoomDB()).execute(x, y);
		                	
		                	v.performClick();
		                    break;
		                default:
		                    break;
            		}
            	} else {
	            	switch (event.getAction()) {
		                case MotionEvent.ACTION_DOWN:
		                	String text = "You click at x = " + event.getX() + " and y = " + event.getY();
		                	
		                	RoomDB room = null;
		                	for (int i=0; i<rooms.size(); i++) {
		                		room = rooms.get(i);
		                		
		                		if (room.getPoint1_x() != null && room.getPoint1_y() != null) {
		                		
			                		double[] xs = {room.getPoint1_x(), room.getPoint2_x(), room.getPoint3_x(), room.getPoint4_x()};
			                		double[] ys = {room.getPoint1_y(), room.getPoint2_y(), room.getPoint3_y(), room.getPoint4_y()};
			                		
			                		if (Utilities.isPointInRoom(xs, ys, event.getX(), event.getY())) {
			                			Toast.makeText(activity, text + " ("+room.getName()+")", Toast.LENGTH_SHORT).show();
			                			
			                			roomId = room.getId();
	    				            	
				                		pieMenu.setHeader(room.getName(), 20);
				                		(new GetSensors()).execute();
				                		(new GetActuators()).execute();
				                			                		
				                		int[] middle = Utilities.getMiddlePointOfRoom(xs, ys);
				                		pivotX = (float)middle[0] / (float)imageView.getWidth();
				                		pivotY = (float)middle[1] / (float)imageView.getHeight();
				                		scaleView(imageView, 1f, 2.5f, pivotX, pivotY);
			
				                		imageView.setAlpha(ZOOMED_ALPHA);
				                		
						        		pieMenu.show(v);
			                		}
		                		}
		                	}
				            
				            v.performClick();
		                    break;
		                default:
		                    break;
	                }
            	}
                return true;
            }
		});
				
		return planView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.planoptions, menu);
	}
	
	
	
	@Override
	public void onDestroyView() {
		if (bmp != null) {
			bmp.recycle();
			System.gc();
		}
		super.onDestroyView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.assignroom:
				(new GetRooms()).execute(true);
				
				return true;
			case R.id.addImage:
				FileChooser fileChooser = new FileChooser(activity, null);
				fileChooser.setFileListener(new FileSelectedListener() {
				    @Override 
				    public void fileSelected(final File file) {
				        // do something with the file
				    	System.out.println(file.getAbsolutePath());
				    	
				    	byte[] data;
						try {
							data = Utilities.readFile(file);
							PersistantStorage.getInstance(activity).saveData(data, Utilities.PLAN_IMAGE_FILENAME);
							
							imageView = (ImageView) activity.findViewById(R.id.plan_image);		
							
							
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inMutable = true;
							
							bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
							imageView.setImageBitmap(bmp);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
				    }
				});
				String[] extensions = {"jpg", "png", "bmp"};
				fileChooser.setExtension(extensions);
				fileChooser.showDialog();
				
				return true;
			case android.R.id.home:
				NavUtils.navigateUpTo(activity, new Intent(activity,
						MenuItemListActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void closePieMenu() {
		imageView.setAlpha(1.0f);
		scaleView(imageView, 2.5f, 1f, pivotX, pivotY);
		pieMenu.dismiss();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		imageView = (ImageView) activity.findViewById(R.id.plan_image);		
		
		byte[] image = (byte[])PersistantStorage.getInstance(activity).getData(Utilities.PLAN_IMAGE_FILENAME);
		if (image != null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inMutable = true;
						
			bmp = BitmapFactory.decodeByteArray(image, 0, image.length, options);
			
			imageView.setImageBitmap(bmp);
		}
				
		pieMenu = new RadialMenuWidget(activity);
		
    	RadialMenuItem menuCloseItem = new RadialMenuItem(getString(R.string.close), null);
		menuCloseItem.setDisplayIcon(android.R.drawable.ic_menu_close_clear_cancel);
		menuCloseItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
			@Override
			public void execute() {

				closePieMenu();
			}
		});
		
		menuSensorItem = new RadialMenuItem(getString(R.string.sensors), null);
		menuSensorItem.setDisplayIcon(R.drawable.signal);
		
		menuActuatorItem = new RadialMenuItem(getString(R.string.actuators), null);
		menuActuatorItem.setDisplayIcon(R.drawable.switcher);
		
		pieMenu.setCenterCircle(menuCloseItem);
		
		pieMenu.setTextColor(Color.BLACK, 255);
		pieMenu.setIconSize(30, 90);
		pieMenu.setTextSize(16);
		pieMenu.setOutlineColor(Color.BLACK, 255);
		
		pieMenu.setInnerRingColor(activity.getResources().getColor(R.color.white), 235);
		pieMenu.setOuterRingColor(activity.getResources().getColor(R.color.white), 235);
		pieMenu.setCenterCircleRadius(45);
		pieMenu.setInnerRingRadius(55,155);
		pieMenu.setOuterRingRadius(165, 350);

		pieMenu.addMenuEntry(new ArrayList<RadialMenuItem>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				if (myXsone.getFeatures().contains("B")) {
					add(menuSensorItem);
				}
				add(menuActuatorItem);
			}
		});
	}
	
	private class GetRooms extends AsyncTask<Boolean, Void, String[]> {
		boolean assignRoomPopup = false;
		
		@Override
		protected String[] doInBackground(Boolean... params) {
			assignRoomPopup = params[0];
			rooms = db.getAllRooms();
			db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			
			if (assignRoomPopup) {
				AlertDialog.Builder builderrooms = new AlertDialog.Builder(activity);
				
				String[] rooms_names = new String[rooms.size()];
				
				for (int i=0; i< rooms.size(); i++){
					rooms_names[i] = rooms.get(i).getName();
				}
	
    			builderrooms.setItems(rooms_names, new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int which) {
    					roomToAssign = (RoomDB)rooms.get(which);
    					
    					// assignment flaggen
    					assignRoom = true;
    					assignCounter = 1;
    					
    					// Meldung, vier Punkte im Uhrzeigersinn drücken
    					AlertDialog messageDialog = new AlertDialog.Builder(activity).create();
    					messageDialog.setTitle(R.string.instruction);
    					messageDialog.setMessage(activity.getResources().getString(R.string.instruction_message));
    					messageDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", 
    							new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
										
									}
								});
    					messageDialog.show();
    				}
    			});
	        	
        	    builderrooms.setTitle(R.string.assignroom);
        	    
        	    // Get the AlertDialog from create()
        		AlertDialog dialogrooms = builderrooms.create();
        		
        		dialogrooms.show();
			}
			
			super.onPostExecute(result);
		}
	}
	
	private class GetSensors extends AsyncTask<Void, Void, String[]> {
		private List<XS_Object> tmp;
		private List<SensorDB> sens_list_db;
		
		private void readSensorsRemote() {
			// Liste neu holen
			tmp = Http.getInstance().get_list_sensors(activity);
			
			if (tmp == null) {
				XsError.printError(getActivity());
			} else {
				myXsone.add_RemObj(tmp);
				
				// Die Liste der Sensoren holen
				List<XS_Object> sens_list = myXsone.getMyActiveSensorList();
				
				// load DB data for sensors
				for (int i=0; i< sens_list.size(); i++) {
					int number = sens_list.get(i).getNumber();
					((Sensor)sens_list.get(i)).setSensorDB(db.getSensor(number));
				}
				
				db.closeDB();
			}
		}
		
		@Override
		protected String[] doInBackground(Void... params) {
			
			readSensorsRemote();
			
			sens_list_db = db.getSensForRoom(roomId);
			db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			List<RadialMenuItem> sensMenuChildren = new ArrayList<RadialMenuItem>();
			for (int i=0; i<sens_list_db.size(); i++){
				final Sensor sensor = myXsone.getSensor(sens_list_db.get(i).getNumber());
				
				String name = sensor.getAppname();
				String wert = Utilities.getWertForSensor(sensor.getUnit(), sensor.getValue(), sensor.getType(), activity);
	            				
				RadialMenuItem child = new RadialMenuItem(sensor.getName(), name+"\n"+wert);
				child.setDisplayIcon(Utilities.getImageForSensorType(sensor.getType(), sensor.getValue()));
				child.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
					@Override
					public void execute() {
						if (myXsone.getFeatures().contains("D")) {
							MenuItemDetailFragmentGraph fragment = new MenuItemDetailFragmentGraph();
							
							Bundle args= new Bundle();
					        args.putInt("sensorNumber", sensor.getNumber());
					        fragment.setArguments(args);
					        
					        closePieMenu();
							
					        activity.getSupportFragmentManager().beginTransaction()
									.replace(R.id.menuitem_detail_container, fragment)
									.addToBackStack(null)
									.commit();
						}
					}
				});
				
				sensMenuChildren.add(child);
			}
			
			if (sensMenuChildren.size() > 0) {
				menuSensorItem.setMenuChildren(sensMenuChildren);
			} else {
				menuSensorItem.setMenuChildren(null);
			}
				
			return;

		}
	}
	
	private class GetActuators extends AsyncTask<Void, Void, String[]> {
		private List<XS_Object> tmp;
		private List<ActuatorDB> act_list_db;
		
		private void readActuatorsRemote() {
			// Liste neu holen
			tmp = Http.getInstance().get_list_actuators();
			
			if (tmp == null) {
				XsError.printError(getActivity());
			} else {
				myXsone.add_RemObj(tmp);
				
				List<XS_Object> act_list = myXsone.getMyActiveActuatorList(true, null);
				
				// load DB data for actuators
				int number;
				for (int i=0; i< act_list.size(); i++) {
					number = act_list.get(i).getNumber();
					((Actuator)act_list.get(i)).setActuatorDB(db.getActuator(number));
				}
				
				db.closeDB();
			}
		}
		
		@Override
		protected String[] doInBackground(Void... params) {
			
			readActuatorsRemote();
			
			act_list_db = db.getActForRoom(roomId);
			db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			List<RadialMenuItem> actMenuChildren = new ArrayList<RadialMenuItem>();
			for (int i=0; i<act_list_db.size(); i++){
				final Actuator actuator = myXsone.getActuator(act_list_db.get(i).getNumber());
				
				String wert = Utilities.getWertForActuator(actuator.getUnit(), actuator.getNewvalue(), actuator.getType(), activity);
	            String name = actuator.getAppname();
				
				RadialMenuItem child = new RadialMenuItem(actuator.getAppname(), name+"\n"+wert);
				child.setDisplayIcon(Utilities.getImageForActuatorType(actuator));
				child.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
					@Override
					public void execute() {						
						builder = new AlertDialog.Builder(activity);
						builder.setTitle(actuator.getAppname());
						
						if (actuator.getActuatorDB().isUseFunction()) {
							NumberPicker np = new NumberPicker(activity);
							
							// functions
							List<Function> functions = actuator.getMyFunction();
							
							int fctSize = 0;
							for (int i=0; i<functions.size(); i++) {
								if (functions.get(i).getDsc() != null && !functions.get(i).getDsc().equals("")) {
									fctSize++;
								}
							}
							
							String[] functStr = new String[fctSize];
							for (int i=0; i<functions.size(); i++) {
								if (functions.get(i).getDsc() != null && !functions.get(i).getDsc().equals("")) {
									functStr[i] = functions.get(i).getDsc();
								}
							}
							
							np.setDisplayedValues(functStr);
							np.setMinValue(0);
							np.setMaxValue(functStr.length-1);
							np.setGravity(Gravity.CENTER);
							np.setWrapSelectorWheel(false);
							np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
							
							newValue = 1;
							isFunction = true;
							
							np.setOnValueChangedListener(new OnValueChangeListener() {
								@Override
								public void onValueChange(NumberPicker picker,
										int oldVal, int newVal) {
									
									// function number = index+1
									newValue = newVal+1;
									isFunction = true;
								}
							});
							
							builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User clicked OK button
						        	   (new SetActuator(actuator, newValue, true, isFunction)).execute();
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User cancelled the dialog
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							
							builder.setView(np);
							
							
						} else if (actuator.getType().equals("temperature")) {
							
							NumberPicker np = new NumberPicker(activity);
								
							String[] temps = Utilities.getTemperatureList();
							double currentValue = actuator.getValue();
							int currentTempIndex = -1;
							for (int i=0; i<temps.length; i++) {
								String tempS = Utilities.getValueFromTemperature(temps[i]);
								if (Double.parseDouble(tempS) == currentValue) {
									currentTempIndex = i;
								}
							}
							
							np.setDisplayedValues(temps);
							np.setMinValue(0);
							np.setMaxValue(temps.length-1);
							np.setGravity(Gravity.CENTER);
							np.setWrapSelectorWheel(false);
							np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
							if (currentTempIndex != -1) np.setValue(currentTempIndex);
															
							np.setOnValueChangedListener(new OnValueChangeListener() {
								@Override
								public void onValueChange(NumberPicker picker,
										int oldVal, int newVal) {
									
									String[] temps=picker.getDisplayedValues();
									newValue = Double.parseDouble(Utilities.getValueFromTemperature(temps[newVal]));
									isFunction = false;
								}
							});
							
							
							builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User clicked OK button
						        	   (new SetActuator(actuator, newValue, true, isFunction)).execute();
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User cancelled the dialog
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							
							builder.setView(np);
							
						} else if (actuator.isDimmable()) {
							SeekBar sb = new SeekBar(activity);
							
							sb.setProgress((int) actuator.getValue());
							sb.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									
								}
							});
							sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				        		
								public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
								}
				
								public void onStartTrackingTouch(SeekBar seekBar) {
								}
				
								public void onStopTrackingTouch(SeekBar seekBar) {
									newValue = seekBar.getProgress();
									isFunction = false;
								}
							});
							
							builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User clicked OK button
						        	   (new SetActuator(actuator, newValue, true, isFunction)).execute();
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User cancelled the dialog
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							
							builder.setView(sb);
						} else if (actuator.isMakro()) {
							NumberPicker np = new NumberPicker(activity);
								
							String[] states = {activity.getResources().getString(R.string.activate)};
							
							np.setDisplayedValues(states);
							np.setMinValue(0);
							np.setMaxValue(1);
							np.setGravity(Gravity.CENTER);
							np.setWrapSelectorWheel(false);
							np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
							np.setValue(0);
							
							np.setOnValueChangedListener(new OnValueChangeListener() {
								@Override
								public void onValueChange(NumberPicker picker,
										int oldVal, int newVal) {
									
									newValue = 100.0;
									isFunction = false;
								}
							});
							
							builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User clicked OK button
						        	   (new SetActuator(actuator, newValue, true, isFunction)).execute();
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User cancelled the dialog
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							
							builder.setView(np);
						} else if (actuator.getType().equals("shutter")) {
							NumberPicker np = new NumberPicker(activity);
								
							String[] states = {activity.getResources().getString(R.string.up), activity.getResources().getString(R.string.down)};
							
							np.setDisplayedValues(states);
							np.setMinValue(0);
							np.setMaxValue(1);
							np.setGravity(Gravity.CENTER);
							np.setWrapSelectorWheel(false);
							np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
							np.setValue(actuator.getNewvalue() == 100.0 ? 0 : 1);
							
							np.setOnValueChangedListener(new OnValueChangeListener() {
								@Override
								public void onValueChange(NumberPicker picker,
										int oldVal, int newVal) {
									
									newValue = newVal == 0 ? 100.0 : 0.0;
									isFunction = false;
								}
							});
							
							builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User clicked OK button
						        	   (new SetActuator(actuator, newValue, true, isFunction)).execute();
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User cancelled the dialog
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							
							builder.setView(np);
						} else {
							// switch
							NumberPicker np = new NumberPicker(activity);
								
							String[] states = {activity.getResources().getString(R.string.on), activity.getResources().getString(R.string.off)};
							
							np.setDisplayedValues(states);
							np.setMinValue(0);
							np.setMaxValue(1);
							np.setGravity(Gravity.CENTER);
							np.setWrapSelectorWheel(false);
							np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
							np.setValue(actuator.getValue() == 100.0 ? 0 : 1);
							
							np.setOnValueChangedListener(new OnValueChangeListener() {
								@Override
								public void onValueChange(NumberPicker picker,
										int oldVal, int newVal) {
									
									newValue = newVal == 0 ? 100.0 : 0.0;
									isFunction = false;
								}
							});
							
							builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User clicked OK button
						        	   (new SetActuator(actuator, newValue, true, isFunction)).execute();
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						               // User cancelled the dialog
						        	   pickerDialog.cancel();
										closePieMenu();
						           }
						       });
							
							builder.setView(np);
						}
						
						pickerDialog = builder.create();
						pickerDialog.show();
					}
				});
				
				actMenuChildren.add(child);
			}
			
			if (actMenuChildren.size() > 0) {
				menuActuatorItem.setMenuChildren(actMenuChildren);
			} else {
				menuActuatorItem.setMenuChildren(null);
			}
				
			return;

		}
	}
	
	private class UpdateRoomDB extends AsyncTask<Integer, Void, String[]> {
    	
    	public UpdateRoomDB(){
    	}
		
		@Override
		protected String[] doInBackground(Integer... params) {
			
			Integer x = params[0];
			Integer y = params[1];
			
			if (x != null && y != null) {
			
				if (assignCounter == 1){
					roomToAssign.setPoint1_x(x);
					roomToAssign.setPoint1_y(y);
				} else if (assignCounter == 2) {
					roomToAssign.setPoint2_x(x);
					roomToAssign.setPoint2_y(y);
				} else if (assignCounter == 3) {
					roomToAssign.setPoint3_x(x);
					roomToAssign.setPoint3_y(y);
				} else if (assignCounter == 4) {
					roomToAssign.setPoint4_x(x);
					roomToAssign.setPoint4_y(y);
				}
			
			}
			
            db.updateRoom(roomToAssign);
     	    db.closeDB();
			
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			super.onPostExecute(result);
			
			if (assignCounter < 4) {
				assignCounter++;
			} else {
				assignRoom = false;
	    		assignCounter = 1;
	    		
	    		(new GetRooms()).execute(false);
	    		
	    		// Meldung mit Summary und dass Raum fertig definiert ist
	    		AlertDialog messageDialog = new AlertDialog.Builder(activity).create();
				messageDialog.setTitle(activity.getResources().getString(R.string.success)+" - "+roomToAssign.getName());
				messageDialog.setMessage(activity.getResources().getString(R.string.success_message));
				messageDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", 
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								
							}
						});
	    		messageDialog.show();
			}
			
			return;

		}
	}
	
	private class SetActuator extends AsyncTask<Void, Void, Boolean> {
		private Actuator actuator;
		private double new_value;
		private double old_value;
		private boolean remote;
		private boolean isfunction;
    	
    	public SetActuator(Actuator act, double val, boolean rem, boolean isfunction){
			this.actuator=act;
			this.new_value=val;
			this.remote=rem;
			this.isfunction=isfunction;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			old_value = actuator.getValue();
			
			boolean setted = false;
			if (!isFunction){
				setted = actuator.setValue(new_value, remote);
			} else {
				setted = actuator.doFunction((int)new_value);
			}
			
			
			return setted;
		}

		@Override
		protected void onPostExecute(Boolean setted) {
			super.onPostExecute(setted);
			
			if (!setted){
				actuator.setValue(old_value, false);
				XsError.printError(activity);
			}
			
			return;

		}
	}
	
}
