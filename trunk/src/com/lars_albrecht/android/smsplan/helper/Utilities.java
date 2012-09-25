package com.lars_albrecht.android.smsplan.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lars_albrecht.android.smsplan.model.ScheduledEvent;
import com.lars_albrecht.android.smsplan.receiver.AlarmEvent;

/**
 * 
 * @author lars
 * 
 */
public class Utilities {

	private final static String TAG = "Utilities";

	/**
	 * Set an alarm in AlarmManager.
	 * 
	 * @param context
	 *            Context
	 * @param nextEvent
	 *            ScheduledEvent
	 */
	public static void setAlarm(final Context context, final ScheduledEvent nextEvent){
		if (nextEvent != null) {
			final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			final Intent intent = new Intent(context, AlarmEvent.class);
			final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
			final Long startTime = nextEvent.getDate().getTime();

			am.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
			Log.d(Utilities.TAG, "Alarm set. Called in " + (startTime - System.currentTimeMillis()) + " milliseconds");
		} else {
			Log.d(Utilities.TAG, "no next event");
		}
	}
}
