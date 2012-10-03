package com.lars_albrecht.android.smsplan.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lars_albrecht.android.smsplan.activities.CreateEventActivity;
import com.lars_albrecht.android.smsplan.model.ScheduledEvent;

public class EventsDataSource {

	private static final String TAG = "EventDataSource";
	// Database fields
	private SQLiteDatabase database;
	private final MySQLiteHelper dbHelper;

	@SuppressWarnings("unused")
	private final String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_PHONENUMBER,
			MySQLiteHelper.COLUMN_MESSAGE, MySQLiteHelper.COLUMN_SENT };

	public EventsDataSource(final Context context) {
		this.dbHelper = new MySQLiteHelper(context);
		this.open();
	}

	public void open() throws SQLException{
		this.database = this.dbHelper.getWritableDatabase();
	}

	public void close(){
		this.dbHelper.close();
	}

	public ScheduledEvent getNextEvent(){
		final Cursor cursor = this.database.rawQuery(
				"SELECT * FROM " + MySQLiteHelper.TABLE_EVENTS + " WHERE date >= " + System.currentTimeMillis() + " AND "
						+ MySQLiteHelper.COLUMN_SENT + " = 0" + " ORDER BY date LIMIT 1", null);
		Log.d(EventsDataSource.TAG, "Suche Zeit " + System.currentTimeMillis());
		return this.cursorToScheduledEvent(cursor);
	}

	public ArrayList<ScheduledEvent> getAllMarkedEvents(){
		final Cursor cursor = this.database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_EVENTS + " WHERE "
				+ MySQLiteHelper.COLUMN_SENT + " = 1 " + " ORDER BY date LIMIT 1", null);
		while (cursor.moveToNext()) {
			this.cursorToScheduledEvent(cursor);
		}

		return null;
	}

	public ScheduledEvent getNextSendeableEvent(){
		final Cursor cursor = this.database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_EVENTS + " WHERE "
				+ MySQLiteHelper.COLUMN_SENT + " = 0" + " ORDER BY date ASC LIMIT 1", null);
		return this.cursorToScheduledEvent(cursor);
	}

	/**
	 * 
	 * @param event
	 */
	public int countSentEvents(){
		return 0;
	}

	public void markEvent(final ScheduledEvent event){
		if (event != null) {
			this.database.rawQuery("UPDATE " + MySQLiteHelper.TABLE_EVENTS + " SET " + MySQLiteHelper.COLUMN_SENT + "= 1 WHERE _id = "
					+ event.getId(), null);
			Log.d(EventsDataSource.TAG, "Event marked");
		} else {
			Log.d(EventsDataSource.TAG, "Event is null, cannot be marked!");
		}
	}

	/**
	 * 
	 * @param event
	 *            ScheduledEvent
	 */
	public void createEvent(final ScheduledEvent event){
		final ContentValues values = new ContentValues();
		final Calendar cal = Calendar.getInstance();
		long targettime = 0;
		switch (CreateEventActivity.EVENT_TYPE) {
			default:
			case CreateEventActivity.TYPE_OWN:
				targettime = event.getDate().getTime();
				break;
			case CreateEventActivity.TYPE_CHRISTMAS:
				cal.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 24, 0, 0, 0);
				targettime = cal.getTimeInMillis();
				break;
			case CreateEventActivity.TYPE_NEWYEAREVE:
				cal.set(cal.get(Calendar.YEAR) + 1, Calendar.JANUARY, 31, 0, 0, 0);
				targettime = cal.getTimeInMillis();
				break;
		}

		values.put(MySQLiteHelper.COLUMN_DATE, targettime);
		values.put(MySQLiteHelper.COLUMN_PHONENUMBER, event.getPhoneNumber());
		values.put(MySQLiteHelper.COLUMN_MESSAGE, event.getMessage());
		values.put(MySQLiteHelper.COLUMN_SENT, 0);

		final long insertId = this.database.insert(MySQLiteHelper.TABLE_EVENTS, null, values);
		Log.d(EventsDataSource.TAG, "Inserted ID: " + insertId);
	}

	/**
	 * 
	 * @param cursor
	 *            Cursor
	 * @return ScheduledEvent
	 */
	private ScheduledEvent cursorToScheduledEvent(final Cursor cursor){
		ScheduledEvent event = null;
		if (cursor.getPosition() <= -1 || cursor.isNull(cursor.getPosition())) {
			cursor.moveToNext();
		}
		event = new ScheduledEvent();
		event.setId(cursor.getInt(0));
		event.setDate(new Date(cursor.getLong(1)));
		event.setPhoneNumber(cursor.getString(2));
		event.setMessage(cursor.getString(3));
		event.setSent(cursor.getInt(4));
		return event;
	}
}
