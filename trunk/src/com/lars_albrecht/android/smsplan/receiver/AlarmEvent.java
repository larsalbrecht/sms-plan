package com.lars_albrecht.android.smsplan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.lars_albrecht.android.smsplan.database.EventsDataSource;
import com.lars_albrecht.android.smsplan.helper.Utilities;
import com.lars_albrecht.android.smsplan.model.ScheduledEvent;

public class AlarmEvent extends BroadcastReceiver {

	private final static String TAG = "AlarmEvent";

	/**
	 * Will be called when alarm is called.
	 * 
	 * @param Context
	 *            context
	 * @param Intent
	 *            intent
	 */
	@Override
	public void onReceive(final Context context, final Intent intent){
		Log.d(AlarmEvent.TAG, "onReceive - send sms");

		// check for event
		EventsDataSource dataSource = new EventsDataSource(context);
		final ScheduledEvent targetEvent = dataSource.getNextSendeableEvent();
		if (targetEvent != null) {
			// this.sendSMS(targetEvent.getPhoneNumber(),
			// targetEvent.getMessage());
			dataSource.markEvent(targetEvent);
		} else {
			Log.d(AlarmEvent.TAG, "no targetEvent found.");
		}
		dataSource.close();

		dataSource = new EventsDataSource(context);
		final ScheduledEvent nextEvent = dataSource.getNextEvent();
		// set new alarm for next event
		if (nextEvent != null) {
			Log.d(AlarmEvent.TAG, "message for next Event: " + nextEvent.getMessage());
			Utilities.setAlarm(context, nextEvent);
		}
		dataSource.close();
	}

	/**
	 * Send a SMS with the "message" to "number".
	 * 
	 * @param number
	 *            String
	 * @param message
	 *            String
	 */
	private void sendSMS(final String number, final String message){
		SmsManager.getDefault().sendTextMessage(number, null, message, null, null);
	}

}
