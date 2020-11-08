package de.infoscout.betterhome.view.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.model.device.Xsone;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class MenuItemListContent {

	/**
	 * An array of sample (dummy) items.
	 */
	private List<MenuItem> ITEMS = null;

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	
	private Map<Integer, MenuItem> ITEM_MAP = null;
	
	public static int ACTUATORS = 1;
	public static int SENSORS = 2;
	public static int SURVEILLANCES = 3;
	public static int ROOMS = 4;
	public static int TIMERS = 5;
	public static int RULES = 6;
	public static int POSITIONS = 7;
	
	private Xsone myXsone;
	
	@SuppressLint("UseSparseArrays")
	private void initObjects(){
		if (ITEM_MAP == null || ITEMS == null) {
			ITEM_MAP = new HashMap<Integer, MenuItem>();
			ITEMS = new ArrayList<MenuItem>();
			
			myXsone = RuntimeStorage.getMyXsone();
			
			LinkedList<String> features = null;
			if (myXsone != null) {
				features = myXsone.getFeatures();
			}
			
			addItem(new MenuItem(ACTUATORS, R.string.actuators, R.drawable.switcher));
			
			if ((features != null && features.contains("B"))) {
				addItem(new MenuItem(SENSORS, R.string.sensors, R.drawable.signal));
			}
			
			addItem(new MenuItem(SURVEILLANCES, R.string.surveillance, R.drawable.cameras));
			addItem(new MenuItem(ROOMS, R.string.rooms, R.drawable.rooms));
			
			if ((features != null && features.contains("C"))) {
				addItem(new MenuItem(TIMERS, R.string.timer, R.drawable.clock));
				addItem(new MenuItem(RULES, R.string.rules, R.drawable.regeln));
			}	
			
			addItem(new MenuItem(POSITIONS, R.string.positions, R.drawable.position));
		}
	}
	
	public MenuItemListContent(){
	}

	private void addItem(MenuItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}
	

	public List<MenuItem> getITEMS() {
		if (ITEMS == null) {
			initObjects();
		}
		
		return ITEMS;
	}

	public Map<Integer, MenuItem> getITEM_MAP() {
		if (ITEM_MAP == null) {
			initObjects();
		}
		
		return ITEM_MAP;
	}


	/**
	 * A dummy item representing a piece of content.
	 */
	public class MenuItem {
		public int id;
		public int content;
		public int image;

		public MenuItem(int id, int content, int image) {
			this.id = id;
			this.content = content;
			this.image = image;
		}
	}
}
