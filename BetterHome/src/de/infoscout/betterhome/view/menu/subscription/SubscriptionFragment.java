package de.infoscout.betterhome.view.menu.subscription;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.controller.service.SubscribeService;
import de.infoscout.betterhome.controller.storage.RuntimeStorage;
import de.infoscout.betterhome.view.menu.MenuItemListActivity;
import de.infoscout.betterhome.view.utils.Utilities;

public class SubscriptionFragment extends Fragment {
	private Activity activity;
	
	private TextView tv;
	private ScrollView sv;
	private boolean go = true;
	private Handler mHandler = new Handler();
	/**
	 * Das Runnable Objekt kann die Textview aktualisieren.
	 */
	private Runnable updateTextView = new Runnable() {
		int old_length = 0;

		public void run() {
			if (go) {	
				if (RuntimeStorage.getSubscribe_data_list().size() > old_length) {
					while (old_length < RuntimeStorage.getSubscribe_data_list().size()) {
						tv.append(RuntimeStorage.getSubscribe_data_list().get(
								old_length++));
					}
					scrollDown();
				} else if (RuntimeStorage.getSubscribe_data_list().size() == 0) {
					tv.setText("");
				}
			
				mHandler.postDelayed(updateTextView, 250);
			}
		}
	};

	public SubscriptionFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
				
		activity = this.getActivity();
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.subscriptionoptions, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.clearsubscription:
				RuntimeStorage.getSubscribe_data_list().clear();
				mHandler.postDelayed(updateTextView, 500);
								
				return true;
			case android.R.id.home:
				NavUtils.navigateUpTo((Activity)activity, new Intent(activity,
						MenuItemListActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_subscription, container, false); 
		 
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		tv = (TextView) activity.findViewById(R.id.abo_text);
		sv = (ScrollView) activity.findViewById(R.id.tab_scroll);
	}
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
	}
	
	private void scrollDown() {
		sv.post(new Runnable()
	    {
	        public void run()
	        {
	        	sv.fullScroll(View.FOCUS_DOWN);
	        }
	    });
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		// prï¿½fen ob service lï¿½uft
		if (SubscribeService.isInstanceCreated()) {
			Log.i(Utilities.TAG, "XS Subscribe Service läuft bereits...");
			mHandler.postDelayed(updateTextView, 500);
			
			// scrollen
			scrollDown();
		
		} else {
			Log.i(Utilities.TAG, "XS Subscribe Service wird gestartet!");
			activity.startService(new Intent(activity, SubscribeService.class));
			mHandler.postDelayed(updateTextView, 500);
			
			tv.setMovementMethod(new ScrollingMovementMethod());
			tv.append("Start in "+(SubscribeService.POST_DELAY)/1000+"s:\n\n");
		}
	}

	/**
	 * Der Tab ï¿½bernimmt die Aktionen des Tabhost fï¿½r Menu und Back Button
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// is activity withing a tabactivity
		if (getActivity().getParent() != null) {
			return getActivity().getParent().onKeyDown(keyCode, event);
		}
		return false;
	}
	
}
