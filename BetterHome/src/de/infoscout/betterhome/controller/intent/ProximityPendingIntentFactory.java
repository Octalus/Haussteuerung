package de.infoscout.betterhome.controller.intent;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;


/**
 * Factory to create a pending intent to use with proximity alerts.
 * 
 * @author Adam Stroud &#60;<a href="mailto:adam.stroud@gmail.com">adam.stroud@gmail.com</a>&#62;
 */
public class ProximityPendingIntentFactory
{
    public static final String PROXIMITY_ACTION = "de.infoscout.betterhome.controller.SEND_XS";
    private static final int REQUEST_CODE = 0;

    public static PendingIntent createPendingIntent(Context context)
    {
        return PendingIntent.getBroadcast(context,
                REQUEST_CODE,
                new Intent(PROXIMITY_ACTION),
                PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
