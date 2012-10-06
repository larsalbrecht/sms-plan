package com.lars_albrecht.android.smsplan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_EVENTS = "events";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_PHONENUMBER = "phonenumber";
	public static final String COLUMN_MESSAGE = "message";
	public static final String COLUMN_SENT = "sent";

	private static final String DATABASE_NAME = "events.db";
	private static final int DATABASE_VERSION = 1;

	private final String[] upgrageList = new String[0];

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + SQLiteHelper.TABLE_EVENTS + "(" + SQLiteHelper.COLUMN_ID
			+ " integer primary key autoincrement, " + SQLiteHelper.COLUMN_DATE + " long, " + SQLiteHelper.COLUMN_PHONENUMBER
			+ " varchar(50), " + SQLiteHelper.COLUMN_MESSAGE + " varchar(140), " + SQLiteHelper.COLUMN_SENT + " int(1));";

	public SQLiteHelper(final Context context) {
		super(context, SQLiteHelper.DATABASE_NAME, null, SQLiteHelper.DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase database){
		database.execSQL(SQLiteHelper.DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion){
		Log.w(SQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will now upgrade the database to new version.");

		// updates database to current version and exec statements to set the
		// new database structure.
		for (int i = oldVersion; i < this.upgrageList.length; i++) {
			db.execSQL(this.upgrageList[i]);
		}

		db.execSQL("DROP TABLE IF EXISTS " + SQLiteHelper.TABLE_EVENTS);
		this.onCreate(db);
	}
}
