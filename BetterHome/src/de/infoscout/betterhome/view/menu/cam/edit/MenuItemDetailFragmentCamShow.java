package de.infoscout.betterhome.view.menu.cam.edit;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import de.infoscout.betterhome.R;
import de.infoscout.betterhome.view.menu.cam.MenuItemDetailActivityCam;

public class MenuItemDetailFragmentCamShow extends Fragment {
		
	private static FragmentActivity activity;
	private boolean tablet = false;
	
	private String camUrl;
	private String camUser;
	private String camPass;
	private String camName;
	private int stream;
	
	private VideoView videoView;
	private WebView webView;
	private ProgressDialog progDailog;
	
	public MenuItemDetailFragmentCamShow() {
		
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		activity = this.getActivity();
		
		if (!Vitamio.isInitialized(activity))
		    return;
		
		if (activity.findViewById(R.id.menuitem_detail_container) != null) {
			tablet = true;
		}
		
		if (!tablet) {
			activity.getActionBar().hide();
		}
		
		Bundle args = getArguments();
		camUrl = args.getString("camUrl");
		stream = args.getInt("stream");
		camUser = args.getString("camUser");
		camPass = args.getString("camPass");
		camName = args.getString("camName");
		
		if (camUser != null && !camUser.equals("") && camPass != null && !camPass.equals("")){
			int pos = camUrl.indexOf("//");
			camUrl = camUrl.substring(0, pos+2) + camUser + ":" + camPass + "@" + camUrl.substring(pos+2, camUrl.length());
		}
		
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null;
		if (stream == 1) {
			view = inflater.inflate(R.layout.fragment_menuitem_detail_cam_edit_stream_vitamio, container, false); 
		} else {
			view = inflater.inflate(R.layout.fragment_menuitem_detail_cam_edit_web, container, false);
		}
		
		getActivity().setTitle(camName);
		
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		if (stream == 1){
			
			// Stream
			videoView = (VideoView)activity.findViewById(R.id.surface_view);
			
			if (!tablet) {
				MediaController mediaController = new MediaController(activity);
		        mediaController.setAnchorView(videoView);
		        videoView.setMediaController(mediaController);
			}
	        
			Uri video = Uri.parse(camUrl);
						
	        videoView.setVideoURI(video);
	        videoView.requestFocus();
	        videoView.setKeepScreenOn(true);
	        videoView.bringToFront();
	        //videoView.postInvalidateDelayed(100);
	        videoView.start();
	        
	        //mediaController.show();
	                
	        progDailog = ProgressDialog.show (activity, getString(R.string.please_wait), getString(R.string.loading_video), true, true);
	        
	        videoView.setOnPreparedListener(new OnPreparedListener() {
	            public void onPrepared(MediaPlayer mp) {
	            	mp.setPlaybackSpeed(1.0f);
	            }
	        });
	        
	        videoView.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
				
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int percent) {
					if (percent > 2){
						progDailog.dismiss();
						mp.start();
					}
				}
			});
	        
	        
		} else {
			// Web page
			webView = (WebView) activity.findViewById(R.id.webView1);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setSupportZoom(true);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.getSettings().setAllowContentAccess(true);
			webView.setWebViewClient(new WebViewClient() {
				@Override
			    public boolean shouldOverrideUrlLoading(WebView view, String url) {
			        view.loadUrl(url);
			        return true;
			    }
			});
			webView.loadUrl(camUrl);
			
			// Damit nun komplett raus gezoomt?
			webView.getSettings().setLoadWithOverviewMode(true) ;
			webView.getSettings().setUseWideViewPort(true);
			webView.setInitialScale(1);
		}
		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			/*NavUtils.navigateUpTo(this, new Intent(this,
					MenuItemDetailActivityTimer.class));*/
			if (videoView != null) videoView.stopPlayback();
			if (webView != null) {
				if (Build.VERSION.SDK_INT < 18) {
					   webView.clearView();
					} else {
					   webView.loadUrl("about:blank");
					}
			}
			//finish();
			NavUtils.navigateUpTo(activity, new Intent(activity,
					MenuItemDetailActivityCam.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
    @Override
    public void onStop() {
        super.onStop();
        if (videoView != null)  videoView.stopPlayback();
    }
    
    @Override
    public void onPause() {
    	if (videoView != null) videoView.stopPlayback();
    	super.onPause();
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void onDestroy() {
       	if (videoView != null) videoView.stopPlayback();
    	if (webView != null) {
			if (Build.VERSION.SDK_INT < 18) {
				   webView.clearView();
				} else {
				   webView.loadUrl("about:blank");
				}
		}
    	
    	super.onDestroy();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
         super.onConfigurationChanged(newConfig);
    }
	
}
