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
	 * Will be called at alarm.
	 * 
	 * @param Context
	 *            context
	 * @param Intent
	 *            intent
	 */
	@Override
	public void onReceive(final Context context, final Intent intent){
		// TODO send sms
		// SmsManager sms = SmsManager.getDefault();
		// sms.sendTextMessage(phoneNumber, null, message, pi, null);
		Log.d(AlarmEvent.TAG, "onReceive - send sms");

		// check for next event
		EventsDataSource model = new EventsDataSource(context);
		final ScheduledEvent targetEvent = model.getNextSendeableEvent();
		if (targetEvent != null) {
			model.markEvent(targetEvent);
			final SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(targetEvent.getPhoneNumber(), null, targetEvent.getMessage(), null, null);

		} else {
			Log.d(AlarmEvent.TAG, "no targetEvent found.");
		}
		model.close();

		model = new EventsDataSource(context);
		final ScheduledEvent nextEvent = model.getNextEvent();
		// set new alarm for next event
		if (nextEvent != null) {
			Log.d(AlarmEvent.TAG, "message for next Event: " + nextEvent.getMessage());
			Utilities.setAlarm(context, nextEvent);
		}
		model.close();

	}

}
