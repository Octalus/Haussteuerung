package com.android.xs.view.widget;

import com.android.xs.controller.intent.Dictate;
import com.android.xs.view.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.RemoteViews;

public class SpeechWidget extends AppWidgetProvider{
	
static public final int SPEECH_RECOGNIZED = 1;

	
	
	public static void recognize(Activity activity) 
	{
		Intent intent = new Intent();
		intent.setAction(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		activity.startActivityForResult(intent, SPEECH_RECOGNIZED);
	}
	
	@Override  
	  public void onUpdate(Context context, AppWidgetManager   
	      appWidgetManager, int[] appWidgetIds)  
	  {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            
            Intent intent = new Intent(context, Dictate.class);
    		
            PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widgetButton, pendingIntent);  
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
		Log.i("XS1", "widget called!!!");
	  }

}
