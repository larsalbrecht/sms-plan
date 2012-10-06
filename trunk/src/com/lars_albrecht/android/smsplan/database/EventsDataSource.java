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
import com.lars_albrecht.android.smsplan.exceptions.InvalidTypeException;
import com.lars_albrecht.android.smsplan.model.ScheduledEvent;

public class EventsDataSource {

	public static final int TYPE_ALL = -1;
	public static final int TYPE_UNSENT = 0;
	public static final int TYPE_SENT = 1;

	private static final String TAG = "EventDataSource";
	// Database fields
	private SQLiteDatabase database;
	private final SQLiteHelper dbHelper;

	@SuppressWarnings("unused")
	private final String[] allColumns = { SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_DATE, SQLiteHelper.COLUMN_PHONENUMBER,
			SQLiteHelper.COLUMN_MESSAGE, SQLiteHelper.COLUMN_SENT };

	private static EventsDataSource instance = null;

	public EventsDataSource(final Context context) {
		this.dbHelper = new SQLiteHelper(context);
		this.open();
		EventsDataSource.instance = this;
	}

	/**
	 * Returns this as an singleton.
	 * 
	 * @param context
	 *            Context
	 * @return EventsDataSource
	 */
	public static EventsDataSource getInstance(final Context context){
		if (EventsDataSource.instance == null) {
			EventsDataSource.instance = new EventsDataSource(context);
		}
		return EventsDataSource.instance;
	}

	/**
	 * Open the database.
	 * 
	 * @throws SQLException
	 */
	public void open() throws SQLException{
		this.database = this.dbHelper.getWritableDatabase();
	}

	/**
	 * Close the database.
	 */
	public void close(){
		this.dbHelper.close();
	}

	/**
	 * Returns the next sendable event.
	 * 
	 * @return ScheduledEvent
	 */
	public ScheduledEvent getNextEvent(){
		final Cursor cursor = this.database.rawQuery(
				"SELECT * FROM " + SQLiteHelper.TABLE_EVENTS + " WHERE date >= " + System.currentTimeMillis() + " AND "
						+ SQLiteHelper.COLUMN_SENT + " = 0" + " ORDER BY date LIMIT 1", null);
		return this.cursorToScheduledEvent(cursor);
	}

	/**
	 * Returns all events that has value sent "type" in database. See
	 * EventsDataSource.TYPE_* to see what is available.
	 * 
	 * @return ArrayList<ScheduledEvent>
	 * @throws Exception
	 */
	public ArrayList<ScheduledEvent> getAllEvents(final int type) throws InvalidTypeException{
		final ArrayList<ScheduledEvent> tempList = new ArrayList<ScheduledEvent>();
		if (type >= -1 && type < 2) {
			final Cursor cursor = this.database.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_EVENTS
					+ (type == EventsDataSource.TYPE_ALL ? "" : " WHERE " + SQLiteHelper.COLUMN_SENT + " = " + type)
					+ " ORDER BY date LIMIT 1", null);
			while (cursor.moveToNext()) {
				tempList.add(this.cursorToScheduledEvent(cursor));
			}
		} else {
			throw new InvalidTypeException("No valid type (" + type + ") given.");
		}

		return tempList;
	}

	/**
	 * Returns the next sendable event.
	 * 
	 * @return ScheduledEvent
	 */
	public ScheduledEvent getNextSendeableEvent(){
		final Cursor cursor = this.database.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_EVENTS + " WHERE " + SQLiteHelper.COLUMN_SENT
				+ " = 0" + " ORDER BY date ASC LIMIT 1", null);
		return this.cursorToScheduledEvent(cursor);
	}

	/**
	 * 
	 * @param event
	 */
	public int countSentEvents(){
		return 0;
	}

	/**
	 * Mark an event in database as send.
	 * 
	 * @param event
	 *            ScheduledEvent
	 */
	public void markEvent(final ScheduledEvent event){
		if (event != null) {
			this.database.rawQuery(
					"UPDATE " + SQLiteHelper.TABLE_EVENTS + " SET " + SQLiteHelper.COLUMN_SENT + "= 1 WHERE _id = " + event.getId(), null);
			Log.d(EventsDataSource.TAG, "Event marked");
		} else {
			Log.d(EventsDataSource.TAG, "Event is null, cannot be marked!");
		}
	}

	/**
	 * 
	 * Create a new event in database.
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

		values.put(SQLiteHelper.COLUMN_DATE, targettime);
		values.put(SQLiteHelper.COLUMN_PHONENUMBER, event.getPhoneNumber());
		values.put(SQLiteHelper.COLUMN_MESSAGE, event.getMessage());
		values.put(SQLiteHelper.COLUMN_SENT, 0);

		final long insertId = this.database.insert(SQLiteHelper.TABLE_EVENTS, null, values);
		Log.d(EventsDataSource.TAG, "Inserted ID: " + insertId);
	}

	/**
	 * Transform a Cursor to a ScheduledEvent.
	 * 
	 * @param cursor
	 *            Cursor
	 * @return ScheduledEvent
	 */
	private ScheduledEvent cursorToScheduledEvent(final Cursor cursor){
		ScheduledEvent tempEvent = null;
		if (cursor.getPosition() <= -1 || cursor.isNull(cursor.getPosition())) {
			cursor.moveToNext();
		}

		tempEvent = new ScheduledEvent();
		tempEvent.setId(cursor.getInt(0));
		tempEvent.setDate(new Date(cursor.getLong(1)));
		tempEvent.setPhoneNumber(cursor.getString(2));
		tempEvent.setMessage(cursor.getString(3));
		tempEvent.setSent(cursor.getInt(4));

		return tempEvent;
	}
}
